package supercurio.euctoolkit.datasources.eucworld

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import supercurio.euctoolkit.notifications.AppStatus
import supercurio.euctoolkit.notifications.AppStatusItem
import supercurio.euctoolkit.vehicle.VehicleState
import java.io.ByteArrayOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL


class EucWorld(private val context: Context, private val coroutineScope: CoroutineScope) {

    private val vehicleState = VehicleState.getInstance()

    private val inetSocketAddress = InetSocketAddress(InetAddress.getLoopbackAddress(), 8080)
    private var status = false

    private var requestCount = 0L
    private var startMs = 0L

    private val url = URL("http://127.0.0.1:8080/api/values")

    private var lastResponse: String? = null

    suspend fun isRunning(): Boolean = withContext(Dispatchers.IO) {
        try {
            Socket().apply { connect(inetSocketAddress); close() }
            true
        } catch (_: Throwable) {
            false
        }
    }

    fun enable(newStatus: Boolean) {
        if (newStatus == status) return
        if (newStatus) {
            Log.i(TAG, "EUC World is available: start")
            fetchWheelDataLoop()
        } else {
            AppStatus.remove(context, AppStatusItem.EUC_WORLD_CONNECTED)
            Log.i(TAG, "EUC World is not available anymore: stop")
        }
        status = newStatus
        coroutineScope.launch(Dispatchers.IO) { vehicleState.hasDataSource(newStatus) }
    }

    private fun fetchWheelDataLoop() {
        logRequestStats()
        readWheelDataHttpConnection()
    }

    private fun logRequestStats() = coroutineScope.launch {

        while (status) {
            startMs = System.currentTimeMillis()
            delay(1000)

            val elapsed = System.currentTimeMillis() - startMs
            val reqPerSecond = requestCount / elapsed.toDouble() * 1000
            Log.i(TAG, "req/s: $reqPerSecond")

            startMs = System.currentTimeMillis()
            requestCount = 0
        }
    }


    private fun readWheelDataHttpConnection() = coroutineScope.launch(Dispatchers.IO) {

        var failureCount = 0
        val outBuffer = ByteArrayOutputStream()

        while (status) {
            val urlConnection = url.openConnection()
            urlConnection.setRequestProperty("http.maxConnections", "1")
            try {
                val inputStream = urlConnection.getInputStream()
                outBuffer.reset()
                inputStream.copyTo(outBuffer)
                inputStream.close()

                val bufferString = outBuffer.toString()

                // Filter identical content
                if (bufferString == lastResponse) {
                    requestCount++
                    continue
                }
                lastResponse = bufferString

                try {
                    Moshi.Builder()
                        .build()
                        .adapter(EucWorldJsonData::class.java)
                        .fromJson(outBuffer.toString())?.let { eucWorldJsonData ->
                            processData(eucWorldJsonData)
                        }

                } catch (t: Throwable) {
                    t.printStackTrace()
                }

                failureCount = 0
            } catch (t: Throwable) {
                failureCount++
                delay(100)
                if (failureCount > 5) enable(false)
            }

            requestCount++
        }
    }


    private fun processData(eucWorldJsonData: EucWorldJsonData) {
        eucWorldJsonData.vsp?.value?.let { vehicleState.setSpeed(it) }
    }

    companion object {
        private const val TAG = "EucWorld"
    }
}
