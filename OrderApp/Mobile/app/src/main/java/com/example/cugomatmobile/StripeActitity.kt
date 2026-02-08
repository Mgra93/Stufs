package com.example.cugomatmobile

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cugomatmobile.service.ApiServiceImpl
import com.example.cugomatmobile.ui.theme.CugomatMobileTheme
import kotlinx.coroutines.launch

class StripeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CugomatMobileTheme {
                StripeScreen()
            }
        }
    }
}

@Composable
fun StripeScreen() {
    val api = ApiServiceImpl.getInstance()
    val scope = rememberCoroutineScope()

    var userData by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var checkoutUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Pregled narudžbe:", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = userData,
                onValueChange = { userData = it },
                label = { Text("Unesi podatke (ime/email)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Button(
                onClick = {
                    if (userData.isBlank()) {
                        status = "Unesi podatke prije plaćanja"
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        status = "Kreiram Stripe Checkout..."
                        val response = api.createStripeCheckoutSession(1.0)
                        if (response?.checkoutUrl.isNullOrEmpty()) {
                            status = "Neuspjeh kod kreiranja Stripe Checkout-a"
                        } else {
                            checkoutUrl = response?.checkoutUrl
                            status = "Otvorite Stripe u WebView-u"
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Obrada..." else "Plati")
            }

            Text(text = status, style = MaterialTheme.typography.bodyLarge)

            checkoutUrl?.let { url ->
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                    if (url != null && url.contains("https://example.com/success")) {
                                        scope.launch {
                                            status = "Plaćanje uspješno!"
                                            checkoutUrl = null
                                        }
                                        return true
                                    } else if (url != null && url.contains("https://example.com/cancel")) {
                                        scope.launch {
                                            status = "Plaćanje otkazano"
                                            checkoutUrl = null
                                        }
                                        return true
                                    }
                                    return false
                                }
                            }
                            loadUrl(url)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStripeScreen() {
    CugomatMobileTheme {
        StripeScreen()
    }
}
