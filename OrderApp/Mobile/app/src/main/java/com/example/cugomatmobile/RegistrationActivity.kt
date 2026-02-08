package com.example.cugomatmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.cugomatmobile.dto.UserDTO
import com.example.cugomatmobile.helpers.InputHelper
import com.example.cugomatmobile.service.ApiServiceImpl
import com.example.cugomatmobile.ui.theme.CugomatMobileTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CugomatMobileTheme {
                RegisterScreen()
            }
        }
    }
}

@Composable
fun RegisterScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fillAllFieldsMessage = stringResource(R.string.registration_fill_all_fields)

    fun validateInputs(): Boolean {
        usernameError = username.isBlank()
        passwordError = password.isBlank()
        confirmPasswordError = confirmPassword.isBlank() || password != confirmPassword
        firstNameError = firstName.isBlank()
        lastNameError = lastName.isBlank()
        emailError = email.isBlank() || !InputHelper.isValidEmail(email)
        phoneError = !InputHelper.isValidPhone(phone)

        return !(usernameError ||
                passwordError ||
                confirmPasswordError ||
                firstNameError ||
                lastNameError ||
                emailError ||
                phoneError)
    }

    fun handleRegister(
        username: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        context: Context,
        scope: CoroutineScope,
        onValidationError: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        if (!validateInputs()) {
            val errorMessage = when {
                password != confirmPassword ->
                    context.getString(R.string.registration_error_password_mismatch)
                emailError && !InputHelper.isValidEmail(email) ->
                    context.getString(R.string.registration_error_email_format)
                phoneError && !InputHelper.isValidPhone(phone) ->
                    context.getString(R.string.registration_error_phone_format)
                usernameError || passwordError || firstNameError ||
                        lastNameError || emailError || confirmPassword.isBlank() || phoneError ->
                    context.getString(R.string.registration_fill_all_fields)
                else ->
                    context.getString(R.string.registration_error)
            }

            onValidationError(errorMessage)
            return
        }

        isLoading = true

        scope.launch {
            try {
                val apiService = ApiServiceImpl.getInstance()
                val userExists = apiService.checkUserExist(username)
                if (userExists) {
                    isLoading = false
                    onValidationError(context.getString(R.string.registration_error_username_exists))
                    return@launch
                }

                val userDTO = UserDTO(
                    username = username,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phone = phone
                )

                val newUserId: Int? = apiService.registerUser(userDTO)
                isLoading = false

                if (newUserId != null) {
                    onSuccess(context.getString(R.string.registration_success))
                    context.startActivity(Intent(context, LoginActivity::class.java))
                } else {
                    onValidationError(context.getString(R.string.registration_error))
                }
            } catch (e: Exception) {
                isLoading = false
                onValidationError("${context.getString(R.string.registration_error)} ${e.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.registration_lbl_title),
                fontSize = 40.sp,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.registration_lbl_username)) },
                isError = usernameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (usernameError) Text(
                text = stringResource(R.string.registration_error_username),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.registration_lbl_password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                isError = passwordError,
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError) Text(
                text = stringResource(R.string.registration_error_password),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.registration_lbl_confirm_password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                isError = confirmPasswordError,
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmPasswordError) Text(
                text = stringResource(R.string.registration_error_confirm_password),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text(stringResource(R.string.registration_lbl_first_name)) },
                isError = firstNameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (firstNameError) Text(
                text = stringResource(R.string.registration_error_first_name),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text(stringResource(R.string.registration_lbl_last_name)) },
                isError = lastNameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (lastNameError) Text(
                text = stringResource(R.string.registration_error_last_name),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.registration_lbl_email)) },
                isError = emailError,
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError) Text(
                text = stringResource(R.string.registration_error_email),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.registration_lbl_phone)) },
                isError = phoneError,
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError) Text(
                text = stringResource(R.string.registration_error_phone),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(stringResource(R.string.registration_btn_back), fontSize = 18.sp, color = MaterialTheme.colorScheme.onSecondary)
                }

                Button(
                    onClick = {
                        handleRegister(
                            username, password, confirmPassword, firstName, lastName, email, phone,
                            context, scope,
                            onValidationError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            onSuccess = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(stringResource(R.string.login_btn_register), fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    CugomatMobileTheme {
        RegisterScreen()
    }
}
