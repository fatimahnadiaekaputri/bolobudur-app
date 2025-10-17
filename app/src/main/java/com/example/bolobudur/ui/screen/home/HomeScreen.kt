package com.example.bolobudur.ui.screen.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.components.SearchBar
import com.example.bolobudur.ui.screen.home.components.GreetingSection
import com.example.bolobudur.ui.screen.home.components.SectionTitle

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                GreetingSection(userName = uiState.userName)
                Spacer(Modifier.height(8.dp))
                SearchBar(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange
                )
                Spacer(Modifier.height(16.dp))
                SectionTitle(title = "Selamat Datang,")
                Text(
                    text = "Apa yang ingin kamu ketahui?",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(16.dp))
            }

            items(uiState.features) { feature ->
                FeatureCard(feature = feature, navController = navController, onCardClick = {
                    navController.navigate("detail/${feature.id}")
                },
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.3f))
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
