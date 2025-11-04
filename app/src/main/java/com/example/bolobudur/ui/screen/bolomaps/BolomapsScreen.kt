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
import com.example.bolobudur.utils.toScreenHeight
import com.mapbox.geojson.Point
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

//    val dummyPath = listOf(
////        listOf(110.203399, -7.607957),
////        listOf(110.203439, -7.607956),
////        listOf(110.203472, -7.607957),
////        listOf(110.20353, -7.607957),
////        listOf(110.203578, -7.607957),
////        listOf(110.203631, -7.607945),
////        listOf(110.203688, -7.607944)
//        listOf(110.20339676735563, -7.6079572633290695),
//        listOf(110.20342681052966, -7.6079572633290695),
//        listOf(110.20344328581865, -7.6079572633290695),
//        listOf(110.20344037841483, -7.6079889632444235),
//        listOf(110.20344134754885, -7.608017781346476),
//        listOf(110.20345685370387, -7.608021623760095),
//        listOf(110.20346557591529, -7.608026426777428),
//        listOf(110.20346557591529, -7.6080523630671735),
//        listOf(110.20346557591529, -7.608087905387578),
//        listOf(110.20346654505113, -7.608128250721208),
//        listOf(110.20346654505113, -7.6081580294172255),
//        listOf(110.20346654505113, -7.608173399065663),
//        listOf(110.20348883514777, -7.608176280874602),
//        listOf(110.20348592774394, -7.608204138361799),
//        listOf(110.20348592774394, -7.608240641272843),
//        listOf(110.20348592774394, -7.608267538152774),
//        listOf(110.20348592774394, -7.608286750208535),
//        listOf(110.20351306351427, -7.608288671413646),
//        listOf(110.20353923014864, -7.608288671413646),
//        listOf(110.20357993380406, -7.6082925138248925),
//        listOf(110.20360610044048, -7.6082925138248925),
//        listOf(110.20360513130453, -7.608313647085566),
//        listOf(110.20364389669209, -7.608311725879545),
//        listOf(110.2036816929417, -7.608311725879545),
//        listOf(110.2037127052518, -7.608312686482535),
//        listOf(110.20373499534838, -7.608313647085566),
//        listOf(110.20375728544496, -7.608314607687575),
//        listOf(110.20375177356033, -7.608329552058123),
//        listOf(110.20376019284879, -7.608336701550087),
//        listOf(110.20378151381146, -7.608336701550087),
//        listOf(110.2038096187157, -7.608340543960921),
//        listOf(110.20382609400468, -7.608339583357932)
//    )


    // 🔹 Simulasi pergerakan dummy
    // 🔹 Simulasi pergerakan dummy
//    LaunchedEffect(isNavigating) {
//        if (isNavigating) {
//            // Konversi dummyPath ke Point
//            val pathPoints = dummyPath.map { coord ->
//                Point.fromLngLat(coord[0], coord[1])
//            }
//
//            pathPoints.forEachIndexed { index, point ->
//                delay(2000L) // simulasi delay per langkah
//
//                val prevPoint = if (index == 0) point else pathPoints[index - 1]
//                val lat = point.latitude()
//                val lon = point.longitude()
//
//                // Hitung bearing dari titik sebelumnya ke titik sekarang
//                val bearing = navigationViewModel.calculateBearing(
//                    prevPoint.latitude(),
//                    prevPoint.longitude(),
//                    lat,
//                    lon
//                )
//
//                // Update ke LocationRepository → otomatis akan masuk ke flow di NavigationViewModel
//                locationRepository.updateFromBluetooth(lat, lon, bearing)
//            }
//
//            // Setelah simulasi selesai
//            isNavigating = false
//        }
//    }


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(title = "BoloMaps", navController = navController) },
        sheetPeekHeight = when {
            isNavigating -> 0.25f.toScreenHeight()
            isPathVisible -> 0.33f.toScreenHeight()
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
                        val instruction by navigationViewModel.turnInstruction.collectAsState()
                        BottomSheetNavigation(
                            remainingDistance = remaining,
                            turnInstruction = instruction
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
        }
    }

    LaunchedEffect(isArrived) {
        if (isArrived) showArrivalPopup = true
    }

    if (showArrivalPopup) {
        AlertDialog(
            onDismissRequest = { showArrivalPopup = false },
            confirmButton = {
                TextButton(onClick = {
                    showArrivalPopup = false
                    navigationViewModel.resetNavigation() // 🟢 hapus garis + reset
                }) {
                    Text("OK")
                }
            },
            title = { Text("Anda sudah sampai 🎉") },
            text = { Text("Selamat! Anda telah tiba di lokasi tujuan.") }
        )
    }
}
