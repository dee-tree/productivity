package edu.app.productivity.data

import edu.app.productivity.data.db.ActionEntity
import edu.app.productivity.data.db.ActionHistoryEntity
import edu.app.productivity.data.db.AppDatabase
import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject

class TimerRepository @Inject constructor(val db: AppDatabase) {

    private val dao by lazy { db.actionHistoryDao() }

    private val _timerState = MutableStateFlow<TimerState>(TimerState.TimerNotInitiated)
    val timerState: StateFlow<TimerState> get() = _timerState

    private val _actions = MutableStateFlow<List<Action>>(listOf(Action.NotInitiatedAction))
    val actions: StateFlow<List<Action>> get() = _actions

    suspend fun updateState(newState: TimerState) {
        _timerState.emit(newState)
    }

    suspend fun createNewAction(newAction: Action) {
        _actions.emit(listOf(newAction))
    }

    suspend fun createNewActions(newActions: List<Action>) {
        _actions.emit(newActions)
    }

    suspend fun saveActionInHistory(action: Action) {
        dao.insert(ActionHistoryEntity(Date(), ActionEntity.fromAction(action)))
    }


}