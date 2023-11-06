package edu.app.productivity.data.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.app.productivity.data.TimerRepository
import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerManager
import edu.app.productivity.domain.TimerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class TimerViewModel @Inject constructor(private val repository: TimerRepository) : ViewModel(),
    TimerManager {

    private var _timerState = MutableStateFlow<TimerState>(TimerState.TimerNotInitiated)
    override val timerState: StateFlow<TimerState>
        get() = _timerState

    private var _action = MutableStateFlow<List<Action>>(listOf(Action.NotInitiatedAction))
    val actions: StateFlow<List<Action>>
        get() = _action

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                launch {
                    repository.timerState.collect {
                        _timerState.emit(it)
                    }
                }

                launch {
                    repository.actions.collect {
                        _action.emit(it)
                    }
                }
            }
        }
    }

    fun clearTimer() {
        viewModelScope.launch {
            repository.updateState(TimerState.TimerNotInitiated)
        }
    }

    fun createAction(newAction: Action) {
        viewModelScope.launch {
            repository.createNewAction(newAction)
        }
    }

    fun createActions(newActions: List<Action>) {
        viewModelScope.launch {
            repository.createNewActions(newActions)
        }
    }

}
