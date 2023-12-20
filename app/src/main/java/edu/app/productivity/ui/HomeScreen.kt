@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import edu.app.productivity.data.vm.ActionTemplateViewModel
import edu.app.productivity.data.vm.TimerViewModel
import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerState
import edu.app.productivity.ui.timer.TimerSurface

@Composable
fun HomeScreen(
    timerViewModel: TimerViewModel = hiltViewModel(),
    templatesViewModel: ActionTemplateViewModel = hiltViewModel(),
) {
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val actions by timerViewModel.actions.collectAsStateWithLifecycle()
    val templates by templatesViewModel.templates.collectAsStateWithLifecycle()

    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerSurface(
            timerState = timerState,
            actions = actions,
            onSetSingleShotTimer = {
                timerViewModel.createActions(it)
                timerViewModel.start(ctx, it.first().duration)
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

        Spacer(Modifier.fillMaxHeight(0.2f))

        AnimatedVisibility(
            visible = (actions.isEmpty() || actions.first() is Action.NotInitiatedAction) && timerState is TimerState.TimerNotInitiated
        ) {
            ActionTemplates(
                templates = templates,
                onTemplateAdd = { name, actions ->  templatesViewModel.addTemplate(name, actions) },
                isTemplateNameValid = templatesViewModel::isNameValid,
                onTemplateDeleted = templatesViewModel::deleteTemplate,
                onTemplateSelected = { (_, actions) ->
                    timerViewModel.createActions(actions)
                    timerViewModel.start(ctx, actions.first().duration)
                }
            )
        }

    }
}
