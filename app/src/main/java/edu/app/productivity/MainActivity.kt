@file:OptIn(ExperimentalLayoutApi::class)

package edu.app.productivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.app.productivity.navigation.NavigationGraph
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.ui.bottom.BottomBar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProductivityTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomBar() },
                    modifier = Modifier.systemBarsPadding()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .consumeWindowInsets(innerPadding)
                            .padding(innerPadding)
                            .imePadding(),
                    ) {
                        NavigationGraph(navController)
                    }
                }
            }
        }
    }
}
