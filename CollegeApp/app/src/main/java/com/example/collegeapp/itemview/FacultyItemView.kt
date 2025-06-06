package com.example.collegeapp.itemview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.collegeapp.R // Make sure R.drawable.baseline_delete_24 exists
import com.example.collegeapp.utils.Constant.isAdmin

/**
 * A reusable composable that shows a single "category" entry in a card.
 * - When you tap the card area (excluding delete icon), calls [onClick].
 * - When you tap the delete icon, calls [delete].
 */
@Composable
fun FacultyItemView(
    category: String, // This expects a String, which matches ViewModel's categoryList
    delete: (category: String) -> Unit,
    onClick: (category: String) -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onClick(category) }
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (categoryTextRef, deleteBtnRef) = createRefs()

            Text(
                text = category,
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
            // The delete button is only shown if isAdmin is true
            if (isAdmin)
                Image(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Delete Category",
                    modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(deleteBtnRef) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .clickable { delete(category) }
                )
        }
    }
}