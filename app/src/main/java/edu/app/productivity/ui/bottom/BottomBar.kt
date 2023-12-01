package edu.app.productivity.ui.bottom

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import edu.app.productivity.R
import edu.app.productivity.theme.ProductivityTheme


@Composable
fun BottomBar(
    onPreferencesClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    onHomeCLick: () -> Unit = {}
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = onPreferencesClick) {
                Icon(Icons.Rounded.Menu, contentDescription = "Preferences")
            }
            IconButton(onClick = onStatisticsClick) {
                Icon(
                    painterResource(R.drawable.round_bar_chart_24),
                    contentDescription = "Statistics"
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onHomeCLick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Rounded.Home, contentDescription = "Home")
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
}


@Preview
@Composable
fun BottomBarPreviewLight() {
    ProductivityTheme {
        BottomBar()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomBarPreviewDark() {
    ProductivityTheme {
        BottomBar()
    }
}
