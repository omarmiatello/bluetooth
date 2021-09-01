package com.github.omarmiatello.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class BluetoothScanViewModel : ViewModel() {
    private val bluetoothAdapter = MutableStateFlow<BluetoothAdapter?>(null)
    private val _isEnabled = MutableStateFlow(false)
    private val isSearching = MutableStateFlow(false)
    private val _deviceList = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    val hasPermissions = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()
    val deviceList = _deviceList.asStateFlow()

    val screenTitle = combine(
        bluetoothAdapter,
        hasPermissions,
        isEnabled,
        isSearching,
        deviceList,
    ) { bluetoothAdapter, hasPermissions, isEnabled, isSearching, deviceList ->
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
        val bluetoothAdapter = bluetoothAdapter.value
        if (bluetoothAdapter != null) {
            val callback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    _deviceList.value = (_deviceList.value + result.device).distinct()
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    _deviceList.value =
                        (_deviceList.value + results.orEmpty().map { it.device }).distinct()
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

    fun setupBluetoothAdapter(context: Context) {
        bluetoothAdapter.value = context.getSystemService<BluetoothManager>()?.adapter
    }

    fun checkEnabled() {
        _isEnabled.value = bluetoothAdapter.value?.isEnabled ?: false
    }
}