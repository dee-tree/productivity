package edu.app.productivity.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ActionHistoryEntity::class, ActionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actionHistoryDao(): ActionHistoryDao
}
