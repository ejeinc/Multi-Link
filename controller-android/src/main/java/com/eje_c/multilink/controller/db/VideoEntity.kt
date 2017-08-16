package com.eje_c.multilink.controller.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

/**
 * Represents a record in VideoEntity table in local SQLite DB.
 */
@Entity(primaryKeys = ["device_imei", "path"])
class VideoEntity {

    /**
     * Device's IMEI
     */
    @ColumnInfo(name = "device_imei")
    var deviceImei: String = ""

    /**
     * Relative path from external storage on VR device.
     */
    @ColumnInfo(name = "path")
    var path: String = ""

    /**
     * Display name.
     */
    @ColumnInfo(name = "name")
    var name: String? = null

    /**
     * Content length.
     */
    @ColumnInfo(name = "length")
    var length: Long = 0

    /**
     * Update time for this record based on SystemClock.uptimeMillis().
     */
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long = 0
}