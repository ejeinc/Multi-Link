package com.eje_c.multilink.data

import com.eje_c.multilink.json.JSON

/**
 * JSON object which is sent between VR devices and controller.
 * @param type Must be one of Message.TYPE_** values.
 * @param data Optional data.
 */
class Message(val type: Int, val data: Any? = null) {

    init {
        check(type == TYPE_PING || type == TYPE_CONTROL_MESSAGE) {
            "Illegal value for type: $type"
        }
    }

    /**
     * Convert to byte array for sending on network.
     */
    fun serialize(): ByteArray {
        return JSON.stringify(this).toByteArray()
    }

    companion object {
        const val TYPE_PING = 0
        const val TYPE_CONTROL_MESSAGE = 1
    }
}