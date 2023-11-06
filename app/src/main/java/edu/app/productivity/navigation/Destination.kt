package edu.app.productivity.navigation

sealed class Destination(
    val route: String
) {
    object HomeScreen: Destination("home_screen")
    object StatisticsScreen: Destination("statistics_screen")
    object PreferencesScreen: Destination("preferences_screen")
}
