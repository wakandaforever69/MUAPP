package com.muapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.muapp.data.AssignmentRepository
import com.muapp.data.SharedAssignment
import com.muapp.data.SubmissionData
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId

// Updated ViewModel to use SharedAssignment and Repository
@RequiresApi(Build.VERSION_CODES.O)
class FacultyViewModel : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val repository = AssignmentRepository

    // Store currently logged in faculty ID (would come from auth system)
    val facultyId = "faculty-123"
    val facultyName = "Professor Smith"

    @RequiresApi(Build.VERSION_CODES.O)
    val assignments = repository.assignments

    // State for tracking submissions to review
    private val _submissionsToReview = MutableStateFlow<List<Pair<SharedAssignment, SubmissionData>>>(emptyList())
    val submissionsToReview: StateFlow<List<Pair<SharedAssignment, SubmissionData>>> = _submissionsToReview.asStateFlow()

    init {
        viewModelScope.launch {
            try {
            // Update submissions to review whenever assignments change
            repository.assignments.collect { allAssignments ->
                val pendingReviews = mutableListOf<Pair<SharedAssignment, SubmissionData>>()

                allAssignments.forEach { assignment ->
                    assignment.submissions.values.forEach { submission ->
                        if (submission.grade == null) {
                            pendingReviews.add(Pair(assignment, submission))
                        }
                    }
                }

                _submissionsToReview.value = pendingReviews
            }
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAssignment(
        title: String,
        subject: String,
        description: String,
        deadline: LocalDateTime,
        resourceLink: String? = null,
        attachedFile: String? = null
    ) {
        val newAssignment = SharedAssignment(
            id = UUID.randomUUID().toString(),
            title = title,
            subject = subject,
            description = description,
            deadline = deadline,
            resourceLink = resourceLink,
            attachedFile = attachedFile,
            createdBy = facultyId,
            submissions = mutableMapOf(),
            dueDate = deadline.toLocalDate()
        )
        repository.addAssignment(newAssignment)
    }

    fun updateAssignment(updatedAssignment: SharedAssignment) {
        repository.updateAssignment(updatedAssignment)
    }

    fun deleteAssignment(assignmentId: String) {
        repository.deleteAssignment(assignmentId)
    }

    fun gradeSubmission(assignmentId: String, studentId: String, grade: String, feedback: String) {
        repository.gradeSubmission(assignmentId, studentId, grade, feedback)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FacultyDashboard(viewModel: FacultyViewModel = viewModel()) {
    var showAddAssignmentDialog by remember { mutableStateOf(false) }
    var currentTabIndex by remember { mutableIntStateOf(0) }
    val assignments by viewModel.assignments.collectAsState()
    val submissionsToReview by viewModel.submissionsToReview.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Faculty Dashboard") },
                actions = {
                    IconButton(onClick = { showAddAssignmentDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Assignment"
                        )
                    }

                    // Badge showing pending submissions
                    if (submissionsToReview.isNotEmpty()) {
                        BadgedBox(
                            badge = {
                                Badge { Text(submissionsToReview.size.toString()) }
                            }
                        ) {
                            IconButton(onClick = { currentTabIndex = 1 }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Pending Submissions"
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tabs for Assignments and Submissions
            TabRow(selectedTabIndex = currentTabIndex) {
                Tab(
                    selected = currentTabIndex == 0,
                    onClick = { currentTabIndex = 0 },
                    text = { Text("Assignments") },
                    icon = { Icon(Icons.Default.Book, contentDescription = null) }
                )

                Tab(
                    selected = currentTabIndex == 1,
                    onClick = { currentTabIndex = 1 },
                    text = { Text("Submissions") },
                    icon = {
                        if (submissionsToReview.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge { Text(submissionsToReview.size.toString()) }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null)
                            }
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null)
                        }
                    }
                )
            }

            // Content based on selected tab
            when (currentTabIndex) {
                0 -> AssignmentsTab(assignments, viewModel)
                1 -> SubmissionsTab(submissionsToReview, viewModel)
            }
        }
    }

    if (showAddAssignmentDialog) {
        AddAssignmentDialog(
            onDismiss = { showAddAssignmentDialog = false },
            onAddAssignment = { title, subject, description, deadline, resourceLink, attachedFile ->
                viewModel.addAssignment(
                    title = title,
                    subject = subject,
                    description = description,
                    deadline = deadline,
                    resourceLink = resourceLink,
                    attachedFile = attachedFile
                )
                showAddAssignmentDialog = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentsTab(assignments: List<SharedAssignment>, viewModel: FacultyViewModel) {
    if (assignments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No assignments created yet. Click the + button to add one.")
        }
    } else {
        LazyColumn {
            items(assignments) { assignment ->
                AssignmentItem(
                    assignment = assignment,
                    onEdit = { viewModel.updateAssignment(it) },
                    onDelete = { viewModel.deleteAssignment(assignment.id) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubmissionsTab(
    submissionsToReview: List<Pair<SharedAssignment, SubmissionData>>,
    viewModel: FacultyViewModel
) {
    if (submissionsToReview.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No pending submissions to review.")
        }
    } else {
        LazyColumn {
            items(submissionsToReview) { (assignment, submission) ->
                SubmissionItem(
                    assignment = assignment,
                    submission = submission,
                    onGrade = { grade, feedback ->
                        viewModel.gradeSubmission(
                            assignmentId = assignment.id,
                            studentId = submission.studentId,
                            grade = grade,
                            feedback = feedback
                        )
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentItem(
    assignment: SharedAssignment,
    onEdit: (SharedAssignment) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showSubmissionsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = assignment.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Subject: ${assignment.subject}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Deadline: ${AssignmentRepository.formatDateTime(assignment.deadline)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = "Description: ${assignment.description}", style = MaterialTheme.typography.bodySmall)

            // Display resource link if available
            if (assignment.resourceLink != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Link, contentDescription = null)
                    Text(
                        text = "Resource: ${assignment.resourceLink}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Display attached file if available
            if (assignment.attachedFile != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Text(
                        text = "Attached: ${File(assignment.attachedFile).name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Submissions count
            Text(
                text = "Submissions: ${assignment.submissions.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }

                Button(onClick = { showSubmissionsDialog = true }) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Submissions")
                }

                Button(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        }
    }

    if (showEditDialog) {
        EditAssignmentDialog(
            assignment = assignment,
            onDismiss = { showEditDialog = false },
            onSave = { updatedAssignment ->
                onEdit(updatedAssignment)
                showEditDialog = false
            }
        )
    }

    if (showSubmissionsDialog) {
        SubmissionsListDialog(
            assignment = assignment,
            onDismiss = { showSubmissionsDialog = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubmissionItem(
    assignment: SharedAssignment,
    submission: SubmissionData,
    onGrade: (String, String) -> Unit
) {
    var showGradeDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Assignment: ${assignment.title}",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "Student: ${submission.studentName}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Submitted: ${AssignmentRepository.formatDateTime(submission.submittedAt)}",
                style = MaterialTheme.typography.bodySmall
            )

            // Display file info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Attachment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(File(submission.filePath).name)

                Spacer(modifier = Modifier.weight(1f))

                // Download button
                IconButton(onClick = {
                    // Here you would implement actual file download
                    // For now, just a placeholder
                }) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download Submission"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showGradeDialog = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Grade, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Grade Submission")
            }
        }
    }

    if (showGradeDialog) {
        GradeSubmissionDialog(
            studentName = submission.studentName,
            assignmentTitle = assignment.title,
            existingGrade = submission.grade,
            existingFeedback = submission.feedback,
            onDismiss = { showGradeDialog = false },
            onSubmit = { grade, feedback ->
                onGrade(grade, feedback)
                showGradeDialog = false
            }
        )
    }
}

@Composable
fun GradeSubmissionDialog(
    studentName: String,
    assignmentTitle: String,
    existingGrade: String? = null,
    existingFeedback: String? = null,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var grade by remember { mutableStateOf(existingGrade ?: "") }
    var feedback by remember { mutableStateOf(existingFeedback ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Grade Submission",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Student: $studentName",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Assignment: $assignmentTitle",
                    style = MaterialTheme.typography.bodyMedium
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
                    label = { Text("Feedback") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
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
                        }
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubmissionsListDialog(
    assignment: SharedAssignment,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Submissions for ${assignment.title}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (assignment.submissions.isEmpty()) {
                    Text("No submissions yet.")
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(assignment.submissions.values.toList()) { submission ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(
                                        text = submission.studentName,
                                        style = MaterialTheme.typography.titleSmall
                                    )

                                    Text(
                                        text = "Submitted: ${AssignmentRepository.formatDateTime(submission.submittedAt)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = File(submission.filePath).name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Spacer(modifier = Modifier.weight(1f))

                                        IconButton(onClick = {
                                            // Download file logic would go here
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = "Download"
                                            )
                                        }
                                    }

                                    if (submission.grade != null) {
                                        Row {
                                            Text(
                                                text = "Grade: ${submission.grade}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssignmentDialog(
    onDismiss: () -> Unit,
    onAddAssignment: (
        title: String,
        subject: String,
        description: String,
        deadline: LocalDateTime,
        resourceLink: String?,
        attachedFile: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf<LocalDateTime?>(null) }
    var resourceLink by remember { mutableStateOf("") }
    var attachedFile by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Add Assignment", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && title.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && subject.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    isError = showError && description.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date picker field
                OutlinedTextField(
                    value = deadline?.format(formatter) ?: "",
                    onValueChange = { },
                    label = { Text("Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && deadline == null,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = resourceLink,
                    onValueChange = { resourceLink = it },
                    label = { Text("Resource Link (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // File upload button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            // Here you would implement file picking
                            // For now, we'll simulate with a placeholder
                            attachedFile = "/path/to/document.pdf"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload Document"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Attach File (Optional)")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Show selected file name
                    attachedFile?.let {
                        Text(
                            text = File(it).name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error messages
                if (showError) {
                    if (title.isEmpty() || subject.isEmpty() || description.isEmpty() || deadline == null) {
                        Text(
                            text = "Please fill in all required fields",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isEmpty() || subject.isEmpty() || description.isEmpty() || deadline == null) {
                                showError = true
                            } else {
                                onAddAssignment(
                                    title,
                                    subject,
                                    description,
                                    deadline!!,
                                    resourceLink.ifEmpty { null },
                                    attachedFile
                                )
                            }
                        }
                    ) {
                        Text("Add Assignment")
                    }
                }
            }
        }
        if(showDatePicker) {
            DateTimePickerDialog(
                initialDateTime = deadline ?: LocalDateTime.now().plusDays(7),
                onDateTimeSelected = { selectedDateTime ->
                    deadline = selectedDateTime
                    showDatePicker = false
                },
                onDismiss = {showDatePicker = false}
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDateTime.toLocalDate()) }
    var showTimePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (!showTimePicker) {
                // Date Picker View
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set Deadline",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Create a proper calendar display instead of using DatePicker
                    CalendarView(
                        initialDate = selectedDate,
                        onDateSelected = { selectedDate = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { showTimePicker = true }) {
                            Text("Next")
                        }
                    }
                }
            } else {
                // Time Picker View
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set Time",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val timePickerState = rememberTimePickerState(
                        initialHour = initialDateTime.hour,
                        initialMinute = initialDateTime.minute
                    )

                    TimePicker(state = timePickerState)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show selected date with a more user-friendly format
                    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
                    Text(
                        text = "Selected date: ${selectedDate.format(formatter)}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Back")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val localTime = LocalTime.of(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                                val localDateTime = LocalDateTime.of(selectedDate, localTime)
                                onDateTimeSelected(localDateTime)
                            }
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    initialDate: LocalDate,
    minDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Month navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) }
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
            }

            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) }
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
            }
        }

        // Day of week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Calendar grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val daysInMonth = currentMonth.lengthOfMonth()
        val startOffset = firstDayOfMonth.dayOfWeek.value % 7

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp),
            content = {
                // Empty items for days before the first day of month
                items(startOffset) {
                    Box(modifier = Modifier.aspectRatio(1f))
                }

                // Actual days in the month
                items(daysInMonth) { day ->
                    val date = currentMonth.atDay(day + 1)
                    val isSelected = date == selectedDate
                    val isToday = date == today
                    val isSelectable = date >= minDate

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .clickable(
                                enabled = isSelectable,
                                onClick = {
                                    selectedDate = date
                                    onDateSelected(date)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (day + 1).toString(),
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                !isSelectable -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditAssignmentDialog(
    assignment: SharedAssignment,
    onDismiss: () -> Unit,
    onSave: (SharedAssignment) -> Unit
) {
    var title by remember { mutableStateOf(assignment.title) }
    var subject by remember { mutableStateOf(assignment.subject) }
    var description by remember { mutableStateOf(assignment.description) }
    var deadline by remember { mutableStateOf(assignment.deadline) }
    var resourceLink by remember { mutableStateOf(assignment.resourceLink ?: "") }
    var attachedFile by remember { mutableStateOf(assignment.attachedFile) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Edit Assignment", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && title.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && subject.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    isError = showError && description.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date picker field
                OutlinedTextField(
                    value = deadline.format(formatter),
                    onValueChange = { },
                    label = { Text("Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = false,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = resourceLink,
                    onValueChange = { resourceLink = it },
                    label = { Text("Resource Link (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // File upload button and current file
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            // Here you would implement file picking
                            // For now, we'll simulate with a placeholder
                            attachedFile = "/path/to/updated_document.pdf"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload Document"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change File")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Show selected file name
                    attachedFile?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Current: ${File(it).name}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            IconButton(onClick = { attachedFile = null }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Remove File"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error messages
                if (showError) {
                    if (title.isEmpty() || subject.isEmpty() || description.isEmpty()) {
                        Text(
                            text = "Please fill in all required fields",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isEmpty() || subject.isEmpty() || description.isEmpty()) {
                                showError = true
                            } else {
                                val updatedAssignment = assignment.copy(
                                    title = title,
                                    subject = subject,
                                    description = description,
                                    deadline = deadline,
                                    resourceLink = resourceLink.ifEmpty { null },
                                    attachedFile = attachedFile
                                )
                                onSave(updatedAssignment)
                            }
                        }
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DateTimePickerDialog(
            initialDateTime = deadline,
            onDateTimeSelected = { selectedDateTime ->
                deadline = selectedDateTime
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewFacultyDashboard() {
    FacultyDashboard()
}