package supercurio.euctoolkit.datasources.eucworld

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EucWorldJsonData(
    /** Current **/
    val ccu: Item<Float>?,

    /** Battery Current **/
    val ccuo: Item<Float>?,

    /** Duration **/
    // TODO: type TBD
    val cua: Item<String>?,

    /** Energy Consumed **/
    // TODO: type TBD
    val ced: Item<String>?,

    /** Power **/
    val cpo: Item<Float>?,

    /** Power Factor **/
    val cpf: Item<Float>?,

    /** Voltage **/
    val cvo: Item<Float>?,

    /** Altitude **/
    val gal: Item<Float>?,

    /** Bearing **/
    val gbe: Item<Float>?,

    /** Distance **/
    val gdi: Item<Float>?,

    /** Journey Tme **/
    val gua: Item<Long>?,

    /** Ride Time **/
    val gur: Item<Long>?,

    /** Speed **/
    val gsp: Item<Float>?,

    /** Avg Speed **/
    val gsa: Item<Float>?,

    /** Avg Riding **/
    val gsr: Item<Float>?,

    /** Top Speed **/
    val gsx: Item<Float>?,

    /** Battery Level **/
    val pba: Item<Int>?,

    /** Battery Charging **/
    val vbch: Item<Int>?,

    /** Battery **/
    val vbf: Item<Double>?,

    /** Battery **/
    val vba: Item<Double>?,

    /** Min Battery **/
    val vbm: Item<Double>?,

    /** Max Battery **/
    val vbx: Item<Double>?,

    /** Current **/
    val vcu: Item<Double>?,

    /** Min Current **/
    val vcn: Item<Double>?,

    /** Avg Current **/
    val vca: Item<Double>?,

    /** Max Current **/
    val vcx: Item<Double>?,

    /** Distance **/
    val vdi: Item<Float>?,

    /** Total Distance **/
    val vdt: Item<Float>?,

    /** User Distance **/
    val vdu: Item<Float>?,

    /** Wheel Distance **/
    val vdv: Item<Float>?,

    /** Journey Time **/
    val vua: Item<Long>?,

    /** Ride Time **/
    // TODO: type TBD
    val vur: Item<Long>?,

    /** Energy Consumption **/
    // TODO: type TBD
    val vec: Item<Float>?,

    /** Avg Energy Consumption **/
    // TODO: type TBD
    val veca: Item<Float>?,

    /** Total Energy Consumed **/
    // TODO: type TBD
    val vedt: Item<String>?,

    /** Faults **/
    val vfa: Item<Int>?,

    /** Hall Sensor **/
    val vfaha: Item<String>?,

    /** IMU (Gyroscope) **/
    val vfaim: Item<String>?,

    /** Lift Sensor "A" **/
    // TODO: type TBD
    val vfala: Item<String>?,

    /** Lift Sensor "B" **/
    // TODO: type TBD
    val vfalb: Item<String>?,

    /** Motor Circuit **/
    val vfamo: Item<String>?,

    /** Load **/
    val vfaol: Item<String>?,

    /** Temperature **/
    val vfaot: Item<String>?,

    /** Serial Number (status) **/
    val vfasn: Item<String>?,

    /** CPU Load **/
    val vlco: Item<Float>?,

    /** Load **/
    // TODO: type TBD
    val vlro: Item<String>?,

    /** Max Load **/
    // TODO: type TBD
    val vlrx: Item<String>?,

    /** Max Regen Load **/
    // TODO: type TBD
    val vlrn: Item<String>?,

    /** Power **/
    val vpo: Item<Double>?,

    /** Min Power **/
    val vpn: Item<Double>?,

    /** Avg Power **/
    val vpa: Item<Double>?,

    /** Max Power **/
    val vpx: Item<Double>?,

    /** Safety Margin **/
    val vsmg: Item<Float>?,

    /** Min Safety Margin **/
    val vsmn: Item<Float>?,

    /** Speed **/
    val vsp: Item<Float>?,

    /** Top Speed **/
    val vsx: Item<Float>?,

    /** Avg Speed **/
    val vsa: Item<Float>?,

    /** Avg Riding **/
    val vsr: Item<Float>?,

    /** Speed Limit **/
    val vsli: Item<Float>?,

    /** Temperature, **/
    val vte: Item<Float>?,

    /** Min Temperature **/
    val vtn: Item<Float>?,

    /** Max Temperature **/
    val vtx: Item<Float>?,

    /** Battery Temperature **/
    val vteb: Item<Float>?,

    /** Min Battery Temperature **/
    val vtebn: Item<Float>?,

    /** Max Battery Temperature **/
    val vtebx: Item<Float>?,

    /** CPU Temperature **/
    val vtec: Item<Float>?,

    /** Min CPU Temperature **/
    val vtecn: Item<Float>?,

    /** Max CPU Temperature **/
    val vtecx: Item<Float>?,

    /** IMU Temperature **/
    val vtei: Item<Float>?,

    /** Min IMU Temperature **/
    val vtein: Item<Float>?,

    /** Max IMU Temperature **/
    val vteix: Item<Float>?,

    /** Motor Temperature **/
    val vtem: Item<Float>?,

    /** Min Mainboard Temperature **/
    val vtemn: Item<Float>?,

    /** Max Mainboard Temperature **/
    val vtemx: Item<Float>?,

    /** Min Motor Temperature **/
    val vtnm: Item<Float>?,

    /** Max Motor Temperature **/
    val vtxm: Item<Float>?,

    /** Voltage **/
    val vvo: Item<Double>?,

    /** Min Voltage **/
    val vvn: Item<Double>?,

    /** Max Voltage **/
    val vvx: Item<Double>?,

    /** Tilt **/
    // TODO: find which type
    val vil: Item<String>?,

    /** Roll **/
    // TODO: find which type
    val vro: Item<String>?,

    /** Fan Status **/
    val vmcf: Item<String>?,

    /** Mode **/
    val vmcs: Item<String>?,

    /** Name **/
    val vmna: Item<String>?,

    /** Model **/
    val vmmo: Item<String>?,

    /** Version **/
    val vfv: Item<String>?,

    /** BLE Version **/
    val vfvb: Item<String>?,

    /** Inverter Version **/
    val vfvi: Item<String>?,

    /** Serial Number **/
    val vmsn: Item<String>?,

    /** Distance **/
    val tdi: Item<Float>?,

    /** Journey Time **/
    val tua: Item<Long>?,

    /** Ride Time **/
    val tur: Item<Long>?,

    /** Avg Speed **/
    val tsa: Item<Float>?,

    /** Avg Riding **/
    val tsr: Item<Float>?,

    /** Top Speed **/
    val tsx: Item<Float>?,

    /** Heart Rate **/
    val uhr: Item<Int>?,

    /** Battery Level **/
    val wba: Item<Float>?


) {
    @JsonClass(generateAdapter = true)
    data class Item<T>(
        @Json(name = "t") val title: String?,
        @Json(name = "v") val value: T?,
        @Json(name = "d") val isValid: Boolean?,
        @Json(name = "p") val type: Int?,
    ) {
        val valueType get() = ValueType.values().find { it.index == type } ?: ValueType.UNKNOWN
    }

    enum class ValueType(val index: Int) {
        STRING(0),
        COMBINED_STRING_AND_LONG(1),
        INT(2),
        LONG(3),
        FLOAT(4),
        DOUBLE(5),
        TEMPERATURE_DEG_C(6),
        SPEED_KPH(7),
        DISTANCE_KM(8),
        PER_DISTANCE_KM(9),
        DATE_UNIX_TIMESTAMP(10),
        ALTITUDE(11),
        UNKNOWN(Int.MAX_VALUE)
    }
}
