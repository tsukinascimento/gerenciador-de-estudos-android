package com.tsuki.gerenciadordeestudos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tsuki.gerenciadordeestudos.data.entity.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    // Adiciona uma nova matéria. Se tiver mesmo ID, substitui (REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    // Atualiza uma matéria existente
    @Update
    suspend fun updateSubject(subject: Subject)

    // Deleta uma matéria
    @Delete
    suspend fun deleteSubject(subject: Subject)

    // Busca todas as matérias em ordem alfabética
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    // Busca uma matéria específica pelo ID
    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectById(id: Int): Flow<Subject>
}