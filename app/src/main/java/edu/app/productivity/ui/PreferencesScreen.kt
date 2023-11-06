@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.app.productivity.R
import edu.app.productivity.data.Preferences
import edu.app.productivity.data.Preferences.Themes
import edu.app.productivity.data.vm.PreferencesViewModel
import edu.app.productivity.theme.ProductivityTheme

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    PreferencesScreenContent(
        preferences = preferences,
        updatePreferences = viewModel::updatePreferences
    )
}

@Composable
fun PreferencesScreenContent(
    preferences: Preferences = Preferences(),
    updatePreferences: (Preferences) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.preferences_header),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start
        )

        Spacer(Modifier.padding(16.dp))

        ThemePreferenceRow(preferences.theme) { updatePreferences(preferences.copy(theme = it)) }

        Spacer(Modifier.padding(8.dp))

        Switch(
            label = stringResource(R.string.preferences_preference_setup_duration_dialing),
            state = preferences.timerSetupIsDial,
            onStateChange = {
                updatePreferences(preferences.copy(timerSetupIsDial = it))
            }
        )


    }
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
