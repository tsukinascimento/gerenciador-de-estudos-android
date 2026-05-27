package com.tsuki.gerenciadordeestudos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenRoute(val route: String, val title: String, val icon: ImageVector) {
    // Nova rota para o Dashboard
    object Dashboard : ScreenRoute("dashboard", "Início", Icons.Filled.Home)

    object Subjects : ScreenRoute("subjects", "Matérias", Icons.Filled.Book)
    object Tasks : ScreenRoute("tasks", "Tarefas", Icons.Filled.CheckCircle)
    object Exams : ScreenRoute("exams", "Provas", Icons.Filled.DateRange)
}