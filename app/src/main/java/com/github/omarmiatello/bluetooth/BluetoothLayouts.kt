package com.github.omarmiatello.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BluetoothTopBar(
    title: String,
    isBluetoothEnabled: Boolean,
    onClickEnableBluetooth: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        actions = {
            if (!isBluetoothEnabled) {
                IconButton(onClick = onClickEnableBluetooth) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Enable Bluetooth"
                    )
                }
            }
        }
    )
}

@Composable
@SuppressLint("MissingPermission")
fun BluetoothItem(
    device: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = { onClick(device) })
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        if (device.name != null) {
            Text(
                text = device.name,
                style = MaterialTheme.typography.body1,
            )
        }
        Text(
            text = device.address,
            style = MaterialTheme.typography.body2,
        )
    }
}