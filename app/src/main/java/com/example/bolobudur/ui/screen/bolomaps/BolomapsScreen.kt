package com.example.bolobudur.ui.screen.bolomaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.screen.bolomaps.components.BottomSheet
import com.example.bolobudur.ui.screen.bolomaps.components.BottomSheetNavigation
import com.example.bolobudur.ui.screen.bolomaps.components.BottomSheetPath
import com.example.bolobudur.ui.screen.bolomaps.maps.MapBox
import com.example.bolobudur.ui.screen.bolomaps.maps.MapViewModel
import com.example.bolobudur.data.repository.LocationRepository
import com.example.bolobudur.ui.components.DefaultPopup
import com.example.bolobudur.ui.screen.bolomaps.components.FloatingInstructionBox
import com.example.bolobudur.utils.toScreenHeight
import com.mapbox.geojson.Point
import compose.icons.FeatherIcons
import compose.icons.feathericons.Map
import compose.icons.feathericons.Search
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolomapsScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    navigationViewModel: NavigationViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val isPathVisible by viewModel.isPathVisible.collectAsState()
    val selectedDestination by viewModel.selectedDestination.collectAsState()
    val pathInfo by viewModel.pathInfo.collectAsState()

    var isNavigating by remember { mutableStateOf(false) }

    val isArrived by navigationViewModel.isArrived.collectAsState() // 🟢 UPDATE
    var showArrivalPopup by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val isSearching = uiState.searchQuery.isNotBlank()

    var showPopup by remember { mutableStateOf(false) }

    val errorMessage by viewModel.errorMessage.collectAsState()

    val currentPosition by navigationViewModel.currentPosition.collectAsState()

    val nearbyPoi by viewModel.nearbyPoi.collectAsState()
    var showNearbyPopup by remember { mutableStateOf(false) }

    LaunchedEffect(currentPosition) {
        currentPosition?.let {
            viewModel.checkNearby(it.latitude(), it.longitude())
        }
    }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(title = "BoloMaps", navController = navController) },
        sheetPeekHeight = when {
            isNavigating -> 0.25f.toScreenHeight()
            isPathVisible -> 0.33f.toScreenHeight()
            isSearching -> 0.95f.toScreenHeight()
            else -> 0.5f.toScreenHeight()
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp)) {
                when {
                    // 🔹 Navigasi aktif → tampilkan arah
                    isNavigating -> {
                        val remaining by navigationViewModel.remainingDistance.collectAsState()
                        BottomSheetNavigation(
                            destinationLabel = selectedDestination?.label ?: "Area tidak diketahui",
                            remainingDistance = remaining,
                            onStopNavigation = {
                                navigationViewModel.resetNavigation() // hapus garis
                                viewModel.resetPath() // hapus shortest path
                                isNavigating = false
                                showPopup = true

                            }
                        )
                    }


                    // 🔹 Path ditemukan → tampilkan tombol mulai navigasi
                    isPathVisible -> {
                        BottomSheetPath(
                            destinationLabel = selectedDestination?.label ?: "Tujuan tidak diketahui",
                            totalDistance = pathInfo?.totalDistance ?: 0f,
                            onCancel = { viewModel.resetPath() },
                            onStartNavigation = {
                                pathInfo?.let {
                                    viewModel.resetPath()
                                    navigationViewModel.startNavigation(it)
                                    isNavigating = true // mulai simulasi dummy
                                }
                            }
                        )
                    }

                    // 🔹 Default → tampilkan list POI
                    else -> {
                        BottomSheet(viewModel = viewModel)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MapBox()

            if (isNavigating) {
                val bearing by navigationViewModel.bearing.collectAsState(initial = 0f)

                FloatingInstructionBox(
                    bearing = bearing,
                )
            }

        }
    }

    DefaultPopup(
        visible = showPopup,
        onDismiss = { showPopup = false },
        title = "Lihat Sekitar",
        description = "Ayo lihat apa yang ada di sekitarmu, dan pelajari lebih dekat!",
        icon = FeatherIcons.Search,
        onConnect = {
            showPopup = false
            navController.navigate("bolofind")
        }
    )

    LaunchedEffect(isArrived) {
        if (isArrived) showArrivalPopup = true
    }

    LaunchedEffect(nearbyPoi) {
        if (nearbyPoi != null) {
            showNearbyPopup = true
        }
    }

//    if (showNearbyPopup) {
//        DefaultPopup(
//            visible = showNearbyPopup,
//            onDismiss = {
//                showNearbyPopup = false
//                viewModel.clearNearbyFlag()
//            },
//            title = "Lihat Sekitar",
//            description = "Kami menemukan cagar budaya di sekitarmu! Mau lihat?",
//            icon = FeatherIcons.Search,
//            onConnect = {
//                showNearbyPopup = false
//                viewModel.clearNearbyFlag()
//                navController.navigate("bolofind")
//            }
//        )
//    }


    if (showArrivalPopup) {
        DefaultPopup(
            visible = showArrivalPopup,
            onDismiss = {
                showArrivalPopup = false
                navigationViewModel.resetNavigation()
                isNavigating = false
                        },
            title = "Lihat Sekitar",
            description = "Kamu sudah sampai! Ayo lihat apa yang ada di sekitarmu, dan pelajari lebih dekat!",
            icon = FeatherIcons.Search,
            onConnect = {
                showPopup = false
                navController.navigate("bolofind")
            }
        )
    }

    if (errorMessage != null) {
        DefaultPopup(
            visible = true,
            onDismiss = { viewModel.clearErrorMessage() },
            title = "Rute Tidak Ditemukan",
            description = errorMessage ?: "Terjadi kesalahan saat memuat rute.",
            icon = FeatherIcons.Map,
            onConnect = { viewModel.clearErrorMessage() },
            showSkip = false
        )
    }

}
