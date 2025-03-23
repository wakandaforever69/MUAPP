package com.muapp.ui

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.muapp.data.AssignmentRepository
import java.io.File
import com.muapp.common.BaseAssignment
import com.muapp.data.SharedAssignment
import java.time.LocalDateTime

// Assignment Data Model
data class StudentAssignment(
    override val id: String,
    override val title: String,
    override val subject: String,
    override val dueDate: LocalDate,
    val status: AssignmentStatus,
    val submittedFile: String? = null,
    val grade: String? = null,
    val feedback: String? = null
) : BaseAssignment

enum class SubmissionStatus {
    SUBMITTING,
    SUCCESS,
    ERROR
}

enum class AssignmentStatus { PENDING, COMPLETED, OVERDUE }
enum class SortOption { DUE_DATE, SUBJECT, STATUS, TITLE }
enum class UserRole { STUDENT, FACULTY}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentsScreen(role: UserRole = UserRole.STUDENT, userId: String = "current_user_id") {

    val context = LocalContext.current

    //to use downloadAssignmentFile
    val handleDownload : (String) -> Unit = {assignmentId ->
        val file = AssignmentRepository.getAssignmentFile(assignmentId)
        file?.let {
            downloadAssignmentFile(context, it.absolutePath, "Assignment_$assignmentId")
        }
    }

    val handleViewSubmission: (String, String) -> Unit = {assignmentId, studentId ->
        val file = AssignmentRepository.getSubmissionFile(assignmentId, studentId)
        file?.let {
            viewSubmittedFile(context, it.absolutePath)
        }
    }

    // Initialize Repository
    DisposableEffect(Unit) {
        AssignmentRepository.initialize(context)
        onDispose {  }
    }

    var assignments by remember { mutableStateOf<List<StudentAssignment>>(sampleAssignments) }
    var selectedFilters by remember { mutableStateOf<Set<AssignmentStatus>>(setOf()) }
    var sortBy by remember { mutableStateOf<SortOption>(SortOption.DUE_DATE) }
    var showUploadDialog by remember { mutableStateOf<Boolean>(false) }

    // Add this block to connect to repository (only if you want to use real data)
    // Collect assignments from repository
    val repoAssignments by AssignmentRepository.assignments.collectAsState()
    val activeAssignments = remember {  AssignmentRepository.getActiveAssignments() }
    val pastAssignments = remember {  AssignmentRepository.getPastAssignments() }

    val assignmentPairs = remember {
        if(role == UserRole.STUDENT) {
            AssignmentRepository.getAssignmentsForStudent(userId)
        } else {
            AssignmentRepository.assignments.value.map{Pair(it,false)}
        }
    }

