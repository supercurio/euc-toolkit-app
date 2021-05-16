package supercurio.euctoolkit.leds

interface LedController {
    fun setStatus(on: Boolean)
    fun setPreset(presetNo: Int)
    fun setPresetSpeed(speed: Float)
    fun setPresetAndSpeed(presetNo: Int, speed: Float)
    fun setBrightness(brightness: Float)
    fun brakeLight(color: Sp110e.BlinkColor, speed: Float)
    fun setColor(red: Int, green: Int, blue: Int)
}
