@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)

package edu.app.productivity.ui.timer

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.app.productivity.R
import edu.app.productivity.data.PreferencesRepository
import edu.app.productivity.data.vm.PreferencesViewModel
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.ProductivityTheme
import java.util.Objects
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


@Composable
fun SingleShotTimerPlanSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onSelected: (List<Action>) -> Unit = {},
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
) {
    val preferences by preferencesViewModel.preferences.collectAsStateWithLifecycle()

    ModalBottomSheet(
        sheetState = sheetState, onDismissRequest = onDismiss, modifier = Modifier
            .nestedScroll(
                rememberNestedScrollInteropConnection()
            ),
        windowInsets = WindowInsets.ime
    ) {
        SingleShotTimerPlanSheetContent(
            onPlanSelected = onSelected,
            sheetState = sheetState,
            timerSetupIsDial = preferences.timerSetupIsDial
        )
    }
}

@Composable
fun SingleShotTimerPlanSheetContent(
    onPlanSelected: (List<Action>) -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(),
    timerSetupIsDial: Boolean = PreferencesRepository.TIMER_SETUP_IS_DIAL_DEFAULT,
    defaultWorkMinutes: Int = 30,
    defaultRestMinutes: Int = 10
) {
    var actions by rememberSaveable { mutableStateOf<List<Action>>(emptyList()) }
    var isWork by rememberSaveable { mutableStateOf(true) } // first task is work always

    val timePickerState = rememberTimePickerState(
        initialMinute = if (isWork) defaultWorkMinutes else defaultRestMinutes,
        is24Hour = true
    )

    var activityName by rememberSaveable { mutableStateOf("") }

    val actionsListState = rememberLazyListState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.single_shot_timer_plan_setup_header),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        DurationPicker(
            state = timePickerState,
            dial = timerSetupIsDial
        )

        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        AnimatedVisibility(visible = isWork) {
            TextField(
                value = activityName,
                onValueChange = { if (it.length < 16) activityName = it },
                label = { Text(text = stringResource(R.string.timer_setup_activity_type)) },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
        }

        AnimatedVisibility(visible = !isWork) {
            Text(
                text = stringResource(R.string.single_shot_timer_rest_select),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                enabled = timePickerState.duration.isPositive(),
                onClick = {
                    actions = actions +
                            if (isWork) Action.Work(timePickerState.duration, activityName)
                            else Action.Rest(timePickerState.duration)
                    isWork = !isWork
                },
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "add another action")
            }

            TextButton(
                enabled = actions.isNotEmpty() || timePickerState.duration.isPositive(),
                onClick = {
                    actions =
                        actions + if (isWork) Action.Work(timePickerState.duration, activityName)
                        else Action.Rest(timePickerState.duration)
                    onPlanSelected(actions)
                },
            ) {
                Text(text = stringResource(R.string.timer_setup_confirm))
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
            }
        }

        AnimatedVisibility(visible = actions.isNotEmpty()) {
            Column(Modifier.padding(bottom = 8.dp, start = 20.dp, end = 20.dp)) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 20.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 16.dp, start = 6.dp, end = 6.dp)
                        .heightIn(max = 256.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = actionsListState
                ) {

                    actions.forEachIndexed { idx, action ->
                        item(key = Objects.hash(action.hashCode(), idx)) {
                            ActionCard(
                                action,
                                idx + 1
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun ActionCard(action: Action, idx: Int? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            if (idx != null) {
                Text("$idx", style = MaterialTheme.typography.bodyLarge)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {

                val title = when (action) {
                    is Action.Work -> stringResource(R.string.action_work_title)
                    is Action.Rest -> stringResource(R.string.action_rest_title)
                    else -> ""
                }

                val activityName = when (action) {
                    is Action.Work -> action.activityName.ifBlank { stringResource(R.string.action_work_default_activity) }
                    is Action.Rest -> stringResource(R.string.action_rest)
                    else -> ""
                }

                Text(text = title, style = MaterialTheme.typography.titleLarge)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(activityName)
                    Text(
                        stringResource(
                            R.string.action_duration,
                            action.duration.inWholeMinutes,
                            action.duration.inWholeSeconds % 60
                        )
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun PreviewActionWorkCardLight() {
    ProductivityTheme {
        Surface {
            ActionCard(Action.Work(15.minutes + 30.seconds, "Study"))
            Spacer(Modifier.padding(16.dp))
            ActionCard(Action.Work(15.minutes + 30.seconds, "Study"), 1)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun PreviewActionRestCardLight() {
    ProductivityTheme {
        Surface {
            ActionCard(Action.Rest(7.minutes + 30.seconds))
            Spacer(Modifier.padding(16.dp))
            ActionCard(Action.Rest(7.minutes + 30.seconds), 1)
        }
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewSingleShotTimerPlanSheetLight() {
    ProductivityTheme {
        Surface {
            SingleShotTimerPlanSheetContent()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewSingleShotTimerPlanSheetLightDial() {
    ProductivityTheme {
        Surface {
            SingleShotTimerPlanSheetContent(timerSetupIsDial = true)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewSingleShotTimerPlanSheetDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            SingleShotTimerPlanSheetContent()
        }
    }
}
