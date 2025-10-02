package com.example.bolobudur.ui.screen.bolomaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bolobudur.R
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.screen.bolomaps.components.BottomSheet
import com.example.bolobudur.ui.screen.bolomaps.maps.MapBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolomapsScreen(navController: NavController) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(title = "BoloMaps", navController = navController) },
        sheetPeekHeight = 200.dp,
        sheetContent = {
            BottomSheet()
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MapBox(mapResId = R.drawable.bolomaps_feature)
        }
    }
}
