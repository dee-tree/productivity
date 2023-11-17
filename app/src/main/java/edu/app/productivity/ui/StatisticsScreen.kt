package edu.app.productivity.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.legend.horizontalLegend
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entryModelOf
import edu.app.productivity.R
import edu.app.productivity.data.vm.StatisticsViewModel
import edu.app.productivity.theme.ProductivityTheme


@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val totallyWorkedMinutes by viewModel.totallyWorkedMinutes.collectAsStateWithLifecycle()
    val totallyRestMinutes by viewModel.totallyRestMinutes.collectAsStateWithLifecycle()

    val workActionsPerDays by viewModel.workActionsPerDays.collectAsStateWithLifecycle()
    val restActionsPerDays by viewModel.restActionsPerDays.collectAsStateWithLifecycle()

    val workEventsCountPerDays by remember(workActionsPerDays) {
        mutableStateOf(workActionsPerDays.map { it.size })
    }

    val restEventsCountPerDays by remember(restActionsPerDays) {
        mutableStateOf(restActionsPerDays.map { it.size })
    }

    val workMinutesPerDays by remember(workActionsPerDays) {
        mutableStateOf(workActionsPerDays.map { actions ->
            actions.sumOf { it.action.duration.inWholeMinutes }.toInt()
        })
    }

    val restMinutesPerDays by remember(restActionsPerDays) {
        mutableStateOf(restActionsPerDays.map { actions ->
            actions.sumOf { it.action.duration.inWholeMinutes }.toInt()
        })
    }

    StatisticsScreenContent(
        totallyWorkedMinutes = totallyWorkedMinutes,
        totallyRestMinutes = totallyRestMinutes,

        workActionCountPerDays = workEventsCountPerDays,
        restActionCountPerDays = restEventsCountPerDays,

        workActionMinutesPerDays = workMinutesPerDays,
        restActionMinutesPerDays = restMinutesPerDays
    )
}

@Composable
fun StatisticsScreenContent(
    forLastDays: Int = 7,
    totallyWorkedMinutes: Int = 0,
    totallyRestMinutes: Int = 0,

    workActionCountPerDays: List<Int> = emptyList(),
    restActionCountPerDays: List<Int> = emptyList(),

    workActionMinutesPerDays: List<Int> = emptyList(),
    restActionMinutesPerDays: List<Int> = emptyList(),

    workColor: Color = MaterialTheme.colorScheme.primary,
    restColor: Color = MaterialTheme.colorScheme.secondary
) {
    check(forLastDays > 0)

    val verticalSpacer = @Composable { Spacer(Modifier.padding(vertical = 16.dp)) }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.statistics_header),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start
        )

        verticalSpacer()

        Text(
            text = pluralStringResource(
                id = R.plurals.statistics_for_days_header,
                count = forLastDays,
                forLastDays
            )
        )

        verticalSpacer()

        AnimatedVisibility(visible = totallyWorkedMinutes == 0 && totallyRestMinutes == 0) {
            StatisticsPlaceholder()
        }

        AnimatedVisibility(visible = totallyWorkedMinutes != 0 || totallyRestMinutes != 0) {
            Column {
                TotallyHoursOnActionType(totallyWorkedMinutes, totallyRestMinutes)

                verticalSpacer()

                PerDayBarChart(
                    forLastDays = forLastDays,
                    workActionCountPerDays = workActionCountPerDays,
                    restActionCountPerDays = restActionCountPerDays,
                    workActionMinutesPerDays = workActionMinutesPerDays,
                    restActionMinutesPerDays = restActionMinutesPerDays,
                    workColor = workColor,
                    restColor = restColor
                )

                verticalSpacer()

            }
        }
    }
}


