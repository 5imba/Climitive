package com.wildraion.climitive.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}