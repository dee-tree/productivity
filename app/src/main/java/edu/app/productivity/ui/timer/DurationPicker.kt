@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui.timer

import android.text.format.DateFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun DurationPicker(
    modifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
    dial: Boolean = false
) {
    if (dial) TimePicker(state = state, colors = durationPickerColors, modifier = modifier.testTag("TimePicker"))
    else TimeInput(state = state, colors = durationPickerColors, modifier = modifier.testTag("TimeInput"))
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

@Composable
@ExperimentalMaterial3Api
fun rememberTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    is24Hour: Boolean = DateFormat.is24HourFormat(LocalContext.current),
    vararg inputs: Any
): TimePickerState = rememberSaveable(
    inputs = inputs,
    saver = TimePickerState.Saver()
) {
    TimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )
}