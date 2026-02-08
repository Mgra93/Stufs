package com.example.cugomatmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cugomatmobile.dto.AuthRequestDTO
import com.example.cugomatmobile.enums.Language
import com.example.cugomatmobile.models.AccessData
import com.example.cugomatmobile.models.DataManager
import com.example.cugomatmobile.service.ApiServiceImpl
import com.example.cugomatmobile.ui.theme.CugomatMobileTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CugomatMobileTheme {
                LoginScreen(
                    selectedLanguage = Language.CROATIAN,
                    onLanguageSelected = { lang ->
                        // Ovdje definiraš što se događa kad korisnik odabere jezik
                        // npr. update locale
                        updateLocale(lang.code)
                        Toast.makeText(this, "Odabrani jezik: ${lang.displayName}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var serverAvailable by remember { mutableStateOf(true) }
    var selectedLang by remember { mutableStateOf(selectedLanguage) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Ping server
    LaunchedEffect(Unit) {
        try {
            serverAvailable = ApiServiceImpl.getInstance().ping()
            if (!serverAvailable) {
                Toast.makeText(
                    context,
                    context.getString(R.string.login_server_unavailable),
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            serverAvailable = false
            Toast.makeText(
                context,
                context.getString(R.string.login_server_unavailable),
                Toast.LENGTH_LONG
            ).show()
        }
    }

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
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.login_app_name),
                fontSize = 64.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.login_lbl_user_name)) },
                isError = usernameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (usernameError) {
                Text(
                    stringResource(R.string.login_req_username),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.login_lbl_user_password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError,
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError) {
                Text(
                    stringResource(R.string.login_req_password),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { handleLogin(username, password, context, scope) },
                    modifier = Modifier.weight(1f),
                    enabled = serverAvailable,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        stringResource(R.string.login_btn_login),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Button(
                    onClick = {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
                    },
                    modifier = Modifier.weight(1f),
                    enabled = serverAvailable,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        stringResource(R.string.login_btn_register),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown za jezik
            LanguageDropdown(selectedLanguage = selectedLang) { lang ->
                selectedLang = lang
                onLanguageSelected(lang)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdown(selectedLanguage: Language, onLanguageSelected: (Language) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(200.dp)
    ) {
        TextField(
            value = selectedLanguage.displayName,
            onValueChange = {},
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            label = { Text(stringResource(R.string.login_language)) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Language.values().forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang.displayName, fontSize = 12.sp) },
                    onClick = {
                        onLanguageSelected(lang)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun handleLogin(username: String, password: String, context: Context, scope: CoroutineScope) {
    if (username.isBlank() || password.isBlank()) return

    scope.launch {
        val authRequestDTO = AuthRequestDTO(username, password)
        val jwtResponse = ApiServiceImpl.getInstance().login(authRequestDTO)
        if (jwtResponse != null && jwtResponse.accessToken != null) {
            val accessData = AccessData(jwtResponse.accessToken ?: "", jwtResponse.refreshToken ?: "")
            ApiServiceImpl.getInstance().setAccessData(accessData)
            DataManager.user = username
            context.startActivity(Intent(context, ScanBarcodeActivity::class.java))
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.login_msg_invalid_data),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CugomatMobileTheme {
        LoginScreen(selectedLanguage = Language.CROATIAN, onLanguageSelected = {})
    }
}
