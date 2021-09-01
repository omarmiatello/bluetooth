package com.github.omarmiatello.bluetooth

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun BluetoothScanScreen(
    vm: BluetoothScanViewModel,
    onClickEnableBluetooth: () -> Unit,
) {
    MainTheme {
        Scaffold(
            topBar = {
                BluetoothTopBar(
                    title = vm.screenTitle.collectAsState().value,
                    isBluetoothEnabled = vm.isEnabled.collectAsState().value,
                    onClickEnableBluetooth = onClickEnableBluetooth
                )
            },
            content = {
                val btList by vm.deviceList.collectAsState()
                LazyColumn {
                    items(btList) {
                        BluetoothItem(
                            device = it,
                            onClick = { }
                        )
                    }
                }
            },
        )
    }
}
