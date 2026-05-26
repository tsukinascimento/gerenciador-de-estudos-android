package com.tsuki.gerenciadordeestudos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tsuki.gerenciadordeestudos.data.entity.Exam
import com.tsuki.gerenciadordeestudos.data.repository.ExamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamViewModel(private val repository: ExamRepository) : ViewModel() {

    val allExams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getExamsBySubject(subjectId: Int) = repository.getExamsBySubject(subjectId)

    fun insertExam(exam: Exam) {
        viewModelScope.launch { repository.insertExam(exam) }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch { repository.updateExam(exam) }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch { repository.deleteExam(exam) }
    }
}

class ExamViewModelFactory(private val repository: ExamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExamViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}