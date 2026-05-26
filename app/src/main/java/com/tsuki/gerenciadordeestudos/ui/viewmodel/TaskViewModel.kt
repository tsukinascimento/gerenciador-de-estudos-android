package com.tsuki.gerenciadordeestudos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tsuki.gerenciadordeestudos.data.entity.Task
import com.tsuki.gerenciadordeestudos.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // Lista de todas as tarefas do aplicativo em tempo real
    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Função para buscar tarefas apenas de uma matéria específica
    fun getTasksBySubject(subjectId: Int) = repository.getTasksBySubject(subjectId)

    fun insertTask(task: Task) {
        viewModelScope.launch { repository.insertTask(task) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }
}

// Factory para ensinar o Android a construir este ViewModel
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}