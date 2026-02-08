package com.example.cugomatmobile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cugomatmobile.models.DataManager
import com.example.cugomatmobile.models.Order
import com.example.cugomatmobile.models.Product
import com.example.cugomatmobile.service.ApiServiceImpl
import com.example.cugomatmobile.ui.theme.CugomatMobileTheme
import java.time.format.DateTimeFormatter

class OrdersActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CugomatMobileTheme {
                OrdersScreen(
                    onCatalogClicked = { openCatalogActivity() },
                    onCartClicked = { openCartActivity() },
                    onProfileClicked = { openProfileActivity() }
                )
            }
        }
    }

    private fun openCatalogActivity() {
        val intent = Intent(this, CatalogActivity::class.java)
        startActivity(intent)
    }

    private fun openCartActivity() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun openProfileActivity() {
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onCatalogClicked: () -> Unit,
    onCartClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    suspend fun getOrders() {
        val currentUser = DataManager.user
        if (currentUser != null) {
            ApiServiceImpl.getInstance().getOrdersByUser(currentUser)?.let { fetchedOrders ->
                orders = fetchedOrders.sortedByDescending { it.createdOn }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            getOrders()
            kotlinx.coroutines.delay(2_000)
        }
    }

    Scaffold(
        topBar = {
            OrdersTopBar(
                onCatalogClicked = onCatalogClicked,
                onCartClicked = onCartClicked,
                onProfileClicked = onProfileClicked
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(orders, key = { it.id ?: 0 }) { order ->
                        OrderItem(order = order) {
                            selectedOrder = order
                            showDialog = true
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedOrder != null) {
        ProductsDialog(
            products = selectedOrder!!.products ?: emptyList(),
            onDismiss = { showDialog = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderItem(order: Order, onClick: () -> Unit) {
    val dateFormatDate = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val dateFormatTime = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = order.client?.name ?: "/",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${stringResource(R.string.order_lbl_status)} ${order.status?.let { stringResource(it.transl) } ?: "/"}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${stringResource(R.string.order_lbl_date)} ${order.createdOn?.let { it.format(dateFormatDate) } ?: "/"}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${stringResource(R.string.order_lbl_table)} ${order.tableCode ?: "/"}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${stringResource(R.string.order_lbl_time)} ${order.createdOn?.let { it.format(dateFormatTime) } ?: "/"}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${stringResource(R.string.order_lbl_price)} ${order.totalPrice?.toString() ?: "/"} ${stringResource(R.string.currency_symbol)}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${stringResource(R.string.order_lbl_discount)} " +
                            when (order.hasDiscount) {
                                true -> stringResource(R.string.order_lbl_yes)
                                false -> stringResource(R.string.order_lbl_no)
                                null -> "/"
                            },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                if (order.hasDiscount == true && order.finalPrice != null) {
                    Text(
                        text = "${stringResource(R.string.order_lbl_final_price)} ${order.finalPrice} ${stringResource(R.string.currency_symbol)}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun ProductsDialog(products: List<Product>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.order_lbl_product)) },
        text = {
            Column {
                products.forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name ?: "",
                            modifier = Modifier.weight(0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "X ${product.quantity ?: 0}",
                            modifier = Modifier.weight(0.2f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "${product.totalPrice ?: 0} ${stringResource(R.string.currency_symbol)}",
                            modifier = Modifier.weight(0.2f),
                            textAlign = TextAlign.End
                        )
                    }
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.order_modal_btn_close))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersTopBar(
    onCatalogClicked: () -> Unit,
    onCartClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.order_lbl_title),
                fontSize = 40.sp
            )
        },
        actions = {
            IconButton(onClick = { onCatalogClicked() }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Catalog",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = { onCartClicked() }) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Cart",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = { onProfileClicked() }) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    CugomatMobileTheme {
        OrdersScreen(
            onCatalogClicked = {},
            onCartClicked = {},
            onProfileClicked = {}
        )
    }
}
