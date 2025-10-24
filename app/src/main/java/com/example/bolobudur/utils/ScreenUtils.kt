package com.example.bolobudur.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun Float.toScreenHeight(): Dp {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    return this * screenHeight
}