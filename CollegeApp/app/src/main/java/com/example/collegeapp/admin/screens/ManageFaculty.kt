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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.itemview.FacultyItemView
import com.example.collegeapp.ui.theme.Purple40
import com.example.collegeapp.viewmodel.FacultyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFacultyScreen(navController: NavController) {
    val context = LocalContext.current
    val facultyViewModel: FacultyViewModel = viewModel()

    val isUploaded by facultyViewModel.isPosted.observeAsState(false)
    val isDeleted by facultyViewModel.isDeleted.observeAsState(false)
    val categoryList by facultyViewModel.categoryList.observeAsState(emptyList())
    val teacherList by facultyViewModel.facultyList.observeAsState(emptyList())

    val branches = listOf(
        "Computer Science",
        "Mechanical Engineering",
        "Electrical Engineering",
        "Civil Engineering",
        "Electronics & Communication",
        "Chemical Engineering",
        "Biomedical Engineering",
        "Aerospace Engineering",
        "Industrial Engineering",
        "Materials Science & Engineering"
    )

    LaunchedEffect(Unit) {
        facultyViewModel.getCategory()
        facultyViewModel.getFaculty()
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isCategoryForm by remember { mutableStateOf(true) } // default to category form
    var isTeacherForm by remember { mutableStateOf(false) }

    // Fields for Category
    var category by remember { mutableStateOf("") }

    // Fields for Teacher
    var teacherName by remember { mutableStateOf("") }
    var teacherEmail by remember { mutableStateOf("") }
    var teacherPosition by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("") }
    var expandedDept by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { pickedUri ->
        imageUri = pickedUri
    }

    LaunchedEffect(isUploaded) {
        if (isUploaded) {
            Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show()
            // Reset all forms
            category = ""
            teacherName = ""
            teacherEmail = ""
            teacherPosition = ""
            selectedDept = ""
            imageUri = null
            isLoading = false
            facultyViewModel.getCategory()
            facultyViewModel.getFaculty()
        }
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            isLoading = false
            facultyViewModel.getCategory()
            facultyViewModel.getFaculty()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage Faculty", color = Color.White) },
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
            Column(modifier = Modifier.padding(paddingValues)) {
                // Toggle buttons
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
                                isCategoryForm = true
                                isTeacherForm = false
                            },
                        // Default Material3 Card color will be used here
                    ) {
                        Text(
                            text = "Add Category",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Black // <--- Changed this line: always Black
                        )
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                isCategoryForm = false
                                isTeacherForm = true
                            },
                        // Default Material3 Card color will be used here
                    ) {
                        Text(
                            text = "Add Teacher",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Black // <--- Changed this line: always Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category form
                if (isCategoryForm) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                placeholder = { Text(text = "Category name...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (category.isBlank()) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a category",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            isLoading = true
                                            facultyViewModel.uploadCategoryToFirestore(category)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Add Category")
                                }
                                OutlinedButton(
                                    onClick = {
                                        category = ""
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Cancel")
                                }
                            }
                        }
                    }
                }

                // Teacher form
                if (isTeacherForm) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            OutlinedTextField(
                                value = teacherName,
                                onValueChange = { teacherName = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = teacherEmail,
                                onValueChange = { teacherEmail = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = teacherPosition,
                                onValueChange = { teacherPosition = it },
                                label = { Text("Position") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Dropdown for Department
                            ExposedDropdownMenuBox(
                                expanded = expandedDept,
                                onExpandedChange = { expandedDept = !expandedDept },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = selectedDept,
                                    onValueChange = {},
                                    label = { Text("Department") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDept)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedDept,
                                    onDismissRequest = { expandedDept = false }
                                ) {
                                    branches.forEach { branch ->
                                        DropdownMenuItem(
                                            text = { Text(branch) },
                                            onClick = {
                                                selectedDept = branch
                                                expandedDept = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Image picker button & preview
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(onClick = {
                                    launcher.launch("image/*")
                                }) {
                                    Text(text = "Pick Image")
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                if (imageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imageUri),
                                        contentDescription = "Selected Image",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // Validation
                                        when {
                                            teacherName.isBlank() -> Toast.makeText(
                                                context,
                                                "Please enter name",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            teacherEmail.isBlank() -> Toast.makeText(
                                                context,
                                                "Please enter email",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            teacherPosition.isBlank() -> Toast.makeText(
                                                context,
                                                "Please enter position",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            selectedDept.isBlank() -> Toast.makeText(
                                                context,
                                                "Please select department",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            imageUri == null -> Toast.makeText(
                                                context,
                                                "Please pick an image",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            else -> {
                                                isLoading = true
                                                facultyViewModel.saveFaculty(
                                                    uri = imageUri!!,
                                                    name = teacherName,
                                                    email = teacherEmail,
                                                    position = teacherPosition,
                                                    department = selectedDept
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Add Teacher")
                                }
                                OutlinedButton(
                                    onClick = {
                                        teacherName = ""
                                        teacherEmail = ""
                                        teacherPosition = ""
                                        selectedDept = ""
                                        imageUri = null
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Cancel")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List categories
                Text(
                    text = "Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                if (categoryList.isEmpty() && !isLoading) {
                    Text(
                        text = "No categories added yet.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(180.dp)
                    ) {
                        items(items = categoryList) { categoryName ->
                            FacultyItemView(
                                category = categoryName,
                                delete = { cat ->
                                    isLoading = true
                                    facultyViewModel.deleteCategory(cat)
                                },
                                onClick = { cat ->
                                    navController.navigate("faculty_details/$cat")
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List teachers
                Text(
                    text = "Teachers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                if (teacherList.isEmpty() && !isLoading) {
                    Text(
                        text = "No teachers added yet.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(items = teacherList) { faculty ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = faculty.imageUrl),
                                            contentDescription = faculty.name,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = faculty.name.orEmpty(),
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "${faculty.department} - ${faculty.position}",
                                                fontSize = 14.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                    IconButton(onClick = {
                                        isLoading = true
                                        facultyViewModel.deleteFaculty(faculty)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Teacher",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

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