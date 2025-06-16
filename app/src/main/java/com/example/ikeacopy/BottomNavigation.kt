package com.example.ikeacopy

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Дом")
    object Bookmarks : BottomNavItem("bookmarks", Icons.Default.Favorite, "Закладки")
    object Cart : BottomNavItem("cart", Icons.Default.ShoppingCart, "Корзина")
    object Notifications : BottomNavItem("notifications", Icons.Default.Notifications, "Уведомления")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Профиль")
}

@Composable
fun BottomNavigation(
    navController: NavController,
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Bookmarks,
        BottomNavItem.Cart,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}