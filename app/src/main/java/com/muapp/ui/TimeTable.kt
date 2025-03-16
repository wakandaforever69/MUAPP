package com.muapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

// Data models
data class Subject(
    val id: String,
    val name: String,
    val instructor: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: DayOfWeek,
    val color: Color
)

// Format time helper function
@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(time: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return time.format(formatter)
}

// Get next upcoming class helper function
@RequiresApi(Build.VERSION_CODES.O)
fun getNextUpcomingClass(subjects: List<Subject>): Subject? {
    val now = LocalTime.now()
    val today = LocalDate.now().dayOfWeek

    // First check classes today that haven't started yet
    val todayClasses = subjects
        .filter { it.dayOfWeek == today && it.startTime.isAfter(now) }
        .sortedBy { it.startTime }

    if (todayClasses.isNotEmpty()) {
        return todayClasses.first()
    }

    // If no classes today, find the next day with classes
    val daysOfWeek = DayOfWeek.values()
    val currentDayIndex = today.ordinal

    for (i in 1..7) {
        val nextDayIndex = (currentDayIndex + i) % 7
        val nextDay = daysOfWeek[nextDayIndex]

        val nextDayClasses = subjects
            .filter { it.dayOfWeek == nextDay }
            .sortedBy { it.startTime }

        if (nextDayClasses.isNotEmpty()) {
            return nextDayClasses.first()
        }
    }

    return null
}

// Mock data for testing
@RequiresApi(Build.VERSION_CODES.O)
val mockSubjects = listOf(
    Subject("CS101", "Introduction to Computer Science", "Dr. Rajesh Tavva",
        LocalTime.of(9, 0), LocalTime.of(10, 30), DayOfWeek.MONDAY, Color(0xFF5D8CAE)),
    Subject("MATH202", "Calculus II", "Dr. Mahipal Jetta",
        LocalTime.of(11, 0), LocalTime.of(12, 30), DayOfWeek.MONDAY, Color(0xFF7A9A7E)),
    Subject("PHYS101", "Physics I", "Dr. Murtaza Bohra",
        LocalTime.of(14, 0), LocalTime.of(15, 30), DayOfWeek.MONDAY, Color(0xFFB87D4B)),
    Subject("ENG205", "Technical Writing", "Prof. Raju",
        LocalTime.of(9, 0), LocalTime.of(10, 30), DayOfWeek.TUESDAY, Color(0xFF9D7AB8)),
    Subject("CS205", "Data Structures", "Dr. Om Prakash",
        LocalTime.of(11, 0), LocalTime.of(12, 30), DayOfWeek.TUESDAY, Color(0xFFB85A5A)),
    Subject("MATH303", "Linear Algebra", "Dr. Mahipal Jetta",
        LocalTime.of(14, 0), LocalTime.of(15, 30), DayOfWeek.WEDNESDAY, Color(0xFF5D8CAE)),
    Subject("CS301", "Database Systems", "Dr. Neha Bharill",
        LocalTime.of(9, 0), LocalTime.of(10, 30), DayOfWeek.THURSDAY, Color(0xFF7A9A7E)),
    Subject("PHYS202", "Physics II", "Dr. Murtaza Bohra",
        LocalTime.of(11, 0), LocalTime.of(12, 30), DayOfWeek.THURSDAY, Color(0xFFB87D4B)),
    Subject("CS401", "Software Engineering", "Dr. Vijay Rao Duddu",
        LocalTime.of(14, 0), LocalTime.of(15, 30), DayOfWeek.FRIDAY, Color(0xFF9D7AB8))
)

