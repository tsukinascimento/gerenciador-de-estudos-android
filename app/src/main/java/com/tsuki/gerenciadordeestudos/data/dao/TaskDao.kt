package com.tsuki.gerenciadordeestudos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tsuki.gerenciadordeestudos.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // Busca todas as tarefas associadas a uma matéria específica
    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId ORDER BY dueDate ASC")
    fun getTasksBySubject(subjectId: Int): Flow<List<Task>>

    // Busca todas as tarefas do aplicativo (pendentes primeiro, depois por data)
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>
}