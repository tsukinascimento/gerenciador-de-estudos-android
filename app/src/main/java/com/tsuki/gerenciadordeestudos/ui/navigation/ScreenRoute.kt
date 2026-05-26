package com.tsuki.gerenciadordeestudos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

// "Sealed class" é uma classe especial no Kotlin que restringe os tipos.
// Garante que só existam exatamente estas três rotas no nosso menu.
sealed class ScreenRoute(val route: String, val title: String, val icon: ImageVector) {
    object Subjects : ScreenRoute("subjects", "Matérias", Icons.Filled.Book)
    object Tasks : ScreenRoute("tasks", "Tarefas", Icons.Filled.CheckCircle)
    object Exams : ScreenRoute("exams", "Provas", Icons.Filled.DateRange)
}