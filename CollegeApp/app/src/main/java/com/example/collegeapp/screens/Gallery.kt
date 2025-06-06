package com.example.collegeapp.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Essential import for LazyColumn items
import androidx.compose.material3.CircularProgressIndicator // For loading indicator
import androidx.compose.material3.Text // For text display
import androidx.compose.material3.MaterialTheme // For theming
import androidx.compose.foundation.layout.Box // For loading overlay
import androidx.compose.foundation.layout.Column // For vertical arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background // For loading overlay background
import androidx.compose.foundation.layout.Arrangement // For content arrangement
import androidx.compose.ui.Alignment // For content alignment
import androidx.compose.ui.graphics.Color // For colors
import androidx.compose.ui.unit.dp // For dimensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegeapp.itemview.GalleryItemView
import com.example.collegeapp.models.GalleryModel // Import GalleryModel
import com.example.collegeapp.viewmodel.GalleryViewModel

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf // For mutableStateOf
import androidx.compose.runtime.remember // For remember
import androidx.compose.runtime.setValue // For set value of mutableState
import androidx.compose.ui.Modifier


@Composable
fun Gallery() {
    val galleryViewModel: GalleryViewModel = viewModel()
    val galleryItemsList by galleryViewModel.galleryList.observeAsState(emptyList())

    // Observe deletion status from ViewModel for UI feedback
    val isDeletedFromVm by galleryViewModel.isDeleted.observeAsState(false)

    // Local loading state to show progress indicator
    var isLoading by remember { mutableStateOf(false) }

    // Use LaunchedEffect to ensure getGallery() is called only once
    LaunchedEffect(Unit) {
        galleryViewModel.getGallery()
    }

    // Effect to react to ViewModel's deletion status
    LaunchedEffect(isDeletedFromVm) {
        if (isDeletedFromVm) {
            // After deletion, reset local loading state
            isLoading = false
            // You might want a Toast here to confirm deletion if not done in ViewModel
            // Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) { // Use Box to allow overlaying loading indicator
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Iterate through the galleryItemsList. Each 'galleryItem' is a GalleryModel object.
            items(items = galleryItemsList) { galleryItem ->
                // Calling GalleryItemView, passing correct parameters to its lambdas
                GalleryItemView(
                    galleryModel = galleryItem, // Pass the entire GalleryModel object

                    // Lambda for deleting a whole category
                    delete = { modelToDelete ->
                        isLoading = true // Start loading indicator
                        galleryViewModel.deleteCategory(modelToDelete.category)
                        // ViewModel will update _isDeleted or refresh galleryList,
                        // triggering LaunchedEffect to reset isLoading.
                    },

                    // Lambda for deleting a single image from a category
                    deleteImage = { imageUrlToDelete, categoryName ->
                        isLoading = true // Start loading indicator
                        // Now the parameters perfectly match the ViewModel's function signature
                        galleryViewModel.deleteGalleryImage(imageUrlToDelete, categoryName)
                        // ViewModel will update _isDeleted or refresh galleryList,
                        // triggering LaunchedEffect to reset isLoading.
                    },

                    // Handle clicks on the category card (if needed, currently shows a Toast)
                    onClick = { categoryName ->
                        // This toast needs a context. If you want it here, pass LocalContext.current
                        // val context = LocalContext.current
                        // Toast.makeText(context, "Clicked on category: $categoryName", Toast.LENGTH_SHORT).show()
                        // You might navigate here: navController.navigate("gallery_category_details/$categoryName")
                    }
                )
            }
        }

        // Display a full-screen loading overlay if isLoading is true
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)), // Semi-transparent black background
                contentAlignment = Alignment.Center // Center the circular progress indicator
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) // Use theme's primary color
            }
        }
    }
}