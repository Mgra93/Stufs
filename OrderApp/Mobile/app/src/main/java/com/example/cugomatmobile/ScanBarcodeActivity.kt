package com.example.cugomatmobile

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cugomatmobile.models.BarcodeData
import com.example.cugomatmobile.models.Client
import com.example.cugomatmobile.models.DataManager
import com.example.cugomatmobile.service.ApiServiceImpl
import com.example.cugomatmobile.ui.theme.CugomatMobileTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.launch

class ScanBarcodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CugomatMobileTheme {
                ScanBarcodeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanBarcodeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val barcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        val scannedText = result?.contents
        if (scannedText != null) {
            try {
                val barcodeData: BarcodeData = jacksonObjectMapper().readValue(scannedText)
                scope.launch {
                    val client: Client? = ApiServiceImpl.getInstance().getClient(barcodeData.clientCode ?: "")
                    if (client != null) {
                        if (client.locationSecret == barcodeData.locationSecret) {
                            val intent = Intent(context, CatalogActivity::class.java)
                            intent.putExtra("clientCode", client.code)
                            DataManager.tableCode = barcodeData.tableCode
                            DataManager.clientCode  = client.code
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, context.getString(R.string.scan_barcode_client_not_found), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.scan_barcode_client_not_found), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, context.getString(R.string.scan_barcode_client_not_found), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.scan_scan_error), Toast.LENGTH_SHORT).show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            barcodeLauncher.launch(ScanOptions())
        } else {
            Toast.makeText(context, context.getString(R.string.scan_camera_not_approved), Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.scan_lbl_title),
                        fontSize = 28.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.scan_barcode_lbl_scan),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                            android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            val options = ScanOptions().apply {
                                setPrompt(context.getString(R.string.scan_barcode_prompt))
                                setBeepEnabled(true)
                                setOrientationLocked(true)
                            }
                            barcodeLauncher.launch(options)
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = stringResource(R.string.scan_barcode_btn_scan))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanBarcodeScreenPreview() {
    CugomatMobileTheme {
        ScanBarcodeScreen()
    }
}
