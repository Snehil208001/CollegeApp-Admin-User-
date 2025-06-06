package com.example.collegeapp.admin.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.R
import com.example.collegeapp.models.CollegeInfoModel
import com.example.collegeapp.ui.theme.Purple40
import com.example.collegeapp.viewmodel.CollegeInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCollegeInfo(navController: NavController) {

    val context = LocalContext.current
    val collegeInfoViewModel: CollegeInfoViewModel = viewModel()

    val isPosted by collegeInfoViewModel.isPosted.observeAsState(false)
    val collegeInfoData by collegeInfoViewModel.collegeInfo.observeAsState(null)

    var isLoading by remember { mutableStateOf(true) } // For initial data loading
    var isSaving by remember { mutableStateOf(false) } // For save operation

    // Local state for the college information form
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var websiteLink by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Holds URI of newly selected image
    var currentImageUrl by remember { mutableStateOf<String?>(null) } // Holds URL of currently stored image

    // Fetch existing college info when the screen is launched
    LaunchedEffect(Unit) {
        isLoading = true // Start loading indicator
        collegeInfoViewModel.getCollegeInfo()
    }

    // Update local state when collegeInfoData is fetched
    LaunchedEffect(collegeInfoData) {
        collegeInfoData?.let { info ->
            name = info.name ?: ""
            address = info.address ?: ""
            phone = info.phone ?: ""
            email = info.email ?: ""
            description = info.description ?: ""
            websiteLink = info.websiteLink ?: ""
            currentImageUrl = info.imageUrl // Set current image URL from fetched data
            imageUri = null // Ensure no pending local URI is carried over
        }
        isLoading = false // Stop loading indicator once data is populated
    }

    // ActivityResultLauncher to pick an image from gallery
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { pickedUri ->
        imageUri = pickedUri // Set the newly picked URI
        currentImageUrl = null // Clear current image URL to indicate a new image is pending
    }

    // Show a Toast + reset form when upload finishes successfully
    LaunchedEffect(isPosted) {
        if (isPosted) {
            isSaving = false // Stop saving indicator
            Toast.makeText(context, "College Info Updated!", Toast.LENGTH_SHORT).show()
            // Important: Re-fetch the data to get the updated imageUrl from Firebase
            collegeInfoViewModel.getCollegeInfo()
            // imageUri will be reset in the collegeInfoData LaunchedEffect
        }
        // Handle case where isPosted becomes false due to an error during save
        if (!isPosted && isSaving) { // If it's false AND we were in saving state
            isSaving = false // Stop saving indicator
            Toast.makeText(context, "Failed to update college info.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage College Info", color = Color.White) },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Purple40),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "College Details",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text(text = "College Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text(text = "Address") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text(text = "Phone Number") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(text = "Email") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text(text = "Description") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(vertical = 4.dp)
                            )

                            OutlinedTextField(
                                value = websiteLink,
                                onValueChange = { websiteLink = it },
                                label = { Text(text = "Website Link") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Image selection/display
                            Image(
                                painter = if (imageUri != null) {
                                    rememberAsyncImagePainter(model = imageUri)
                                } else if (!currentImageUrl.isNullOrEmpty()) {
                                    rememberAsyncImagePainter(model = currentImageUrl)
                                } else {
                                    painterResource(id = R.drawable.image_placeholder)
                                },
                                contentDescription = "College Image",
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                                    .clickable { launcher.launch("image/*") }
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // Reset form fields to current fetched data or empty
                                        collegeInfoData?.let { info ->
                                            name = info.name ?: ""
                                            address = info.address ?: ""
                                            phone = info.phone ?: ""
                                            email = info.email ?: ""
                                            description = info.description ?: ""
                                            websiteLink = info.websiteLink ?: ""
                                            currentImageUrl = info.imageUrl // Restore previous image URL
                                        } ?: run { // If no data, clear all fields
                                            name = ""
                                            address = ""
                                            phone = ""
                                            email = ""
                                            description = ""
                                            websiteLink = ""
                                            currentImageUrl = null
                                        }
                                        imageUri = null // Clear any newly selected image
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                ) {
                                    Text(text = "Reset")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Update College Info Button
                    Button(
                        onClick = {
                            if (name.isBlank() || address.isBlank() || phone.isBlank() || email.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please fill all required fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Determine the final URI to use for saving:
                                // 1. Use imageUri if a new image was selected.
                                // 2. Otherwise, convert currentImageUrl to a Uri if it exists.
                                val finalUri: Uri? = imageUri ?: if (!currentImageUrl.isNullOrEmpty()) {
                                    try {
                                        Uri.parse(currentImageUrl)
                                    } catch (e: Exception) {
                                        Log.e("ManageCollegeInfo", "Invalid currentImageUrl: $currentImageUrl", e)
                                        null
                                    }
                                } else {
                                    null
                                }

                                if (finalUri != null) {
                                    isSaving = true // Show saving indicator
                                    collegeInfoViewModel.saveCollegeInfo(
                                        uri = finalUri,
                                        name = name,
                                        address = address,
                                        phone = phone,
                                        email = email,
                                        description = description,
                                        websiteLink = websiteLink
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please provide an image for the college",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        enabled = !isSaving // Disable button while saving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "Update College Info")
                        }
                    }
                }
            }
        }
    }
}