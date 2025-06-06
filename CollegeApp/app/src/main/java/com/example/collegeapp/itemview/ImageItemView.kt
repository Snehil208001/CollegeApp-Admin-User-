package com.example.collegeapp.itemview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon // Explicitly import Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Import Color for tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.R // Make sure R is correctly imported for your placeholder
import com.example.collegeapp.utils.Constant // Import Constant object

/**
 * Displays a single image card with a delete icon on the top-right.
 * @param imageUrl The URL of the image to display.
 * @param delete A lambda to call when the delete icon is clicked, receiving the image URL.
 */
@Composable
fun ImageItemView(
    imageUrl: String,
    delete: (image: String) -> Unit // This lambda takes the imageUrl to delete
) {
    OutlinedCard(modifier = Modifier.padding(4.dp)) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) { // fillMaxWidth for ConstraintLayout
            val (imageRef, deleteBtnRef) = createRefs()

            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Gallery Image", // Added content description for accessibility
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .constrainAs(imageRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Crop
            )
            // Show delete icon only if the user is an admin
            if (Constant.isAdmin) // Use Constant.isAdmin
                Card( // Consider adding a background to this card for better visibility of the icon
                    modifier = Modifier
                        .padding(4.dp)
                        .constrainAs(deleteBtnRef) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                        .clickable { delete(imageUrl) } // Call delete with imageUrl
                ) {
                    // Using Material3 Icon for consistency and better tinting
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24), // Assuming this drawable exists
                        contentDescription = "Delete Image",
                        modifier = Modifier.padding(8.dp), // Padding inside the icon's card
                        tint = Color.Red // Make delete icon clearly visible
                    )
                }
        }
    }
}