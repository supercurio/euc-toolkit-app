package supercurio.euctoolkit.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SuspendingBluetoothGatt(
    val gatt: BluetoothGatt,
    private val callback: SuspendingBluetoothGattCallback
) {

    fun setAndWriteCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        callback.writeLock.withLock {
            characteristic.value = value
            gatt.writeCharacteristic(characteristic)
            callback.condition.await()
        }
    }

    companion object {
        private const val TAG = "SuspendingBluetoothGatt"
    }
}

open class SuspendingBluetoothGattCallback : BluetoothGattCallback() {
    val writeLock = ReentrantLock()
    val condition: Condition = writeLock.newCondition()
}

suspend fun BluetoothDevice.connectGattAndDiscoverServices(
    context: Context,
    onDisconnect: () -> Unit,
) =
    suspendCoroutine<SuspendingBluetoothGatt> { cont ->

        val callback = object : SuspendingBluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothGatt.STATE_CONNECTED -> gatt.discoverServices()
                    BluetoothGatt.STATE_DISCONNECTED -> onDisconnect()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                cont.resume(SuspendingBluetoothGatt(gatt, this))
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                writeLock.withLock { condition.signal() }
            }
        }

        this.connectGatt(context, false, callback)
    }
