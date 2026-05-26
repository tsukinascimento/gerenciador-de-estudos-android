package com.tsuki.gerenciadordeestudos.data.repository

import com.tsuki.gerenciadordeestudos.data.dao.ExamDao
import com.tsuki.gerenciadordeestudos.data.entity.Exam
import kotlinx.coroutines.flow.Flow

class ExamRepository(private val examDao: ExamDao) {

    val allExams: Flow<List<Exam>> = examDao.getAllExams()

    fun getExamsBySubject(subjectId: Int): Flow<List<Exam>> {
        return examDao.getExamsBySubject(subjectId)
    }

    suspend fun insertExam(exam: Exam) {
        examDao.insertExam(exam)
    }

    suspend fun updateExam(exam: Exam) {
        examDao.updateExam(exam)
    }

    suspend fun deleteExam(exam: Exam) {
        examDao.deleteExam(exam)
    }
}