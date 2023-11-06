@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.app.productivity.data.vm.TimerViewModel
import edu.app.productivity.domain.Action
import edu.app.productivity.ui.timer.TimerSurface

@Composable
fun HomeScreen(
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()

    val action by timerViewModel.action.collectAsStateWithLifecycle()

    val ctx = LocalContext.current

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
                timerViewModel.createAction(it)
                timerViewModel.start(ctx, it.duration)
            },
            onTimerPause = { timerViewModel.pause(ctx) },
            onTimerResume = { timerViewModel.resume(ctx) },
            onTimerCancel = {
                timerViewModel.cancel(ctx)
            },
            onTimerClear = {
                timerViewModel.clearTimer()
                timerViewModel.createAction(Action.NotInitiatedAction)
            },
            onTimerRestore = {
                timerViewModel.start(ctx, timerViewModel.timerState.value.remaining)
            },
        )
    }
}
