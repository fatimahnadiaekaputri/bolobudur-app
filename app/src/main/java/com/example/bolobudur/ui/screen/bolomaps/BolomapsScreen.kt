package com.example.bolobudur.ui.screen.bolomaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bolobudur.R
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.screen.bolomaps.components.BottomSheet
import com.example.bolobudur.ui.screen.bolomaps.maps.MapBox
import com.example.bolobudur.ui.utils.toScreenHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolomapsScreen(navController: NavController) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(title = "BoloMaps", navController = navController) },
        sheetPeekHeight = 0.5f.toScreenHeight(),
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            BottomSheet()
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
