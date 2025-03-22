package com.muapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.time.LocalDate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


// Assignment Data Model
data class StudentAssignment(
    val id: String,
    val title: String,
    val subject: String,
    val dueDate: LocalDate,
    val status: AssignmentStatus,
    val submittedFile: String? = null,
    val grade: String? = null,
    val feedback: String? = null
)

enum class AssignmentStatus { PENDING, COMPLETED, OVERDUE }
enum class SortOption { DUE_DATE, SUBJECT, STATUS, TITLE }
enum class UserRole { STUDENT, FACULTY, PARENT }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentsScreen(role: UserRole = UserRole.STUDENT) {
    var assignments by remember { mutableStateOf<List<StudentAssignment>>(sampleAssignments) }
    var selectedFilters by remember { mutableStateOf<Set<AssignmentStatus>>(setOf()) }
    var sortBy by remember { mutableStateOf<SortOption>(SortOption.DUE_DATE) }
    var showUploadDialog by remember { mutableStateOf<Boolean>(false) }


    Scaffold(
        topBar = { AppHeader(role) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showUploadDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Upload, contentDescription = "Upload Assignment")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SortingFilteringBar(
                selectedFilters = selectedFilters,
                onFilterSelected = { selectedFilters = it },
                sortBy = sortBy,
                onSortSelected = { sortBy = it }
            )

            LazyColumn {
                items(filterAndSortAssignments(assignments, selectedFilters, sortBy)) { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        role = role,
                        onGradeSubmit = { grade, feedback ->
                            assignments = assignments.map {
                                if (it.id == assignment.id) it.copy(
                                    grade = grade,
                                    feedback = feedback
                                ) else it
                            }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }

    if (showUploadDialog) {
        FileUploadDialog(
            onDismiss = { showUploadDialog = false },
            onFileSelected = { file ->
                assignments = assignments.map {
                    if (it.status == AssignmentStatus.PENDING) it.copy(submittedFile = file)
                    else it
                }
            }
        )
    }
}

@Composable
fun SortingFilteringBar(
    selectedFilters: Set<AssignmentStatus>,
    onFilterSelected: (Set<AssignmentStatus>) -> Unit,
    sortBy: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text("Filter Assignments", style = MaterialTheme.typography.titleMedium)

        Row {
            AssignmentStatus.entries.forEach { status ->
                FilterChip(
                    selected = status in selectedFilters,
                    onClick = {
                        val newFilters = if (status in selectedFilters) {
                            selectedFilters - status
                        } else {
                            selectedFilters + status
                        }
                        onFilterSelected(newFilters)
                    },
                    label = { Text(status.name) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Sort By", style = MaterialTheme.typography.titleMedium)
        Row {
            SortOption.entries.forEach { option ->
                FilterChip(
                    selected = option == sortBy,
                    onClick = { onSortSelected(option) },
                    label = { Text(option.name) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

fun filterAndSortAssignments(
    assignments: List<StudentAssignment>,
    selectedFilters: Set<AssignmentStatus>,
    sortBy: SortOption
): List<StudentAssignment> {
    return assignments
        .filter { it.status in selectedFilters || selectedFilters.isEmpty() }
        .sortedWith(
            when (sortBy) {
                SortOption.DUE_DATE -> compareBy { it.dueDate }
                SortOption.TITLE -> compareBy { it.title }
                SortOption.SUBJECT -> compareBy { it.subject }
                SortOption.STATUS -> compareBy { it.status }  // Uses enum ordinal comparison
            }
        )
}

@Composable
fun FileUploadDialog(
    onDismiss: () -> Unit,
    onFileSelected: (String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Upload Assignment", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        if (fileName.isNotEmpty()) {
                            onFileSelected(fileName)
                            onDismiss()
                        }
                    }) {
                        Text("Upload")
                    }
                }
            }
        }
    }
}

@Composable
fun AssignmentCard(
    assignment: StudentAssignment,
    role: UserRole,
    onGradeSubmit: (String, String) -> Unit = { _, _ -> }
) {
    var showGradeDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { if (role == UserRole.FACULTY) showGradeDialog = true },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusIndicator(assignment.status)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(Icons.Default.Book, assignment.subject)
            InfoRow(Icons.Default.Event, "Due: ${assignment.dueDate}")

            if (assignment.submittedFile != null) {
                InfoRow(Icons.Default.Attachment, "Submitted: ${assignment.submittedFile}")
            }

            if (assignment.grade != null) {
                InfoRow(Icons.Default.Grade, "Grade: ${assignment.grade}")
            }

            if (assignment.feedback != null) {
                InfoRow(Icons.Default.Feedback, "Feedback: ${assignment.feedback}")
            }


            if (role == UserRole.FACULTY) {
                Button(
                    onClick = { showGradeDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Grade")
                }
            }
        }
    }

    if (showGradeDialog) {
        GradeSubmissionDialog(
            assignment = assignment,
            onDismiss = { showGradeDialog = false },
            onSubmit = onGradeSubmit
        )
    }
}

@Composable
fun StatusIndicator(status: AssignmentStatus) {
    val color = when (status) {
        AssignmentStatus.PENDING -> Color.Yellow
        AssignmentStatus.COMPLETED -> Color.Green
        AssignmentStatus.OVERDUE -> Color.Red
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color, shape = RoundedCornerShape(50))
    )
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
@Composable
fun GradeSubmissionDialog(
    assignment: StudentAssignment,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    TODO("Not yet implemented")
}

//@Composable
//fun GradeSubmissionDialog(
//    assignment: Assignment,
//    onDismiss: () -> Unit,
//    onSubmit: (String, String) -> Unit
//) {
//    var grade by remember { mutableStateOf("") }
//    var feedback by remember { mutableStateOf("") }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            elevation = CardDefaults.cardElevation(8.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Grade Submission - ${assignment.title}",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Grade Input
//                OutlinedTextField(
//                    value = grade,
//                    onValueChange = { grade = it },
//                    label = { Text("Grade") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Feedback Input
//                OutlinedTextField(
//                    value = feedback,
//                    onValueChange = { feedback = it },
//                    label = { Text("Feedback (optional)") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Action Buttons
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    TextButton(onClick = onDismiss) {
//                        Text("Cancel")
//                    }
//                    Button(
//                        onClick = {
//                            onSubmit(grade, feedback)
//                            onDismiss()
//                        }
//                    ) {
//                        Text("Submit")
//                    }
//                }
//            }
//        }
//    }
//}

// Sample assignments data
@RequiresApi(Build.VERSION_CODES.O)
val sampleAssignments = listOf(
    // CSE Subjects
    StudentAssignment(
        id = "1",
        title = "Data Structures Assignment",
        subject = "Data Structures & Algorithms",
        dueDate = LocalDate.now().plusDays(3),
        status = AssignmentStatus.PENDING,

    ),
    StudentAssignment(
        id = "2",
        title = "Database Normalization Task",
        subject = "Database Management Systems",
        dueDate = LocalDate.now().plusDays(5),
        status = AssignmentStatus.COMPLETED,
        submittedFile = "normalization_report.pdf",
        grade = "A",
        feedback = "Great understanding of concepts!"
    ),
    StudentAssignment(
        id = "3",
        title = "Java Multithreading Exercise",
        subject = "Operating Systems",
        dueDate = LocalDate.now().minusDays(1),
        status = AssignmentStatus.OVERDUE
    ),
    StudentAssignment(
        id = "4",
        title = "Network Protocol Analysis",
        subject = "Computer Networks",
        dueDate = LocalDate.now().plusDays(4),
        status = AssignmentStatus.PENDING
    ),
    StudentAssignment(
        id = "5",
        title = "Cybersecurity Threat Report",
        subject = "Cybersecurity",
        dueDate = LocalDate.now().minusDays(2),
        status = AssignmentStatus.OVERDUE
    ),

    // Mathematics Subjects
    StudentAssignment(
        id = "6",
        title = "Linear Algebra Worksheet",
        subject = "Mathematics",
        dueDate = LocalDate.now().plusDays(2),
        status = AssignmentStatus.PENDING
    ),
    StudentAssignment(
        id = "7",
        title = "Calculus Differentiation Problems",
        subject = "Mathematics",
        dueDate = LocalDate.now().plusDays(1),
        status = AssignmentStatus.COMPLETED,
        submittedFile = "calculus_solutions.pdf",
        grade = "B+",
        feedback = "Good attempt, but review chain rule"
    )
)


@Composable
@OptIn(ExperimentalMaterial3Api::class)

fun AppHeader(role: UserRole) {
    TopAppBar(
        title = { Text("${role.name} Dashboard") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(onClick = { /* Handle notifications */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }
    )
}

