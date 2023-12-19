package edu.app.productivity.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import edu.app.productivity.domain.Action

@Entity("actions_template")
@TypeConverters(BaseConverters.ActionListConverter::class)
data class ActionsTemplateEntity(
    @ColumnInfo("name") @PrimaryKey val name: String,
    @ColumnInfo("actions") val actions: List<Action>
)
