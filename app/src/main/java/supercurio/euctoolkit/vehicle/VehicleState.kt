package supercurio.euctoolkit.vehicle

import android.os.SystemClock
import android.util.Log
import supercurio.euctoolkit.AppStatus
import supercurio.euctoolkit.AppStatusItem
import supercurio.euctoolkit.leds.FindLedController
import supercurio.euctoolkit.leds.Sp110e
import kotlin.math.absoluteValue

class VehicleState {

    private val led = FindLedController.getInstance()

    private var lastSpeed = SpeedPoint(Float.MIN_VALUE, 0)

    @Synchronized
    fun setSpeed(newSpeed: Float) {
        val currentLed = led.current ?: return
        if (!AppStatus.has(AppStatusItem.LED_CONTROLLER_CONNECTED)) return

        Log.i(TAG, "New speed: $newSpeed")
        val nextLastSpeed = SpeedPoint(newSpeed.absoluteValue, SystemClock.elapsedRealtime())

        if (newSpeed != lastSpeed.speed) {
            val acceleration = calculateAcceleration(newSpeed)

            when {
                newSpeed == 0f -> currentLed.setPresetAndSpeed(
                    STOPPED_PRESET_NO,
                    0f
                )

                acceleration < -1f -> {
                    Log.i(TAG, "Braking")
                    currentLed.brakeLight(Sp110e.BlinkColor.RED_PULSE, 1f)
                }

                else -> currentLed.setPresetAndSpeed(
                    RIDING_PRESET_NO,
                    newSpeed.absoluteValue / SPEED_MAX
                )
            }
        }

        lastSpeed = nextLastSpeed
    }

    private fun calculateAcceleration(newSpeed: Float): Float {
        val newTime = SystemClock.elapsedRealtime()

        val acceleration =
            (newSpeed.absoluteValue - lastSpeed.speed) / (newTime - lastSpeed.time) * 1000 / 3.6f

        Log.i(TAG, "Acceleration: $acceleration")

        return acceleration
    }

    fun hasDataSource(newStatus: Boolean) = led.current?.setStatus(newStatus)


    data class SpeedPoint(val speed: Float, val time: Long)


    companion object {
        private const val SPEED_MAX = 50

        private const val RIDING_PRESET_NO = 3
        private const val STOPPED_PRESET_NO = 64

        private const val TAG = "VehicleState"

        private var instance: VehicleState? = null

        fun getInstance(): VehicleState {
            if (instance == null) instance = VehicleState()
            return instance!!
        }

        fun delete() {
            instance = null
        }
    }
}
