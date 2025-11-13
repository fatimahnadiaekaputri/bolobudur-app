package com.example.bolobudur.ui.screen.bolofind

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.model.FeatureData
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolofindScreen(
    navController: NavController,
    viewModel: BoloFindViewModel = hiltViewModel(),
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val features by viewModel.features.collectAsState()
    val zoneName by viewModel.zoneName.collectAsState()
    val currentPos by navigationViewModel.currentPosition.collectAsState()

    LaunchedEffect(currentPos) {
        currentPos?.let {
            viewModel.loadNearby(
                lat = it.latitude(),
                lon = it.longitude()
            )
        }
    }


    Scaffold(
        topBar = { TopBar(title = "BoloFind", navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // --- Lokasi user ---
            Text(
                text = "Kamu sedang berada di",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(Icons.Outlined.Place, contentDescription = "Location")
                Text(
                    text = zoneName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // --- Nearby items ---
            Text(
                text = "Lihat apa yang ada di sekitar kamu",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(features) { feature ->
                    val featureData = FeatureData(
                        id = feature.id, // ✅ pakai feature.id, bukan dari properties
                        title = feature.properties.label,
                        description = feature.properties.culturalSite.description
                            ?.split(" ")
                            ?.take(50)
                            ?.joinToString(" ")
                            ?: "Tidak ada deskripsi.",
                        imageUrl = feature.properties.culturalSite.imageUrl
                    )

                    FeatureCard(
                        feature = featureData,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
