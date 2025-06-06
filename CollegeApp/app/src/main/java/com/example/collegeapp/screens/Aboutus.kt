package com.example.collegeapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize // For filling screen vertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState // For verticalScroll
import androidx.compose.foundation.verticalScroll // For verticalScroll
import androidx.compose.material3.CircularProgressIndicator // For loading indicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme // For styling loading text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // Crucial for data fetching
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment // For aligning loading indicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale // For image scaling
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // For text alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter // For loading images
import com.example.collegeapp.viewmodel.CollegeInfoViewModel

// Ensure your CollegeInfoModel.kt (used by CollegeInfoViewModel) has these fields:
// data class CollegeInfoModel(
//     val imageUrl: String? = null,
//     val name: String? = null,
//     val address: String? = null,
//     val phone: String? = null,
//     val email: String? = null,
//     val description: String? = null,
//     val websiteLink: String? = null
// )

@Composable
fun AboutUs() {
    val collegeInfoViewModel: CollegeInfoViewModel = viewModel()
    val collegeInfoData by collegeInfoViewModel.collegeInfo.observeAsState(null)

    // FIX 1: Call getCollegeInfo() inside LaunchedEffect(Unit)
    // This ensures data is fetched only once when the Composable is first displayed.
    LaunchedEffect(Unit) {
        collegeInfoViewModel.getCollegeInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize() // Make column fill max size
            .verticalScroll(rememberScrollState()) // Make content scrollable
            .padding(16.dp), // Apply padding to the entire column
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
    ) {
        // Display loading indicator if data is null (still fetching)
        if (collegeInfoData == null) {
            // You might want a custom loading state from ViewModel for finer control
            // For now, this assumes null means loading.
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 64.dp) // Add some space from top
                    .height(48.dp)
            )
            Text(
                text = "Loading College Information...",
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else {
            // Data is available, display it
            collegeInfoData?.let { info -> // Use let for safe access, though null check already performed
                // College Image
                Image(
                    painter = rememberAsyncImagePainter(model = info.imageUrl), // FIX 2: Access imageUrl from info
                    contentDescription = "College Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Fixed height for college image
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop // Scale image to fill bounds
                )

                Text(
                    text = "College Details",
                    fontSize = 24.sp, // Slightly larger for prominence
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // FIX 3: Access values from 'info' object and make fields read-only
                OutlinedTextField(
                    value = info.name ?: "", // Use 'info.name' and provide default empty string for null
                    onValueChange = { }, // FIX 4: Make read-only, so no value change
                    label = { Text(text = "College Name") },
                    readOnly = true, // FIX 5: Make text field read-only
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = info.address ?: "",
                    onValueChange = { },
                    label = { Text(text = "Address") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = info.phone ?: "",
                    onValueChange = { },
                    label = { Text(text = "Phone Number") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = info.email ?: "",
                    onValueChange = { },
                    label = { Text(text = "Email") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = info.description ?: "",
                    onValueChange = { },
                    label = { Text(text = "Description") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp) // Maintain specific height
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = info.websiteLink ?: "",
                    onValueChange = { },
                    label = { Text(text = "Website Link") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}