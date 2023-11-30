package edu.app.productivity.data.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.app.productivity.data.ActionHistoryRepository
import edu.app.productivity.data.calendarDaysBetween
import edu.app.productivity.data.db.ActionHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val repository: ActionHistoryRepository) :
    ViewModel() {

    private val _actionHistory = MutableStateFlow<List<ActionHistoryEntity>>(emptyList())
    val actionHistory: StateFlow<List<ActionHistoryEntity>>
        get() = _actionHistory

    private val _totallyWorkedMinutes = MutableStateFlow(0)
    val totallyWorkedMinutes: StateFlow<Int>
        get() = _totallyWorkedMinutes

    private val _totallyRestMinutes = MutableStateFlow(0)
    val totallyRestMinutes: StateFlow<Int>
        get() = _totallyRestMinutes

    private val _workActionsPerDays = MutableStateFlow<List<List<ActionHistoryEntity>>>(emptyList())
    val workActionsPerDays: StateFlow<List<List<ActionHistoryEntity>>>
        get() = _workActionsPerDays

    private val _restActionsPerDays = MutableStateFlow<List<List<ActionHistoryEntity>>>(emptyList())
    val restActionsPerDays: StateFlow<List<List<ActionHistoryEntity>>>
        get() = _restActionsPerDays

    val lastDaysCountFlow = repository.getAccountingDaysCount()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                launch {

                    lastDaysCountFlow.collect { lastDaysCount ->

                        launch(Dispatchers.IO) {
                            repository.getHistory(lastDaysCount).collect { historyActions ->
                                _actionHistory.emit(historyActions)

                                val (restActions, workActions) = historyActions.groupBy { it.action.isRest }
                                    .let {
                                        (it[true] ?: emptyList()) to (it[false] ?: emptyList())
                                    }

                                _totallyWorkedMinutes.emit(
                                    workActions.sumOf { it.action.duration.inWholeMinutes }.toInt()
                                )
                                _totallyRestMinutes.emit(
                                    restActions.sumOf { it.action.duration.inWholeMinutes }.toInt()
                                )

                                _workActionsPerDays.emit(
                                    workActions.toActionsPerDay(lastDaysCount)
                                )
                                _restActionsPerDays.emit(
                                    restActions.toActionsPerDay(lastDaysCount)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

private fun List<ActionHistoryEntity>.toActionsPerDay(days: Int) = groupBy {
    it.completedAt.toInstant().atZone(ZoneId.systemDefault()).let { instant ->
        instant.dayOfYear + instant.year
    }
}.map { (day, actions) -> day to actions }.sortedByDescending { it.first }.map { it.second }.let {

    val nearestEmptyDays = it.firstOrNull()
        ?.firstOrNull()
        ?.completedAt
        ?.toInstant()
        ?.let { nearestDay ->
            calendarDaysBetween(
                nearestDay.atZone(ZoneId.systemDefault()),
                Instant.now().atZone(ZoneId.systemDefault())
            )
        } ?: days

    List(nearestEmptyDays) { emptyList<ActionHistoryEntity>() } + it + List(days - nearestEmptyDays - it.size) { emptyList() }
}
