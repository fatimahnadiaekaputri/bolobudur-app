package com.example.bolobudur.ui.screen.bolomaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.screen.bolomaps.components.BottomSheet
import com.example.bolobudur.ui.screen.bolomaps.components.BottomSheetPath
import com.example.bolobudur.ui.screen.bolomaps.maps.MapBox
import com.example.bolobudur.ui.screen.bolomaps.maps.MapViewModel
import com.example.bolobudur.utils.toScreenHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolomapsScreen(navController: NavController, viewModel: MapViewModel = hiltViewModel()) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val isPathVisible by viewModel.isPathVisible.collectAsState()

    val selectedDestination by viewModel.selectedDestination.collectAsState()
    val pathInfo by viewModel.pathInfo.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMapData()
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(title = "BoloMaps", navController = navController) },
        sheetPeekHeight = if (isPathVisible)
            0.33f.toScreenHeight()   // kalau sedang navigasi, 1/3 layar
        else
            0.5f.toScreenHeight(),   // kalau belum, 1/2 layar
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp)) {
                if (isPathVisible) {
                    BottomSheetPath(
                        destinationLabel = selectedDestination?.label ?: "Tujuan tidak diketahui",
                        totalDistance = pathInfo?.totalDistance ?: 0f,
                        onCancel = {
                            viewModel.resetPath()
                        }
                    )
                } else {
                    BottomSheet(viewModel = viewModel)
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
}

