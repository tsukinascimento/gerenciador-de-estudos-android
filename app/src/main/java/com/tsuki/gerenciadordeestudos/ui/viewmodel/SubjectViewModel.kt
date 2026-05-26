package com.tsuki.gerenciadordeestudos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tsuki.gerenciadordeestudos.data.entity.Subject
import com.tsuki.gerenciadordeestudos.data.repository.SubjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubjectViewModel(private val repository: SubjectRepository) : ViewModel() {

    // Transformamos o Flow num StateFlow. O Compose "adora" o StateFlow porque
    // ele avisa imediatamente o ecrã para se redesenhar sempre que houver uma alteração na base de dados.
    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Mantém o fluxo vivo durante 5s caso o ecrã seja rodado
            initialValue = emptyList()
        )

    fun insertSubject(subject: Subject) {
        viewModelScope.launch {
            repository.insertSubject(subject)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            repository.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }
}

// O "Factory" ensina o Android a criar o nosso ViewModel, já que ele precisa do Repositório no construtor
class SubjectViewModelFactory(private val repository: SubjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubjectViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}