    // This effect updates your UI when repository data changes
    LaunchedEffect(repoAssignments) {
        assignments = if (role == UserRole.STUDENT) {
            repoAssignments.map { sharedAssignment ->
                val submission = sharedAssignment.submissions[userId]
                val status = when {
                    submission != null -> AssignmentStatus.COMPLETED
                    sharedAssignment.isDeadlinePassed() -> AssignmentStatus.OVERDUE
                    else -> AssignmentStatus.PENDING
                }

                StudentAssignment(
                    id = sharedAssignment.id,
                    title = sharedAssignment.title,
                    subject = sharedAssignment.subject,
                    dueDate = sharedAssignment.deadline.toLocalDate(),
                    status = status,
                    submittedFile = submission?.fileName,
                    grade = submission?.grade,
                    feedback = submission?.feedback
                )
            }
        } else {
            // For faculty, map all assignments
            repoAssignments.flatMap { sharedAssignment ->
                if (sharedAssignment.submissions.isEmpty()) {
                    // Assignment with no submissions
                    listOf(
                        StudentAssignment(
                            id = sharedAssignment.id,
                            title = sharedAssignment.title,
                            subject = sharedAssignment.subject,
                            dueDate = sharedAssignment.deadline.toLocalDate(),
                            status = if (sharedAssignment.isDeadlinePassed())
                                AssignmentStatus.OVERDUE else AssignmentStatus.PENDING
                        )
                    )
                } else {
                    // Map each submission to a StudentAssignment
                    sharedAssignment.submissions.map { (studentId, submission) ->
                        StudentAssignment(
                            id = "${sharedAssignment.id}_$studentId",
                            title = "${sharedAssignment.title} (${submission.studentName})",
                            subject = sharedAssignment.subject,
                            dueDate = sharedAssignment.deadline.toLocalDate(),
                            status = AssignmentStatus.COMPLETED,
                            submittedFile = submission.fileName,
                            grade = submission.grade,
                            feedback = submission.feedback
                        )
                    }
                }
            }
        }
    }


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
                // Show active assignments first
                item {
                    Text("Active Assignments", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                items(activeAssignments) { assignment ->
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
                        },
                        onViewSubmission = if (role == UserRole.FACULTY) {
                            { assignmentId ->
                                // Extract studentId from the assignment ID (format: "${sharedAssignment.id}_$studentId")
                                val parts = assignmentId.split("_")
                                if (parts.size > 1) {
                                    handleViewSubmission(parts[0], parts[1])
                                }
                            }
                        } else {
                            { _ -> }
                        },
                        onDownload = if (role == UserRole.STUDENT) {
                            handleDownload
                        } else {
                            { _ -> }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }

                // Separator
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Past Assignments", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }

                // Show past assignments
                items(pastAssignments) { assignment ->
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
                        },
                        onViewSubmission = if (role == UserRole.FACULTY) {
                            { assignmentId ->
                                // Extract studentId from the assignment ID (format: "${sharedAssignment.id}_$studentId")
                                val parts = assignmentId.split("_")
                                if (parts.size > 1) {
                                    handleViewSubmission(parts[0], parts[1])
                                }
                            }
                        } else {
                            { _ -> }
                        },
                        onDownload = if (role == UserRole.STUDENT) {
                            handleDownload
                        } else {
                            { _ -> }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }

    if(showUploadDialog) {
        FileUploadDialog(
            onDismiss = { showUploadDialog = false },
            onFileSelected = { path ->
                // Get the assignment ID
                val pendingAssignment = assignments.find {it.status == AssignmentStatus.PENDING}
                pendingAssignment?.let {
                    // Call submitAssignment from repository
                    val success = AssignmentRepository.submitAssignment(
                        assignmentId = it.id,
                        studentId = userId,
                        studentName = "Student Name",
                        filePath = path,
                        fileName = path.substringAfterLast("/")
                    )

                    if (success) {
                        assignments = assignments.map {assignment ->
                            if(assignment.id == it.id) assignment.copy(
                                status = AssignmentStatus.COMPLETED,
                                submittedFile = path.substringAfterLast("/")
                            ) else assignment
                        }
                    }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FileUploadDialog(
    onDismiss: () -> Unit,
    onFileSelected: (String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var showFilePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    Button(onClick = { showFilePicker = true}) {
                        Text("Browse Files")
                    }
                }

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

    // Use FilePicker when needed
    if(showFilePicker) {
        FilePicker(
            onFileSelected = {file ->
                // Use uploadAssignmentFile
                val path = AssignmentRepository.uploadAssignmentFile(
                    context,
                    file.absolutePath,
                    fileName
                )
                path?.let {
                    fileName = file.name
                    onFileSelected(it)
                }
                showFilePicker = false
            },
            onDismiss = { showFilePicker = false }
        )
    }
}

@Composable
fun AssignmentCard(
    assignment: BaseAssignment,
    role: UserRole,
    onGradeSubmit: (String, String) -> Unit = { _, _ -> },
    onViewSubmission: (String) -> Unit = { },
    onDownload: (String) -> Unit = { } // Add this parameter
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
                StatusIndicator(
                    when (assignment) {
                        is StudentAssignment -> assignment.status
                        is SharedAssignment -> "Shared" // Adjust this if SharedAssignment has a status
                        else -> "Unknown"
                    } as AssignmentStatus
                )
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

            when (assignment) {
                is StudentAssignment -> {
                    if (assignment.submittedFile != null) {
                        InfoRow(
                            icon = Icons.Default.Attachment,
                            text = "Submitted: ${assignment.submittedFile}",
                            onClick = { onViewSubmission(assignment.id) }
                        )
                    }

                    if (role == UserRole.STUDENT) {
                        Button(
                            onClick = { onDownload(assignment.id) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = "Download")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download Assignment")
                            }
                        }
                    }

                    if (assignment.grade != null) {
                        InfoRow(Icons.Default.Grade, "Grade: ${assignment.grade}")
                    }

                    if (assignment.feedback != null) {
                        InfoRow(Icons.Default.Feedback, "Feedback: ${assignment.feedback}")
                    }
                }
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
            assignment = assignment as StudentAssignment,
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
fun InfoRow(icon: ImageVector, text: String, onClick: (() -> Unit) ? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
//@Composable
//fun GradeSubmissionDialog(
//    assignment: StudentAssignment,
//    onDismiss: () -> Unit,
//    onSubmit: (String, String) -> Unit
//) {
//    TODO("Not yet implemented")
//}

@Composable
fun GradeSubmissionDialog(
    assignment: StudentAssignment,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var grade by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Grade Submission - ${assignment.title}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grade Input
                OutlinedTextField(
                    value = grade,
                    onValueChange = { grade = it },
                    label = { Text("Grade") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Feedback Input
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Feedback (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onSubmit(grade, feedback)
                            onDismiss()
                        }
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

// Utility functions
private fun downloadAssignmentFile(context: Context, url: String, title: String) {
    // Implementation for downloading assignment files
    // This would typically use a DownloadManager or similar
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(url.toUri())
        .setTitle("Downloading $title")
        .setDescription("Downloading assignment file")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Assignment_$title.pdf")

    downloadManager.enqueue(request)
}

private fun viewSubmittedFile(context: Context, url: String) {
    // Implementation for viewing submitted files
    // This would typically open the file in the appropriate app
    val file = File(url)
    if (file.exists()) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        intent.setDataAndType(uri, context.contentResolver.getType(uri))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "View Submission"))
    }
}

// File picker composable
@Composable
    fun FilePicker(
    onFileSelected: (File) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Convert URI to File object
            val file = createTempFileFromUri(context, uri)
            file?.let { onFileSelected(it) }
        }
        onDismiss()
    }

    // Launch the file picker
    LaunchedEffect(true) {
        launcher.launch("*/*")
    }
}

// Helper function to convert URI to File
private fun createTempFileFromUri(context: Context, uri: Uri): File? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = getFileNameFromUri(context, uri) ?: "temp_file_${System.currentTimeMillis()}"
        val tempFile = File(context.cacheDir, fileName)

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    result = it.getString(nameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result
}

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

