package com.github.omarmiatello.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    private val permissionsRequired = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
    )

    private val askPermissions = registerForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onInvoke = { it.launch(permissionsRequired) },
        callback = { if (it.all { it.value }) enableBluetooth() }
    )

    private val enableBluetooth = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onInvoke = { it.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) },
        callback = { mainViewModel.scanLeDevice(this) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askPermissions()

        setContent {
            MainTheme {
                Column {
                    val isBTEnabled by mainViewModel.isBTEnabled.collectAsState()
                    if (!isBTEnabled) {
                        Button(onClick = { askPermissions() }) {
                            Text(text = "Enable Bluetooth")
                        }
                    }

                    val btList by mainViewModel.btList.collectAsState()
                    if (btList.isEmpty()) Text(text = "No devices found")
                    LazyColumn {
                        items(btList) {
                            Text(text = "${it.name} | ${it.address} | ${it.uuids}")
                        }
                    }
                }
            }
        }
    }
}
