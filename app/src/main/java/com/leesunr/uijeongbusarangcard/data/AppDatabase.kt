package com.leesunr.uijeongbusarangcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.leesunr.uijeongbusarangcard.data.dao.StoreDao
import com.leesunr.uijeongbusarangcard.data.entity.Store

@Database(entities = [Store::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "appDatabase")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}