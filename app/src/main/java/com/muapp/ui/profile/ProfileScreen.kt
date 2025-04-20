package com.muapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.muapp.android.ui.theme.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // Create student profile state
    val studentProfileState = rememberStudentProfileState()

    // Tab state
    val pagerState = rememberPagerState(initialPage = 0)
    val tabTitles = listOf("Personal", "Contact", "Parent Details", "Tenth", "Twelfth")

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Student Profile", color = TextPrimary) },
                backgroundColor = Surface,
                contentColor = TextPrimary,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (validateAllTabs(studentProfileState)) {
                            saveProfile(studentProfileState)
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("Profile saved successfully!")
                            }
                        } else {
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("Please fill all required fields")
                            }
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = Primary)
                    }
                }
            )
        },
        backgroundColor = Background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = Surface,
                contentColor = Primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Primary,
                        height = 3.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Pager content
            HorizontalPager(
                count = tabTitles.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> PersonalDetailsTab(studentProfileState)
                    1 -> ContactDetailsTab(studentProfileState)
                    2 -> ParentDetailsTab(studentProfileState)
                    3 -> EducationDetailsTab(studentProfileState)
                    4 -> EducationDetailsTab(studentProfileState, isForTenth = false)
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    enabled = pagerState.currentPage > 0,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < tabTitles.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                // On last page - validate and save
                                if (validateAllTabs(studentProfileState)) {
                                    saveProfile(studentProfileState)
                                    scaffoldState.snackbarHostState.showSnackbar("Profile saved successfully!")
                                } else {
                                    scaffoldState.snackbarHostState.showSnackbar("Please fill all required fields")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (pagerState.currentPage == tabTitles.size - 1) "Save" else "Next")
                }
            }
        }
    }
}

fun validateAllTabs(state: StudentProfileState): Boolean {
    // Personal tab validation
    if (state.firstName.isEmpty() || state.lastName.isEmpty() ||
        state.fatherName.isEmpty() || state.motherName.isEmpty() ||
        state.dob.isEmpty() || state.parentMobile.isEmpty() ||
        state.gender.isEmpty() || state.bloodGroup.isEmpty() ||
        state.motherTongue.isEmpty() || state.religion.isEmpty() ||
        state.nationalIdCard.isEmpty() || state.caste.isEmpty() ||
        state.category.isEmpty()) {
        return false
    }

    // Contact tab validation
    if (state.currentAddress.addressLine1.isEmpty() ||
        state.currentAddress.country.isEmpty() ||
        state.currentAddress.state.isEmpty() ||
        state.currentAddress.city.isEmpty() ||
        state.currentAddress.pinCode.isEmpty()) {
        return false
    }

    // Check permanent address validation only if not same as current
    if (!state.sameAsCurrentAddress) {
        if (state.permanentAddress.addressLine1.isEmpty() ||
            state.permanentAddress.country.isEmpty() ||
            state.permanentAddress.state.isEmpty() ||
            state.permanentAddress.city.isEmpty() ||
            state.permanentAddress.pinCode.isEmpty()) {
            return false
        }
    }

    // Parent details validation
    if (state.fatherDetails.firstName.isEmpty() ||
        state.fatherDetails.lastName.isEmpty() ||
        state.fatherDetails.email.isEmpty() ||
        state.fatherDetails.mobileNumber.isEmpty() ||
        state.fatherDetails.occupation.isEmpty() ||
        state.fatherDetails.annualIncome.isEmpty()) {
        return false
    }

    if (state.motherDetails.firstName.isEmpty() ||
        state.motherDetails.lastName.isEmpty() ||
        state.motherDetails.email.isEmpty() ||
        state.motherDetails.mobileNumber.isEmpty() ||
        state.motherDetails.occupation.isEmpty() ||
        state.motherDetails.annualIncome.isEmpty()) {
        return false
    }

    // Tenth education validation
    if (state.tenthDetails.obtainedMarks.isEmpty() ||
        state.tenthDetails.totalMarks.isEmpty() ||
        state.tenthDetails.yearOfPassing.isEmpty() ||
        state.tenthDetails.board.isEmpty() ||
        state.tenthDetails.schoolName.isEmpty() ||
        state.tenthDetails.place.isEmpty()) {
        return false
    }

    // Twelfth education validation
    if (state.twelfthDetails.obtainedMarks.isEmpty() ||
        state.twelfthDetails.totalMarks.isEmpty() ||
        state.twelfthDetails.yearOfPassing.isEmpty() ||
        state.twelfthDetails.board.isEmpty() ||
        state.twelfthDetails.schoolName.isEmpty() ||
        state.twelfthDetails.place.isEmpty()) {
        return false
    }

    return true
}

fun saveProfile(state: StudentProfileState) {
    // This function would save the profile data to your backend or local storage
    Log.d("ProfileScreen", "Saving profile data for: ${state.firstName} ${state.lastName}")
    // Implement your save logic here
}

