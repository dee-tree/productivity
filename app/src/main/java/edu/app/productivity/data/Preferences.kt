package edu.app.productivity.data

data class Preferences(
    /**
     * Set up duration via dial or via keyboard like numbers
     */
    val timerSetupIsDial: Boolean = false,
    val theme: Themes = Themes.SYSTEM,
) {


    enum class Themes {
        LIGHT, SYSTEM, DARK
    }
}
