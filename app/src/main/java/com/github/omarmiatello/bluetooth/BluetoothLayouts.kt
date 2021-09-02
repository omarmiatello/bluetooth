package com.github.omarmiatello.bluetooth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    device: EasyDevice,
    onClick: (EasyDevice) -> Unit,
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
        if (device.services.isNotEmpty()) {
            device.services.forEach { easyService ->
                ServiceLayout(easyService)
            }

        }
    }
}

@Composable
private fun ServiceLayout(service: EasyService) {
    Card(
        modifier = Modifier.padding(4.dp),
        backgroundColor = Color(0xFFFFCDD2),
    ) {
        Column {
            Text(text = "Service: ${service.uuid}")
            service.characteristics.orEmpty().forEach {
                CharacteristicLayout(it)
            }
        }
    }
}

@Composable
private fun CharacteristicLayout(characteristic: EasyCharacteristic) {
    Card(
        modifier = Modifier.padding(4.dp),
        backgroundColor = Color(0xFFB2EBF2),
    ) {
        Column {
            Text(text = "${characteristic.uuid} [${characteristic.properties}]: ${characteristic.value}")
            characteristic.descriptors.orEmpty().forEach { descriptor ->
                DescriptorLayout(descriptor)
            }
        }
    }
}

@Composable
private fun DescriptorLayout(descriptor: EasyDescriptor) {
    Card(
        modifier = Modifier.padding(4.dp),
        backgroundColor = Color(0xFFC8E6C9),
    ) {
        Text(text = "${descriptor.uuid}: ${descriptor.value}")
    }
}