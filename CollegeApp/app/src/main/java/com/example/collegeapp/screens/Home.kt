package com.example.collegeapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
// Coil Imports
import coil.compose.rememberAsyncImagePainter

import com.example.collegeapp.viewmodel.BannerViewModel
import com.example.collegeapp.viewmodel.CollegeInfoViewModel
import com.example.collegeapp.viewmodel.NoticeViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Home() {
    val bannerViewModel: BannerViewModel = viewModel()
    val bannerList by bannerViewModel.bannerList.observeAsState(emptyList())
    // Trigger data fetch for banners
    LaunchedEffect(Unit) { // Use LaunchedEffect to call getBanner once
        bannerViewModel.getBanner()
    }

    val collegeInfoViewModel: CollegeInfoViewModel = viewModel()
    val collegeInfoData by collegeInfoViewModel.collegeInfo.observeAsState(null)
    // Trigger data fetch for college info
    LaunchedEffect(Unit) { // Use LaunchedEffect to call getCollegeInfo once
        collegeInfoViewModel.getCollegeInfo()
    }

    val noticeViewModel: NoticeViewModel = viewModel()
    val noticeList by noticeViewModel.noticeList.observeAsState(emptyList())
    // Trigger data fetch for notices
    LaunchedEffect(Unit) { // Use LaunchedEffect to call getNotice once
        noticeViewModel.getNotice()
    }

    val pagerState = rememberPagerState(initialPage = 0)

    // Store only URLs here, painters will be created in the Composable scope
    val imageUrls = remember(bannerList) {
        bannerList.map { it.url }
    }

    // Auto-scrolling effect for the banner pager
    LaunchedEffect(imageUrls.size) { // Re-launch effect if the number of images changes
        if (imageUrls.isNotEmpty()) {
            try {
                while (true) {
                    yield() // Allow other coroutines to run
                    delay(2600) // Delay for 2.6 seconds
                    // Calculate the next page, ensuring it wraps around
                    val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                    pagerState.animateScrollToPage(page = nextPage)
                }
            } catch (e: Exception) {
                // This catch block handles cancellations (e.g., when the composable leaves the screen)
                // and other unexpected exceptions in the coroutine.
                // Log the exception if needed for debugging.
                // println("Banner auto-scroll interrupted: ${e.message}")
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Apply overall padding to the LazyColumn
    ) {
        //---
        // Banner Slider Section
        item {
            if (imageUrls.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalPager(
                        count = imageUrls.size,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp) // Maintain your specified height for the pager
                            .padding(vertical = 4.dp) // Padding around the pager itself
                    ) { pagerIndex ->
                        val painter = rememberAsyncImagePainter(model = imageUrls[pagerIndex])
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp) // Card should match pager height
                                .padding(4.dp), // Padding around each individual card
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "Banner Image",
                                contentScale = ContentScale.Crop, // Crop to fill the bounds
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp) // Image should fill card height
                            )
                        }
                    }

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier.padding(vertical = 8.dp),
                        activeColor = MaterialTheme.colorScheme.primary // Use theme's primary color
                    )
                }
            } else {
                // Placeholder when banners are loading or empty
                Text(
                    text = "Loading Banners...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Keep height consistent with the banner
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color for text
                )
            }
        }

        //---
        // College Information Section
        item {
            // Add some padding to separate from the banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "College Information",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface, // Use theme color
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                collegeInfoData?.let { info ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Ensure null checks for properties
                            Text(
                                text = info.name ?: "N/A", // Default to "N/A" if null
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = info.address ?: "Address not available",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = info.description ?: "No description available",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = info.phone ?: "Phone not available",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = info.email ?: "Email not available",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } ?: run {
                    // Placeholder when college info is loading or empty
                    Text(
                        text = "Loading College Info...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        //---
        // Notices Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Latest Notices",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        if (noticeList.isNotEmpty()) {
            items(noticeList) { notice ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Show notice image if available
                        notice.imageUrl?.let { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUrl),
                                contentDescription = "Notice Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            text = notice.title ?: "Untitled Notice",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        // Clickable or styled link
                        Text(
                            text = notice.link ?: "No link available",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

        } else {
            item {
                // Placeholder when no notices are available
                Text(
                    text = "No notices available.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}