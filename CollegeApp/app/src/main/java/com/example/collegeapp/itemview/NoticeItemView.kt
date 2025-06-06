// File: com/example/collegeapp/itemview/NoticeItemView.kt

package com.example.collegeapp.itemview

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.collegeapp.R
import com.example.collegeapp.models.NoticeModel
import com.example.collegeapp.ui.theme.SkyBlue

@SuppressLint("SuspiciousIndentation")
@Composable
fun NoticeItemView(
    noticeModel: NoticeModel,
    delete: (NoticeModel) -> Unit
) {
    OutlinedCard(modifier = Modifier.padding(4.dp)) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (imageRef, deleteBtnRef, textColumnRef) = createRefs()

            // Title + Link column
            Column(
                modifier = Modifier
                    .constrainAs(textColumnRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(8.dp)

            ) {
                if (noticeModel.link!="")
                Text(
                    text = noticeModel.title.orEmpty(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = noticeModel.link.orEmpty(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = SkyBlue,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Image below the text
            Image(
                painter = rememberAsyncImagePainter(model = noticeModel.imageUrl),
                contentDescription = "Notice Image",
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .padding(top = 60.dp)
                    .constrainAs(imageRef) {
                        top.linkTo(textColumnRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Crop
            )

            // Delete button (icon) overlaid in top‐right
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .constrainAs(deleteBtnRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        delete(noticeModel)
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Delete Notice",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
