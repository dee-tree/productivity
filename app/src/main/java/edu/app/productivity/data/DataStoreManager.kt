package edu.app.productivity.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DataStoreManager.PreferencesDataStoreName)

class DataStoreManager(private val ctx: Context) {

    suspend fun edit(editor: MutablePreferences.() -> Unit) {
        ctx.dataStore.edit(editor)
    }

    fun <T> get(getter: Preferences.() -> T): Flow<T> = ctx.dataStore.data.map(getter)

    companion object {
        internal const val PreferencesDataStoreName = "productivity_data_store"
    }
}