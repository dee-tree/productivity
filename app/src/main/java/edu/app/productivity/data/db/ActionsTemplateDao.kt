package edu.app.productivity.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionsTemplateDao {

    @Query("SELECT * FROM actions_template")
    fun getAll(): Flow<List<ActionsTemplateEntity>>

    @Insert
    suspend fun insert(vararg actions: ActionsTemplateEntity)

    @Delete
    suspend fun delete(action: ActionsTemplateEntity)
}
