package edu.app.productivity.domain

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class TimerState(open val remaining: Duration) {

    open val isRunning: Boolean = false
    open val isPaused: Boolean = false
    open val isCancelled: Boolean = false

    open fun countdown(): TimerState = throw IllegalStateException("Countdown is not implemented")

    open fun pause(): TimerPaused = throw IllegalStateException("Timer can't pause at this state")

    open fun resume(): TimerRunning =
        throw IllegalStateException("Timer can't continue at this state")

    open fun restore(): TimerRunning = TimerRunning(remaining)


    open fun complete(): TimerCompleted =
        throw IllegalStateException("Timer can't complete at this state")

    open fun cancel() = TimerCancelled(remaining)

    object TimerNotInitiated : TimerState(Duration.ZERO)

    data class TimerRunning(override val remaining: Duration) : TimerState(remaining) {
        override val isRunning: Boolean = true

        override fun countdown(): TimerState = TimerRunning(remaining - 1.seconds)
        override fun pause(): TimerPaused = TimerPaused(remaining)
    }

    object TimerCompleted : TimerState(Duration.ZERO)

    data class TimerPaused(
        override val remaining: Duration,
        val pausedAt: Long = System.currentTimeMillis()
    ) : TimerState(remaining) {
        override val isPaused: Boolean = true
        override fun resume(): TimerRunning = TimerRunning(remaining)
    }

    data class TimerCancelled(override val remaining: Duration) : TimerState(remaining) {
        override val isCancelled: Boolean = true
    }

    companion object
}
