// File: com/example/collegeapp/navigation/NavGraph.kt
package com.example.collegeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.collegeapp.admin.screens.AdminDashboard
import com.example.collegeapp.admin.screens.FacultyDetailScreen
import com.example.collegeapp.admin.screens.ManageBanner
import com.example.collegeapp.admin.screens.ManageCollegeInfo
import com.example.collegeapp.admin.screens.ManageFacultyScreen
import com.example.collegeapp.admin.screens.ManageGallery
import com.example.collegeapp.admin.screens.ManageNotice
import com.example.collegeapp.screens.AboutUs
import com.example.collegeapp.screens.BottomNav
import com.example.collegeapp.screens.Faculty
import com.example.collegeapp.screens.Gallery
import com.example.collegeapp.screens.Home
import com.example.collegeapp.utils.Constant.isAdmin

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = if (isAdmin) Routes.AdminDashboard.route else Routes.BottomNav.route
    ) {
        composable(Routes.BottomNav.route) {
            BottomNav(navController)
        }

        composable(Routes.Home.route) {
            Home()
        }

        composable(Routes.Gallery.route) {
            Gallery()
        }

        composable(Routes.AboutUs.route) {
            AboutUs()
        }

        composable(Routes.Faculty.route) {
            Faculty(navController)
        }

        composable(Routes.AdminDashboard.route) {
            AdminDashboard(navController)
        }

        composable(Routes.ManageBanner.route) {
            ManageBanner(navController)
        }

        composable(Routes.ManageNotice.route) {
            ManageNotice(navController)
        }

        composable(Routes.ManageGallery.route) {
            ManageGallery(navController)
        }

        composable(Routes.ManageCollegeInfo.route) {
            ManageCollegeInfo(navController)
        }

        composable(Routes.ManageFaculty.route) {
            ManageFacultyScreen(navController)
        }

        // Pass “category” as NavArgument to FacultyDetailScreen:
        composable(
            route = Routes.FacultyDetailScreen.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            FacultyDetailScreen(
                category = category,
                navController = navController
            )
        }
    }
}