// Main Timetable Screen
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen() {
    val coroutineScope = rememberCoroutineScope()

    // State variables
    var selectedDay by remember { mutableStateOf(LocalDate.now().dayOfWeek) }
    var showFilters by remember { mutableStateOf(false) }
    var filterInstructor by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("Time") } // Options: "Time", "Subject"
    var showReminders by remember { mutableStateOf(false) }
    var showUpcomingClasses by remember { mutableStateOf(true) }

    // Theme colors
    val isDarkTheme = true
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF212121)
    val accentColor = Color(0xFF3D84C6)

    // Filter the subjects based on selected day and filters
    val filteredSubjects = mockSubjects.filter {
        it.dayOfWeek == selectedDay &&
                (filterInstructor.isEmpty() || it.instructor.contains(filterInstructor, ignoreCase = true))
    }

    // Sort the subjects
    val sortedSubjects = when (sortBy) {
        "Subject" -> filteredSubjects.sortedBy { it.name }
        else -> filteredSubjects.sortedBy { it.startTime }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timetable", color = textColor) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceColor
                ),
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = textColor)
                    }
                    IconButton(onClick = { showReminders = !showReminders }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Reminders", tint = textColor)
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                // Day navigation
                DayNavigationBar(selectedDay, onDaySelected = { selectedDay = it })

                // Filters and sort options
                AnimatedVisibility(visible = showFilters) {
                    FiltersSection(
                        filterInstructor = filterInstructor,
                        onFilterInstructorChange = { filterInstructor = it },
                        sortBy = sortBy,
                        onSortByChange = { sortBy = it },
                        textColor = textColor,
                        surfaceColor = surfaceColor
                    )
                }

                // Upcoming classes notification
                if (showUpcomingClasses) {
                    UpcomingClassesAlert(
                        subjects = mockSubjects,
                        onDismiss = { showUpcomingClasses = false },
                        accentColor = accentColor,
                        textColor = textColor
                    )
                }

                // Reminder dialog
                if (showReminders) {
                    ReminderDialog(
                        subjects = sortedSubjects,
                        onDismiss = { showReminders = false },
                        accentColor = accentColor,
                        textColor = textColor,
                        backgroundColor = surfaceColor
                    )
                }

                // Timetable content
                if (sortedSubjects.isEmpty()) {
                    EmptyTimetable(textColor)
                } else {
                    SubjectList(
                        subjects = sortedSubjects,
                        textColor = textColor,
                        surfaceColor = surfaceColor
                    )
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayNavigationBar(selectedDay: DayOfWeek, onDaySelected: (DayOfWeek) -> Unit) {
    val days = DayOfWeek.values().toList()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                val isSelected = day == selectedDay
                val today = LocalDate.now().dayOfWeek == day

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isSelected -> Color(0xFF3D84C6)
                                today -> Color(0xFF3D84C6).copy(alpha = 0.3f)
                                else -> Color.Transparent
                            }
                        )
                        .clickable { onDaySelected(day) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        color = if (isSelected) Color.White else Color.LightGray,
                        fontWeight = if (isSelected || today) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        Divider(color = Color.DarkGray.copy(alpha = 0.3f))
    }
}

@Composable
fun FiltersSection(
    filterInstructor: String,
    onFilterInstructorChange: (String) -> Unit,
    sortBy: String,
    onSortByChange: (String) -> Unit,
    textColor: Color,
    surfaceColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor.copy(alpha = 0.5f))
            .padding(8.dp)
    ) {
        Text("Filters & Sorting", color = textColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Instructor filter
        OutlinedTextField(
            value = filterInstructor,
            onValueChange = onFilterInstructorChange,
            label = { Text("Filter by Instructor") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedBorderColor = Color(0xFF3D84C6),
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sort options
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sort by:", color = textColor)
            Spacer(modifier = Modifier.width(8.dp))
            Row {
                RadioButton(
                    selected = sortBy == "Time",
                    onClick = { onSortByChange("Time") },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF3D84C6),
                        unselectedColor = Color.Gray
                    )
                )
                Text("Time", color = textColor, modifier = Modifier.clickable { onSortByChange("Time") })

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = sortBy == "Subject",
                    onClick = { onSortByChange("Subject") },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF3D84C6),
                        unselectedColor = Color.Gray
                    )
                )
                Text("Subject", color = textColor, modifier = Modifier.clickable { onSortByChange("Subject") })
            }
        }
    }
}

@Composable
fun SubjectList(
    subjects: List<Subject>,
    textColor: Color,
    surfaceColor: Color
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subjects) { subject ->
            SubjectCard(subject = subject, textColor = textColor, surfaceColor = surfaceColor)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubjectCard(subject: Subject, textColor: Color, surfaceColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Colored indicator
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(subject.color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Subject details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subject.instructor,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = "Time",
                        tint = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${formatTime(subject.startTime)} - ${formatTime(subject.endTime)}",
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyTimetable(textColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Event,
                contentDescription = "No Classes",
                tint = textColor.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No classes scheduled for this day",
                color = textColor.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingClassesAlert(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    accentColor: Color,
    textColor: Color
) {
    val nextClass = getNextUpcomingClass(subjects)

    if (nextClass != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = accentColor.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = "Upcoming Class",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "Upcoming Class",
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = nextClass.name,
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${nextClass.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${formatTime(nextClass.startTime)} - ${formatTime(nextClass.endTime)}",
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    accentColor: Color,
    textColor: Color,
    backgroundColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Set Reminders", color = textColor)
        },
        text = {
            Column {
                Text(
                    "Choose which classes you want to be reminded about.",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (subjects.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .height(300.dp)
                    ) {
                        LazyColumn {
                            items(subjects) { subject ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(subject.name, color = textColor)
                                        Text(
                                            "${subject.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${formatTime(subject.startTime)}",
                                            color = textColor.copy(alpha = 0.7f),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Remind", color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Checkbox(
                                            checked = false,  // This would be connected to state in a real app
                                            onCheckedChange = { /* Set reminder logic */ },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = accentColor,
                                                uncheckedColor = textColor.copy(alpha = 0.5f)
                                            )
                                        )
                                    }
                                }

                                if (subject != subjects.last()) {
                                    Divider(color = textColor.copy(alpha = 0.1f))
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No classes available for this day.",
                            color = textColor.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor
                )
            ) {
                Text("Save Reminders")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        },
        containerColor = backgroundColor
    )
}
