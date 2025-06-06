package com.example.collegeapp.admin.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.R
import com.example.collegeapp.itemview.GalleryItemView
import com.example.collegeapp.models.GalleryModel
import com.example.collegeapp.ui.theme.Purple40
import com.example.collegeapp.viewmodel.GalleryViewModel

// Enum for managing screen state (Add Category, Add Image, or viewing gallery)
enum class GalleryScreenState {
    ADD_CATEGORY_FORM,
    ADD_IMAGE_FORM,
    VIEW_GALLERY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageGallery(navController: NavController) {
    val context = LocalContext.current
    val galleryViewModel: GalleryViewModel = viewModel()

    val isUploaded by galleryViewModel.isPosted.observeAsState(false)
    val isDeleted by galleryViewModel.isDeleted.observeAsState(false)
    val galleryItemsList by galleryViewModel.galleryList.observeAsState(emptyList())

    var currentScreenState by remember { mutableStateOf(GalleryScreenState.ADD_CATEGORY_FORM) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val categoryOptionsForCategoryForm = listOf("Campus Photos", "Events", "Sports", "Labs", "Library", "Annual Function")
    var selectedCategoryNameForNewCategory by remember { mutableStateOf("Select Category Name") }
    var isDropdownExpandedForCategoryForm by remember { mutableStateOf(false) }

    val existingCategoriesForImageForm = galleryItemsList.map { it.category }.distinct().sorted()
    var selectedCategoryForImageForm by remember { mutableStateOf("Select Existing Category") }
    var isDropdownExpandedForImageForm by remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { pickedUri -> imageUri = pickedUri }

    LaunchedEffect(Unit) {
        galleryViewModel.getGallery()
    }

    LaunchedEffect(isUploaded) {
        if (isUploaded) {
            Toast.makeText(context, "Uploaded successfully!", Toast.LENGTH_SHORT).show()
            imageUri = null
            selectedCategoryNameForNewCategory = "Select Category Name"
            selectedCategoryForImageForm = "Select Existing Category"
            isLoading = false
            galleryViewModel.getGallery()
        }
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show()
            isLoading = false
            galleryViewModel.getGallery()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Gallery", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Purple40),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Changed the main Column to LazyColumn to make the entire screen scrollable
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding from Scaffold here
            ) {
                item { // Wrap the toggle buttons row in 'item'
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    currentScreenState = GalleryScreenState.ADD_CATEGORY_FORM
                                    imageUri = null
                                }
                            // Removed .background() modifier to keep default card appearance
                        ) {
                            Text(
                                text = "Add Category",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                // Apply text color based on state
                                color = if (currentScreenState == GalleryScreenState.ADD_CATEGORY_FORM) Purple40 else Color.Black
                            )
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    currentScreenState = GalleryScreenState.ADD_IMAGE_FORM
                                    imageUri = null
                                }
                            // Removed .background() modifier to keep default card appearance
                        ) {
                            Text(
                                text = "Add Image",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                // Apply text color based on state
                                color = if (currentScreenState == GalleryScreenState.ADD_IMAGE_FORM) Purple40 else Color.Black
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // --- ADD CATEGORY FORM ---
                if (currentScreenState == GalleryScreenState.ADD_CATEGORY_FORM) {
                    item { // Wrap the form content in an 'item' block for LazyColumn
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {

                                ExposedDropdownMenuBox(
                                    expanded = isDropdownExpandedForCategoryForm,
                                    onExpandedChange = { isDropdownExpandedForCategoryForm = !isDropdownExpandedForCategoryForm }
                                ) {
                                    OutlinedTextField(
                                        readOnly = true,
                                        value = selectedCategoryNameForNewCategory,
                                        onValueChange = {},
                                        label = { Text("Select New Category Name") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpandedForCategoryForm) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = isDropdownExpandedForCategoryForm,
                                        onDismissRequest = { isDropdownExpandedForCategoryForm = false }
                                    ) {
                                        categoryOptionsForCategoryForm.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(item) },
                                                onClick = {
                                                    selectedCategoryNameForNewCategory = item
                                                    isDropdownExpandedForCategoryForm = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Image(
                                    painter = if (imageUri == null)
                                        painterResource(id = R.drawable.image_placeholder)
                                    else
                                        rememberAsyncImagePainter(model = imageUri),
                                    contentDescription = "Category Image",
                                    modifier = Modifier
                                        .height(220.dp)
                                        .fillMaxWidth()
                                        .clickable { launcher.launch("image/*") }
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (selectedCategoryNameForNewCategory == "Select Category Name" || imageUri == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Please select category name and image",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                isLoading = true
                                                galleryViewModel.saveGalleryImage(imageUri!!, selectedCategoryNameForNewCategory)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Add Category")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            selectedCategoryNameForNewCategory = "Select Category Name"
                                            imageUri = null
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }

                // --- ADD IMAGE FORM ---
                if (currentScreenState == GalleryScreenState.ADD_IMAGE_FORM) {
                    item { // Wrap the form content in an 'item' block for LazyColumn
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {

                                ExposedDropdownMenuBox(
                                    expanded = isDropdownExpandedForImageForm,
                                    onExpandedChange = { isDropdownExpandedForImageForm = !isDropdownExpandedForImageForm }
                                ) {
                                    OutlinedTextField(
                                        readOnly = true,
                                        value = selectedCategoryForImageForm,
                                        onValueChange = {},
                                        label = { Text("Select Existing Category") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpandedForImageForm) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = isDropdownExpandedForImageForm,
                                        onDismissRequest = { isDropdownExpandedForImageForm = false }
                                    ) {
                                        existingCategoriesForImageForm.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(item) },
                                                onClick = {
                                                    selectedCategoryForImageForm = item
                                                    isDropdownExpandedForImageForm = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Image(
                                    painter = if (imageUri == null)
                                        painterResource(id = R.drawable.image_placeholder)
                                    else
                                        rememberAsyncImagePainter(model = imageUri),
                                    contentDescription = "New Image",
                                    modifier = Modifier
                                        .height(220.dp)
                                        .fillMaxWidth()
                                        .clickable { launcher.launch("image/*") }
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (selectedCategoryForImageForm == "Select Existing Category" || imageUri == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Please select category and image",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                isLoading = true
                                                galleryViewModel.saveGalleryImage(imageUri!!, selectedCategoryForImageForm)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Add Image")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            selectedCategoryForImageForm = "Select Existing Category"
                                            imageUri = null
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // --- DISPLAY GALLERY ITEMS ---
                item { // Wrap the Gallery header in 'item'
                    Text(
                        text = "Gallery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                if (galleryItemsList.isEmpty() && !isLoading) {
                    item { // Wrap the empty state text in 'item'
                        Text(
                            text = "No gallery items added yet.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    // This section now correctly uses items from the main LazyColumn
                    items(galleryItemsList) { galleryModel ->
                        GalleryItemView(
                            galleryModel = galleryModel,
                            delete = { modelToDelete ->
                                isLoading = true
                                galleryViewModel.deleteCategory(modelToDelete.category)
                            },
                            deleteImage = { imageUrlToDelete, categoryName ->
                                isLoading = true
                                galleryViewModel.deleteGalleryImage(categoryName, imageUrlToDelete, )
                            },
                            onClick = { categoryName ->
                                Toast.makeText(context, "Clicked on category: $categoryName", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}