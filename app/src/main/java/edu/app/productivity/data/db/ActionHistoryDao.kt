package edu.app.productivity.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionHistoryDao {

    @Query("SELECT * FROM actions_history")
    fun getAll(): Flow<List<ActionHistoryEntity>>

    @Insert
    suspend fun insert(vararg actions: ActionHistoryEntity)

    @Delete
    suspend fun delete(action: ActionHistoryEntity)
}
