// File: com/example/collegeapp/admin/screens/ManageNotice.kt

package com.example.collegeapp.admin.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.R
import com.example.collegeapp.itemview.NoticeItemView
import com.example.collegeapp.ui.theme.Purple40
import com.example.collegeapp.utils.Constant.NOTICE
import com.example.collegeapp.viewmodel.NoticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageNotice(navController: NavController) {
    val context = LocalContext.current

    // Obtain the ViewModel
    val noticeViewModel: NoticeViewModel = viewModel()

    // Observe LiveData flags and lists
    val isUploaded by noticeViewModel.isPosted.observeAsState(false)
    val noticeList by noticeViewModel.noticeList.observeAsState(emptyList())
    val isDeleted by noticeViewModel.isDeleted.observeAsState(false)

    // Fetch all existing notices when this screen first appears
    LaunchedEffect(Unit) {
        noticeViewModel.getNotice()
    }

    // Local state for the “add notice” form
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isNotice by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    // ActivityResultLauncher to pick an image from gallery
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { pickedUri ->
        imageUri = pickedUri
    }

    // Show a Toast + reset form when upload finishes successfully
    LaunchedEffect(isUploaded) {
        if (isUploaded) {
            Toast.makeText(context, "Notice Uploaded", Toast.LENGTH_SHORT).show()
            imageUri = null
            title = ""
            link = ""
            isNotice = false
            noticeViewModel.getNotice()
        }
    }

    // Show a Toast + refresh list when a delete finishes
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            Toast.makeText(context, "Notice Deleted", Toast.LENGTH_SHORT).show()
            noticeViewModel.getNotice()
            imageUri = null
            isNotice = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage Notice", color = Color.White) },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Purple40),
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isNotice = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Notice",
                    tint = Color.Black
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // Show the “Add Notice” card when the FAB is tapped or after picking an image
            if (isNotice || imageUri != null) {
                ElevatedCard(modifier = Modifier.padding(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text(text = "Notice title ...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )

                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        placeholder = { Text(text = "Notice link ...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )

                    // Tappable placeholder or chosen image
                    Image(
                        painter = if (imageUri == null)
                            painterResource(id = R.drawable.image_placeholder)
                        else
                            rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Notice Image",
                        modifier = Modifier
                            .height(220.dp)
                            .fillMaxWidth()
                            .clickable { launcher.launch("image/*") }
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )

                    Row {
                        Button(
                            onClick = {
                                if (imageUri == null ) {
                                    Toast.makeText(
                                        context,
                                        "Please provide image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else if (title == ""){
                                    Toast.makeText(
                                        context,
                                        "Please provide title",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    imageUri?.let { uri ->
                                        noticeViewModel.saveNotice(uri, title, link)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(text = "Add Notice")
                        }

                        OutlinedButton(
                            onClick = {
                                imageUri = null
                                isNotice = false
                                title = ""
                                link = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                }
            }

            // Show the existing notices in a vertical list
            LazyColumn {
                items(items = noticeList) { noticeModel ->
                    NoticeItemView(
                        noticeModel = noticeModel,
                        delete = { modelToDelete ->
                            noticeViewModel.deleteNotice(modelToDelete)
                        }
                    )
                }
            }
        }
    }
}
