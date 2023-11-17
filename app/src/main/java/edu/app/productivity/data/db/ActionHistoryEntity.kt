package edu.app.productivity.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity("actions_history")
data class ActionHistoryEntity(
    @ColumnInfo("completed_at") @PrimaryKey @field:TypeConverters(BaseConverters.DateConverter::class) val completedAt: Date,
    @Embedded val action: ActionEntity
)
