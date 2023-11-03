@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui.timer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun DurationPicker(
    modifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
    dial: Boolean = false
) {
    if (dial) TimePicker(state = state, colors = durationPickerColors, modifier = modifier)
    else TimeInput(state = state, colors = durationPickerColors, modifier = modifier)
}

val TimePickerState.duration: Duration
    get() = hour.hours + minute.minutes

val durationPickerColors: TimePickerColors
    @Composable get() = TimePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.background,
        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.background,
        clockDialColor = MaterialTheme.colorScheme.surface,
        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.secondary,
        periodSelectorBorderColor = MaterialTheme.colorScheme.secondary,
        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.secondary,
        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.secondary,
    )
