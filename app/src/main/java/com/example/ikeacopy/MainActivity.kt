package com.example.ikeacopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ikeacopy.Data.AppDataBase
import com.example.ikeacopy.Screen.*
import com.example.ikeacopy.ViewModel.AppViewModel
import com.example.ikeacopy.ui.theme.IkeaCopyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IkeaCopyTheme {
                IKEAApp()
            }
        }
    }
}

@Composable
fun IKEAApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = remember { AppDataBase.getDatabase(context) }
    val repository = remember { Repository(db.userDao(), db.productDao()) }
    val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(repository))

    NavHost(navController = navController, startDestination = "sign_in") {
        composable("sign_in") {
            SignInScreen(
                viewModel = viewModel,
                onSignInSuccess = { navController.navigate("home") },
                onNavigateToSignUp = { navController.navigate("sign_up") }
            )
        }
        composable("sign_up") {
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = { navController.navigate("home") },
                onNavigateToSignIn = { navController.navigate("sign_in") }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("bookmarks") {
            BookmarksScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("notifications") {
            NotificationsScreen(navController)
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                viewModel = viewModel,
                onLogout = {
                    viewModel.logout()
                    navController.navigate("sign_in") {
                        popUpTo(0)
                    }
                }
            )
        }
        composable("cart") {
            CartScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 0
            ProductScreen(
                productId = productId,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

class AppViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

