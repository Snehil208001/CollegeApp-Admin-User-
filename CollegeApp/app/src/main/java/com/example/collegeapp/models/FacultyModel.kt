// File: com/example/collegeapp/models/FacultyModel.kt
package com.example.collegeapp.models

/**
 * Model representing a single faculty member. Firestore expects a no-arg constructor,
 * so we provide default values.
 */
data class FacultyModel(
    val imageUrl:   String? = "",
    val name:       String? = "",
    val email:      String? = "",
    val position:   String? = "",
    val docId:      String  = "",
    val department: String  = ""
) {
    // Explicit no-arg secondary constructor is optional here—defaults suffice.
    constructor() : this("", "", "", "", "", "")
}
