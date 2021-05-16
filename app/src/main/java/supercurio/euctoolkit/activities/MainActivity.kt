package supercurio.euctoolkit.activities

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import supercurio.euctoolkit.R
import supercurio.euctoolkit.leds.FindLedController
import supercurio.euctoolkit.leds.Sp110e
import supercurio.euctoolkit.services.BackgroundService
import supercurio.euctoolkit.vehicle.VehicleState

class MainActivity : AppCompatActivity() {

    private val led = FindLedController.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED ->
                FindLedController
                    .getInstance()
                    .enableBleScanning(applicationContext)

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ->
                showPermissionsAlertDialog()

            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        BackgroundService.enable(applicationContext, true)

        findViewById<Button>(R.id.buttonLedOn).setOnClickListener {
            led.current?.setStatus(true)
        }

        findViewById<Button>(R.id.buttonLedOff).setOnClickListener {
            led.current?.setStatus(false)
        }

        findViewById<EditText>(R.id.editTextNumber).apply {
            addTextChangedListener {
                val text = this.text.toString()
                if (text.isNotEmpty())
                    led.current?.setPreset(text.toInt())
            }
        }

        findViewById<SeekBar>(R.id.seekBarSpeed).apply {
            max = 1023

            setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    val speed = seekBar.progress / max.toFloat()
                    led.current?.setPresetSpeed(speed)
                }
            })
        }

        findViewById<SeekBar>(R.id.seekBarBrightness).apply {
            max = 1023

            setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    val brightness = seekBar.progress / max.toFloat()
                    led.current?.setBrightness(brightness)
                }
            })
        }

        findViewById<Button>(R.id.buttonBlinkRed).setOnClickListener {
            led.current?.brakeLight(Sp110e.BlinkColor.RED_PULSE, 1f)
        }

        val tvSpeedIndicator = findViewById<TextView>(R.id.tvSpeedIndicator)

        findViewById<SeekBar>(R.id.seekBarVehicleSpeed).apply {
            max = 100
            progress = 50
        }
            .setOnSeekBarChangeListener(
                object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val value = progress - 50
                        tvSpeedIndicator.text = value.toString()

                        VehicleState
                            .getInstance()
                            .setSpeed(value.toFloat())
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                    }
                }
            )
    }

    private fun showPermissionsAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_alert_title)
            .setMessage(R.string.explain_location_permission_requirement)
            .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .create()
            .show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            led.enableBleScanning(applicationContext)
        } else {
            Toast.makeText(
                this,
                R.string.explain_location_permission_requirement,
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}
