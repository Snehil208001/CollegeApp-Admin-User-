package com.example.collegeapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.collegeapp.R
import com.example.collegeapp.models.BottomNavItem
import com.example.collegeapp.models.NavItem
import com.example.collegeapp.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNav(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val navController1 = rememberNavController()

    val list = listOf(
        NavItem("Website", R.drawable.globe),
        NavItem("Notice", R.drawable.notice),
        NavItem("Notes", R.drawable.notes),
        NavItem("Contact Us", R.drawable.customer_service)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Image(
                    painter = painterResource(id = R.drawable.college_app),
                    contentDescription = null,
                    modifier = Modifier.height(220.dp),
                    contentScale = ContentScale.Crop
                )
                Divider()
                Text(text = "")
                list.forEachIndexed { index, items ->
                    NavigationDrawerItem(
                        label = { Text(text = items.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            Toast.makeText(context, items.title, Toast.LENGTH_SHORT).show()
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Image(
                                painter = painterResource(id = items.icon),
                                contentDescription = null,
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "College App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.list),
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            bottomBar = {
                MyBottomNav(navController = navController1)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController1,
                startDestination = Routes.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Routes.Home.route) {
                    Home()
                }
                composable(Routes.Gallery.route) {
                    Gallery()
                }
                composable(Routes.AboutUs.route) {
                    AboutUs()
                }
                composable(Routes.Faculty.route) {
                    Faculty(navController)
                }
            }
        }
    }
}

@Composable
fun MyBottomNav(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val list = listOf(
        BottomNavItem("Home", R.drawable.home, Routes.Home.route),
        BottomNavItem("Faculty", R.drawable.graduate, Routes.Faculty.route),
        BottomNavItem("Gallery", R.drawable.image_gallery_1, Routes.Gallery.route),
        BottomNavItem("About Us", R.drawable.info, Routes.AboutUs.route)
    )

    BottomAppBar {
        list.forEach { item ->
            val currentRoute = backStackEntry?.destination?.route
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(25.dp)
                    )
                }
            )
        }
    }
}
