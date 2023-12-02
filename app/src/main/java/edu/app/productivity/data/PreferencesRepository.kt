package edu.app.productivity.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import edu.app.productivity.data.db.AppDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val dataStore: DataStoreManager,
    private val db: AppDatabase
) {

    fun getPreferences(): Flow<Preferences> = dataStore.get {
        Preferences(
            timerSetupIsDial = this[TimerSetupIsDialKey] ?: TIMER_SETUP_IS_DIAL_DEFAULT,
            theme = this[ThemeKey]?.let { Preferences.Themes.values()[it] } ?: THEME_DEFAULT,
            accountingDaysCount = this[AccountingDaysCountKey] ?: ACCOUNTING_DAYS_COUNT_DEFAULT
        )
    }

    suspend fun savePreferences(preferences: Preferences) {
        dataStore.edit {
            this[TimerSetupIsDialKey] = preferences.timerSetupIsDial
            this[ThemeKey] = preferences.theme.ordinal
            this[AccountingDaysCountKey] = preferences.accountingDaysCount
        }
    }

    suspend fun clearAllData() {
        db.clearAllTables()
    }

    companion object {
        private val TimerSetupIsDialKey = booleanPreferencesKey("timer_setup_is_dial")
        private val ThemeKey = intPreferencesKey("theme")
        val AccountingDaysCountKey = intPreferencesKey("accounting_days_count")

        const val TIMER_SETUP_IS_DIAL_DEFAULT = false
        val THEME_DEFAULT = Preferences.Themes.SYSTEM
        const val ACCOUNTING_DAYS_COUNT_DEFAULT = 7
        val ACCOUNTING_DAYS_RANGE = 7..30
    }

}
