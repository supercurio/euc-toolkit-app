package supercurio.euctoolkit

import android.app.Application
import supercurio.euctoolkit.notifications.Notifications

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Notifications.createNotificationChannels(applicationContext)
    }
}
