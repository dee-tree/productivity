@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerState
import edu.app.productivity.domain.saver.saver
import edu.app.productivity.ui.timer.TimerSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun HomeScreen() {
    var timerState by rememberSaveable(stateSaver = TimerState.saver) {
        mutableStateOf(TimerState.TimerNotInitiated)
    }
    var action by rememberSaveable(stateSaver = Action.saver) {
        mutableStateOf(Action.NotInitiatedAction)
    }

    if (timerState.isRunning) {
        LaunchedEffect(timerState) {
            if (!timerState.isRunning) return@LaunchedEffect

            launch(Dispatchers.Default) {
                while (true) {
                    if (this@LaunchedEffect.isActive) {
                        delay(1.seconds)
                        if (timerState.isRunning)
                            timerState = timerState.countdown()
                        else return@launch
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerSurface(
            timerState = timerState,
            action = action,
            onSetSingleShotTimer = {
                action = it
                timerState = TimerState.TimerRunning(action.duration)
            },
            onTimerPause = { timerState = timerState.pause() },
            onTimerResume = { timerState = timerState.resume() },
            onTimerCancel = { timerState = timerState.cancel() },
            onTimerClear = { timerState = TimerState.TimerNotInitiated },
            onTimerRestore = { timerState = TimerState.TimerRunning(timerState.remaining) },
        )
    }
}
