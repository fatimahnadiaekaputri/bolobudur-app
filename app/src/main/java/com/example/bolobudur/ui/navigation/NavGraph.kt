package com.example.bolobudur.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bolobudur.ui.screen.splash.SplashScreen
import com.example.bolobudur.ui.screen.splash.SplashViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
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

        // Home Screen (contoh, kalau nanti ada HomeViewModel)
        composable("home") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            // Contoh kalau pakai HiltViewModel
            // val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)

            // TODO: ganti dengan HomeScreen(viewModel = homeViewModel)
            // sementara pake placeholder:
            androidx.compose.material3.Text("Hello from Home!")
        }
    }
}
