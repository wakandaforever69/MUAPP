package com.muapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muapp.android.ui.theme.*

@Composable
fun ParentDetailsTab(state: StudentProfileState) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Father's Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            backgroundColor = Surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Father's Details",
                    style = MaterialTheme.typography.h6,
                    color = TextPrimary
                )

                // Father's First Name
                OutlinedTextField(
                    value = state.fatherDetails.firstName,
                    onValueChange = { state.fatherDetails.firstName = it },
                    label = { Text("First Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Father's Last Name
                OutlinedTextField(
                    value = state.fatherDetails.lastName,
                    onValueChange = { state.fatherDetails.lastName = it },
                    label = { Text("Last Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Father's Email
                OutlinedTextField(
                    value = state.fatherDetails.email,
                    onValueChange = { state.fatherDetails.email = it },
                    label = { Text("Email*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Father's Mobile Number
                OutlinedTextField(
                    value = state.fatherDetails.mobileNumber,
                    onValueChange = {
                        // Allow only digits and limit length to 10
                        if ((it.isEmpty() || it.all { char -> char.isDigit() }) && it.length <= 10) {
                            state.fatherDetails.mobileNumber = it
                        }
                    },
                    label = { Text("Mobile Number*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Father's Occupation
                OutlinedTextField(
                    value = state.fatherDetails.occupation,
                    onValueChange = { state.fatherDetails.occupation = it },
                    label = { Text("Occupation*") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Father's Annual Income
                OutlinedTextField(
                    value = state.fatherDetails.annualIncome,
                    onValueChange = {
                        // Allow only digits for annual income
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            state.fatherDetails.annualIncome = it
                        }
                    },
                    label = { Text("Annual Income (₹)*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )
            }
        }

        // Mother's Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            backgroundColor = Surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Mother's Details",
                    style = MaterialTheme.typography.h6,
                    color = TextPrimary
                )

                // Mother's First Name
                OutlinedTextField(
                    value = state.motherDetails.firstName,
                    onValueChange = { state.motherDetails.firstName = it },
                    label = { Text("First Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Mother's Last Name
                OutlinedTextField(
                    value = state.motherDetails.lastName,
                    onValueChange = { state.motherDetails.lastName = it },
                    label = { Text("Last Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Mother's Email
                OutlinedTextField(
                    value = state.motherDetails.email,
                    onValueChange = { state.motherDetails.email = it },
                    label = { Text("Email*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Mother's Mobile Number
                OutlinedTextField(
                    value = state.motherDetails.mobileNumber,
                    onValueChange = {
                        // Allow only digits and limit length to 10
                        if ((it.isEmpty() || it.all { char -> char.isDigit() }) && it.length <= 10) {
                            state.motherDetails.mobileNumber = it
                        }
                    },
                    label = { Text("Mobile Number*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Mother's Occupation
                OutlinedTextField(
                    value = state.motherDetails.occupation,
                    onValueChange = { state.motherDetails.occupation = it },
                    label = { Text("Occupation*") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Mother's Annual Income
                OutlinedTextField(
                    value = state.motherDetails.annualIncome,
                    onValueChange = {
                        // Allow only digits for annual income
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            state.motherDetails.annualIncome = it
                        }
                    },
                    label = { Text("Annual Income (₹)*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.White, // Change text color here
                        fontSize = 16.sp, // Optional: You can also change font size if needed
                        fontWeight = FontWeight.Normal // Optional: Customize font weight
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )
            }
        }

        // Required Fields Indicator
        Text(
            text = "* Required Fields",
            style = MaterialTheme.typography.caption,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Spacer at the bottom for better scrolling experience
        Spacer(modifier = Modifier.height(16.dp))
    }
}