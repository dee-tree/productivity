package edu.app.productivity.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val dataStore: DataStoreManager
) {

    fun getPreferences(): Flow<Preferences> = dataStore.get {
        Preferences(
            this[TimerSetupIsDialKey] ?: TIMER_SETUP_IS_DIAL_DEFAULT
        )
    }

    suspend fun savePreferences(preferences: Preferences) {
        dataStore.edit {
            this[TimerSetupIsDialKey] = preferences.timerSetupIsDial
        }
    }

    companion object {
        private val TimerSetupIsDialKey = booleanPreferencesKey("timer_setup_is_dial")

        const val TIMER_SETUP_IS_DIAL_DEFAULT = false
    }

}