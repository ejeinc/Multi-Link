package com.eje_c.multilink.controller.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Database for app. Implementation is generated with Room library.
 */
@Database(entities = [DeviceEntity::class, VideoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val deviceDao: DeviceDao

    abstract val videoDao: VideoDao

}