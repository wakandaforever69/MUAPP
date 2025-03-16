package com.muapp.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.muapp.android.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AttendanceRecord(
    val date: LocalDate,
    val subject: String,
    val status: AttendanceStatus,
    val percentage: Float
)

data class ScanHistoryEntry(
    val message: String,
    val timestamp: LocalDateTime,
    val isSuccess: Boolean
)

enum class AttendanceStatus {
    PRESENT, ABSENT, LEAVE
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AttendanceScannerScreen(navController: NavController) {
    var scanResult by remember { mutableStateOf("") }
    var scanHistory by remember { mutableStateOf(listOf<ScanHistoryEntry>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Gradient colors
    val gradientColors = listOf(
        Color(0xFF121212),
        Color(0xFF1E1E30),
        Color(0xFF252540)
    )

    // State variables for QR scanner and math verification
    var showQrScanner by remember { mutableStateOf(false) }
    var showMathVerification by remember { mutableStateOf(false) }
    var scannedCode by remember { mutableStateOf("") }

    // Math verification variables
    var firstNumber by remember { mutableStateOf(0) }
    var secondNumber by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }

    // Snackbar state
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // Lazy list state for auto-scrolling
    val lazyListState = rememberLazyListState()

    // Attendance tracking logic
    val attendanceRecords = remember { mutableStateOf(generateSampleAttendance()) }
    val overallAttendance = remember {
        mutableStateOf(calculateOverallAttendance(attendanceRecords.value))
    }

    // QR Scanner Dialog
    if (showQrScanner) {
        Dialog(onDismissRequest = { showQrScanner = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "QR Scanner",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // This is where you would integrate your actual QR scanner library
                    // For demonstration, we'll simulate a scan with a button

                    Text(
                        "Position the QR code within the frame",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Camera Preview")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulate scanning
                    Button(
                        onClick = {
                            // Simulate a QR code scan
                            val mockScannedData = "Attendance_MU12345_${System.currentTimeMillis()}"
                            if (mockScannedData.startsWith("Attendance_MU")) {
                                scannedCode = mockScannedData
                                showQrScanner = false

                                // Generate random math problem
                                firstNumber = Random.nextInt(1, 20)
                                secondNumber = Random.nextInt(1, 20)
                                showMathVerification = true
                            } else {
                                errorMessage = "Invalid QR Code. Please try again."
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        "Invalid QR Code. Please try again.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Primary)
                    ) {
                        Text("Simulate Scan", color = Color.White)
                    }

                    Button(
                        onClick = { showQrScanner = false },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        }
    }

    // Math Verification Dialog
    if (showMathVerification) {
        Dialog(onDismissRequest = { showMathVerification = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Verify Your Attendance",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        "Please solve this math problem to mark your attendance:",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        "$firstNumber + $secondNumber = ?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    OutlinedTextField(
                        value = userAnswer,
                        onValueChange = { userAnswer = it },
                        label = { Text("Your Answer") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                showMathVerification = false
                                userAnswer = ""
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                        ) {
                            Text("Cancel", color = Color.White)
                        }

                        Button(
                            onClick = {
                                try {
                                    val answer = userAnswer.toInt()
                                    if (answer == firstNumber + secondNumber) {
                                        // Mark attendance as successful
                                        val successMessage = "Scan Successful: ${scannedCode.split("_")[1]}"
                                        scanResult = successMessage

                                        // Add to scan history with timestamp
                                        val newEntry = ScanHistoryEntry(
                                            message = successMessage,
                                            timestamp = LocalDateTime.now(),
                                            isSuccess = true
                                        )
                                        scanHistory = scanHistory + newEntry

                                        errorMessage = null
                                        showMathVerification = false
                                        userAnswer = ""

                                        // Show success snackbar
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "Attendance marked successfully!",
                                                duration = SnackbarDuration.Short
                                            )
                                            // Auto-scroll to the latest item
                                            if (scanHistory.isNotEmpty()) {
                                                lazyListState.animateScrollToItem(scanHistory.size - 1)
                                            }
                                        }
                                    } else {
                                        // Add to scan history with timestamp
                                        val failureMessage = "Math verification failed"
                                        val newEntry = ScanHistoryEntry(
                                            message = failureMessage,
                                            timestamp = LocalDateTime.now(),
                                            isSuccess = false
                                        )
                                        scanHistory = scanHistory + newEntry

                                        errorMessage = "Incorrect answer. Please try again."
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "Incorrect answer. Please try again.",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                } catch (e: NumberFormatException) {
                                    errorMessage = "Please enter a valid number."
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Please enter a valid number.",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Primary)
                        ) {
                            Text("Submit", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance Scanner",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick =  {
                        Log.d("Navigation","Back button Clicked")
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = Color(0xFF1A1A2E),
                elevation = 8.dp
            )
        },
        snackbarHost = { hostState ->
            SnackbarHost(hostState = hostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    backgroundColor = Color(0xFF323232),
                    contentColor = Color.White,
                    action = {
                        TextButton(onClick = { data.dismiss() }) {
                            Text("Dismiss", color = Primary)
                        }
                    }
                ) {
                    Text(data.message)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Overall Attendance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp)),
                    backgroundColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Overall Attendance", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = overallAttendance.value / 100f,
                                modifier = Modifier.size(120.dp),
                                color = when {
                                    overallAttendance.value >= 75f -> Green
                                    overallAttendance.value >= 65f -> Amber
                                    else -> Red
                                },
                                strokeWidth = 12.dp
                            )

                            Text(
                                text = "${overallAttendance.value.toInt()}%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val statusText = when {
                            overallAttendance.value >= 75f -> "Good Standing"
                            overallAttendance.value >= 65f -> "Warning"
                            else -> "Low Attendance"
                        }

                        Text(
                            text = statusText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                overallAttendance.value >= 75f -> Green
                                overallAttendance.value >= 65f -> Amber
                                else -> Red
                            }
                        )
                    }
                }

                // QR Scanner Button
                Button(
                    onClick = { showQrScanner = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan QR Code", color = Color.White, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (scanResult.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        backgroundColor = Color(0xFF292929),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Green,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(scanResult, color = Color.White)
                        }
                    }
                }

                errorMessage?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        backgroundColor = Color(0xFF292929),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Red,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(it, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scan History Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scan History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (scanHistory.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                scanHistory = listOf()
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        "Scan history cleared",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear history",
                                tint = Color.White
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 16.dp),
                    backgroundColor = Color(0xFF1A1A2E),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp
                ) {
                    if (scanHistory.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No scan history yet",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            state = lazyListState
                        ) {
                            items(scanHistory) { historyItem ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    backgroundColor = Color(0xFF292929),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (historyItem.isSuccess)
                                                Icons.Default.CheckCircle else Icons.Default.Error,
                                            contentDescription = null,
                                            tint = if (historyItem.isSuccess) Green else Red,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = historyItem.message,
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = historyItem.timestamp.format(
                                                    DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")
                                                ),
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Attendance History
                Text(
                    text = "Attendance History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    backgroundColor = Color(0xFF1A1A2E),
                    shape = RoundedCornerShape(18.dp),
                    elevation = 4.dp
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        items(attendanceRecords.value) { record ->
                            AttendanceRecordItem(record)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AttendanceRecordItem(record: AttendanceRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        backgroundColor = Color.White,
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Icon(
                imageVector = when (record.status) {
                    AttendanceStatus.PRESENT -> Icons.Default.CheckCircle
                    AttendanceStatus.ABSENT -> Icons.Default.Cancel
                    AttendanceStatus.LEAVE -> Icons.Default.HourglassEmpty
                },
                contentDescription = null,
                tint = when (record.status) {
                    AttendanceStatus.PRESENT -> Green
                    AttendanceStatus.ABSENT -> Red
                    AttendanceStatus.LEAVE -> Amber
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(32
                .dp))

            Column {
                Text(record.subject, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Date: ${record.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}")
                Text("Status: ${record.status.name.lowercase().capitalize()}")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
// Helper Functions
private fun generateSampleAttendance(): List<AttendanceRecord> {
    val today = LocalDate.now()
    return listOf(
        AttendanceRecord(today.minusDays(0), "Database Systems", AttendanceStatus.PRESENT, 0.85f),
        AttendanceRecord(today.minusDays(1), "Software Engineering", AttendanceStatus.ABSENT, 0.70f),
        AttendanceRecord(today.minusDays(2), "Artificial Intelligence", AttendanceStatus.LEAVE, 0.78f)
    )
}

private fun calculateOverallAttendance(records: List<AttendanceRecord>): Float {
    val presentCount = records.count { it.status == AttendanceStatus.PRESENT }
    return (presentCount.toFloat() / records.size) * 100
}