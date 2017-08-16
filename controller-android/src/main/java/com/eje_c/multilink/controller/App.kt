package com.eje_c.multilink.controller

import android.app.Application
import android.arch.persistence.room.Room
import com.eje_c.multilink.controller.db.AppDatabase

/**
 * Custom [Application] which holds global state of app.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        db = Room.inMemoryDatabaseBuilder(this, AppDatabase::class.java).build()
    }

    companion object {
        lateinit var db: AppDatabase
    }
}