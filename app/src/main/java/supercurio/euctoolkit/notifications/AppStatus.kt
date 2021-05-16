package supercurio.euctoolkit

import android.content.Context
import supercurio.euctoolkit.notifications.Notifications

object AppStatus {
    private val statusList = mutableSetOf<AppStatusItem>()

    fun add(context: Context, item: AppStatusItem) {
        statusList.add(item)
        Notifications.updateForegroundServiceNotificationStatus(context)
    }

    fun remove(context: Context, item: AppStatusItem) {
        statusList.remove(item)
        Notifications.updateForegroundServiceNotificationStatus(context)
    }

    fun has(statusItem: AppStatusItem) = statusList.contains(statusItem)

    fun getList() = statusList
}

enum class AppStatusItem {
    EUC_WORLD_CONNECTED,
    LED_CONTROLLER_CONNECTED
}
