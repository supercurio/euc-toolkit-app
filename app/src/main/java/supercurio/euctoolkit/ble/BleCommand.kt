package supercurio.euctoolkit.ble

class BleCommand(vararg command: Int) {
    val byteArray = command.map { it.toByte() }.toByteArray()
}
