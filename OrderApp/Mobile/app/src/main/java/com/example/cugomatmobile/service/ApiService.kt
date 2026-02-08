package com.example.cugomatmobile.service

import com.example.cugomatmobile.dto.AuthRequestDTO
import com.example.cugomatmobile.dto.CategoryDTO
import com.example.cugomatmobile.dto.ClientDTO
import com.example.cugomatmobile.dto.JwtResponseDTO
import com.example.cugomatmobile.dto.OrderCreateDTO
import com.example.cugomatmobile.dto.OrderDTO
import com.example.cugomatmobile.dto.ProductDTO
import com.example.cugomatmobile.dto.RefreshTokenRequestDTO
import com.example.cugomatmobile.dto.StripeCheckoutResponseDTO
import com.example.cugomatmobile.dto.UserDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    //LOGIN
    @POST("/api/auth/login")
    suspend fun login(@Body authRequestDTO: AuthRequestDTO): Response<JwtResponseDTO>

    @GET("/api/auth/ping")
    suspend fun ping(): Response<ResponseBody>
    @POST("/api/auth/refreshToken")
    suspend fun refreshToken(@Body refreshTokenRequestDTO: RefreshTokenRequestDTO): Response<JwtResponseDTO>

    //ORDER
    @POST("/api/order/create")
    suspend fun createOrder(@Body createDTO: OrderCreateDTO): Response<Integer>

    @GET("/api/order/byUser")
    suspend fun getOrdersByUser(@Query("userName") userName: String): List<OrderDTO>

    @GET("/api/order/checkDiscount")
    suspend fun checkDiscount(@Query("user") user: String, @Query("clientCode") clientCode: String): Response<Boolean>

    //CATEGORY
    @GET("/api/category/list")
    suspend fun getCategoryList(@Query("clientCode") clientCode: String): List<CategoryDTO>

    //PRODUCT
    @GET("/api/product/list")
    suspend fun getProductList(@Query("clientCode") clientCode: String, @Query("categoryId") categoryId: Int?): List<ProductDTO>

    //CLIENT
    @GET("/api/client")
    suspend fun getClient(@Query("code") clientCode: String): ClientDTO

    //USER
    @POST("/api/user/register")
    suspend fun register(@Body userDTO: UserDTO): Response<Int>
    @GET("/api/user/checkExist")
    suspend fun checkUserExist(@Query("username") username: String): Response<Boolean>

    @POST("/api/stripe/payment")
    suspend fun createStripeCheckoutSession(@Query("amount") amount: Double): Response<StripeCheckoutResponseDTO>
}