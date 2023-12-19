package edu.app.productivity.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.ProductivityTheme
import kotlin.time.Duration.Companion.minutes

@Composable
fun ActionCard(action: Action, idx: Int? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Column {
                val title = when (action) {
                    is Action.Work -> action.activityName.ifBlank {
                        stringResource(R.string.action_work_title)
                    }
                    is Action.Rest -> stringResource(R.string.action_rest_title)
                    else -> ""
                }

                val content = when (action) {
                    is Action.Work -> stringResource(R.string.action_work_title)
                    is Action.Rest -> stringResource(R.string.action_rest_title)
                    else -> ""
                }
                Row {
                    if (idx != null) {
                        Text(
                            "$idx",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.width(16.dp)
                        )
                    }
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(Modifier.padding(vertical = 4.dp))

                Row {
                    val durationContent = when {
                        action.duration.inWholeHours > 0 -> stringResource(
                            R.string.action_duration,
                            action.duration.inWholeHours,
                            action.duration.inWholeMinutes % 60
                        )
                        else -> stringResource(
                            R.string.action_duration_minutes,
                            action.duration.inWholeMinutes,
                        )
                    }

                    Text(text = content, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.padding(horizontal = 8.dp))
                    VerticalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.heightIn(min = 8.dp, max = 16.dp)
                    )
                    Spacer(Modifier.padding(horizontal = 8.dp))
                    Text(text = durationContent, style = MaterialTheme.typography.bodyMedium)

                }

            }
        }
    }
}

private val WorkCardPreview = Action.Work(30.minutes, "Cooking")
private val RestCardPreview = Action.Rest(15.minutes)


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun ActionCardPreviewLight() {
    Surface {
        ProductivityTheme(darkTheme = false) {
            Column {
                ActionCard(action = WorkCardPreview)
                Spacer(Modifier.padding(15.dp))
                ActionCard(action = RestCardPreview)
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ActionCardPreviewDark() {
    Surface {
        ProductivityTheme(darkTheme = true) {
            Column {
                ActionCard(action = WorkCardPreview)
                Spacer(Modifier.padding(15.dp))
                ActionCard(action = RestCardPreview)
            }
        }
    }
}
