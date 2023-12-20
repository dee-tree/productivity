@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)

package edu.app.productivity.ui.timer


import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerState
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimerSurface(
    timerState: TimerState,
    actions: List<Action>,
    onSetSingleShotTimer: (List<Action>) -> Unit = {},
    onTimerPause: () -> Unit = {},
    onTimerCancel: () -> Unit = {},
    onTimerRestore: () -> Unit = {},
    onTimerResume: () -> Unit = {},
    onTimerClear: () -> Unit = {},
    defaultTimerRestoreActionTimeout: Duration = 3.seconds
) {
    val stateTitleId = rememberTimerHeaderStringId(timerState, actions.firstOrNull())

    val scope = rememberCoroutineScope()

    val singleShotTimerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSingleShotTimerSheet by remember { mutableStateOf(false) }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = stateTitleId,
            transitionSpec = { animatedVerticalTransition().using(SizeTransform(clip = false)) },
            contentAlignment = Alignment.Center,
            label = "Title animation"
        ) { target ->
            Text(
                style = Typography.headlineMedium,
                text = stringResource(target),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.padding(12.dp))

        AnimatedVisibility(visible = timerState is TimerState.TimerNotInitiated) {
            Button(
                onClick = { showSingleShotTimerSheet = true },
                modifier = Modifier.padding(top = 64.dp)
            ) {
                Text(text = stringResource(R.string.single_shot_timer_plan_setup_action))
            }
        }

        Spacer(modifier = Modifier.padding(12.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 64.dp)
        ) {

            AnimatedVisibility(
                visible = actions.isNotEmpty() && (timerState.isRunning || timerState.isPaused)
            ) {
                Column {
                    if (actions.isNotEmpty()) {
                        TimerAnimation(actions.first(), timerState)
                    }

                    Spacer(modifier = Modifier.padding(16.dp))
                    TimerText(timerState.remaining)
                }
            }

            AnimatedVisibility(visible = timerState.isRunning || timerState.isPaused) {
                LabeledActions(actions)
            }

            AnimatedVisibility(visible = timerState is TimerState.TimerCompleted && actions.isEmpty()) {
                // actions chain is completed
                OutlinedButton(onClick = { onTimerClear() }) {
                    Text(text = stringResource(R.string.timer_all_actions_completed_state_action_ok))
                }
            }


            AnimatedVisibility(visible = timerState is TimerState.TimerCancelled) {
                TimerCancelledCard(
                    onTimerRestore = onTimerRestore,
                    onTimerClear = onTimerClear,
                    defaultTimerRestoreActionTimeout = defaultTimerRestoreActionTimeout
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Row {

                AnimatedVisibility(timerState.isRunning || timerState.isPaused) {
                    Row {
                        TextButton(
                            onClick = onTimerCancel,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text(text = stringResource(R.string.cancel_timer))
                        }

                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                        IconButton(onClick = if (timerState.isRunning) onTimerPause else onTimerResume) {
                            Crossfade(targetState = timerState.isRunning, label = "") { running ->
                                Icon(
                                    painterResource(
                                        if (running) R.drawable.round_pause_24
                                        else R.drawable.round_play_arrow_24
                                    ),
                                    contentDescription = if (running) "pause" else "resume"
                                )
                            }
                        }
                    }

                }

            }

        }
    }

    if (showSingleShotTimerSheet) {
        ActionsSetupBottomSheet(
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
fun LabeledActions(actions: List<Action>) {
    val scope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.padding(vertical = 4.dp))

        val rowState = rememberLazyListState()

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(
                12.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            state = rowState,
            modifier = Modifier.fillMaxWidth(0.4f),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
        ) {
            if (!rowState.isScrollInProgress) {
                scope.launch {
                    delay(300)

                    // need scroll to first or last?
                    rowState.layoutInfo.visibleItemsInfo.first().let { first ->
                        if (first.index == 0 && first.offset == 0) {
                            rowState.animateScrollToItem(first.index)
                            return@launch
                        }
                    }

                    rowState.layoutInfo.visibleItemsInfo.last().let { last ->
                        if (last.index == actions.size - 1 && last.offset + last.size == rowState.layoutInfo.viewportSize.width) {
                            rowState.animateScrollToItem(last.index)
                            return@launch
                        }
                    }


                    val halfedViewPortSize = rowState.layoutInfo.viewportSize.width / 2
                    val elToScroll = rowState.layoutInfo.visibleItemsInfo.minBy {
                        min(
                            abs(it.offset + it.size - halfedViewPortSize),
                            abs(halfedViewPortSize - it.offset)
                        )
                    }

                    rowState.animateScrollToItem(
                        elToScroll.index,
                        scrollOffset = -(halfedViewPortSize - elToScroll.size / 2)
                    )
                }
            }

            items(actions.size) { actionIdx ->
                val labelAlpha = 0.4f + 0.6f * (actions.size - actionIdx).toFloat() / actions.size
                MarkedLabel(actions[actionIdx], modifier = Modifier.alpha(labelAlpha))
            }
        }
    }
}

@Composable
fun TimerCancelledCard(
    onTimerRestore: () -> Unit = {},
    onTimerClear: () -> Unit = {},
    defaultTimerRestoreActionTimeout: Duration = 3.seconds
) {
    var restoreActionRemain by remember { mutableStateOf(defaultTimerRestoreActionTimeout) }

    LaunchedEffect(restoreActionRemain) {
        if (restoreActionRemain == Duration.ZERO || restoreActionRemain.isNegative()) {
            onTimerClear()
        } else {
            1.seconds.also { delayUnit ->
                delay(delayUnit)
                restoreActionRemain -= delayUnit
            }
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${restoreActionRemain.inWholeSeconds}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))

            Row {
                TextButton(
                    onClick = onTimerRestore,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.surfaceTint)
                ) {
                    Text(text = stringResource(R.string.timer_cancelled_state_restore_message))
                    Spacer(Modifier.padding(horizontal = 6.dp))
                    Icon(Icons.Rounded.Refresh, "restore timer")
                }

                Spacer(Modifier.padding(horizontal = 16.dp))

                IconButton(onClick = onTimerClear) {
                    Icon(Icons.Rounded.Clear, "clear timer")
                }
            }
        }
    }
}


