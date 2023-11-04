@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui.timer


import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerState
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.theme.Typography
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimerSurface(
    timerState: TimerState,
    action: Action,
    onSetSingleShotTimer: (Action) -> Unit = {},
    onTimerPause: () -> Unit = {},
    onTimerCancel: () -> Unit = {},
    onTimerResume: () -> Unit = {},
) {
    val ctx = LocalContext.current
    val stateTitle = remember { getStateMessage(timerState, action, ctx) }

    val scope = rememberCoroutineScope()

    val singleShotTimerSheetState = rememberModalBottomSheetState()
    var showSingleShotTimerSheet by remember { mutableStateOf(false) }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            style = Typography.headlineMedium,
            text = stateTitle,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(32.dp))
        if (timerState is TimerState.TimerRunning || timerState is TimerState.TimerPaused) {
            CoinMiningAnimation()
            Spacer(modifier = Modifier.padding(16.dp))
            TimerText(timerState.remaining)
        } else if (timerState is TimerState.TimerNotInitiated) {
            OutlinedButton(
                onClick = { showSingleShotTimerSheet = true }
            ) {
                Text(text = stringResource(R.string.single_shot_timer_plan_setup_action))
            }

        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row {
            if (timerState.isRunning || timerState.isPaused) {
                // TODO: add dialog confirmation on cancel
                TextButton(
                    onClick = onTimerCancel,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text(text = stringResource(R.string.cancel_timer))
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 6.dp))

            when (timerState) {
                is TimerState.TimerRunning -> IconButton(onClick = onTimerPause) {
                    Icon(
                        painterResource(id = R.drawable.round_pause_24),
                        contentDescription = "pause"
                    )
                }

                is TimerState.TimerPaused -> IconButton(onClick = onTimerResume) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "resume")
                }

                else -> {}
            }

        }

    }


    if (showSingleShotTimerSheet) {
        SingleShotTimerPlanSheet(
            sheetState = singleShotTimerSheetState,
            onDismiss = { showSingleShotTimerSheet = false },
            onSelected = { newAction ->
                scope.launch { singleShotTimerSheetState.hide() }.invokeOnCompletion {
                    if (!singleShotTimerSheetState.isVisible) {
                        showSingleShotTimerSheet = false
                        onSetSingleShotTimer(newAction)
                    }
                }
            }
        )
    }
}

@Composable
fun CoinMiningAnimation() {
    Box(
        modifier = Modifier
            .size(256.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text("TODO") // TODO: mining animation
    }
}

@Composable
fun TimerText(leftDuration: Duration) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = "${leftDuration.inWholeMinutes}:${leftDuration.inWholeSeconds % 60}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .width(256.dp)
                .padding(vertical = 16.dp)
        )
    }
}

private fun getStateMessage(timer: TimerState, action: Action, context: Context) = when (timer) {
    is TimerState.TimerRunning -> when (action) {
        is Action.Work -> listOf(
            context.getString(R.string.timer_running_state_header_1),
            context.getString(R.string.timer_running_state_header_2),
            context.getString(R.string.timer_running_state_header_3),
            context.getString(R.string.timer_running_state_header_4),
        ).random()

        is Action.Rest -> listOf(
            context.getString(R.string.timer_relaxing_state_header_1),
            context.getString(R.string.timer_relaxing_state_header_2),
            context.getString(R.string.timer_relaxing_state_header_3),
            context.getString(R.string.timer_relaxing_state_header_4),
        ).random()

        else -> throw IllegalStateException("Can't get title for Action $action")
    }

    is TimerState.TimerPaused -> context.getString(R.string.timer_paused_state_header)

    is TimerState.TimerNotInitiated -> context.getString(R.string.timer_setup_state_header)
    else -> "ToDo state header"
}


private val previewTimerSurfaceStateRunning =
    TimerState.TimerRunning(150.seconds) to Action.Work(180.seconds, "Study")

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PreviewTimerSurfaceLight() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            val (timer, action) = previewTimerSurfaceStateRunning
            TimerSurface(timer, action)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun PreviewTimerSurfaceDark() {
    ProductivityTheme {
        Surface {
            val (timer, action) = previewTimerSurfaceStateRunning
            TimerSurface(timer, action)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun PreviewTimerTextLight() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            TimerText(150.seconds)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun PreviewTimerTextDark() {
    ProductivityTheme {
        Surface {
            TimerText(150.seconds)
        }
    }
}