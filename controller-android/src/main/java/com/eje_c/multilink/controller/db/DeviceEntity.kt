package com.eje_c.multilink.controller.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Represents a record in DeviceEntity table in local SQLite DB.
 */
@Entity
class DeviceEntity {

    /**
     * Device's IMEI
     */
    @PrimaryKey
    var imei: String = ""

    /**
     * Device's display name
     */
    @ColumnInfo(name = "name")
    var name: String? = null

    /**
     * Update time for this record based on SystemClock.uptimeMillis().
     */
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long = 0
}