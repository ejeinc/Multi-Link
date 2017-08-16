package com.eje_c.multilink.json

import com.google.gson.Gson

object JSON {
    val gson = Gson()

    /**
     * Convert an object to a com.eje_c.multilink.json.JSON string.
     */
    fun stringify(obj: Any): String = gson.toJson(obj)

    /**
     * Construct an object from com.eje_c.multilink.json.JSON string.
     */
    inline fun <reified T> parse(json: String): T = gson.fromJson(json, T::class.java)
}