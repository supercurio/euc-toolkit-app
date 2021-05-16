package supercurio.euctoolkit.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import supercurio.euctoolkit.services.BackgroundService

class AutoStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val validActions = listOf(
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_BOOT_COMPLETED
        )

        if (validActions.contains(intent.action)) BackgroundService.enable(context, true)
    }
}
