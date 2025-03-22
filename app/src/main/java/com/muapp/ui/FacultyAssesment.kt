package com.muapp.ui
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.LaunchedEffect

data class Assignment(
    val id: Int,
    val title: String,
    val description: String,
    val deadline: String,
    val resourceLink: String? = null, // Nullable resource link
    val documentPath: String? = null // Path to the uploaded document
)

class FacultyViewModel : ViewModel() {
    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> get() = _assignments.asStateFlow()

    fun addAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _assignments.value = _assignments.value + assignment
        }
    }

    fun updateAssignment(updatedAssignment: Assignment) {
        viewModelScope.launch {
            _assignments.value = _assignments.value.map {
                if (it.id == updatedAssignment.id) updatedAssignment else it
            }
        }
    }

    fun deleteAssignment(assignmentId: Int) {
        viewModelScope.launch {
            _assignments.value = _assignments.value.filter { it.id != assignmentId }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyDashboard(viewModel: FacultyViewModel = viewModel()) {
    var showAddAssignmentDialog by remember { mutableStateOf(false) }
    val assignments by viewModel.assignments.collectAsState()

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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (assignments.isEmpty()) {
                Text(
                    text = "No assignments added yet.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                AssignmentList(
                    assignments = assignments,
                    onEditAssignment = { updatedAssignment ->
                        viewModel.updateAssignment(updatedAssignment)
                    },
                    onDeleteAssignment = { assignmentId ->
                        viewModel.deleteAssignment(assignmentId) // Pass the delete function
                    }
                )
            }
        }
    }

    if (showAddAssignmentDialog) {
        AddAssignmentDialog(
            assignments = assignments,
            onDismiss = { showAddAssignmentDialog = false },
            onAddAssignment = { newAssignment ->
                viewModel.addAssignment(newAssignment)
                showAddAssignmentDialog = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentList(assignments: List<Assignment>, onEditAssignment: (Assignment) -> Unit, onDeleteAssignment: (Int) -> Unit) {
    LazyColumn {
        items(assignments) { assignment ->
            AssignmentItem(assignment = assignment, onEditAssignment = onEditAssignment, onDeleteAssignment = onDeleteAssignment)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentItem(assignment: Assignment, onEditAssignment: (Assignment) -> Unit, onDeleteAssignment: (Int) -> Unit) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = assignment.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Deadline: ${assignment.deadline}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Description: ${assignment.description}", style = MaterialTheme.typography.bodySmall)

            // Display document info if available
            assignment.documentPath?.let {
                Text(
                    text = "Document: ${File(it).name}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

//            Edit and Delete Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Button(onClick = { onDeleteAssignment(assignment.id) }) {
                    Text("Delete")
                }
            }
            Button(onClick = { showEditDialog = true }) {
                Text("Edit Assignment")
            }
        }
    }

    if (showEditDialog) {
        EditAssignmentDialog(
            assignment = assignment,
            onDismiss = { showEditDialog = false },
            onSave = { updatedAssignment ->
                onEditAssignment(updatedAssignment)
                showEditDialog = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssignmentDialog(
    assignments: List<Assignment>,
    onDismiss: () -> Unit,
    onAddAssignment: (Assignment) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var resourceLink by remember { mutableStateOf("") }
    var documentPath by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // For date picker
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val context = LocalContext.current

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
                if (showError && title.isEmpty()) {
                    Text(
                        text = "Title is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && description.isEmpty()
                )
                if (showError && description.isEmpty()) {
                    Text(
                        text = "Description is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Date picker field
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && deadline.isEmpty(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )
                if (showError && deadline.isEmpty()) {
                    Text(
                        text = "Deadline is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                OutlinedTextField(
                    value = resourceLink,
                    onValueChange = { resourceLink = it },
                    label = { Text("Resource Link") },
                    modifier = Modifier.fillMaxWidth()
                )

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
                            documentPath = "/path/to/document.pdf"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload Document"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload Document")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Show selected file name
                    documentPath?.let {
                        Text(
                            text = File(it).name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
                            showError = true
                        } else {
                            val newAssignment = Assignment(
                                id = assignments.size + 1,
                                title = title,
                                description = description,
                                deadline = deadline,
                                resourceLink = resourceLink.ifEmpty { null },
                                documentPath = documentPath
                            )
                            onAddAssignment(newAssignment)
                            onDismiss()
                        }
                    }) {
                        Text("Add")
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DateTimePicker(
            onDateTimeSelected = { selectedDateTime ->
                deadline = selectedDateTime.format(formatter)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (!showTimePicker) {
        // Date picker dialog
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = { showTimePicker = true }) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            // Use a composable lambda without directly calling Text
            val titleText: @Composable () -> Unit = { Text("Select Date") }
            val headlineText: @Composable () -> Unit = { Text("Select date for assignment deadline") }

            // Get current date in millis for validation
            val todayMillis = LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000

            // Create DatePicker state with validation
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= todayMillis
                    }
                }
            )

            // Update selectedDate when state changes
            val selectedMillis = datePickerState.selectedDateMillis
            LaunchedEffect(selectedMillis) {
                selectedMillis?.let { millis ->
                    selectedDate = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
            }

            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = titleText,
                headline = headlineText
            )
        }
    } else {
        // Time picker
        val timePickerState = rememberTimePickerState()

        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = {
                val localTime = LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )
                val localDateTime = LocalDateTime.of(selectedDate, localTime)
                onDateTimeSelected(localDateTime)
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onConfirm) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssignmentDialog(
    assignment: Assignment,
    onDismiss: () -> Unit,
    onSave: (Assignment) -> Unit
) {
    var title by remember { mutableStateOf(assignment.title) }
    var description by remember { mutableStateOf(assignment.description) }
    var deadline by remember { mutableStateOf(assignment.deadline) }
    var resourceLink by remember { mutableStateOf(assignment.resourceLink ?: "") }
    var documentPath by remember { mutableStateOf(assignment.documentPath) }
    var showDatePicker by remember { mutableStateOf(false) }

    // For date picker
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
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date picker field
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )

                OutlinedTextField(
                    value = resourceLink,
                    onValueChange = { resourceLink = it },
                    label = { Text("Resource Link") },
                    modifier = Modifier.fillMaxWidth()
                )

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
                            // For now, we'll simulate with a placeholder if no file is selected
                            if (documentPath == null) {
                                documentPath = "/path/to/document.pdf"
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload Document"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload Document")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Show selected file name
                    documentPath?.let {
                        Text(
                            text = File(it).name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val updatedAssignment = assignment.copy(
                            title = title,
                            description = description,
                            deadline = deadline,
                            resourceLink = resourceLink.ifEmpty { null },
                            documentPath = documentPath
                        )
                        onSave(updatedAssignment)
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DateTimePicker(
            onDateTimeSelected = { selectedDateTime ->
                deadline = selectedDateTime.format(formatter)
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