package com.github.omarmiatello.bluetooth

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract

class HolderForActivity<I, O>(
    val contract: ActivityResultContract<I, O>,
    val onInvoke: (ActivityResultLauncher<I>) -> Unit,
    val callback: (O) -> Unit,
)

fun <I, O> ComponentActivity.registerForActivityResult(
    holder: HolderForActivity<I, O>
): () -> Unit {
    val launcher = registerForActivityResult(holder.contract, holder.callback)
    return { holder.onInvoke(launcher) }
}

