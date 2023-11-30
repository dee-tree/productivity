package edu.app.productivity.data

import edu.app.productivity.data.db.ActionEntity
import edu.app.productivity.data.db.ActionHistoryEntity
import edu.app.productivity.data.db.AppDatabase
import edu.app.productivity.domain.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class ActionHistoryRepository @Inject constructor(
    private val db: AppDatabase,
    private val dataStore: DataStoreManager
) {

    private val dao by lazy { db.actionHistoryDao() }

    suspend fun getHistory(): Flow<List<ActionHistoryEntity>> = withContext(Dispatchers.IO) {
        dao.getAll()
    }

    suspend fun getHistory(lastDays: Int): Flow<List<ActionHistoryEntity>> = withContext(Dispatchers.IO) {
        dao.getAll(lastDays)
    }

    suspend fun insertAction(action: Action) {
        dao.insert(ActionHistoryEntity(Date(), ActionEntity.fromAction(action)))
    }

    suspend fun insertAction(action: ActionHistoryEntity) {
        dao.insert(action)
    }

    suspend fun deleteAction(action: ActionHistoryEntity) {
        dao.delete(action)
    }

    fun getAccountingDaysCount() = dataStore.get {
        this[PreferencesRepository.AccountingDaysCountKey]
            ?: PreferencesRepository.ACCOUNTING_DAYS_COUNT_DEFAULT
    }

}
