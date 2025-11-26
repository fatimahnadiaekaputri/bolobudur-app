package com.example.bolobudur.ui.screen.connection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ConnectivityGate(
    connectivityViewModel: ConnectionViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val isConnected by connectivityViewModel.isConnected.collectAsState()

    if (isConnected) {
        content()
    } else {
        ConnectionScreen()
    }
}
