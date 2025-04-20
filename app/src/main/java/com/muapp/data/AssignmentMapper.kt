package com.muapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.muapp.ui.AssignmentStatus
import com.muapp.ui.StudentAssignment
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
object AssignmentMapper {
    fun mapToStudentAssignment(sharedAssignment: SharedAssignment, studentId: String): StudentAssignment {
        val submission = sharedAssignment.submissions[studentId]
        val status = when {
            submission != null -> AssignmentStatus.COMPLETED
            sharedAssignment.isDeadlinePassed() -> AssignmentStatus.OVERDUE
            else -> AssignmentStatus.PENDING
        }

        return StudentAssignment(
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

    fun mapToSharedAssignmentList(studentAssignments: List<StudentAssignment>, userId: String): List<SharedAssignment> {
        return studentAssignments.map { studentAssignment ->
            SharedAssignment(
                id = studentAssignment.id,
                title = studentAssignment.title,
                subject = studentAssignment.subject,
                description = "", // No direct mapping, so using empty string
                deadline = studentAssignment.dueDate.atStartOfDay().plusHours(23).plusMinutes(59),
                createdBy = userId,
                submissions = mutableMapOf(),
                resourceLink =  null,
                attachedFile = null,
                dueDate = studentAssignment.dueDate // No way to map back submissions without context
            )
        }
    }
}