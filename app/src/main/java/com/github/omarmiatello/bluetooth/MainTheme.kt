package com.github.omarmiatello.bluetooth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background,
            content = content
        )
    }
}