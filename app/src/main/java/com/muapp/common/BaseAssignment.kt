package com.muapp.common

import java.time.LocalDate

interface BaseAssignment {
    val id: String
    val title: String
    val subject: String
    val dueDate: LocalDate
}