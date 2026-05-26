package com.tsuki.gerenciadordeestudos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tsuki.gerenciadordeestudos.data.entity.Exam
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam)

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    // Busca todas as provas associadas a uma matéria
    @Query("SELECT * FROM exams WHERE subjectId = :subjectId ORDER BY examDate ASC")
    fun getExamsBySubject(subjectId: Int): Flow<List<Exam>>

    // Busca todas as provas agendadas, da mais próxima para a mais distante
    @Query("SELECT * FROM exams ORDER BY examDate ASC")
    fun getAllExams(): Flow<List<Exam>>
}