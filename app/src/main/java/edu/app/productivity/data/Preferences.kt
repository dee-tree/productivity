package edu.app.productivity.data

data class Preferences(
    /**
     * Set up duration via dial or via keyboard like numbers
     */
    val timerSetupIsDial: Boolean = PreferencesRepository.TIMER_SETUP_IS_DIAL_DEFAULT,
    val theme: Themes = PreferencesRepository.THEME_DEFAULT,
    val accountingDaysCount: Int = PreferencesRepository.ACCOUNTING_DAYS_COUNT_DEFAULT
) {


    enum class Themes {
        LIGHT, SYSTEM, DARK
    }
}
