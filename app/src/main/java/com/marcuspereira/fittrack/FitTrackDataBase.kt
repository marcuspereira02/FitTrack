package com.marcuspereira.fittrack

import androidx.room.Database
import androidx.room.RoomDatabase

@Database([CategoryEntity::class, ActivityEntity::class], version = 1 )

abstract class FitTrackDataBase : RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao
    abstract fun getActivityDao(): ActivityDao
}