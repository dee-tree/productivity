package edu.app.productivity.domain

import kotlin.time.Duration

sealed class Action(open val duration: Duration) {

    open val isRest = false
    open val isWork = false

    object NotInitiatedAction : Action(Duration.ZERO)

    data class Rest(override val duration: Duration) : Action(duration) {
        override val isRest = true
    }
    data class Work(override val duration: Duration, val activityName: String) : Action(duration) {
        override val isWork = true
    }

    companion object
}
