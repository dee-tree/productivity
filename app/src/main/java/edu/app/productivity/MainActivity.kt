@file:OptIn(ExperimentalLayoutApi::class)

package edu.app.productivity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.app.productivity.navigation.Destination
import edu.app.productivity.navigation.NavigationGraph
import edu.app.productivity.navigation.navigateSingleTop
import edu.app.productivity.service.TimerService
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.ui.bottom.BottomBar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isTimerServiceBound by mutableStateOf(false)
    private lateinit var timerService: TimerService

    private val timerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as TimerService.TimerServiceBinder
            timerService = binder.getService()
            isTimerServiceBound = true
            Log.d(TAG, "${TimerService.TAG} is connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isTimerServiceBound = false
            Log.d(TAG, "${TimerService.TAG} is disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProductivityTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomBar(
                            onPreferencesClick = { navController.navigateSingleTop(Destination.PreferencesScreen) },
                            onHomeCLick = { navController.navigateSingleTop(Destination.HomeScreen) },
                            onStatisticsClick = { navController.navigateSingleTop(Destination.StatisticsScreen) }
                        )
                    },
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, timerServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(timerServiceConnection)
        isTimerServiceBound = false
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1

        private const val TAG = "MainActivity"
    }
}
