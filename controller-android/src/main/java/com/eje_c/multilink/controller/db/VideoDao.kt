package com.eje_c.multilink.controller.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.os.SystemClock
import com.eje_c.multilink.controller.UPDATE_TIME_THRESHOLD_FOR_CLEAR

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(videoEntity: List<VideoEntity>)

    /**
     * Get videos which are available for all VR devices.
     */
    @Query("SELECT *, COUNT(*) as count FROM VideoEntity GROUP BY path HAVING count = (SELECT COUNT(*) FROM DeviceEntity)")
    fun query(): LiveData<List<VideoEntity>>

    /**
     * Delete videos which are not recently updated.
     */
    @Query("DELETE FROM VideoEntity WHERE updated_at < :updateTimeThreshold")
    fun clear(updateTimeThreshold: Long = SystemClock.uptimeMillis() - UPDATE_TIME_THRESHOLD_FOR_CLEAR)
}