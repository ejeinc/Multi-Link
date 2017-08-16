package com.eje_c.multilink.controller.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.os.SystemClock
import com.eje_c.multilink.controller.UPDATE_TIME_THRESHOLD_FOR_CLEAR

@Dao
interface DeviceDao {

    /**
     * Create device entity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(deviceEntity: DeviceEntity)

    /**
     * Get devices which are recently updated.
     */
    @Query("SELECT * FROM DeviceEntity")
    fun query(): LiveData<List<DeviceEntity>>

    /**
     * Delete devices which are not recently updated.
     */
    @Query("DELETE FROM DeviceEntity WHERE updated_at < :updateTimeThreshold")
    fun clear(updateTimeThreshold: Long = SystemClock.uptimeMillis() - UPDATE_TIME_THRESHOLD_FOR_CLEAR)

}