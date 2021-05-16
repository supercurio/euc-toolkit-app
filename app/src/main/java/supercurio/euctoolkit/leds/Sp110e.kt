package supercurio.euctoolkit.leds

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanFilter
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import supercurio.euctoolkit.AppStatus
import supercurio.euctoolkit.AppStatusItem
import supercurio.euctoolkit.ble.BleCommand
import supercurio.euctoolkit.ble.SuspendingBluetoothGatt
import supercurio.euctoolkit.ble.connectGattAndDiscoverServices
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.roundToInt

class Sp110e(
    private val context: Context,
    private val btDevice: BluetoothDevice,
    private val onDisconnect: () -> Unit
) : LedController {

    private val setLock = ReentrantLock()

    private var characteristic: BluetoothGattCharacteristic? = null
    private var suspendingGatt: SuspendingBluetoothGatt? = null

    private var currentPresetNo = 0
    private var currentBrakeLight = false

    suspend fun connect() {
        suspendingGatt =
            btDevice.connectGattAndDiscoverServices(context, onDisconnect = {
                AppStatus.remove(
                    context,
                    AppStatusItem.LED_CONTROLLER_CONNECTED
                )

                onDisconnect()
            }).apply {
                val service = gatt.getService(CONTROL_SERVICE_UUID.uuid)
                characteristic = service.getCharacteristic(CHAR_UUID)
            }

        AppStatus.add(context, AppStatusItem.LED_CONTROLLER_CONNECTED)
    }

    override fun setStatus(on: Boolean) = setLock.withLock {
        writeCommand(
            if (on) Commands.TURN_ON else Commands.TURN_OFF
        )
    }

    override fun setColor(red: Int, green: Int, blue: Int) = setLock.withLock {
        Log.i(TAG, "Set color")
        currentBrakeLight = false
        writeCommand(BleCommand(red, green, blue, 0x1e))
    }

    override fun setBrightness(brightness: Float) = setLock.withLock {
        writeCommand(BleCommand((brightness * 255).roundToInt(), 0xed, 0x29, 0x2a))
    }


    override fun setPreset(presetNo: Int) = setLock.withLock {
        Log.i(TAG, "Set preset: $presetNo")
        currentBrakeLight = false
        currentPresetNo = presetNo
        writeCommand(presetCommand(presetNo))
    }

    override fun setPresetSpeed(speed: Float) = setLock.withLock {
        Log.i(TAG, "Set speed: $speed")
        currentBrakeLight = false
        writeCommand(presetSpeedCommand(speed))
    }

    override fun setPresetAndSpeed(presetNo: Int, speed: Float) = setLock.withLock {
        setPresetSpeed(speed)
        if (presetNo != currentPresetNo) {
            currentPresetNo = presetNo
            setPreset(presetNo)
        }
    }

    override fun brakeLight(color: BlinkColor, speed: Float) = setLock.withLock {
        if (currentBrakeLight) return
        Log.i(TAG, "Brake: set preset")
        currentPresetNo = color.presetNo
        writeCommand(presetCommand(color.presetNo))
        Log.i(TAG, "Brake: set speed")
        writeCommand(presetSpeedCommand(speed))
        currentBrakeLight = true
    }

    fun disconnect() {
        suspendingGatt?.gatt?.disconnect()
    }

    object Commands {
        val TURN_OFF = BleCommand(0xb0, 0x4f, 0xc2, 0xab)
        val TURN_ON = BleCommand(0xfa, 0x0e, 0xc7, 0xaa)
    }

    enum class BlinkColor(val presetNo: Int) {
        RED_PULSE(57),
        RED_CIRCLE(5),
        GREEN_PULSE(58),
        BLUE_PULSE(59),
        YELLOW_PULSE(60),
        CYAN_PULSE(61),
        MAGENTA_PULSE(62),
        WHITE_PULSE(63),
    }

    private fun presetCommand(presetNo: Int) = BleCommand(presetNo, 0x09, 0xfa, 0x2c)
    private fun presetSpeedCommand(speed: Float) =
        BleCommand((speed * 255).roundToInt(), 0x10, 0x34, 0x03)

    companion object {
        private const val TAG = "SP110E"

        private val SCAN_SERVICE_UUID: ParcelUuid =
            ParcelUuid.fromString("0000ffb0-0000-1000-8000-00805f9b34fb")
        val CONTROL_SERVICE_UUID: ParcelUuid =
            ParcelUuid.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        val CHAR_UUID: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")

        fun scanFilter(): ScanFilter = ScanFilter.Builder()
            .setServiceUuid(SCAN_SERVICE_UUID)
            .build()
    }

    private fun writeCommand(command: BleCommand) {
        characteristic?.apply {
            suspendingGatt?.setAndWriteCharacteristic(this, command.byteArray)
        }
    }
}
