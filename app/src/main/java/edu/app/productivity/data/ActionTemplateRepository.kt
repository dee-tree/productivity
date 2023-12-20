package edu.app.productivity.data

import edu.app.productivity.data.db.ActionsTemplateEntity
import edu.app.productivity.data.db.AppDatabase
import edu.app.productivity.domain.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActionTemplateRepository @Inject constructor(
    private val db: AppDatabase
) {
    private val dao by lazy { db.actionsTemplateDao() }

    suspend fun getTemplates(): Flow<List<ActionsTemplateEntity>> = withContext(Dispatchers.IO) {
        dao.getAll()
    }

    suspend fun insertTemplate(template: ActionsTemplateEntity) {
        dao.insert(template)
    }

    suspend fun insertTemplate(name: String, actions: List<Action>) {
        insertTemplate(ActionsTemplateEntity(name, actions))
    }

    suspend fun deleteTemplate(template: ActionsTemplateEntity) {
        dao.delete(template)
    }

}
