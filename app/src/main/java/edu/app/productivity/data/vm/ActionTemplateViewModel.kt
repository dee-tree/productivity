package edu.app.productivity.data.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.app.productivity.data.ActionTemplateRepository
import edu.app.productivity.data.db.ActionsTemplateEntity
import edu.app.productivity.domain.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ActionTemplateViewModel @Inject constructor(
    private val repository: ActionTemplateRepository
) : ViewModel() {
    private var _templates = MutableStateFlow<List<ActionsTemplateEntity>>(emptyList())
    val templates: StateFlow<List<ActionsTemplateEntity>>
        get() = _templates

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                launch {
                    repository.getTemplates().collect {
                        _templates.emit(it)
                    }
                }
            }
        }
    }

    fun isNameValid(name: String) = name.isNotBlank() && templates.value.all {
        !it.name.equals(name, ignoreCase = true)
    }

    fun addTemplate(name: String, actions: List<Action>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                launch {
                    repository.insertTemplate(name, actions)
                }
            }
        }
    }

    fun deleteTemplate(template: ActionsTemplateEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                launch {
                    repository.deleteTemplate(template)
                }
            }
        }
    }

}