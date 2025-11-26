package com.example.bolobudur.ui.screen.bolofind

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.DefaultPopup
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.model.FeatureData
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Search

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

    var showUnknownAreaDialog by remember { mutableStateOf(false) }



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

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

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
                        id = feature.id,
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.3f),
                        onCardClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                set("title", feature.properties.culturalSite.name)
                                set("description", feature.properties.culturalSite.description)
                                set("imageUrl", feature.properties.culturalSite.imageUrl)
                            }
                            navController.navigate("culturalSite/${feature.id}")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            LaunchedEffect(zoneName) {
                if (zoneName == "Area tidak dikenal") {
                    showUnknownAreaDialog = true
                }
            }

            if (showUnknownAreaDialog) {
                DefaultPopup(
                    visible = true,
                    onDismiss = {
                        showUnknownAreaDialog = false
                    },
                    title = "Lokasi Tidak Dikenali",
                    description = "Sepertinya tidak ada cagar budaya di sekitarmu. Coba buka BoloMaps dan cari cagar budaya terdekat!",
                    icon = FeatherIcons.Search,
                    onConnect = {
                        navController.navigate("bolomaps")
                    }
                )
            }
        }
    }
}
