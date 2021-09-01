package com.github.omarmiatello.bluetooth

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import androidx.core.content.getSystemService


//        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
//        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)


//    private var bluetoothService: BluetoothLeService? = null

//    // Code to manage Service lifecycle.
//    private val serviceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(
//            componentName: ComponentName,
//            service: IBinder
//        ) {
//            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
//            bluetoothService?.let { bluetooth ->
//                if (!bluetooth.initialize()) {
//                    finish()
//                }
//                // perform device connection
//                // bluetooth.connect(deviceAddress)
//            }
//        }
//
//        override fun onServiceDisconnected(componentName: ComponentName) {
//            bluetoothService = null
//        }
//    }

class BluetoothLeService : Service() {
    private val binder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED
    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            }
        }
    }

    override fun onBind(intent: Intent) = binder

    fun initialize(): Boolean {
        bluetoothAdapter = applicationContext.getSystemService<BluetoothManager>()?.adapter
        return bluetoothAdapter != null
    }

    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // connect to the GATT server on the device
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                return true
            } catch (exception: IllegalArgumentException) {
                return false
            }
        } ?: run {
            return false
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2

    }
}