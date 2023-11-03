package edu.app.productivity.domain.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import edu.app.productivity.domain.Action
import kotlin.time.Duration.Companion.milliseconds

val Action.Companion.saver: Saver<Action, Any>
    get() = mapSaver(
        save = {
            buildMap<String, Any> {
                this[isWorkSaverField] = it.isWork
                if (it is Action.Work) this[activityNameSaverField] = it.activityName
                this[durationSaverField] = it.duration.inWholeMilliseconds
            }

        },
        restore = { map ->
            val duration = (map.getValue(durationSaverField) as Long).milliseconds
            val isWork = map[isWorkSaverField] as Boolean?
            check(isWork != null) // ?: throw IllegalStateException("no needed keys in saver")
            when (isWork) {
                true -> Action.Work(duration, map.getValue(activityNameSaverField) as String)
                false -> Action.Rest(duration)
            }
        }
    )


private val Action.Companion.isWorkSaverField: String get() = "isWork"
private val Action.Companion.activityNameSaverField: String get() = "activityName"
private val Action.Companion.durationSaverField: String get() = "duration"
