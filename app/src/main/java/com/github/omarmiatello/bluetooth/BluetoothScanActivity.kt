package com.github.omarmiatello.bluetooth

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

class BluetoothScanActivity : ComponentActivity() {
    private val vm: BluetoothScanViewModel by viewModels()

    private val askPermissions = registerForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onInvoke = { it.launch(arrayOf(BLUETOOTH_CONNECT, BLUETOOTH_SCAN)) },
        callback = {
            vm.hasPermissions.value = it.all { it.value }
            if (vm.hasPermissions.value) enableBluetooth()
        }
    )

    private val enableBluetooth = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onInvoke = { it.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) },
        callback = { vm.checkEnabled() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothScanScreen(
                vm = vm,
                onClickEnableBluetooth = askPermissions,
            )
        }

        askPermissions()
    }
}
