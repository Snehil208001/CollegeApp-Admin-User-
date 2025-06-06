package com.example.collegeapp.admin.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.itemview.BannerItemView
import com.example.collegeapp.ui.theme.Purple40
import com.example.collegeapp.utils.Constant.BANNER
import com.example.collegeapp.viewmodel.BannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBanner(navController: NavController) {

    val context = LocalContext.current

    val bannerViewModel: BannerViewModel = viewModel()
    val isUploaded by bannerViewModel.isPosted.observeAsState(false)
    val bannerList by bannerViewModel.bannerList.observeAsState(emptyList())
    val isDeleted by bannerViewModel.isDeleted.observeAsState(false)

    LaunchedEffect(Unit) {
        bannerViewModel.getBanner()
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        imageUri = it
    }

    LaunchedEffect(isUploaded) {
        if (isUploaded) {
            Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show()
            imageUri = null
            bannerViewModel.getBanner()
        }
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            Toast.makeText(context, "Image Deleted", Toast.LENGTH_SHORT).show()
            bannerViewModel.getBanner()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage Banner", color = Color.White) },
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
            FloatingActionButton(onClick = { launcher.launch("image/*") }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Banner Image",
                    tint = Color.Black
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            imageUri?.let { uri ->
                ElevatedCard(modifier = Modifier.padding(8.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = BANNER,
                        modifier = Modifier
                            .height(220.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )

                    Row {
                        Button(
                            onClick = { bannerViewModel.saveImage(uri) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(text = "Add Image")
                        }
                        OutlinedButton(
                            onClick = { imageUri = null },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                }
            }

            LazyColumn {
                items(bannerList ?: emptyList()) { bannerModel ->
                    BannerItemView(bannerModel = bannerModel, delete = { model ->
                        bannerViewModel.deleteBanner(model)
                    })
                }
            }
        }
    }
}
