package com.example.agricommend

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.agricommend.adminPage.AddCropScreen
import com.example.agricommend.adminPage.AdminDashboardScreen
import com.example.agricommend.adminPage.AdminLoginScreen
import com.example.agricommend.adminPage.DeleteCropScreen
import com.example.agricommend.adminPage.UpdateCropScreen
import com.example.agricommend.userPage.FormsScreen
import com.example.agricommend.userPage.LoginScreen
import com.example.agricommend.userPage.ResultScreen
import com.example.agricommend.userPage.SearchPage
import com.example.agricommend.userPage.SignUpScreen
import com.example.agricommend.userPage.UserDashboardScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "initial_screen") {
        composable("initial_screen") {
            InitialScreen(navController = navController)
        }

        composable("login_screen") {
            LoginScreen(navController = navController)
        }

        composable("signup_screen") {
            SignUpScreen(navController = navController)
        }

        composable("admin_login_screen") {
            AdminLoginScreen(navController = navController)
        }

        composable("admin_dashboard_screen") {
            AdminDashboardScreen(navController = navController)
        }

        composable("user_dashboard_screen") {
            UserDashboardScreen(navController = navController)
        }

        composable("search_page_screen") {
            SearchPage(navController = navController)
        }

        composable(
            "forms_screen/{cropId}/{cropName}",
            arguments = listOf(
                navArgument("cropId") { type = NavType.StringType },
                navArgument("cropName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cropName = backStackEntry.arguments?.getString("cropName") ?: ""
            FormsScreen(navController = navController, cropName = cropName)
        }

        composable(
            "result_screen/{cropName}/{weeksToGrow}/{hasWeed}",
            arguments = listOf(
                navArgument("cropName") { type = NavType.StringType },
                navArgument("weeksToGrow") { type = NavType.IntType },
                navArgument("hasWeed") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val cropName = backStackEntry.arguments?.getString("cropName") ?: ""
            val weeksToGrow = backStackEntry.arguments?.getInt("weeksToGrow") ?: 0
            val hasWeed = backStackEntry.arguments?.getBoolean("hasWeed") ?: false
            ResultScreen(
                navController = navController,
                cropName = cropName,
                weeksToGrow = weeksToGrow,
                hasWeed = hasWeed
            )
        }

        composable("update_crop_screen") {
            UpdateCropScreen(navController = navController)
        }

        composable("delete_crop_screen") {
            DeleteCropScreen(navController = navController)
        }

        composable("add_crop_screen") {
            AddCropScreen(navController = navController)
        }

        composable(
            "forgot_password_screen/{isAdmin}",
            arguments = listOf(navArgument("isAdmin") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false
            ForgotPasswordScreen(navController = navController, isAdmin = isAdmin)
        }
    }
}
