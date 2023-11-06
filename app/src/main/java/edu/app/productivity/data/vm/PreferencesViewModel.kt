package edu.app.productivity.data.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.app.productivity.data.Preferences
import edu.app.productivity.data.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val repository: PreferencesRepository,
) : ViewModel() {

    val preferences: StateFlow<Preferences> = repository.getPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), Preferences())

    fun updatePreferences(newPreferences: Preferences) {
        viewModelScope.launch {
            repository.savePreferences(newPreferences)
        }
    }
}
