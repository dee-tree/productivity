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

    private val _totallyWorkedHours = MutableStateFlow(0.0)
    val totallyWorkedHours: StateFlow<Double>
        get() = _totallyWorkedHours

    private val _totallyRestHours = MutableStateFlow(0.0)
    val totallyRestHours: StateFlow<Double>
        get() = _totallyRestHours

    private val _workActionsPerDays = MutableStateFlow<List<List<ActionHistoryEntity>>>(emptyList())
    val workActionsPerDays: StateFlow<List<List<ActionHistoryEntity>>>
        get() = _workActionsPerDays

    private val _restActionsPerDays = MutableStateFlow<List<List<ActionHistoryEntity>>>(emptyList())
    val restActionsPerDays: StateFlow<List<List<ActionHistoryEntity>>>
        get() = _restActionsPerDays

    private val lastDaysCountFlow = repository.getStatisticsDaysCount()

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

                                _totallyWorkedHours.emit(workActions.sumOf { it.action.duration.inWholeMinutes } / 60.0)
                                _totallyRestHours.emit(restActions.sumOf { it.action.duration.inWholeMinutes } / 60.0)

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
