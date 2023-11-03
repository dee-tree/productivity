package edu.app.productivity.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.app.productivity.ui.HomeScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.HomeScreen.route) {
        composable(Destination.HomeScreen.route) {
            HomeScreen()
        }

        composable(Destination.StatisticsScreen.route) {
            // TODO: statistics
        }

        composable(Destination.MenuScreen.route) {
            // TODO: menu
        }
    }
}