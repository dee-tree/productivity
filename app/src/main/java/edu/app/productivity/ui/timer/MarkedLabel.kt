package edu.app.productivity.ui.timer

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.CadetGray
import edu.app.productivity.theme.ChinaRose
import edu.app.productivity.theme.Eminence
import edu.app.productivity.theme.FrenchGray
import edu.app.productivity.theme.Kappel
import edu.app.productivity.theme.MayaBlue
import edu.app.productivity.theme.Nyanza
import edu.app.productivity.theme.PinkLavander
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.theme.Saffron
import edu.app.productivity.theme.TropicalIndigo
import edu.app.productivity.theme.YellowGreen


@Composable
fun Marker(color: Color = remember { randomMarkerColor }) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color = color, shape = CircleShape)
    ) {}
}

private val randomMarkerColor: Color
    get() = listOf(
        CadetGray, TropicalIndigo, Kappel, MayaBlue, ChinaRose, Eminence,
        PinkLavander, Nyanza, FrenchGray, YellowGreen, Saffron
    ).random()

@Composable
fun MarkedLabel(action: Action) {
    when (action) {
        is Action.Work -> MarkedLabel(text = action.activityName)
        is Action.Rest -> MarkedLabel(
            text = stringResource(R.string.action_rest_title),
            markerColor = MaterialTheme.colorScheme.secondary
        )

        else -> {}
    }
}

@Composable
fun MarkedLabel(
    markerColor: Color = remember { randomMarkerColor },
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Marker(markerColor)
        Spacer(Modifier.padding(horizontal = 4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}


@Composable
private fun MarkedLabelPreviewData() {
    MarkedLabel(text = "It is a label")
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun MarkedLabelPreviewLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            MarkedLabelPreviewData()
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MarkedLabelPreviewDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            MarkedLabelPreviewData()
        }
    }
}