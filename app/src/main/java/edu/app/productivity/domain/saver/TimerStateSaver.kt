package edu.app.productivity.domain.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import edu.app.productivity.domain.TimerState
import kotlin.time.Duration.Companion.milliseconds


private enum class TimerStateType {
    NOT_INITIATED, RUNNING, PAUSED, COMPLETED
}

private val TimerState.type: TimerStateType
    get() = when (this) {
        is TimerState.TimerNotInitiated -> TimerStateType.NOT_INITIATED
        is TimerState.TimerRunning -> TimerStateType.RUNNING
        is TimerState.TimerPaused -> TimerStateType.PAUSED
        is TimerState.TimerCompleted -> TimerStateType.COMPLETED
    }

val TimerState.Companion.saver: Saver<TimerState, Any>
    get() = mapSaver(
        save = {
            buildMap {
                this[typeSaverField] = it.type
                this[remainingSaverField] = it.remaining.inWholeMilliseconds

                if (it is TimerState.TimerPaused) {
                    this[pausedAtField] = it.pausedAt
                }
            }
        },
        restore = { map ->
            val type = map[typeSaverField] as TimerStateType?
            val remaining = (map[remainingSaverField] as Long?)?.milliseconds
            check(type != null)
            check(remaining != null)
            when (type) {
                TimerStateType.NOT_INITIATED -> TimerState.TimerNotInitiated
                TimerStateType.RUNNING -> TimerState.TimerRunning(remaining)
                TimerStateType.PAUSED -> {
                    val pausedAt = map[pausedAtField] as Long?
                    check(pausedAt != null)
                    TimerState.TimerPaused(remaining, pausedAt)
                }

                TimerStateType.COMPLETED -> TimerState.TimerCompleted
            }
        }
    )

private val TimerState.Companion.typeSaverField: String get() = "type"
private val TimerState.Companion.remainingSaverField: String get() = "remaining"
private val TimerState.Companion.pausedAtField: String get() = "pausedAt"
