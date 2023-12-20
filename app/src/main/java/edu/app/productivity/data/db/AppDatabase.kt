package edu.app.productivity.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ActionHistoryEntity::class, ActionEntity::class, ActionsTemplateEntity::class],
    version = 2,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actionHistoryDao(): ActionHistoryDao

    abstract fun actionsTemplateDao(): ActionsTemplateDao
}
