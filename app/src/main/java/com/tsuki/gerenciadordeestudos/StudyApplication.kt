package com.tsuki.gerenciadordeestudos

import android.app.Application
import com.tsuki.gerenciadordeestudos.data.AppDatabase
import com.tsuki.gerenciadordeestudos.data.repository.ExamRepository
import com.tsuki.gerenciadordeestudos.data.repository.SubjectRepository
import com.tsuki.gerenciadordeestudos.data.repository.TaskRepository

class StudyApplication : Application() {

    // A palavra "lazy" (preguiçoso) significa que a base de dados só será ativada
    // no momento exato em que for necessária pela primeira vez, poupando memória.
    val database by lazy { AppDatabase.getDatabase(this) }

    val subjectRepository by lazy { SubjectRepository(database.subjectDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val examRepository by lazy { ExamRepository(database.examDao()) }
}