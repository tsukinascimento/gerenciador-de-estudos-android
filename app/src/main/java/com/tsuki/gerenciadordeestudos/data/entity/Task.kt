package com.tsuki.gerenciadordeestudos.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subjectId"),
            onDelete = ForeignKey.CASCADE // Se a matéria for apagada, as tarefas também serão
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long, // Utilizei Long (Timestamp Unix) para guardar datas, é a prática mais segura em SQLite
    val isCompleted: Boolean = false,
    val subjectId: Int // Chave Estrangeira que liga esta tarefa à matéria
)