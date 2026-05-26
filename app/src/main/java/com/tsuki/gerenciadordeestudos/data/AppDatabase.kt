package com.tsuki.gerenciadordeestudos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tsuki.gerenciadordeestudos.data.dao.ExamDao
import com.tsuki.gerenciadordeestudos.data.dao.SubjectDao
import com.tsuki.gerenciadordeestudos.data.dao.TaskDao
import com.tsuki.gerenciadordeestudos.data.entity.Exam
import com.tsuki.gerenciadordeestudos.data.entity.Subject
import com.tsuki.gerenciadordeestudos.data.entity.Task

// 1. Quais as entidades (tabelas) que pertencem a esta base de dados
@Database(entities = [Subject::class, Task::class, Exam::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 2. Acesso aos DAOs que criei
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun examDao(): ExamDao

    // 3. Garante que apenas uma instância da base de dados é aberta de cada vez
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_manager_database" // Nome do ficheiro da base de dados no sistema
                )
                    .fallbackToDestructiveMigration(dropAllTables = true) // Se mudarmos a estrutura, limpa e recria
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}