package com.github.omarmiatello.bluetooth

import android.bluetooth.*

data class EasyDevice(
    val name: String?,
    val address: String,
    val services: List<EasyService>,
    val obj: BluetoothDevice,
)

data class EasyService(
    val uuid: String,
    val characteristics: List<EasyCharacteristic>?,
)

data class EasyCharacteristic(
    val uuid: String,
    val value: String,
    val descriptors: List<EasyDescriptor>?,
    val properties: String,
)

data class EasyDescriptor(
    val uuid: String,
    val value: String,
)

fun BluetoothDevice.toEasyDevice(services: List<EasyService> = emptyList()) = EasyDevice(
    name = name,
    address = address,
    services = services,
    obj = this,
)

fun BluetoothGatt.toEasyDevice() = device.toEasyDevice(services.map { it.toEasyService() })

fun BluetoothGattService.toEasyService() = EasyService(
    uuid = "$uuid",
    characteristics = characteristics?.map { it.toEasyCharacteristic() }
)

fun BluetoothGattCharacteristic.toEasyCharacteristic() = EasyCharacteristic(
    uuid = "$uuid",
    value = "$value",
    properties = properties.toString(),
    descriptors = descriptors?.map { it.toEasyDescriptor() }
)

private fun BluetoothGattDescriptor.toEasyDescriptor() = EasyDescriptor(
    uuid = "$uuid",
    value = "$value",
)