@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun PerDayBarChart(
    forLastDays: Int,
    workActionCountPerDays: List<Int> = emptyList(),
    restActionCountPerDays: List<Int> = emptyList(),

    workActionMinutesPerDays: List<Int> = emptyList(),
    restActionMinutesPerDays: List<Int> = emptyList(),

    workColor: Color = MaterialTheme.colorScheme.primary,
    restColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var showInMinutes by rememberSaveable { mutableStateOf(false) }

        Text(
            stringResource(R.string.statistics_totally_worked_per_days_title),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.padding(vertical = 4.dp))

        @Composable
        fun column(color: Color): LineComponent {
            val corner = 8.dp
            return lineComponent(
                shape = RoundedCornerShape(topStart = corner, topEnd = corner),
                color = color,
                thickness = 16.dp
            )
        }

        AnimatedContent(
            targetState = showInMinutes,
            transitionSpec = { animatedHorizontalTransition().using(SizeTransform(clip = false)) },
            contentAlignment = Alignment.Center,
            label = "Per day data chart",
            modifier = Modifier.padding(horizontal = 8.dp)

        ) { _ ->

            val workChart = columnChart(
                columns = listOf(column(MaterialTheme.colorScheme.primary))
            )

            val restChart = columnChart(
                columns = listOf(column(MaterialTheme.colorScheme.secondary))
            )

            val workModel by remember(showInMinutes) {
                mutableStateOf(
                    entryModelOf(
                        *(if (showInMinutes) workActionMinutesPerDays else workActionCountPerDays)
                            .toTypedArray(),
                    )
                )
            }

            val restModel by remember(showInMinutes) {
                mutableStateOf(
                    entryModelOf(
                        *(if (showInMinutes) restActionMinutesPerDays else restActionCountPerDays)
                            .toTypedArray(),
                    )
                )
            }

            val model = remember(workModel, restModel) { workModel + restModel }

            val chart = remember(workChart, restChart) { workChart + restChart }

            Chart(
                chart = chart,
                legend = horizontalLegend(
                    items = listOf(
                        chartLegend(
                            workColor,
                            stringResource(R.string.statistics_totally_worked_per_days_legend)
                        ),
                        chartLegend(
                            restColor,
                            stringResource(R.string.statistics_totally_rest_per_days_legend)
                        ),
                    ),
                    iconSize = 8.dp,
                    iconPadding = 4.dp,
                    spacing = 8.dp
                ),
                bottomAxis = rememberBottomAxis(
                    label = textComponent(color = MaterialTheme.colorScheme.onBackground),
                    titleComponent = textComponent(color = MaterialTheme.colorScheme.onBackground),
                    title = stringResource(R.string.statistics_totally_per_day_data_xaxis_days_before)
                ),
                startAxis = rememberStartAxis(
                    label = textComponent(color = MaterialTheme.colorScheme.onBackground),
                    titleComponent = textComponent(color = MaterialTheme.colorScheme.onBackground),
                    title = stringResource(
                        if (showInMinutes) R.string.statistics_totally_per_day_data_yaxis_minutes
                        else R.string.statistics_totally_per_day_data_yaxis_events
                    )
                ),
                model = model
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Switch(
                label = stringResource(R.string.statistics_events_vs_minutes),
                state = showInMinutes,
                onStateChange = { showInMinutes = it }
            )
        }

    }
}

@Composable
private fun chartLegend(
    color: Color,
    text: String,
) = legendItem(
    icon = shapeComponent(CircleShape, color = color),
    label = textComponent(color = MaterialTheme.colorScheme.onBackground),
    labelText = text
)

@Composable
fun TotallyHoursOnActionType(
    totallyWorkedMinutes: Int = 0,
    totallyRestMinutes: Int = 0
) {
    Column {
        val workString = if (totallyWorkedMinutes > 60) stringResource(
            R.string.statistics_worked_hours_totally_title,
            totallyWorkedMinutes / 60.0
        ) else stringResource(
            R.string.statistics_worked_minutes_totally_title,
            totallyWorkedMinutes
        )

        val restString = if (totallyRestMinutes > 60) stringResource(
            R.string.statistics_rest_hours_totally_title,
            totallyRestMinutes / 60.0
        ) else stringResource(
            R.string.statistics_rest_minutes_totally_title,
            totallyRestMinutes
        )

        Text(text = workString)
        Spacer(Modifier.padding(vertical = 4.dp))
        Text(text = restString)
    }
}

@Composable
fun StatisticsPlaceholder() {
    Column {
        Text(
            text = stringResource(R.string.statistics_no_data_title)
        )

        Spacer(Modifier.padding(vertical = 16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painterResource(R.drawable.round_bar_chart_24),
                contentDescription = "your charts will be here",
                modifier = Modifier
                    .size(128.dp)
            )
        }
    }
}

private fun animatedHorizontalTransition() =
    slideInHorizontally { width -> width / 2 } + fadeIn() togetherWith
            slideOutHorizontally { width -> -width / 2 } + fadeOut()


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun StatisticsPlaceholderPreviewLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            StatisticsPlaceholder()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun StatisticsPlaceholderPreviewDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            StatisticsPlaceholder()
        }
    }
}


@Composable
private fun StatisticsScreenContentWithSomeData() {
    StatisticsScreenContent(
        forLastDays = 7,
        totallyWorkedMinutes = 1950,
        totallyRestMinutes = 180,
        workActionCountPerDays = listOf(3, 0, 2, 1, 4, 0, 0),
        restActionCountPerDays = listOf(2, 0, 1, 0, 3, 0, 0),
        workActionMinutesPerDays = listOf(585, 0, 390, 195, 780, 0, 0),
        restActionMinutesPerDays = listOf(60, 0, 30, 0, 90, 0, 0)
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun StatisticsScreenPreviewLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            StatisticsScreenContentWithSomeData()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun StatisticsScreenPreviewDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            StatisticsScreenContentWithSomeData()
        }
    }
}
