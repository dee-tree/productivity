@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)

package edu.app.productivity.ui.timer

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import edu.app.productivity.ui.ActionCard
import kotlinx.coroutines.launch
import java.util.Objects


@Composable
fun ActionsSetupBottomSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onSelected: (List<Action>) -> Unit = {},
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
) {
    val preferences by preferencesViewModel.preferences.collectAsStateWithLifecycle()

    var currentActions by rememberSaveable { mutableStateOf<List<Action>>(emptyList()) }
    var showDismissConfirmation by remember { mutableStateOf(false) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (currentActions.isNotEmpty()) {
                showDismissConfirmation = true
            } else onDismiss()
        },
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
        windowInsets = WindowInsets.ime
    ) {
        ActionsSetupBottomSheetContent(
            actions = currentActions,
            onActionsChange = { currentActions = it },
            onPlanSelected = { onSelected(currentActions) },
            sheetState = sheetState,
            timerSetupIsDial = preferences.timerSetupIsDial
        )
    }

    val scope = rememberCoroutineScope()

    if (showDismissConfirmation) {
        ActionsSetupBottomSheetDismissConfirmation(
            onConfirmed = {
                showDismissConfirmation = false
                onDismiss()
            },
            onDeclined = {
                showDismissConfirmation = false
                scope.launch {
                    sheetState.expand()
                }
            }
        )
    }
}

@Composable
fun ActionsSetupBottomSheetDismissConfirmation(
    onConfirmed: () -> Unit = {},
    onDeclined: () -> Unit = {}
) {
    AlertDialog(
        icon = { Icon(Icons.Rounded.ArrowDropDown, "dismiss request") },
        title = { Text(stringResource(R.string.single_shot_dismiss_confirmation_confirmation_title)) },
        text = { Text(stringResource(R.string.single_shot_dismiss_confirmation_confirmation_content)) },
        tonalElevation = 16.dp,
        confirmButton = {
            TextButton(
                onClick = onConfirmed,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.single_shot_dismiss_confirmation_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDeclined,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(stringResource(R.string.single_shot_dismiss_confirmation_decline))
            }
        },
        onDismissRequest = onDeclined
    )
}

@Composable
fun ActionsSetupBottomSheetContent(
    actions: List<Action>,
    onActionsChange: (List<Action>) -> Unit,
    onPlanSelected: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(),
    timerSetupIsDial: Boolean = PreferencesRepository.TIMER_SETUP_IS_DIAL_DEFAULT,
    defaultWorkMinutes: Int = 30,
    defaultRestMinutes: Int = 10
) {
    var isWork by rememberSaveable { mutableStateOf(true) } // first task is work always

    val timePickerState = rememberTimePickerState(
        initialMinute = if (isWork) defaultWorkMinutes else defaultRestMinutes,
        is24Hour = true
    )

    var activityName by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

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

        val durationPickerFocus by remember { mutableStateOf(FocusRequester()) }

        DurationPicker(
            state = timePickerState,
            dial = timerSetupIsDial,
            modifier = Modifier.focusRequester(durationPickerFocus)
        )

        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        AnimatedVisibility(visible = isWork) {
            OutlinedTextField(
                value = activityName,
                onValueChange = { if (it.length < 16) activityName = it },
                label = { Text(text = stringResource(R.string.timer_setup_activity_type)) },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                trailingIcon = {
                    if (activityName.isNotEmpty()) {
                        IconButton(onClick = { activityName = "" }) {
                            Icon(Icons.Rounded.Clear, "clear text")
                        }
                    }
                }
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
                    val newActions = actions +
                            if (isWork) Action.Work(timePickerState.duration, activityName)
                            else Action.Rest(timePickerState.duration)
                    onActionsChange(newActions)
                    isWork = !isWork
                    durationPickerFocus.requestFocus()
                },
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "add another action")
            }

            TextButton(
                enabled = actions.isNotEmpty() || timePickerState.duration.isPositive(),
                onClick = {
                    val newActions = actions +
                            if (isWork) Action.Work(timePickerState.duration, activityName)
                            else Action.Rest(timePickerState.duration)
                    onActionsChange(newActions)
                    onPlanSelected()
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
                            val dismissState = rememberDismissState(
                                positionalThreshold = { it / 3 }
                            )

                            LaunchedEffect(dismissState.isDismissed(DismissDirection.EndToStart)) {
                                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                                    scope.launch {
                                        val newActions = actions.filterIndexed { i, _ -> i != idx }
                                        onActionsChange(newActions)
                                        dismissState.snapTo(DismissValue.Default)
                                    }
                                }
                            }

                            SwipeToDismissBox(
                                state = dismissState,
                                directions = setOf(DismissDirection.EndToStart),
                                backgroundContent = {
                                    ListItemDismissDeletableBackground(dismissState)
                                }) {
                                ActionCard(action, idx + 1)
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun ListItemDismissDeletableBackground(
    dismissState: DismissState
) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.background
            else -> MaterialTheme.colorScheme.error
        },
        label = "color animation"
    )
    val alignment = Alignment.CenterEnd
    val icon = Icons.Rounded.Delete
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 1f else 1.5f,
        label = "icon scale animation"
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(color, CardDefaults.shape)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Delete Icon",
            modifier = Modifier.scale(scale)
        )
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@OptIn(ExperimentalMaterial3Api::class)
private fun PreviewActionsSetupBottomSheetLight() {
    ProductivityTheme {
        Surface {
            var actions by remember { mutableStateOf(emptyList<Action>()) }
            ActionsSetupBottomSheetContent(
                actions = actions,
                onActionsChange = { actions = it }
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewActionsSetupBottomSheetLightDial() {
    ProductivityTheme {
        Surface {
            var actions by remember { mutableStateOf(emptyList<Action>()) }
            ActionsSetupBottomSheetContent(
                actions = actions,
                onActionsChange = { actions = it },
                timerSetupIsDial = true
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewActionsSetupBottomSheetDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            var actions by remember { mutableStateOf(emptyList<Action>()) }
            ActionsSetupBottomSheetContent(
                actions = actions,
                onActionsChange = { actions = it }
            )
        }
    }
}
