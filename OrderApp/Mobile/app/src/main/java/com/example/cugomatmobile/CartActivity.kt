package com.example.cugomatmobile

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cugomatmobile.dto.OrderCreateDTO
import com.example.cugomatmobile.dto.ProductDTO
import com.example.cugomatmobile.models.CartManager
import com.example.cugomatmobile.models.DataManager
import com.example.cugomatmobile.models.Product
import com.example.cugomatmobile.service.ApiServiceImpl
import com.example.cugomatmobile.ui.theme.CugomatMobileTheme
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CugomatMobileTheme {
                CartScreen(
                    onCatalogClicked = { startActivity(Intent(this, CatalogActivity::class.java)) },
                    onOrdersClicked = { startActivity(Intent(this, OrdersActivity::class.java)) },
                    onProfileClicked = {
                        ApiServiceImpl.getInstance().clearAccessData()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun CartScreen(
    onCatalogClicked: () -> Unit,
    onOrdersClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val api = ApiServiceImpl.getInstance()

    var cartItems by remember { mutableStateOf(CartManager.getItems()) }
    var totalPrice by remember { mutableStateOf(CartManager.getTotalPrice()) }

    var discountedPrice by remember { mutableStateOf<BigDecimal?>(null) }
    var showDiscountDialog by remember { mutableStateOf(false) }
    var checkoutUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    BackHandler(enabled = checkoutUrl != null) {}

    fun proceedToPayment(amount: BigDecimal) {
        scope.launch {
            isLoading = true
            try {
                val response = api.createStripeCheckoutSession(amount.toDouble())
                checkoutUrl = response?.checkoutUrl
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.cart_payment_error), Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            if (checkoutUrl == null) {
                CartTopBar(onCatalogClicked, onOrdersClicked, onProfileClicked)
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems, key = { it.id ?: it.hashCode() }) { product ->
                        CartItemCard(product) {
                            cartItems = CartManager.getItems()
                            totalPrice = CartManager.getTotalPrice()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${stringResource(R.string.cart_lbl_total)} $totalPrice €",
                    fontSize = 24.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (cartItems.isEmpty()) {
                            Toast.makeText(context, context.getString(R.string.cart_msg_empty), Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            try {
                                val totalQuantity = cartItems.sumOf { it.quantity ?: 1 }

                                if (totalQuantity > 1) {
                                    val user = DataManager.user ?: ""
                                    val clientCode = DataManager.clientCode ?: ""

                                    val hasDiscount = user.isNotEmpty() && clientCode.isNotEmpty() &&
                                            api.checkDiscount(user, clientCode)

                                    if (hasDiscount) {
                                        val cheapest = CartManager.getCheapestProduct()
                                        discountedPrice = totalPrice - (cheapest?.price ?: BigDecimal.ZERO)

                                        if (discountedPrice!! < totalPrice) {
                                            showDiscountDialog = true
                                        } else {
                                            proceedToPayment(totalPrice)
                                        }
                                    } else {
                                        proceedToPayment(totalPrice)
                                    }
                                } else {
                                    proceedToPayment(totalPrice)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, context.getString(R.string.cart_discount_check_error), Toast.LENGTH_LONG).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLoading) stringResource(R.string.cart_processing_msg) else stringResource(R.string.cart_pay_msg))
                }
            }

            if (showDiscountDialog && discountedPrice != null) {
                val cheapest = CartManager.getCheapestProduct()
                AlertDialog(
                    onDismissRequest = { showDiscountDialog = false },
                    title = { Text(stringResource(R.string.cart_discount_title)) },
                    text = {
                        Text(
                            buildString {
                                appendLine("${stringResource(R.string.cart_dialog_total_price)} $totalPrice €")
                                appendLine("${stringResource(R.string.cart_dialog_discount)}  ${cheapest?.price} €")
                                appendLine("${stringResource(R.string.cart_dialog_final_price)} $discountedPrice €")
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            proceedToPayment(discountedPrice!!)
                            showDiscountDialog = false
                        }) {
                            Text(stringResource(R.string.cart_dialog_btn_accept))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDiscountDialog = false }) {
                            Text(stringResource(R.string.cart_dialog_btn_cancel))
                        }
                    }
                )
            }

            checkoutUrl?.let { url ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {

                    AndroidView(
                        factory = {
                            WebView(it).apply {
                                settings.javaScriptEnabled = true
                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(view: WebView?, newUrl: String?): Boolean {
                                        when {
                                            newUrl?.contains("success") == true -> {
                                                scope.launch {
                                                    checkoutUrl = null
                                                    val hasDiscountApplied = discountedPrice != null && discountedPrice!! < totalPrice
                                                    val finalPriceValue = discountedPrice ?: totalPrice

                                                    val orderId = api.createOrder(
                                                        OrderCreateDTO(
                                                            user = DataManager.user ?: "",
                                                            clientCode = DataManager.clientCode ?: "",
                                                            tableCode = DataManager.tableCode ?: "",
                                                            productList = cartItems.map { p ->
                                                                ProductDTO(
                                                                    id = p.id,
                                                                    quantity = p.quantity ?: 1,
                                                                    name = p.name,
                                                                    price = p.price,
                                                                    category = null,
                                                                    sumPrice = null
                                                                )
                                                            },
                                                            totalPrice = totalPrice,
                                                            hasDiscount = hasDiscountApplied,
                                                            finalPrice = finalPriceValue
                                                        )
                                                    )

                                                    if (orderId != null) {
                                                        CartManager.clear()
                                                        cartItems = emptyList()
                                                        totalPrice = BigDecimal.ZERO
                                                        context.startActivity(Intent(context, OrdersActivity::class.java))
                                                    }
                                                }
                                                return true
                                            }
                                            newUrl?.contains("cancel") == true -> {
                                                checkoutUrl = null
                                                return true
                                            }
                                        }
                                        return false
                                    }
                                }
                                loadUrl(url)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    IconButton(
                        onClick = { checkoutUrl = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cart_stripe_close),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(
    onCatalogClicked: () -> Unit,
    onOrdersClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.cart_lbl_title), fontSize = 32.sp) },
        actions = {
            IconButton(onClick = onCatalogClicked) {
                Icon(Icons.Default.Menu, null)
            }
            IconButton(onClick = onOrdersClicked) {
                Icon(Icons.TwoTone.DateRange, null)
            }
            IconButton(onClick = onProfileClicked) {
                Icon(Icons.Default.AccountCircle, null)
            }
        }
    )
}

@Composable
fun CartItemCard(product: Product, onCartUpdated: () -> Unit) {
    val quantity = remember { mutableStateOf(product.quantity ?: 1) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${product.name} (${product.price} €)",
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(onClick = {
                if (quantity.value > 1) {
                    quantity.value--
                    CartManager.addProduct(product, -1)
                    onCartUpdated()
                }
            }) {
                Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Red)
            }

            Text(quantity.value.toString())

            IconButton(onClick = {
                quantity.value++
                CartManager.addProduct(product, 1)
                onCartUpdated()
            }) {
                Icon(Icons.Default.KeyboardArrowUp, null, tint = Color.Green)
            }

            IconButton(onClick = {
                CartManager.removeProduct(product.id!!)
                onCartUpdated()
            }) {
                Icon(Icons.Default.Delete, null, tint = Color.Red)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    CugomatMobileTheme {
        CartScreen({}, {}, {})
    }
}
