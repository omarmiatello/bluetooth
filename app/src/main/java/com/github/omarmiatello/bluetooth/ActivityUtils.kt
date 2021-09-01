package com.github.omarmiatello.bluetooth

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun <I, O> ComponentActivity.registerForActivityResult(
    contract: ActivityResultContract<I, O>,
    onInvoke: (ActivityResultLauncher<I>) -> Unit,
    callback: (O) -> Unit,
): () -> Unit {
    val launcher = registerForActivityResult(contract, callback)
    return { onInvoke(launcher) }
}

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
