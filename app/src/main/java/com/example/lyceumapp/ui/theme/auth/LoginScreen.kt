package com.example.lyceumapp.ui.theme.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lyceumapp.R
import com.example.lyceumapp.ui.theme.network.ApiClient

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var isLoading by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    val handleLogin = {
        emailError = !email.contains("@1511.ru")
        passwordError = password.isBlank()

        if (!emailError && !passwordError) {
            isLoading = true
            authError = null

            ApiClient.login(
                username = email,
                password = password,
                onSuccess = {
                    isLoading = false
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onError = { error ->
                    isLoading = false
                    authError = error
                }
            )
        }
    }

    val errorColor by animateColorAsState(
        targetValue = if (emailError || passwordError) Color.Red else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 130.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Лого",
            modifier = Modifier
                .size(150.dp)
                .padding(vertical = 30.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Добро Пожаловать",
            modifier = Modifier.padding(start = 30.dp),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Войдите чтобы продолжить",
            modifier = Modifier.padding(start = 30.dp),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(80.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Email address") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = com.example.lyceumapp.ui.theme.TextFieldTextDark,
                unfocusedTextColor = com.example.lyceumapp.ui.theme.TextFieldDisabledTextDark,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesterEmail)
                .padding(horizontal = 30.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusRequesterPassword.requestFocus() }
            )
        )

        AnimatedVisibility(visible = emailError) {
            Text(
                text = "Вы указали неверный email",
                color = errorColor,
                modifier = Modifier.padding(start = 30.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesterPassword)
                .padding(horizontal = 30.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = com.example.lyceumapp.ui.theme.TextFieldTextDark,
                unfocusedTextColor = com.example.lyceumapp.ui.theme.TextFieldDisabledTextDark,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    handleLogin()
                }
            ),
            trailingIcon = { IconButton(onClick =  {passwordVisible = !passwordVisible})  {
                Icon(if (passwordVisible) Icons.Default.Check else Icons.Default.Clear, null)
            }},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        AnimatedVisibility(visible = passwordError) {
            Text(
                text = "Введите пароль",
                color = errorColor,
                modifier = Modifier.padding(start = 30.dp, top = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("(Используйте данные для входа на 1511.ru)", color = Color.Gray, fontSize = 10.sp)

        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            onClick = handleLogin,
            enabled = !isLoading
        ) {
            Text("Войти")
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        authError?.let { error ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 100.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center

            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Text("@sawe", color = Color.LightGray)
        }
    }
}