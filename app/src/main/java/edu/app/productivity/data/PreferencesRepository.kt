package edu.app.productivity.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val dataStore: DataStoreManager
) {

    fun getPreferences(): Flow<Preferences> = dataStore.get {
        Preferences(
            timerSetupIsDial = this[TimerSetupIsDialKey] ?: TIMER_SETUP_IS_DIAL_DEFAULT,
            theme = this[ThemeKey]?.let { Preferences.Themes.values()[it] } ?: THEME_DEFAULT
        )
    }

    suspend fun savePreferences(preferences: Preferences) {
        dataStore.edit {
            this[TimerSetupIsDialKey] = preferences.timerSetupIsDial
            this[ThemeKey] = preferences.theme.ordinal
        }
    }

    companion object {
        private val TimerSetupIsDialKey = booleanPreferencesKey("timer_setup_is_dial")
        private val ThemeKey = intPreferencesKey("theme")

        const val TIMER_SETUP_IS_DIAL_DEFAULT = false
        val THEME_DEFAULT = Preferences.Themes.SYSTEM
    }

}