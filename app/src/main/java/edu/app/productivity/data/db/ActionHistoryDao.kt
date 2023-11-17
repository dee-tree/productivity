package edu.app.productivity.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.temporal.ChronoUnit
import java.util.Date

@Dao
interface ActionHistoryDao {

    @Query("SELECT * FROM actions_history")
    fun getAll(): Flow<List<ActionHistoryEntity>>

    @Query("SELECT * FROM actions_history WHERE completed_at >= :fromDateInLong")
    fun getAll(fromDateInLong: Long): Flow<List<ActionHistoryEntity>>

    fun getAll(fromDate: Date): Flow<List<ActionHistoryEntity>> = getAll(fromDate.time)

    fun getAll(forLastDays: Int) = getAll(
        Date.from(Date().toInstant().minus(forLastDays.toLong(), ChronoUnit.DAYS))
    )


    @Insert
    suspend fun insert(vararg actions: ActionHistoryEntity)

    @Delete
    suspend fun delete(action: ActionHistoryEntity)
}
