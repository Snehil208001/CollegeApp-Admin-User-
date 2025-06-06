package com.example.collegeapp.itemview

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.collegeapp.R
import com.example.collegeapp.models.GalleryModel
import com.example.collegeapp.utils.Constant // Import Constant object

/**
 * Displays a category card with a delete icon and its image grid.
 * @param galleryModel The GalleryModel object containing category and images.
 * @param delete A lambda to call when the category delete icon is clicked, receiving the GalleryModel.
 * @param deleteImage A lambda to call when an image's delete icon is clicked, receiving imageUrl and category.
 * @param onClick A lambda to call when the category card itself (excluding delete icon) is clicked, receiving the category name.
 */
@Composable
fun GalleryItemView(
    galleryModel: GalleryModel,
    delete: (galleryModel: GalleryModel) -> Unit, // Delete category: takes the whole model
    deleteImage: (imageUrl: String, category: String) -> Unit, // Delete image: takes URL and category
    onClick: (category: String) -> Unit // Click on category card: takes category name
) {
    val context = LocalContext.current

    OutlinedCard(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onClick(galleryModel.category) } // Card click handler
    ) {
        Column {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (categoryTextRef, deleteBtnRef) = createRefs()

                Text(
                    text = galleryModel.category,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .constrainAs(categoryTextRef) {
                            start.linkTo(parent.start)
                            end.linkTo(deleteBtnRef.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                // Show delete icon for category only if user is admin
                if (Constant.isAdmin) // Use Constant.isAdmin
                    Image( // Using Image as per your original code
                        painter = painterResource(id = R.drawable.baseline_delete_24), // Assuming this drawable exists
                        contentDescription = "Delete Category",
                        modifier = Modifier
                            .padding(8.dp)
                            .constrainAs(deleteBtnRef) {
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                            .clickable { // Click handler for category delete icon
                                delete(galleryModel) // Pass the entire galleryModel for deletion
                                Toast.makeText(context, "Deleting category: ${galleryModel.category}", Toast.LENGTH_SHORT).show()
                            }
                    )
            }

            // Display images in a grid if available
            val imagesList = galleryModel.images ?: emptyList()
            if (imagesList.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    modifier = Modifier
                        .height(200.dp) // Fixed height to prevent infinite expansion in a scrollable column
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items(imagesList) { imageUrl ->
                        // Pass image URL and category name to ImageItemView's delete lambda
                        ImageItemView(imageUrl = imageUrl) {
                            deleteImage(imageUrl, galleryModel.category) // Pass imageUrl and current category name
                        }
                    }
                }
            } else {
                Text(
                    text = "No images in this category.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}