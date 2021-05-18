package supercurio.euctoolkit.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import supercurio.euctoolkit.R
import supercurio.euctoolkit.datasources.DataSourceMonitor
import supercurio.euctoolkit.leds.FindLedController
import supercurio.euctoolkit.notifications.Notifications

class BackgroundService : Service() {

    private lateinit var dataSourceMonitor: DataSourceMonitor
    private val led = FindLedController.getInstance()

    override fun onBind(intent: Intent?): IBinder? {
        throw Error("Should not be bound")
    }

    override fun onCreate() {
        dataSourceMonitor = DataSourceMonitor(applicationContext)
        dataSourceMonitor.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            Notifications.ID_FOREGROUND_SERVICE,
            Notifications.foregroundServiceNotificationBuilder(
                applicationContext,
                getString(R.string.service_notification_title_wait)
            )
        )

        led.enableBleScanning(applicationContext)

        Log.i(TAG, "Started")

        return START_STICKY
    }

    override fun onDestroy() {
        dataSourceMonitor.stop()
        FindLedController.delete()
    }

    companion object {
        private const val TAG = "BackgroundService"

        private var isRunning = false

        fun enable(context: Context, status: Boolean) {
            if (status == isRunning) return

            isRunning = status

            if (status) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(getIntent(context))
                } else {
                    context.startService(getIntent(context))
                }
            } else {
                context.stopService(getIntent(context))
            }
        }

        private fun getIntent(context: Context) = Intent(context, BackgroundService::class.java)
    }
}
