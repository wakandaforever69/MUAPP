package com.muapp.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.muapp.android.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(navController: NavController, userRole: String?) {
    // Ensure role is correctly passed
    val role = userRole ?: "guest"
    Log.d("Dashboard", "Loaded role: $role") // Debugging log

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("MU Campus Connect", color = TextPrimary) },
                backgroundColor = Surface,
                contentColor = TextPrimary,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextPrimary)
                    }
                }
            )
        },
        drawerContent = {
            DrawerHeader(role)
            DrawerBody(
                navController = navController,
                role = role,
                closeDrawer = { scope.launch { scaffoldState.drawerState.close() } }
            )
        },
        backgroundColor = Background,
        bottomBar = {
            BottomNavigation(
                backgroundColor = Surface,
                contentColor = TextPrimary
            ) {
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { /* Already on home */ }

                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alerts") },
                    selected = false,
                    onClick = { /* Navigate to alerts */ }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /* Navigate to settings */ }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientMiddle, GradientEnd),
                        startY = 0f,
                        endY = 2000f
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // User welcome
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    backgroundColor = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome to your Dashboard",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Role: ${role.capitalize()}",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dashboard content based on role
                when (role) {
                    "student" -> StudentDashboard(navController)
                    "parent" -> ParentDashboard(navController)
                    "faculty" -> FacultyDashboard(navController)
                    else -> GuestDashboard(navController)
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(role: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Surface,CardBackground)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = TextPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = role.capitalize(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "ID: MU${(10000..99999).random()}",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Text (
                text = role.capitalize(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun DrawerBody(
    navController: NavController,
    role: String,
    closeDrawer: () -> Unit
) {
    val items = when (role) {
        "student" -> listOf(
            MenuItem("Dashboard", Icons.Default.Dashboard),
            MenuItem("Attendance", Icons.Default.CheckCircle),
            MenuItem("Timetable", Icons.Default.Schedule),
            MenuItem("Assignments", Icons.AutoMirrored.Filled.Assignment),
            MenuItem("Results", Icons.Default.Score),
            MenuItem("Library", Icons.Default.Book)
        )
        "faculty" -> listOf(
            MenuItem("Dashboard", Icons.Default.Dashboard),
            MenuItem("Manage Classes", Icons.Default.Group),
            MenuItem("Attendance", Icons.Default.PersonSearch),
            MenuItem("Upload Materials", Icons.Default.Upload),
            MenuItem("Assessments", Icons.Default.Assessment)
        )
        "parent" -> listOf(
            MenuItem("Dashboard", Icons.Default.Dashboard),
            MenuItem("Track Location", Icons.Default.LocationOn),
            MenuItem("View Reports", Icons.Default.Assessment),
            MenuItem("Pay Fees", Icons.Default.Payment),
            MenuItem("Contact Faculty", Icons.Default.ContactMail)
        )
        else -> listOf(
            MenuItem("Dashboard", Icons.Default.Dashboard),
            MenuItem("About", Icons.Default.Info),
            MenuItem("Contact", Icons.Default.Email)
        )
    }

    // Add common items
    val allItems = items + listOf(
        MenuItem("Settings", Icons.Default.Settings),
        MenuItem("Help", Icons.AutoMirrored.Filled.Help),
        MenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        allItems.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (item.title == "Logout") {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            // Handle navigation to other screens
                            closeDrawer()
                        }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (item.title == "Logout") Error else Primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    color = if (item.title == "Logout") Error else Color.Black
                )
            }
            if (item.title != "Logout") {
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

data class MenuItem(val title: String, val icon: ImageVector)

@Composable
fun StudentDashboard(navController: NavController) {
    DashboardContent(
        navController = navController,
        items = listOf(
            DashboardItem("Attendance Scanner", Icons.Default.QrCode, "Scan QR to mark attendance"),
            DashboardItem("View Timetable", Icons.Default.CalendarToday, "Check your class schedule"),
            DashboardItem("Results", Icons.Default.Score, "View your academic performance"),
            DashboardItem("Assignments", Icons.AutoMirrored.Filled.Assignment, "View and submit assignments"),
            DashboardItem("Profile", Icons.Default.AccountCircle, "View and edit your profile")
        )
    )
}

@Composable
fun FacultyDashboard(navController: NavController) {
    DashboardContent(
        navController = navController,
        items = listOf(
            DashboardItem("Manage Attendance", Icons.Default.QrCode, "Generate QR codes for attendance"),
            DashboardItem("Class Schedule", Icons.Default.CalendarToday, "View your teaching schedule"),
            DashboardItem("Upload Materials", Icons.Default.FileUpload, "Share resources with students"),
            DashboardItem("Assessments", Icons.Default.Assessment, "Create and grade assessments"),
            DashboardItem("Profile", Icons.Default.AccountCircle, "View and edit your profile")
        )
    )
}

@Composable
fun ParentDashboard(navController: NavController) {
    DashboardContent(
        navController = navController,
        items = listOf(
            DashboardItem("Track Location", Icons.Default.LocationOn, "Check your child's campus location"),
            DashboardItem("View Report", Icons.Default.Assessment, "Check academic performance"),
            DashboardItem("Attendance Log", Icons.Default.CheckCircle, "Monitor attendance records"),
            DashboardItem("Pay Fees", Icons.Default.Payment, "Make fee payments online"),
            DashboardItem("Profile", Icons.Default.AccountCircle, "View and edit your profile")
        )
    )
}

@Composable
fun GuestDashboard(navController: NavController) {
    DashboardContent(
        navController = navController,
        items = listOf(
            DashboardItem("Limited Access", Icons.Default.Block, "Please login for full access"),
            DashboardItem("About MU", Icons.Default.Info, "Learn about our institution"),
            DashboardItem("Contact Us", Icons.Default.Email, "Get in touch with us")
        )
    )
}

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val description: String
)

@Composable
fun DashboardContent(
    navController: NavController,
    items: List<DashboardItem>
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            PremiumDashboardCard(
                item = item,
                onClick = {
                    navController.navigate(item.title.replace(" ", "").lowercase())
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PremiumDashboardCard(
    item: DashboardItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CardBackground,
        elevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient background
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Primary, Accent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Accent
            )
        }
    }
}

// Extension function to capitalize first letter
fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}
