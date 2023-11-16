package edu.app.productivity.data.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.app.productivity.data.ActionHistoryRepository
import edu.app.productivity.data.db.ActionHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val repository: ActionHistoryRepository) :
    ViewModel() {

    private val _actionHistory = MutableStateFlow<List<ActionHistoryEntity>>(emptyList())
    val actionHistory: StateFlow<List<ActionHistoryEntity>>
        get() = _actionHistory

    private val _totallyWorkedHours = MutableStateFlow(0.0)
    val totallyWorkedHours: StateFlow<Double>
        get() = _totallyWorkedHours

    private val _totallyRestHours = MutableStateFlow(0.0)
    val totallyRestHours: StateFlow<Double>
        get() = _totallyRestHours

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                launch {
                    repository.getHistory().collect { historyActions ->
                        _actionHistory.emit(historyActions)

                        val (restActions, workActions) = historyActions.groupBy { it.action.isRest }
                            .let { it[true]!! to it[false]!! }

                        _totallyWorkedHours.emit(workActions.sumOf { it.action.duration.inWholeMinutes } / 60.0)
                        _totallyRestHours.emit(restActions.sumOf { it.action.duration.inWholeMinutes } / 60.0)
                    }
                }
            }
        }
    }

}
