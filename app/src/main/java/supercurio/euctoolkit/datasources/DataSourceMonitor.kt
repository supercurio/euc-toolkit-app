package supercurio.euctoolkit.datasources

import android.content.Context
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import supercurio.euctoolkit.AppStatus
import supercurio.euctoolkit.AppStatusItem
import supercurio.euctoolkit.datasources.eucworld.EucWorld

class DataSourceMonitor(private val context: Context) {

    private val coroutineScope = MainScope()
    private var monitor = false

    private val eucWorld = EucWorld(context, coroutineScope)

    fun start() = coroutineScope.launch {
        monitor = true
        while (monitor) {
            if (eucWorld.isRunning()) {
                AppStatus.add(context, AppStatusItem.EUC_WORLD_CONNECTED)
                eucWorld.enable(true)
            }
            delay(DELAY_MS)
        }
    }

    fun stop() {
        eucWorld.enable(false)
        monitor = false
        coroutineScope.cancel()
    }

    companion object {
        private const val DELAY_MS = 1000L
    }
}
