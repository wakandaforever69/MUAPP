package com.muapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import com.muapp.common.BaseAssignment
import java.time.LocalDate

// Updated SharedAssignment class with deadline enforcement
@RequiresApi(Build.VERSION_CODES.O)
data class SharedAssignment(
    override val id: String,
    override val title: String,
    override val subject: String,
    val description: String,
    val deadline: LocalDateTime,
    val resourceLink: String? = null,
    val attachedFile: String? = null,
    val createdBy: String,
    val submissions: MutableMap<String, SubmissionData> = mutableMapOf(),
    override val dueDate: LocalDate
) : BaseAssignment {
    // Method to check if deadline has passed
    fun isDeadlinePassed(): Boolean {
        return LocalDateTime.now().isAfter(deadline)
    }

    // Method to calculate time remaining until deadline
    fun timeRemainingUntilDeadline(): String {
        if (isDeadlinePassed()) {
            return "Deadline passed"
        }

        val now = LocalDateTime.now()
        val daysDiff = java.time.Duration.between(now, deadline).toDays()
        val hoursDiff = java.time.Duration.between(now, deadline).toHours() % 24
        val minutesDiff = java.time.Duration.between(now, deadline).toMinutes() % 60

        return when {
            daysDiff > 0 -> "$daysDiff days, $hoursDiff hours"
            hoursDiff > 0 -> "$hoursDiff hours, $minutesDiff minutes"
            else -> "$minutesDiff minutes"
        }
    }

    // Method to check if a student has already submitted
    fun hasStudentSubmitted(studentId: String): Boolean {
        return submissions.containsKey(studentId)
    }
}

data class SubmissionData(
    val studentId: String,
    val studentName: String,
    val filePath: String,
    val submittedAt: LocalDateTime,
    var grade: String? = null,
    var feedback: String? = null,
    val fileName: String = "submission.pdf" // Add file name for better UI display
)