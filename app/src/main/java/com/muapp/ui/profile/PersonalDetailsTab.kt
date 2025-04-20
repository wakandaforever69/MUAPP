package com.muapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.muapp.android.ui.theme.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PersonalDetailsTab(state: StudentProfileState) {
    val scrollState = rememberScrollState()

    // Gender options
    val genderOptions = listOf("Male", "Female", "Other")

    // Blood group options
    val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // Category options
    val categoryOptions = listOf("General", "OBC", "SC", "ST", "Others")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.h6,
            color = TextPrimary
        )

        // Name fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { state.firstName = it },
                label = { Text("First Name*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )

            OutlinedTextField(
                value = state.lastName,
                onValueChange = { state.lastName = it },
                label = { Text("Last Name*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )
        }

        // Parent names
        OutlinedTextField(
            value = state.fatherName,
            onValueChange = { state.fatherName = it },
            label = { Text("Father's Name*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = TextSecondary
            )
        )

        OutlinedTextField(
            value = state.motherName,
            onValueChange = { state.motherName = it },
            label = { Text("Mother's Name*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = TextSecondary
            )
        )

        // Date of Birth and Parent Mobile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Date picker would be implemented here
            OutlinedTextField(
                value = state.dob,
                onValueChange = { state.dob = it },
                label = { Text("Date of Birth*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { /* Show date picker */ }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )

            OutlinedTextField(
                value = state.parentMobile,
                onValueChange = {
                    // Allow only digits and limit to 15 characters
                    if (it.all { char -> char.isDigit() } && it.length <= 15) {
                        state.parentMobile = it
                    }
                },
                label = { Text("Parent's Mobile*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )
        }

        // Gender and Blood Group
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Gender dropdown
            var genderExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = state.gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                state.gender = option
                                genderExpanded = false
                            }
                        ) {
                            Text(text = option)
                        }
                    }
                }
            }

            // Blood group dropdown
            var bloodGroupExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = bloodGroupExpanded,
                onExpandedChange = { bloodGroupExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = state.bloodGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Group*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupExpanded) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = bloodGroupExpanded,
                    onDismissRequest = { bloodGroupExpanded = false }
                ) {
                    bloodGroupOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                state.bloodGroup = option
                                bloodGroupExpanded = false
                            }
                        ) {
                            Text(text = option)
                        }
                    }
                }
            }
        }

        // Mother tongue and Religion
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.motherTongue,
                onValueChange = { state.motherTongue = it },
                label = { Text("Mother Tongue*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )

            OutlinedTextField(
                value = state.religion,
                onValueChange = { state.religion = it },
                label = { Text("Religion*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )
        }

        // National ID Card
        OutlinedTextField(
            value = state.nationalIdCard,
            onValueChange = {
                // For Aadhar, limit to 12 digits
                if (it.all { char -> char.isDigit() } && it.length <= 12) {
                    state.nationalIdCard = it
                }
            },
            label = { Text("Aadhar Card Number*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = TextSecondary
            )
        )

        // Caste and Sub-caste
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.caste,
                onValueChange = { state.caste = it },
                label = { Text("Caste*") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )

            OutlinedTextField(
                value = state.subCaste,
                onValueChange = { state.subCaste = it },
                label = { Text("Sub-Caste") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                )
            )
        }

        // Category dropdown
        var categoryExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = state.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category*") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            state.category = option
                            categoryExpanded = false
                        }
                    ) {
                        Text(text = option)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "* Required fields",
            color = TextSecondary,
            style = MaterialTheme.typography.caption
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}