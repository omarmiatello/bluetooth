package com.github.omarmiatello.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    val isBTEnabled = MutableStateFlow(false)
    val btList = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    @SuppressLint("MissingPermission")
    fun scanLeDevice(context: Context) {
        val bluetoothAdapter = context.getSystemService<BluetoothManager>()?.adapter ?: return
        isBTEnabled.value = bluetoothAdapter.isEnabled
        val leScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                btList.value = (btList.value + result.device).distinct()
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                btList.value = (btList.value + results.orEmpty().map { it.device }).distinct()
            }
        }
        val settings = ScanSettings.Builder().setReportDelay(1000)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        println("scanLeDevice")
        viewModelScope.launch {
            while (true) {
                bluetoothLeScanner.startScan(null, settings, leScanCallback)
                println("startScan")
                delay(10000)
                bluetoothLeScanner.stopScan(leScanCallback)
                println("stopScan")
                delay(10000)
            }
        }
    }
}