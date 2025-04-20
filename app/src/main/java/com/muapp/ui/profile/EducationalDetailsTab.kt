package com.muapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.muapp.android.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EducationDetailsTab(
    state: StudentProfileState,
    isForTenth: Boolean = true
) {
    val scrollState = rememberScrollState()

    // Choose which education details to display based on isForTenth
    val educationDetails = if (isForTenth) state.tenthDetails else state.twelfthDetails
    val title = if (isForTenth) "10th Grade Details" else "12th Grade Details"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Education Details Section
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
                    text = title,
                    style = MaterialTheme.typography.h6,
                    color = TextPrimary
                )

                // Marks Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Obtained Marks
                    OutlinedTextField(
                        value = educationDetails.obtainedMarks,
                        onValueChange = {
                            // Allow only valid marks
                            if (it.isEmpty() || (it.all { char -> char.isDigit() || char == '.' } && it.toFloatOrNull() != null)) {
                                educationDetails.obtainedMarks = it
                                // Auto-calculate percentage when both fields have values
                                if (educationDetails.obtainedMarks.isNotEmpty() && educationDetails.totalMarks.isNotEmpty()) {
                                    val obtained = educationDetails.obtainedMarks.toFloatOrNull() ?: 0f
                                    val total = educationDetails.totalMarks.toFloatOrNull() ?: 1f
                                    if (total > 0) {
                                        val percentage = (obtained / total) * 100
                                        educationDetails.percentage = String.format("%.2f", percentage)
                                    }
                                }
                            }
                        },
                        label = { Text("Obtained Marks*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        )
                    )

                    // Total Marks
                    OutlinedTextField(
                        value = educationDetails.totalMarks,
                        onValueChange = {
                            // Allow only valid marks
                            if (it.isEmpty() || (it.all { char -> char.isDigit() || char == '.' } && it.toFloatOrNull() != null)) {
                                educationDetails.totalMarks = it
                                // Auto-calculate percentage when both fields have values
                                if (educationDetails.obtainedMarks.isNotEmpty() && educationDetails.totalMarks.isNotEmpty()) {
                                    val obtained = educationDetails.obtainedMarks.toFloatOrNull() ?: 0f
                                    val total = educationDetails.totalMarks.toFloatOrNull() ?: 1f
                                    if (total > 0) {
                                        val percentage = (obtained / total) * 100
                                        educationDetails.percentage = String.format("%.2f", percentage)
                                    }
                                }
                            }
                        },
                        label = { Text("Total Marks*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        )
                    )
                }

                // Percentage (auto-calculated)
                OutlinedTextField(
                    value = educationDetails.percentage,
                    onValueChange = { },
                    label = { Text("Percentage (%)") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary,
                        disabledTextColor = TextPrimary,
                        disabledBorderColor = TextSecondary
                    )
                )

                // Year of Passing
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                var yearExpanded by remember { mutableStateOf(false) }
                val years = (currentYear downTo currentYear - 50).map { it.toString() }

                ExposedDropdownMenuBox(
                    expanded = yearExpanded,
                    onExpandedChange = { yearExpanded = it }
                ) {
                    OutlinedTextField(
                        value = educationDetails.yearOfPassing,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Year of Passing*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = yearExpanded,
                        onDismissRequest = { yearExpanded = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                onClick = {
                                    educationDetails.yearOfPassing = year
                                    yearExpanded = false
                                }
                            ) {
                                Text(text = year)
                            }
                        }
                    }
                }

                // Board
                OutlinedTextField(
                    value = educationDetails.board,
                    onValueChange = { educationDetails.board = it },
                    label = { Text("Board/University*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // School Name
                OutlinedTextField(
                    value = educationDetails.schoolName,
                    onValueChange = { educationDetails.schoolName = it },
                    label = { Text("School/College Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Place
                OutlinedTextField(
                    value = educationDetails.place,
                    onValueChange = { educationDetails.place = it },
                    label = { Text("Place*") },
                    modifier = Modifier.fillMaxWidth(),
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