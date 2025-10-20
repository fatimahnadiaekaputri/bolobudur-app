package com.example.bolobudur.ui.navigation

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bolobudur.ui.screen.login.LoginScreen
import com.example.bolobudur.ui.screen.register.RegisterScreen
import com.example.bolobudur.ui.screen.home.BorobudurpediaScreen
import com.example.bolobudur.ui.screen.home.HomeScreen
import com.example.bolobudur.ui.screen.home.ProfileScreen
import com.example.bolobudur.ui.screen.splash.SplashScreen
import com.example.bolobudur.ui.screen.splash.SplashViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.ui.screen.bolofind.BolofindScreen
import com.example.bolobudur.ui.screen.bolomaps.BolomapsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.systemBarsPadding()
    ) {
        // Splash Screen
        composable("splash") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("splash")
            }
            val splashViewModel: SplashViewModel = hiltViewModel(parentEntry)

            SplashScreen(
                viewModel = splashViewModel,
                navController = navController
            )
        }

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Borobudurpedia.route) {
            BorobudurpediaScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable("detail/{featureId}") { backStackEntry ->
            val featureId = backStackEntry.arguments?.getString("featureId")?.toIntOrNull()

            when (featureId) {
                1 -> BolomapsScreen(navController = navController)
                2 -> BolofindScreen(navController = navController)
            }
        }



    }
}
