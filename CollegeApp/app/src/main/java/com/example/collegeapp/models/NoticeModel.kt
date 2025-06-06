// File: com/example/collegeapp/models/NoticeModel.kt

package com.example.collegeapp.models

data class NoticeModel(
    val imageUrl: String? = "",
    val title:    String? = "",
    val link:     String? = "",
    val docId:    String  = ""
) {
    // Primary constructor already has default values, so a no‐arg constructor is built‐in.
    // If you still want an explicit no‐arg secondary constructor, it must call all four params:
    constructor() : this("", "", "", "")
}
