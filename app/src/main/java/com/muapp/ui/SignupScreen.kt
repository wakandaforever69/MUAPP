package com.muapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.core.content.edit
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.edit
import com.muapp.android.ui.theme.*
import com.muapp.ui.PremiumTextField

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChanged: ((Boolean) -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, tint = Primary)
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordVisibilityChanged?.invoke(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        modifier = modifier,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            focusedBorderColor = Primary,
            unfocusedBorderColor = LightGray.copy(alpha = 0.3f),
            backgroundColor = Surface.copy(alpha = 0.7f),
            focusedLabelColor = Primary,
            unfocusedLabelColor = LightGray,
            cursorColor = Primary
        )
    )
}

@Composable
fun SignupScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Student") } // Default role
    var signupInProgress by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Background, Surface),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Join MU Campus Connect",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Username field
            PremiumTextField(
                value = username,
                onValueChange = { username = it },
                labelText = "Username",
                icon = Icons.Outlined.Person,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            PremiumTextField(
                value = password,
                onValueChange = { password = it },
                labelText = "Password",
                icon = Icons.Outlined.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChanged = { passwordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            PremiumTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                labelText = "Confirm Password",
                icon = Icons.Outlined.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChanged = { passwordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Selection
            Box(modifier = Modifier.fillMaxWidth()) {
                var expanded by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Select Role") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.School,
                            contentDescription = null,
                            tint = Primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Show options",
                                tint = Primary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { expanded = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = LightGray.copy(alpha = 0.3f),
                        backgroundColor = Surface.copy(alpha = 0.7f),
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = LightGray,
                        cursorColor = Primary
                    )
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Surface)
                ) {
                    listOf("Student", "Parent", "Faculty").forEach { role ->
                        DropdownMenuItem(
                            onClick = {
                                selectedRole = role
                                expanded = false
                            }
                        ) {
                            Text(role, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sign Up Button
            Button(
                onClick = {
                    if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    signupInProgress = true
                    saveUser(sharedPreferences, username, password, selectedRole.lowercase())
                    Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary),
                enabled = !signupInProgress && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (signupInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SIGN UP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Link
            Row(
                modifier = Modifier.clickable { navController.navigate("login") },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Text(
                    text = "Login",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Function to Save User Data Locally
fun saveUser(sharedPreferences: SharedPreferences, username: String, password: String, role: String) {
    sharedPreferences.edit(commit = true) {
        putString("${username}_password", password)
        putString("${username}_role", role)
    }
}



