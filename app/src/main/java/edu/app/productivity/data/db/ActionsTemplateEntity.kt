package edu.app.productivity.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import edu.app.productivity.domain.Action

@Entity("actions_template")
data class ActionsTemplateEntity(
    @ColumnInfo("name") @PrimaryKey val name: String,
    @ColumnInfo("actions") @field:TypeConverters(BaseConverters.ActionListConverter::class) val actions: List<Action>
)
