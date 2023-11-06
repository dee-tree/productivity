package edu.app.productivity.domain

import android.content.Context
import edu.app.productivity.service.TimerService
import edu.app.productivity.service.setDuration
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface TimerManager {
    val timerState: StateFlow<TimerState>

    fun start(ctx: Context, duration: Duration) = TimerService.triggerAction(
        ctx, TimerService.TimerActions.START
    ) { setDuration(duration) }

    fun pause(ctx: Context) = TimerService.triggerAction(ctx, TimerService.TimerActions.PAUSE)
    fun resume(ctx: Context) = TimerService.triggerAction(ctx, TimerService.TimerActions.RESUME)
    fun cancel(ctx: Context) = TimerService.triggerAction(ctx, TimerService.TimerActions.CANCEL)
}
