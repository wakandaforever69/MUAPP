package com.muapp.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// Data class for Address
class Address {
    var addressLine1 by mutableStateOf("")
    var addressLine2 by mutableStateOf("")
    var country by mutableStateOf("")
    var state by mutableStateOf("")
    var city by mutableStateOf("")
    var pinCode by mutableStateOf("")

    fun copy(): Address {
        val newAddress = Address()
        newAddress.addressLine1 = this.addressLine1
        newAddress.addressLine2 = this.addressLine2
        newAddress.country = this.country
        newAddress.state = this.state
        newAddress.city = this.city
        newAddress.pinCode = this.pinCode
        return newAddress
    }
}

// Data class for Parent Details
class ParentDetails {
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var mobileNumber by mutableStateOf("")
    var occupation by mutableStateOf("")
    var annualIncome by mutableStateOf("")
}

// Data class for Education Details
class EducationDetails {
    var obtainedMarks by mutableStateOf("")
    var totalMarks by mutableStateOf("")
    var percentage by mutableStateOf("")
    var yearOfPassing by mutableStateOf("")
    var board by mutableStateOf("")
    var schoolName by mutableStateOf("")
    var place by mutableStateOf("")
}

// Main state class for student profile
class StudentProfileState {
    // Personal Details
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var fatherName by mutableStateOf("")
    var motherName by mutableStateOf("")
    var dob by mutableStateOf("")
    var parentMobile by mutableStateOf("")
    var gender by mutableStateOf("")
    var bloodGroup by mutableStateOf("")
    var motherTongue by mutableStateOf("")
    var religion by mutableStateOf("")
    var nationalIdCard by mutableStateOf("")
    var caste by mutableStateOf("")
    var subCaste by mutableStateOf("")
    var category by mutableStateOf("")

    // Contact Details
    var currentAddress = Address()
    var permanentAddress = Address()
    var sameAsCurrentAddress by mutableStateOf(false)

    // Parent Details
    var fatherDetails = ParentDetails()
    var motherDetails = ParentDetails()

    // Education Details
    var tenthDetails = EducationDetails()
    var twelfthDetails = EducationDetails()
}

@Composable
fun rememberStudentProfileState(): StudentProfileState {
    return remember { StudentProfileState() }
}