@Composable
fun TimerAnimation(action: Action, timerState: TimerState) {
    val shouldAnimBeActive = timerState.isRunning
    val modifier = Modifier.size(size = 256.dp)
    val iterations = LottieConstants.IterateForever

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = if (action.isWork) R.raw.timer_animation_work else R.raw.timer_animation_rest)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.75f))
    ) {
        if (action.isWork) {
            LottieAnimation(
                modifier = modifier,
                composition = composition,
                iterations = iterations,
                isPlaying = shouldAnimBeActive
            )
        } else if (action.isRest) {
            LottieAnimation(
                modifier = modifier,
                composition = composition,
                speed = 2f,
                iterations = iterations,
                isPlaying = shouldAnimBeActive
            )
        }
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
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            // Minutes
            AnimatedTimerText(leftDuration.inWholeMinutes)
            Text(
                text = ":", textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
            )
            // Seconds
            AnimatedTimerText(leftDuration.inWholeSeconds % 60)
        }

    }
}

@Composable
private fun AnimatedTimerText(units: Long) {
    AnimatedContent(
        targetState = units,
        transitionSpec = { animatedVerticalTransition().using(SizeTransform(clip = false)) },
        contentAlignment = Alignment.Center,
        label = "Timer animation"
    ) { units ->
        Text(
            text = String.format("%02d", units),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,

            )
    }
}

@Composable
private fun rememberTimerHeaderStringId(timerState: TimerState, action: Action? = null) = remember(
    timerState::class, action
) { getTimerHeaderStringId(timerState, action) }


@StringRes
private fun getTimerHeaderStringId(timer: TimerState, action: Action? = null): Int = when (timer) {
    is TimerState.TimerRunning -> when (action) {
        is Action.Work -> listOf(
            R.string.timer_running_state_header_1,
            R.string.timer_running_state_header_2,
            R.string.timer_running_state_header_3,
            R.string.timer_running_state_header_4,
        ).random()

        is Action.Rest -> listOf(
            R.string.timer_relaxing_state_header_1,
            R.string.timer_relaxing_state_header_2,
            R.string.timer_relaxing_state_header_3,
            R.string.timer_relaxing_state_header_4,
        ).random()

        else -> R.string.timer_dots
    }

    is TimerState.TimerPaused -> R.string.timer_paused_state_header

    is TimerState.TimerCancelled -> R.string.timer_cancelled_state_header

    is TimerState.TimerNotInitiated -> R.string.timer_setup_state_header

    is TimerState.TimerCompleted -> {
        if (action == null) R.string.timer_all_actions_completed_state_header
        else R.string.timer_completed_state_header
    }
}


private fun animatedVerticalTransition() =
    slideInVertically { height -> height } + fadeIn() togetherWith
            slideOutVertically { height -> -height } + fadeOut()

private val previewTimerSurfaceStateNotInitiated =
    TimerState.TimerNotInitiated to listOf(Action.NotInitiatedAction)

private val previewTimerSurfaceStateRunning =
    TimerState.TimerRunning(150.seconds) to listOf(Action.Work(180.seconds, "Study"))

private val previewTimerSurfaceStateCancelled =
    TimerState.TimerCancelled(150.seconds) to listOf(Action.Work(180.seconds, "Study"))


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun PreviewTimerSurfaceNotInitiatedLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            val (timer, action) = previewTimerSurfaceStateNotInitiated
            TimerSurface(timer, action)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PreviewTimerSurfaceNotInitiatedDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            val (timer, action) = previewTimerSurfaceStateNotInitiated
            TimerSurface(timer, action)
        }
    }
}

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
private fun PreviewTimerCancelledCardLight() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            TimerCancelledCard()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun PreviewTimerCancelledCardDark() {
    ProductivityTheme {
        Surface {
            TimerCancelledCard()
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