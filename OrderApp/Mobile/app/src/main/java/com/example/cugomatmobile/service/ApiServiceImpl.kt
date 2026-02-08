package com.example.cugomatmobile.service

import android.util.Log
import com.example.cugomatmobile.dto.*
import com.example.cugomatmobile.helpers.HeadersHelper
import com.example.cugomatmobile.mappers.*
import com.example.cugomatmobile.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.Response
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class ApiServiceImpl private constructor() {

    companion object {
        private const val BASE_URL_MOBILE_TEST = "";
        private const val HTTP_STATUS_UNAUTHORIZED = 401;
        private const val HTTP_STATUS_FORBIDDEN = 403;

        private var instance: ApiServiceImpl? = null

        fun getInstance(): ApiServiceImpl {
            if (instance == null) instance = ApiServiceImpl()
            return instance!!
        }
    }

    private val retrofit: Retrofit
    private val apiService: ApiService
    private var accessData: AccessData? = null

    init {
        val mapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

        val client = createHttpClient().newBuilder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val isRefresh = originalRequest.url.encodedPath.contains("/api/auth/refreshToken")
                val request = originalRequest.newBuilder().apply {
                    if (!isRefresh) accessData?.bearerToken?.let { header(HeadersHelper.AUTHORIZATION, it) }
                    header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                    header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                }.build()

                var response = chain.proceed(request)

                if (!isRefresh && (response.code == HTTP_STATUS_FORBIDDEN || response.code == HTTP_STATUS_UNAUTHORIZED) && !accessData?.refreshToken.isNullOrEmpty()) {
                    Log.i("ApiService", "Token expired, attempting refresh")
                    synchronized(this) {
                        if (refreshAccessToken()) {
                            val newToken = accessData?.bearerToken ?: ""
                            val newRequest = originalRequest.newBuilder()
                                .header(HeadersHelper.AUTHORIZATION, newToken)
                                .build()
                            response.close()
                            response = chain.proceed(newRequest)
                        }
                    }
                }

                response
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_MOBILE_TEST)
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun createHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val context = SSLContext.getInstance("SSL")
            context.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = context.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun setAccessData(accessData: AccessData) {
        this.accessData = accessData
    }

    fun clearAccessData() {
        this.accessData = null
    }

    private fun refreshAccessToken(): Boolean {
        val refreshToken = accessData?.refreshToken ?: return false
        return try {
            val request = RefreshTokenRequestDTO(refreshToken)
            val response = runBlocking { apiService.refreshToken(request) }
            if (response.isSuccessful && response.body() != null) {
                val newJwt = JwtResponseMapper.toEntity(response.body()!!)
                accessData?.accessToken = newJwt.accessToken
                accessData?.refreshToken = newJwt.refreshToken ?: refreshToken
                Log.i("ApiService", "Token refreshed successfully")
                true
            } else {
                Log.e("ApiService", "Refresh token failed: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Exception refreshing token: ${e.message}", e)
            false
        }
    }

    suspend fun login(authRequestDTO: AuthRequestDTO): JwtResponse? {
        return try {
            val result: Response<JwtResponseDTO> = apiService.login(authRequestDTO)
            if (result.isSuccessful) result.body()?.let { JwtResponseMapper.toEntity(it) } else {
                Log.e("ApiService", "Login failed: ${result.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Exception login: ${e.message}", e)
            null
        }
    }

    suspend fun createOrder(orderCreateDTO: OrderCreateDTO): Int? {
        return try {
            val response: Response<Integer> = apiService.createOrder(orderCreateDTO)
            if (response.isSuccessful) response.body()?.toInt() else null
        } catch (e: Exception) {
            Log.e("ApiService", "Exception createOrder: ${e.message}", e)
            null
        }
    }

    suspend fun getOrdersByUser(userName: String): List<Order>? {
        return try {
            val response = apiService.getOrdersByUser(userName)
            val orderList = OrderMapper.toEntityList(response).mapNotNull { it }
            if (orderList.isNotEmpty()) orderList else null
        } catch (e: Exception) {
            Log.e("ApiService", "Exception getOrdersByUser: ${e.message}", e)
            null
        }
    }

    suspend fun getCategoryList(clientCode: String): List<Category>? {
        return try {
            val response = apiService.getCategoryList(clientCode)
            val categoryList = CategoryMapper.toEntityList(response)
            if (categoryList.isNotEmpty()) categoryList else null
        } catch (e: Exception) {
            Log.e("ApiService", "Exception getCategoryList: ${e.message}", e)
            null
        }
    }

    suspend fun getProductList(clientCode: String, categoryId: Int? = null): List<Product>? {
        return try {
            val response = apiService.getProductList(clientCode, categoryId)
            val productList = ProductMapper.toEntityList(response)
            if (productList.isNotEmpty()) productList else null
        } catch (e: Exception) {
            Log.e("ApiService", "Exception getProductList: ${e.message}", e)
            null
        }
    }

    suspend fun getClient(clientCode: String): Client? {
        return try {
            val response = apiService.getClient(clientCode)
            ClientMapper.toEntity(response)
        } catch (e: Exception) {
            Log.e("ApiService", "Exception getClient: ${e.message}", e)
            null
        }
    }

    suspend fun registerUser(userDTO: UserDTO): Int? {
        return try {
            val response: Response<Int> = apiService.register(userDTO)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("ApiService", "Exception registerUser: ${e.message}", e)
            null
        }
    }

    suspend fun createStripeCheckoutSession(amount: Double): StripeCheckoutResponse? {
        return try {
            val response = apiService.createStripeCheckoutSession(amount)
            if (response.isSuccessful) response.body()?.let { StripeMapper.toEntity(it) } else null
        } catch (e: Exception) {
            Log.e("ApiService", "Exception createStripeCheckoutSession: ${e.message}", e)
            null
        }
    }

    suspend fun ping(): Boolean {
        return try {
            val response: Response<ResponseBody> = apiService.ping()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ApiService", "Exception ping: ${e.message}", e)
            false
        }
    }

    suspend fun checkUserExist(username: String): Boolean {
        return try {
            val response: Response<Boolean> = apiService.checkUserExist(username)
            if (response.isSuccessful) {
                response.body() ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Exception in checkUserExist: ${e.message}", e)
            false
        }
    }

    suspend fun checkDiscount(user: String, clientCode: String): Boolean {
        return try {
            val response: Response<Boolean> = apiService.checkDiscount(user, clientCode)
            if (response.isSuccessful) {
                response.body() ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Exception checkDiscount: ${e.message}", e)
            false
        }
    }
}
