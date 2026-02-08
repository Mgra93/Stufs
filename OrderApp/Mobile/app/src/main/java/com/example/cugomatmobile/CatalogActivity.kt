package com.example.cugomatmobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cugomatmobile.models.CartManager
import com.example.cugomatmobile.models.Category
import com.example.cugomatmobile.models.DataManager
import com.example.cugomatmobile.models.Product
import com.example.cugomatmobile.service.ApiServiceImpl
import com.example.cugomatmobile.ui.theme.CugomatMobileTheme
import kotlinx.coroutines.launch

class CatalogActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CugomatMobileTheme {
                CatalogScreen(
                    onCartClicked = {
                        startActivity(Intent(this, CartActivity::class.java))
                    },
                    onOrdersClicked = {
                        startActivity(Intent(this, OrdersActivity::class.java))
                    },
                    onProfileClicked = {
                        openProfileActivity()
                    }
                )
            }
        }
    }

    private fun openProfileActivity() {
        ApiServiceImpl.getInstance().clearAccessData()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun CatalogScreen(
    onCartClicked: () -> Unit,
    onOrdersClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    val context = LocalContext.current
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showQuantityDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf(1) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch {
            try {
                val clientCode = DataManager.clientCode

                if (!clientCode.isNullOrEmpty()) {
                    val apiService = ApiServiceImpl.getInstance()

                    categories = apiService.getCategoryList(clientCode) ?: emptyList()
                    selectedCategory = categories.firstOrNull()

                    selectedCategory?.let { category ->
                        products = apiService.getProductList(clientCode, category.id) ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                categories = emptyList()
                products = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CatalogTopBar(
                    onCartClicked = onCartClicked,
                    onOrdersClicked = onOrdersClicked,
                    onProfileClicked = onProfileClicked
                )
            }
        ) { innerPadding ->
            CatalogContent(
                modifier = Modifier.padding(innerPadding),
                categories = categories,
                selectedCategory = selectedCategory,
                products = products,
                onCategorySelected = { category ->
                    selectedCategory = category

                    val clientCode = DataManager.clientCode
                    if (!clientCode.isNullOrEmpty()) {
                        scope.launch {
                            products = ApiServiceImpl.getInstance()
                                .getProductList(clientCode, category.id) ?: emptyList()
                        }
                    }
                },
                onProductClick = { product ->
                    selectedProduct = product
                    showQuantityDialog = true
                    quantity = 1
                }
            )
        }

        if (showQuantityDialog && selectedProduct != null) {
            QuantitySelectionDialog(
                product = selectedProduct!!,
                quantity = quantity,
                onQuantityChanged = { newQuantity -> quantity = newQuantity },
                onDismiss = { showQuantityDialog = false },
                onAddToCart = {
                    CartManager.addProduct(selectedProduct!!, quantity)
                    showQuantityDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopBar(
    onCartClicked: () -> Unit,
    onOrdersClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.catalog_lbl_title),
                fontSize = 40.sp
            )
        },
        actions = {
            IconButton(onClick = onCartClicked) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Cart",
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onOrdersClicked) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Orders",
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onProfileClicked) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile / Logout",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    )
}

@Composable
fun CatalogContent(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    selectedCategory: Category?,
    products: List<Product>,
    onCategorySelected: (Category) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Column(modifier = modifier) {
        HorizontalCategoryList(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedCategory != null) {
            ProductList(products = products, onProductClick = onProductClick)
        } else {
            Text(text = stringResource(R.string.catalog_lbl_no_active_category))
        }
    }
}

@Composable
fun HorizontalCategoryList(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .wrapContentWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            category.name?.let {
                Text(
                    text = it,
                    color = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ProductList(products: List<Product>, onProductClick: (Product) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product, onClick = { onProductClick(product) })
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            product.name?.let {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = "${product.price} ${stringResource(id = R.string.currency_symbol)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun QuantitySelectionDialog(
    product: Product,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { product.name?.let { Text(it) } },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.catalog_lbl_quantity),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Decrease Quantity",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Text(text = "$quantity", style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { onQuantityChanged(quantity + 1) }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Increase Quantity",
                                tint = Color.Green,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val totalPrice = product.price?.multiply(quantity.toBigDecimal())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(R.string.catalog_lbl_product_total) +
                                " $totalPrice " +
                                stringResource(R.string.currency_symbol),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAddToCart) {
                Text(stringResource(R.string.catalog_btn_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.catalog_btn_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CatalogPreview() {
    CugomatMobileTheme {
        CatalogScreen(
            onCartClicked = {},
            onOrdersClicked = {},
            onProfileClicked = {}
        )
    }
}
