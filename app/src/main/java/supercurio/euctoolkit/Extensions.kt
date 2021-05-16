package supercurio.euctoolkit

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.getSystemService

val Context.bluetoothManager
    get() = getSystemService<BluetoothManager>()!!
