package com.example.collegeapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.collegeapp.itemview.FacultyItemView
import com.example.collegeapp.viewmodel.FacultyViewModel
import com.example.collegeapp.itemview.FacultyModelItemView // Import the new item view for FacultyModel
import com.example.collegeapp.models.FacultyModel // Import FacultyModel

import androidx.compose.runtime.*

@Composable
fun Faculty(navController: NavController) {
    val facultyViewModel: FacultyViewModel = viewModel()
    val categoryList by facultyViewModel.categoryList.observeAsState(emptyList())
    val facultyList by facultyViewModel.facultyList.observeAsState(emptyList()) // Observe faculty list
    var isLoading by remember { mutableStateOf(false) } // This isLoading is for category deletion

    // Use LaunchedEffect to ensure data is fetched when the composable is first displayed.
    LaunchedEffect(Unit) {
        facultyViewModel.getCategory()
        facultyViewModel.getFaculty() // Fetch faculty members (teachers)
    }

    // This LaunchedEffect will observe changes in the categoryList for deletion status.
    LaunchedEffect(categoryList) {
        if (isLoading && categoryList.isNotEmpty()) {
            isLoading = false
        }
    }

    // You might want a separate isLoading for faculty deletion, or a combined one in ViewModel
    // For now, let's assume this isLoading is primarily for category deletion
    // as per your original code.

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Display a loading indicator when a category deletion operation is in progress
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Deleting category...",
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- Categories Section ---
                item {
                    Text(
                        text = "Faculty Categories",
                        style = MaterialTheme.typography.headlineMedium, // Larger text for section title
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(items = categoryList) { categoryString -> // 'categoryString' is a String here
                    FacultyItemView(
                        category = categoryString, // Pass the String directly
                        delete = { cat -> // 'cat' will be the category name (String)
                            isLoading = true // Use isLoading for category deletion
                            facultyViewModel.deleteCategory(cat)
                        },
                        onClick = { cat -> // 'cat' will be the category name (String)
                            navController.navigate("faculty_details/$cat")
                        }
                    )
                }

                // --- Faculty Members (Teachers) Section ---
                item {
                    Text(
                        text = "All Faculty Members",
                        style = MaterialTheme.typography.headlineMedium, // Larger text for section title
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(items = facultyList) { facultyMember -> // 'facultyMember' is a FacultyModel object
                    FacultyModelItemView(
                        facultyModel = facultyMember, // Pass the entire FacultyModel object
                        onDelete = { modelToDelete -> // This will be called when delete icon on a faculty member is clicked
                            // Assuming you want to use the same isLoading for all deletions
                            // Or you might need a separate state if actions are distinct
                            isLoading = true // Start loading for faculty member deletion
                            facultyViewModel.deleteFaculty(modelToDelete)
                            // You might need another LaunchedEffect to reset isLoading based on _isDeleted LiveData
                            // or facultyList update if deleteFaculty also triggers a refresh.
                        },
                        onClick = { model -> // Click handler for faculty member item
                            // You might navigate to a faculty member's specific details screen
                            navController.navigate("faculty_member_details/${model.docId}")
                        }
                    )
                }
            }
        }
    }
}