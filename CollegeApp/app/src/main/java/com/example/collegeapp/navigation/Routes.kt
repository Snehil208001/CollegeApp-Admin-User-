// File: com/example/collegeapp/navigation/Routes.kt
package com.example.collegeapp.navigation

sealed class Routes(val route: String) {
    object BottomNav           : Routes("bottom_nav")
    object Home                : Routes("home")
    object Gallery             : Routes("gallery")
    object AboutUs             : Routes("about_us")
    object Faculty             : Routes("faculty")
    object AdminDashboard      : Routes("admin_dashboard")
    object ManageBanner        : Routes("manage_banner")
    object ManageFaculty       : Routes("manage_faculty")
    object ManageGallery       : Routes("manage_gallery")
    object ManageCollegeInfo   : Routes("college_info")
    object ManageNotice        : Routes("manage_notice")

    // <-- pass “category” as the path parameter -->
    object FacultyDetailScreen : Routes("faculty_details/{category}")
}
