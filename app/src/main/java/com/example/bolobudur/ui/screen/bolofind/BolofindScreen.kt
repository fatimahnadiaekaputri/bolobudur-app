package com.example.bolobudur.ui.screen.bolofind

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.model.FeatureData
import com.example.bolobudur.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolofindScreen(navController: NavController) {

    val nearbyFeatures = listOf(
        FeatureData(
            id = 1,
            title = "Dhyani Buddha",
            description = "Dhyani Buddha Waisacana yang menggambarkan sikap tangan vitarka mudra.",
            imageRes = R.drawable.bolofind_feature // Ganti dengan gambar kamu sendiri di drawable
        ),
        FeatureData(
            id = 2,
            title = "Dhyani Buddha",
            description = "Dhyani Buddha Waisacana yang menggambarkan sikap tangan vitarka mudra.",
            imageRes = R.drawable.bolomaps_feature
        )
    )

    Scaffold(
        topBar = { TopBar(title = "BoloFind", navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
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
                    text = "Area Stupa",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            Text(
                text = "Lihat apa yang ada di sekitar kamu",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(nearbyFeatures) { feature ->
                    FeatureCard(
                        feature = feature,
                        navController = navController,
                        onCardClick = {
                            navController.navigate("detail/${feature.id}")
                        },
                            modifier = Modifier.fillMaxWidth().aspectRatio(1.3f))
                        Spacer(Modifier.height(12.dp)
                    )
                }
            }
        }
    }
}