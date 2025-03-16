package com.muapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.muapp.android.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.ui.layout.ContentScale
import com.muapp.android.ui.theme.Primary
import androidx.compose.material.CircularProgressIndicator
import com.muapp.android.ui.theme.MUAPPTheme

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginInProgress by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 1.5f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("MU Campus Login", fontSize = 28.sp, color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter username", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    cursorColor = Primary,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    backgroundColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter password", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    cursorColor = Primary,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    backgroundColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    loginInProgress = true
                    val savedPassword = sharedPreferences.getString("${username}_password", null)
                    val userRole = sharedPreferences.getString("${username}_role", "guest")

                    if (savedPassword != null && savedPassword == password) {
                        Toast.makeText(context, "Logged in as $userRole", Toast.LENGTH_SHORT).show()
                        navController.navigate("dashboard/$userRole") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        loginInProgress = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary),
                enabled = !loginInProgress && username.isNotEmpty() && password.isNotEmpty()
            ) {
                if (loginInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Don't have an account? Sign Up",
                color = Color(0xFF3F9BF6),
                modifier = Modifier.clickable { navController.navigate("signup") }
            )
        }
    }
}