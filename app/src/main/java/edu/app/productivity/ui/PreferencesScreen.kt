@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.app.productivity.R
import edu.app.productivity.data.Preferences
import edu.app.productivity.data.Preferences.Themes
import edu.app.productivity.data.PreferencesRepository
import edu.app.productivity.data.vm.PreferencesViewModel
import edu.app.productivity.theme.ProductivityTheme

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    PreferencesScreenContent(
        preferences = preferences,
        updatePreferences = viewModel::updatePreferences,
        clearAllData = viewModel::clearAllData
    )
}

@Composable
fun PreferencesScreenContent(
    preferences: Preferences = Preferences(),
    updatePreferences: (Preferences) -> Unit = {},
    clearAllData: () -> Unit = {}
) {

    @Composable
    fun horizontalDivider() {
        HorizontalDivider(
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(R.string.preferences_header),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start
        )

        Spacer(Modifier.padding(16.dp))

        ThemePreferenceRow(preferences.theme) { updatePreferences(preferences.copy(theme = it)) }

        horizontalDivider()

        Switch(
            label = stringResource(R.string.preferences_preference_setup_duration_dialing),
            state = preferences.timerSetupIsDial,
            onStateChange = {
                updatePreferences(preferences.copy(timerSetupIsDial = it))
            }
        )

        horizontalDivider()

        StatisticsAccountingDaysRow(preferences.accountingDaysCount) {
            updatePreferences(preferences.copy(accountingDaysCount = it))
        }

        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
            ForgetMeRow(clearAllData)
        }

    }
}


@Composable
fun ForgetMeRow(
    clearAllData: () -> Unit
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(start = 16.dp)
    ) {
        OutlinedButton(
            onClick = { showConfirmationDialog = true },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            )
        ) {
            Text(stringResource(R.string.forget_me_confirmation_action))
        }
    }

    if (showConfirmationDialog) {
        ForgetMeConfirmation(
            onConfirmed = { clearAllData(); showConfirmationDialog = false },
            onDeclined = { showConfirmationDialog = false }
        )
    }
}

@Composable
fun ForgetMeConfirmation(
    onConfirmed: () -> Unit = {},
    onDeclined: () -> Unit = {}
) {
    AlertDialog(
        icon = { Icon(Icons.Rounded.Lock, "forget me title icon") },
        title = { Text(stringResource(R.string.forget_me_confirmation_title)) },
        text = { Text(stringResource(R.string.forget_me_confirmation_content)) },
        tonalElevation = 16.dp,
        confirmButton = {
            TextButton(
                onClick = onConfirmed,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.forget_me_confirmation_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDeclined) {
                Text(stringResource(R.string.forget_me_confirmation_decline))
            }
        },

        onDismissRequest = onDeclined
    )
}


@Composable
private fun ThemePreferenceRow(
    initialTheme: Themes,
    onThemeSelected: (Themes) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(R.string.preferences_preference_theme),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.padding(start = 8.dp))

        SingleChoiceSegmentedButtonRow {
            Themes.values().forEachIndexed { idx, theme ->
                SegmentedButton(
                    selected = initialTheme == theme,
                    onClick = { onThemeSelected(theme) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = idx, count = Themes.values().size
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.surfaceTint,
                        activeContentColor = MaterialTheme.colorScheme.primary,
                        inactiveContainerColor = MaterialTheme.colorScheme.background,
                        inactiveContentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    when (theme) {
                        Themes.LIGHT -> {
                            Icon(
                                painterResource(R.drawable.round_light_mode_24),
                                contentDescription = "light"
                            )
                        }

                        Themes.SYSTEM -> {
                            Icon(
                                painterResource(R.drawable.round_system_mode_24),
                                contentDescription = "system"
                            )
                        }

                        Themes.DARK -> {
                            Icon(
                                painterResource(R.drawable.round_dark_mode_24),
                                contentDescription = "dark"
                            )
                        }
                    }


                }
            }
        }
    }
}

@Composable
fun StatisticsAccountingDaysRow(
    initialDaysCount: Int,
    onDaysCountChange: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(R.string.preferences_preference_statistics_accounting_days),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.padding(start = 8.dp))

        var currentTextDays by remember(initialDaysCount) { mutableIntStateOf(initialDaysCount) }

        OutlinedTextField(
            value = if (currentTextDays == 0) "" else currentTextDays.toString(),
            onValueChange = {
                if (it.isBlank()) currentTextDays = 0
                val selectedDays = it.toIntOrNull() ?: return@OutlinedTextField
                if (selectedDays > 999) return@OutlinedTextField
                currentTextDays = selectedDays
                if (selectedDays in PreferencesRepository.ACCOUNTING_DAYS_RANGE) {
                    onDaysCountChange(selectedDays)
                }
            },
            isError = currentTextDays !in PreferencesRepository.ACCOUNTING_DAYS_RANGE,
            singleLine = true, maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            label = {
                Text(
                    stringResource(
                        R.string.preferences_preference_statistics_accounting_days_select,
                        PreferencesRepository.ACCOUNTING_DAYS_RANGE.first,
                        PreferencesRepository.ACCOUNTING_DAYS_RANGE.last,
                    )
                )
            }
        )

    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun PreviewPreferencesScreenLight() {
    ProductivityTheme {
        Surface {
            PreferencesScreenContent()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PreviewPreferencesScreenDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            PreferencesScreenContent()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun PreviewForgetMeConfirmationLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            ForgetMeConfirmation()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PreviewForgetMeConfirmationDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            ForgetMeConfirmation()
        }
    }
}


