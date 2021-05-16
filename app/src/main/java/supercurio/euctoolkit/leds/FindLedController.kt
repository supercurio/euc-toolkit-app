package supercurio.euctoolkit.leds

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import supercurio.euctoolkit.bluetoothManager

class FindLedController {

    var current: LedController? = null

    private var isScanningBle = false

    fun enableBleScanning(context: Context) {
        if (isScanningBle) return
        isScanningBle = true

        val scanFilter = Sp110e.scanFilter()

        val scanSettings = ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .build()

        context.bluetoothManager.adapter.bluetoothLeScanner.startScan(
            listOf(scanFilter),
            scanSettings,
            LedControllerScanCallback(context)
        )
    }

    inner class LedControllerScanCallback(val context: Context) : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (result == null) return
            Log.i(TAG, "result: $result")

            stopScan(context, this)

            current = Sp110e(context, result.device, onDisconnect = {
                current = null
                enableBleScanning(context)
            }).apply {
                GlobalScope.launch { connect() }
            }
        }
    }

    fun stopScan(context: Context, scanCallback: ScanCallback) {
        isScanningBle = false
        context.bluetoothManager.adapter.bluetoothLeScanner.stopScan(scanCallback)
    }

    companion object {
        private const val TAG = "FindLedController"

        private var instance: FindLedController? = null

        fun getInstance(): FindLedController {
            if (instance == null) instance = FindLedController()
            return instance!!
        }

        fun delete() {
            instance = null
        }
    }
}
