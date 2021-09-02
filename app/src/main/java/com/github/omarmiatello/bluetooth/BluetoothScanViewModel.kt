package com.github.omarmiatello.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class BluetoothScanViewModel(
    private val app: Application
) : AndroidViewModel(app) {
    private val bluetoothAdapter = app.getSystemService<BluetoothManager>()?.adapter
    private val _isEnabled = MutableStateFlow(false)
    private val isSearching = MutableStateFlow(false)
    val easyDeviceSet = sortedSetOf<EasyDevice>(compareBy { it.address })
    private val _deviceList = MutableStateFlow(easyDeviceSet.toList())

    val hasPermissions = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()
    val deviceList = _deviceList.asStateFlow()

    val screenTitle = combine(
        hasPermissions,
        isEnabled,
        isSearching,
        deviceList,
    ) { hasPermissions, isEnabled, isSearching, deviceList ->
        when {
            bluetoothAdapter == null -> "No bluetooth HW"
            !hasPermissions -> "Bluetooth permission required"
            !isEnabled -> "Bluetooth disabled"
            isSearching -> "Bluetooth devices (searching)"
            deviceList.isEmpty() -> "Bluetooth devices not found"
            else -> "Bluetooth devices"
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "Bluetooth devices")

    init {
        if (bluetoothAdapter != null) {
            val callback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    updateDevice(result.device.toEasyDevice())
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>) {
                    results.forEach { result ->
                        updateDevice(result.device.toEasyDevice())
                    }
                }
            }
            val scanner = bluetoothAdapter.bluetoothLeScanner
            viewModelScope.launch {
                while (true) {
                    if (isEnabled.value) {
                        isSearching.value = true
                        scanner.startScan(callback)
                        delay(10000)
                    }
                    isSearching.value = false
                    scanner.stopScan(callback)
                    delay(5000)
                }
            }
        }
    }

    private fun updateDevice(device: EasyDevice) {
        easyDeviceSet.remove(device)
        easyDeviceSet.add(device)
        _deviceList.value = easyDeviceSet.toList()
    }

    val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            updateDevice(gatt.toEasyDevice())
            if (newState == BluetoothProfile.STATE_CONNECTED) gatt.discoverServices()
            println("onConnectionStateChange $newState: ${gatt.toEasyDevice()}")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            updateDevice(gatt.toEasyDevice())
            gatt.services.flatMap { it.characteristics }.forEach { characteristic ->
                gatt.readCharacteristic(characteristic)
            }

            //gatt?.disconnect()
        }

        override fun onPhyRead(gatt: BluetoothGatt, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
            println("onPhyRead: ${gatt.toEasyDevice()}")
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            updateDevice(gatt.toEasyDevice())
            println("onCharacteristicRead: ${gatt.toEasyDevice()}")
            println("onCharacteristicRead2: ${characteristic.toEasyCharacteristic()}")
            characteristic.descriptors.forEach { descriptor ->
                gatt.readDescriptor(descriptor)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            println("onCharacteristicChanged: ${gatt.toEasyDevice()}")
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            updateDevice(gatt.toEasyDevice())
            println("onDescriptorRead: ${gatt.toEasyDevice()}")
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            println("onReadRemoteRssi: ${gatt.toEasyDevice()}")
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            println("onMtuChanged: ${gatt.toEasyDevice()}")
        }

        override fun onServiceChanged(gatt: BluetoothGatt) {
            super.onServiceChanged(gatt)
            println("onServiceChanged: ${gatt.toEasyDevice()}")
        }
    }

    fun checkEnabled() {
        _isEnabled.value = bluetoothAdapter?.isEnabled ?: false
    }

    fun connect(device: BluetoothDevice) {
        device.connectGatt(app, true, gattCallback)
    }
}
