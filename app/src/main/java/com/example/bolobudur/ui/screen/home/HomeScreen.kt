package com.example.bolobudur.ui.screen.home

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.components.SearchBar
import com.example.bolobudur.ui.screen.bluetooth.BluetoothViewModel
import com.example.bolobudur.ui.screen.bluetooth.DeviceSelectionDialog
import com.example.bolobudur.ui.screen.home.components.GreetingSection
import com.example.bolobudur.ui.screen.home.components.SectionTitle
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.graphics.Color
import com.example.bolobudur.ui.components.Loader
import com.example.bolobudur.ui.screen.home.components.SearchResultSheet


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    btViewModel: BluetoothViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val isBluetoothEnabled by btViewModel.isBluetoothEnabled.collectAsState()
    val pairedDevices by btViewModel.pairedDevices.collectAsState()
    val scannedDevices by btViewModel.scannedDevices.collectAsState()
    val isScanning by btViewModel.isScanning.collectAsState()

    var showBtEnablePopup by remember { mutableStateOf(false) }
    var showDeviceSelectionPopup by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }

    //  cek Bluetooth otomatis
    LaunchedEffect(isBluetoothEnabled) {
        btViewModel.checkBluetoothEnabled()
        if (!isBluetoothEnabled) {
            showBtEnablePopup = true
            showDeviceSelectionPopup = false
        } else {
            showBtEnablePopup = false
            showDeviceSelectionPopup = true
            btViewModel.loadDevices()
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {

            // konten utama
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    GreetingSection(userName = uiState.userName)
                    Spacer(Modifier.height(8.dp))

                    // 🔍 SearchBar di sini
                    SearchBar(
                        value = uiState.searchQuery,
                        onValueChange = {
                            homeViewModel.onSearchQueryChange(it)
                            showSheet = it.isNotBlank()
                        },
                        onSearchSubmit = {
                            homeViewModel.onSearchSubmit()
                            showSheet = true
                        }
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
                    FeatureCard(
                        feature = feature,
                        navController = navController,
                        onCardClick = { navController.navigate("detail/${feature.id}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            // overlay hasil pencarian
            if (showSheet) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 120.dp)
                ) {
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = cardElevation(6.dp)
                    ) {
                        when {
                            uiState.isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .height(200.dp),
                                    contentAlignment = Center
                                ) {
                                    Loader()
                                }
                            }
                            uiState.searchResult?.data?.let { data ->
                                data.poi.features.isEmpty() && data.categories.features.isEmpty() && data.sites.features.isEmpty()
                            } ?: true
                                -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(Color.White),
                                    contentAlignment = Center
                                ) {
                                    Text(
                                        text = "Tidak ditemukan",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            else -> {
                                SearchResultSheet(
                                    searchResponse = uiState.searchResult,
                                    onPoiClick = { lat, lon, label ->
                                        navController.navigate("bolomaps/$lat/$lon/$label")
                                        showSheet = false
                                    },
                                    onCategoryClick = {
//                                        navController.navigate("borobudurpedia/$it")
                                        showSheet = false
                                    },
                                    onSiteClick = {
//                                        navController.navigate("culturalsite/$it")
                                        showSheet = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Popup Bluetooth ---
    if (showBtEnablePopup) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Bluetooth Mati") },
            text = { Text("Silakan nyalakan Bluetooth untuk melanjutkan") },
            confirmButton = {
                TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }) { Text("Buka Bluetooth") }
            }
        )
    }

    if (showDeviceSelectionPopup) {
        DeviceSelectionDialog(
            pairedDevices = pairedDevices,
            scannedDevices = scannedDevices,
            isScanning = isScanning,
            onDismiss = { showDeviceSelectionPopup = false },
            onDeviceSelected = { device ->
                btViewModel.startService(context, device.address, device.isDummy)
                showDeviceSelectionPopup = false
            }
        )
    }
}
