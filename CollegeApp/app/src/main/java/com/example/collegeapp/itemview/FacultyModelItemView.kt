package com.example.collegeapp.itemview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.models.FacultyModel
import com.example.collegeapp.utils.Constant.isAdmin

// This Composable will display individual faculty members (teachers)
@Composable
fun FacultyModelItemView(
    facultyModel: FacultyModel,
    onDelete: (FacultyModel) -> Unit, // onDelete takes the full FacultyModel
    onClick: (FacultyModel) -> Unit // onClick also takes the full FacultyModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick(facultyModel) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Faculty Image (if available)
            Image(
                painter = rememberAsyncImagePainter(model = facultyModel.imageUrl),
                contentDescription = "Faculty Image",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = facultyModel.name.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = facultyModel.position.toString(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = facultyModel.department,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Delete button for faculty member (only if admin)
            if (isAdmin) {
                IconButton(onClick = { onDelete(facultyModel) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Faculty",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}