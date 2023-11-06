package edu.app.productivity.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.app.productivity.R
import edu.app.productivity.data.vm.PreferencesViewModel
import edu.app.productivity.theme.ProductivityTheme

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.preferences_header),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start
        )


    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun PreviewPreferencesScreenLight() {
    ProductivityTheme {
        Surface {
            PreferencesScreen()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PreviewPreferencesScreenDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            PreferencesScreen()
        }
    }
}
