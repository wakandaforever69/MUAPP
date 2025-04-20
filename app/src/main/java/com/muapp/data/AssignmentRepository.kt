package com.muapp.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.core.net.toUri

@RequiresApi(Build.VERSION_CODES.O)
object AssignmentRepository {
    // In-memory storage of assignments
    private val _assignments = MutableStateFlow<List<SharedAssignment>>(emptyList())
    val assignments: StateFlow<List<SharedAssignment>> = _assignments.asStateFlow()

    // File storage directories - would be initialized in app context
    private var assignmentFilesDir: File? = null
    private var submissionFilesDir: File? = null

    // Initialize storage directories
    fun initialize(context: Context) {
        assignmentFilesDir = File(context.filesDir, "assignments")
        submissionFilesDir = File(context.filesDir, "submissions")

        // Create directories if they don't exist
        assignmentFilesDir?.mkdirs()
        submissionFilesDir?.mkdirs()
    }

    // Add a new assignment
    fun addAssignment(assignment: SharedAssignment) {
        val currentList = _assignments.value.toMutableList()
        currentList.add(assignment)
        _assignments.value = currentList
    }

    // Update an existing assignment
    fun updateAssignment(updatedAssignment: SharedAssignment) {
        val currentList = _assignments.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedAssignment.id }

        if (index != -1) {
            // Keep existing submissions when updating
            val existingSubmissions = currentList[index].submissions
            val assignmentWithSubmissions = updatedAssignment.copy(submissions = existingSubmissions)
            currentList[index] = assignmentWithSubmissions
            _assignments.value = currentList
        }
    }

    // Delete an assignment
    fun deleteAssignment(assignmentId: String) {
        val currentList = _assignments.value.toMutableList()
        val assignmentToRemove = currentList.find { it.id == assignmentId }

        assignmentToRemove?.let {
            // Delete associated files
            it.attachedFile?.let { filePath ->
                File(filePath).delete()
            }

            // Delete submission files
            it.submissions.values.forEach { submission ->
                File(submission.filePath).delete()
            }

            currentList.removeIf { assignment -> assignment.id == assignmentId }
            _assignments.value = currentList
        }
    }

    // Helper to format datetime
    fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        return dateTime.format(formatter)
    }

    // Submit assignment
    fun submitAssignment(assignmentId: String, studentId: String, studentName: String,
                         filePath: String, fileName: String): Boolean {
        val currentList = _assignments.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == assignmentId }

        if (index != -1) {
            val assignment = currentList[index]

            // Check if deadline has passed
            if (assignment.isDeadlinePassed()) {
                return false
            }

            // Copy file to submissions directory
            val fileId = UUID.randomUUID().toString()
            val destFile = File(submissionFilesDir, "${fileId}_${fileName}")

            try {
                File(filePath).copyTo(destFile, overwrite = true)

                // Create submission data
                val submission = SubmissionData(
                    studentId = studentId,
                    studentName = studentName,
                    filePath = destFile.absolutePath,
                    submittedAt = LocalDateTime.now(),
                    fileName = fileName
                )

                // Add submission to assignment
                val updatedAssignment = assignment.copy()
                updatedAssignment.submissions[studentId] = submission

                currentList[index] = updatedAssignment
                _assignments.value = currentList

                return true
            } catch (e: IOException) {
                // Handle file copy error
                return false
            }
        }

        return false
    }

    // Grade a submission
    fun gradeSubmission(assignmentId: String, studentId: String, grade: String, feedback: String) {
        val currentList = _assignments.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == assignmentId }

        if (index != -1) {
            val assignment = currentList[index]
            val submission = assignment.submissions[studentId]

            submission?.let {
                it.grade = grade
                it.feedback = feedback
                _assignments.value = currentList
            }
        }
    }

    // Upload assignment file and return its path
    fun uploadAssignmentFile(context: Context, sourceUri: String, fileName: String): String? {
        assignmentFilesDir?.let { directory ->
            val fileId = UUID.randomUUID().toString()
            val destFile = File(directory, "${fileId}_${fileName}")

            try {
                // Simplified version - in a real app, you'd use ContentResolver
                val inputStream = context.contentResolver.openInputStream(sourceUri.toUri())
                val outputStream = FileOutputStream(destFile)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                return destFile.absolutePath
            } catch (e: IOException) {
                // Handle file copy error
                return null
            }
        }

        return null
    }

    // Get assignment file for download
    fun getAssignmentFile(assignmentId: String): File? {
        val assignment = _assignments.value.find { it.id == assignmentId }

        return assignment?.attachedFile?.let { File(it) }.takeIf { it?.exists() == true }
    }

    // Get submission file for download
    fun getSubmissionFile(assignmentId: String, studentId: String): File? {
        val assignment = _assignments.value.find { it.id == assignmentId }
        val submission = assignment?.submissions?.get(studentId)

        return submission?.filePath?.let { File(it) }.takeIf { it?.exists() == true }
    }

    // Get assignments for a specific student with submission status
    fun getAssignmentsForStudent(studentId: String): List<Pair<SharedAssignment, Boolean>> {
        return _assignments.value.map { assignment ->
            Pair(assignment, assignment.hasStudentSubmitted(studentId))
        }
    }

    // Filter by deadline status
    fun getActiveAssignments(): List<SharedAssignment> {
        return _assignments.value.filter { !it.isDeadlinePassed() }
    }

    fun getPastAssignments(): List<SharedAssignment> {
        return _assignments.value.filter { it.isDeadlinePassed() }
    }
}