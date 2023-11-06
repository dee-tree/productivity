package edu.app.productivity.data

import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerRepository {
    init {
        println("REPOSITORY CREATED!!")
    }

    private val _timerState = MutableStateFlow<TimerState>(TimerState.TimerNotInitiated)
    val timerState: StateFlow<TimerState> get() = _timerState

    private val _action = MutableStateFlow<Action>(Action.NotInitiatedAction)
    val action: StateFlow<Action> get() = _action

    suspend fun updateState(newState: TimerState) {
        _timerState.emit(newState)
    }

    suspend fun createNewAction(newAction: Action) {
        _action.emit(newAction)
    }


}