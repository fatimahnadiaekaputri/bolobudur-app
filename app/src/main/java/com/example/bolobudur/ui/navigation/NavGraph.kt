package com.example.bolobudur.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bolobudur.ui.screen.login.LoginScreen
import com.example.bolobudur.ui.screen.register.RegisterScreen
import com.example.bolobudur.ui.screen.borobudurpedia.BorobudurpediaScreen
import com.example.bolobudur.ui.screen.home.HomeScreen
import com.example.bolobudur.ui.screen.profile.ProfileScreen
import com.example.bolobudur.ui.screen.splash.SplashScreen
import com.example.bolobudur.ui.screen.splash.SplashViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.ui.screen.bolofind.BolofindScreen
import com.example.bolobudur.ui.screen.bolomaps.BolomapsScreen
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel
import com.example.bolobudur.ui.screen.bolomaps.maps.MapViewModel
import com.example.bolobudur.ui.screen.borobudurpedia.CategoryScreen
import com.example.bolobudur.ui.screen.borobudurpedia.CulturalSiteScreen
import com.example.bolobudur.ui.screen.profile.UpdateProfileScreen


@RequiresApi(Build.VERSION_CODES.S)
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
            ProfileScreen(
                navController = navController,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("updateProfile") {
            UpdateProfileScreen(
                onProfileUpdated = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }


        composable("detail/{featureId}") { backStackEntry ->
            val featureId = backStackEntry.arguments?.getString("featureId")?.toIntOrNull()

            when (featureId) {
                1 -> BolomapsScreen(navController = navController)
                2 -> BolofindScreen(navController = navController)
            }
        }

        composable(
            route = "bolomaps/{toLat}/{toLon}/{label}",
        ) { backStackEntry ->
            val toLat = backStackEntry.arguments?.getString("toLat")?.toDoubleOrNull() ?: 0.0
            val toLon = backStackEntry.arguments?.getString("toLon")?.toDoubleOrNull() ?: 0.0
            val label = backStackEntry.arguments?.getString("label") ?: ""

            val mapViewModel: MapViewModel = hiltViewModel()
            val navigationViewModel: NavigationViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                val currentPos = navigationViewModel.currentPosition.value
                val fromLat = currentPos?.latitude() ?: 0.0
                val fromLon = currentPos?.longitude() ?: 0.0

                mapViewModel.getShortestPath(fromLat, fromLon, toLat, toLon, label)
            }

            BolomapsScreen(
                navController = navController,
                viewModel = mapViewModel,
                navigationViewModel = navigationViewModel
            )
        }

        composable("cultural-site") {
            CulturalSiteScreen(navController = navController)
        }

        composable(
            route = "category/{categoryName}"
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Kategori"
            CategoryScreen(
                navController = navController,
                categoryName = categoryName
            )
        }




    }
}
