package edu.app.productivity.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.app.productivity.ui.HomeScreen
import edu.app.productivity.ui.PreferencesScreen
import edu.app.productivity.ui.StatisticsScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.HomeScreen.route) {
        composable(Destination.HomeScreen.route) {
            HomeScreen()
        }

        composable(Destination.StatisticsScreen.route) {
            StatisticsScreen()
        }

        composable(Destination.PreferencesScreen.route) {
            PreferencesScreen()
        }
    }
}

fun NavHostController.navigate(destination: Destination) = navigate(destination.route)

fun NavHostController.navigateSingleTop(destination: Destination) = navigate(destination.route) {
    // Pop up to the start destination of the graph to
    // avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(graph.findStartDestination().id) {
        saveState = true
    }
    // Avoid multiple copies of the same destination when
    // reselecting the same item
    launchSingleTop = true
    // Restore state when reselecting a previously selected item
    restoreState = true
}