package supercurio.euctoolkit.updater

import android.app.Activity
import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import supercurio.euctoolkit.BuildConfig
import supercurio.euctoolkit.R
import java.io.File
import java.net.URL

class SelfUpdater(private val activity: Activity) : ContextWrapper(activity.applicationContext) {

    private val coroutineScope = MainScope()

    fun suggestUpdateIfAvailable() = coroutineScope.launch {
        checkLatestVersion()?.let { lastVersion ->
            if (lastVersion.versionCode > BuildConfig.VERSION_CODE)
                AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle(R.string.update_prompt_title)
                    .setMessage(
                        getString(
                            R.string.update_prompt_message,
                            lastVersion.versionName,
                            lastVersion.versionCode,
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE,
                        )
                    )
                    .setPositiveButton(R.string.update_now) { _, _ ->
                        coroutineScope.launch {
                            requestInstallUpdate(lastVersion)
                        }
                    }
                    .create()
                    .show()
        }
    }

    private suspend fun checkLatestVersion(): LastVersion? = withContext(Dispatchers.IO) {
        val urlCheck = URL(UpdaterConfig.URL_CHECK)
        return@withContext try {
            val text = urlCheck.readText()
            val splitText = text.split("\n")

            LastVersion(
                splitText[0].toLong(),
                splitText[1],
                splitText[2]
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    private suspend fun requestInstallUpdate(lastVersion: LastVersion) =
        withContext(Dispatchers.IO) {

            val destDir = File(filesDir, "apk")
            destDir.mkdirs()
            val apkFile = File(destDir, "update.apk")

            try {
                Log.i(TAG, "Download: ${lastVersion.apkUrl}")
                URL(lastVersion.apkUrl)
                    .readBytes()
                    .let { apkFile.writeBytes(it) }

                Log.i(TAG, "Finished download")

                activity.runOnUiThread {
                    val uri = FileProvider.getUriForFile(
                        applicationContext,
                        "${packageName}.file_provider",
                        apkFile
                    )

                    val intent = Intent(Intent.ACTION_VIEW).setDataAndType(
                        uri,
                        "application/vnd.android.package-archive"
                    ).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    activity.startActivity(intent)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    t.printStackTrace()
                    Toast.makeText(
                        applicationContext,
                        R.string.update_failed_download,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

    class LastVersion(
        val versionCode: Long,
        val versionName: String,
        val apkUrl: String
    )

    companion object {
        private const val TAG = "SelfUpdater"
    }
}
