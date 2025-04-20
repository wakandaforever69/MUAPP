package com.muapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.muapp.android.ui.theme.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactDetailsTab(state: StudentProfileState) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Address Section
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
                    text = "Current Address",
                    style = MaterialTheme.typography.h6,
                    color = TextPrimary
                )

                // Address Line 1 & 2
                OutlinedTextField(
                    value = state.currentAddress.addressLine1,
                    onValueChange = { state.currentAddress.addressLine1 = it },
                    label = { Text("Address Line 1*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                OutlinedTextField(
                    value = state.currentAddress.addressLine2,
                    onValueChange = { state.currentAddress.addressLine2 = it },
                    label = { Text("Address Line 2") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )

                // Country dropdown
                var countryExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = countryExpanded,
                    onExpandedChange = { countryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.currentAddress.country,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Country*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = countryExpanded,
                        onDismissRequest = { countryExpanded = false }
                    ) {
                        LocationData.countries.forEach { country ->
                            DropdownMenuItem(
                                onClick = {
                                    state.currentAddress.country = country
                                    // Reset state and city when country changes
                                    state.currentAddress.state = ""
                                    state.currentAddress.city = ""
                                    countryExpanded = false
                                }
                            ) {
                                Text(text = country)
                            }
                        }
                    }
                }

                // State dropdown (dependent on selected country)
                var stateExpanded by remember { mutableStateOf(false) }
                val availableStates = LocationData.statesByCountry[state.currentAddress.country] ?: emptyList()

                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = {
                        // Only allow expanding if country is selected
                        if (state.currentAddress.country.isNotEmpty()) {
                            stateExpanded = it
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = state.currentAddress.state,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State/Province*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        availableStates.forEach { state_ ->
                            DropdownMenuItem(
                                onClick = {
                                    state.currentAddress.state = state_
                                    // Reset city when state changes
                                    state.currentAddress.city = ""
                                    stateExpanded = false
                                }
                            ) {
                                Text(text = state_)
                            }
                        }
                    }
                }

                // City dropdown (dependent on selected state)
                var cityExpanded by remember { mutableStateOf(false) }
                val availableCities = LocationData.citiesByState["${state.currentAddress.country}-${state.currentAddress.state}"] ?: emptyList()

                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = {
                        // Only allow expanding if state is selected
                        if (state.currentAddress.state.isNotEmpty()) {
                            cityExpanded = it
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = state.currentAddress.city,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("City*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false }
                    ) {
                        availableCities.forEach { city ->
                            DropdownMenuItem(
                                onClick = {
                                    state.currentAddress.city = city
                                    cityExpanded = false
                                }
                            ) {
                                Text(text = city)
                            }
                        }
                    }
                }

                // Pin/Postal Code
                OutlinedTextField(
                    value = state.currentAddress.pinCode,
                    onValueChange = {
                        // Only allow numeric input for pin code
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            state.currentAddress.pinCode = it
                        }
                    },
                    label = { Text("Pin/Postal Code*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextSecondary
                    )
                )
            }
        }

        // Permanent Address Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            backgroundColor = Surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Permanent Address",
                        style = MaterialTheme.typography.h6,
                        color = TextPrimary
                    )

                    // Same as current address checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.sameAsCurrentAddress,
                            onCheckedChange = { checked ->
                                state.sameAsCurrentAddress = checked

                                // Copy current address to permanent address if checked
                                if (checked) {
                                    state.permanentAddress = state.currentAddress.copy()
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Primary
                            )
                        )

                        Text(
                            text = "Same as Current Address",
                            color = TextPrimary
                        )
                    }
                }

                // If not same as current, show permanent address fields
                if (!state.sameAsCurrentAddress) {
                    // Address Line 1 & 2
                    OutlinedTextField(
                        value = state.permanentAddress.addressLine1,
                        onValueChange = { state.permanentAddress.addressLine1 = it },
                        label = { Text("Address Line 1*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        )
                    )

                    OutlinedTextField(
                        value = state.permanentAddress.addressLine2,
                        onValueChange = { state.permanentAddress.addressLine2 = it },
                        label = { Text("Address Line 2") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        )
                    )

                    // Country dropdown for permanent address
                    var countryExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = countryExpanded,
                        onExpandedChange = { countryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.permanentAddress.country,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Country*") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false }
                        ) {
                            LocationData.countries.forEach { country ->
                                DropdownMenuItem(
                                    onClick = {
                                        state.permanentAddress.country = country
                                        // Reset state and city when country changes
                                        state.permanentAddress.state = ""
                                        state.permanentAddress.city = ""
                                        countryExpanded = false
                                    }
                                ) {
                                    Text(text = country)
                                }
                            }
                        }
                    }

                    // State dropdown for permanent address
                    var stateExpanded by remember { mutableStateOf(false) }
                    val availableStates = LocationData.statesByCountry[state.permanentAddress.country] ?: emptyList()

                    ExposedDropdownMenuBox(
                        expanded = stateExpanded,
                        onExpandedChange = {
                            if (state.permanentAddress.country.isNotEmpty()) {
                                stateExpanded = it
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = state.permanentAddress.state,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("State/Province*") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = stateExpanded,
                            onDismissRequest = { stateExpanded = false }
                        ) {
                            availableStates.forEach { state_ ->
                                DropdownMenuItem(
                                    onClick = {
                                        state.permanentAddress.state = state_
                                        // Reset city when state changes
                                        state.permanentAddress.city = ""
                                        stateExpanded = false
                                    }
                                ) {
                                    Text(text = state_)
                                }
                            }
                        }
                    }

                    // City dropdown for permanent address
                    var cityExpanded by remember { mutableStateOf(false) }
                    val availableCities = LocationData.citiesByState["${state.permanentAddress.country}-${state.permanentAddress.state}"] ?: emptyList()

                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = {
                            if (state.permanentAddress.state.isNotEmpty()) {
                                cityExpanded = it
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = state.permanentAddress.city,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("City*") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false }
                        ) {
                            availableCities.forEach { city ->
                                DropdownMenuItem(
                                    onClick = {
                                        state.permanentAddress.city = city
                                        cityExpanded = false
                                    }
                                ) {
                                    Text(text = city)
                                }
                            }
                        }
                    }

                    // Pin/Postal Code for permanent address
                    OutlinedTextField(
                        value = state.permanentAddress.pinCode,
                        onValueChange = {
                            // Only allow numeric input for pin code
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                state.permanentAddress.pinCode = it
                            }
                        },
                        label = { Text("Pin/Postal Code*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
                        )
                    )
                } else {
                    // If same as current address, show the copied information in a non-editable format
                    Text(
                        text = "Using current address information",
                        style = MaterialTheme.typography.body2,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Spacer at the bottom for better scrolling experience
        Spacer(modifier = Modifier.height(16.dp))
    }
}