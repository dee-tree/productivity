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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.app.productivity.R
import edu.app.productivity.data.vm.StatisticsViewModel
import edu.app.productivity.theme.ProductivityTheme


@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {

    val totallyWorkedHours by viewModel.totallyWorkedHours.collectAsStateWithLifecycle()
    val totallyRestHours by viewModel.totallyRestHours.collectAsStateWithLifecycle()

    StatisticsScreenContent(
        totallyWorkedHours = totallyWorkedHours,
        totallyRestHours = totallyRestHours
    )
}

@Composable
fun StatisticsScreenContent(
    forLastDays: Int = 7,
    totallyWorkedHours: Double = 0.0,
    totallyRestHours: Double = 0.0,
) {
    check(forLastDays > 0)

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.statistics_header),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start
        )

        Text(
            text = pluralStringResource(
                id = R.plurals.statistics_for_days_header,
                count = forLastDays,
                forLastDays
            )
        )

        Spacer(Modifier.padding(16.dp))

        Column {
            Text(
                text = stringResource(
                    R.string.statistics_worked_hours_totally_title,
                    totallyWorkedHours
                )
            )
            Spacer(Modifier.padding(vertical = 4.dp))
            Text(
                text = stringResource(
                    R.string.statistics_rest_hours_totally_title,
                    totallyRestHours
                )
            )

        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text()
            Spacer(Modifier.padding(vertical = 4.dp))

        }

        Spacer(Modifier.padding(16.dp))

    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun StatisticsScreenPreviewLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            StatisticsScreenContent()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun StatisticsScreenPreviewDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            StatisticsScreenContent()
        }
    }
}
