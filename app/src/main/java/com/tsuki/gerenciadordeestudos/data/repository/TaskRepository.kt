package com.tsuki.gerenciadordeestudos.data.repository

import com.tsuki.gerenciadordeestudos.data.dao.TaskDao
import com.tsuki.gerenciadordeestudos.data.entity.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksBySubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksBySubject(subjectId)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}