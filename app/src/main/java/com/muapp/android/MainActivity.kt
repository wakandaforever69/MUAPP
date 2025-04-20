package com.muapp.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.muapp.android.ui.theme.MUAPPTheme
import com.muapp.android.ui.theme.Primary
import com.muapp.ui.DashboardScreen
import com.muapp.ui.LoginScreen
import com.muapp.ui.SignupScreen
import com.muapp.ui.AttendanceScannerScreen
import com.muapp.ui.TimetableScreen
import androidx.compose.ui.platform.LocalContext
import com.muapp.ui.AssignmentsScreen
import com.muapp.ui.FacultyDashboard
import com.muapp.ui.PreviewFacultyDashboard
import com.muapp.ui.ResultsScreen
import com.muapp.ui.profile.ProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MUAPPTheme(darkTheme = true) {
                SetStatusBarColor(color = Primary)
                AppNavHost()
            }
        }
    }
}

@Composable
fun SetStatusBarColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignupScreen(navController)
        }
        composable(
            route = "dashboard/{userRole}",
            arguments = listOf(navArgument("userRole") { type = NavType.StringType })
        ) { backStackEntry ->
            val userRole = backStackEntry.arguments?.getString("userRole") ?: "guest"
            DashboardScreen(navController, userRole)
        }

        // Add routes for dashboard items
        composable("attendancescanner") {
            val navController = rememberNavController()
            AttendanceScannerScreen(navController = navController)
        }

        composable("viewtimetable") {
            TimetableScreen()
        }
        composable("manageattendance") {
            FeatureScreen("Manage Attendance", navController)
        }
        composable("uploadmaterials") {
            FeatureScreen("Upload Materials", navController)
        }
        composable("tracklocation") {
            FeatureScreen("Track Location", navController)
        }
        composable("viewreport") {
            FeatureScreen("View Report", navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("limitedaccess") {
            FeatureScreen("Limited Access", navController)
        }
        composable("results") {
            ResultsScreen()
        }
        composable("assignments") {
            AssignmentsScreen()
        }

        composable("assessments") {
            PreviewFacultyDashboard()
        }
    }
}

@Composable
fun FeatureScreen(title: String, navController: NavController) {
    // Placeholder for feature screens
    androidx.compose.material.Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = { androidx.compose.material.Text(title) },
                backgroundColor = Primary,
                contentColor = Color.White,
                navigationIcon = {
                    androidx.compose.material.IconButton(onClick = { navController.popBackStack() }) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            com.muapp.android.ui.theme.GradientStart,
                            com.muapp.android.ui.theme.GradientMiddle,
                            com.muapp.android.ui.theme.GradientEnd
                        )
                    )
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material.Text(
                text = "This is the $title screen\nFeature coming soon!",
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 18.sp
            )
        }
    }
}
