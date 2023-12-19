package edu.app.productivity.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.ProductivityTheme
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun ActionCard(action: Action, idx: Int? = null) {
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
