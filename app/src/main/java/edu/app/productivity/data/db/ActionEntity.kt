package edu.app.productivity.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.app.productivity.domain.Action
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Entity("actions_table")
data class ActionEntity(
    @ColumnInfo(name = "is_rest") val isRest: Boolean,
    @ColumnInfo(name = "duration") val durationMillis: Long,
    @ColumnInfo(name = "activity_name") val name: String?,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0

    init {
        require(!duration.isNegative())
    }

    val duration: Duration
        get() = durationMillis.milliseconds

    fun toAction(): Action = when {
        isRest -> Action.Rest(duration)
        else -> Action.Work(duration, name ?: "Work")
    }

    companion object {
        fun fromAction(action: Action) = when (action) {
            is Action.Rest -> ActionEntity(true, action.duration.inWholeMilliseconds, null)
            is Action.Work -> ActionEntity(
                false,
                action.duration.inWholeMilliseconds,
                action.activityName
            )

            else -> throw IllegalArgumentException("invalid type of $action")
        }
    }
}
