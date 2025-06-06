package com.example.collegeapp.models

data class BannerModel(
    val url : String,
    val docId : String
){
    constructor() : this("", "")
}
