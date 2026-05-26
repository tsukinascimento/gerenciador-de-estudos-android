package com.tsuki.gerenciadordeestudos.data.repository

import com.tsuki.gerenciadordeestudos.data.dao.SubjectDao
import com.tsuki.gerenciadordeestudos.data.entity.Subject
import kotlinx.coroutines.flow.Flow

// O repositório recebe o DAO no seu construtor para poder aceder às funções da base de dados
class SubjectRepository(private val subjectDao: SubjectDao) {

    // Variável que expõe a lista de matérias (o Flow garante a atualização em tempo real)
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()

    fun getSubjectById(id: Int): Flow<Subject> {
        return subjectDao.getSubjectById(id)
    }

    suspend fun insertSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
    }

    suspend fun deleteSubject(subject: Subject) {
        subjectDao.deleteSubject(subject)
    }
}