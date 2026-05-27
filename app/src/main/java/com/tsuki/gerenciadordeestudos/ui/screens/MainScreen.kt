package com.tsuki.gerenciadordeestudos.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tsuki.gerenciadordeestudos.ui.navigation.ScreenRoute
import com.tsuki.gerenciadordeestudos.ui.viewmodel.ExamViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.TaskViewModel

@Composable
fun MainScreen(
    subjectViewModel: SubjectViewModel,
    taskViewModel: TaskViewModel,
    examViewModel: ExamViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.Dashboard.route, // AGORA INICIA NO DASHBOARD
            modifier = Modifier.padding(innerPadding)
        ) {
            // Rota 0: Dashboard (NOVO)
            composable(ScreenRoute.Dashboard.route) {
                DashboardScreen(
                    subjectViewModel = subjectViewModel,
                    taskViewModel = taskViewModel,
                    examViewModel = examViewModel
                )
            }
            // Rota 1: Matérias
            composable(ScreenRoute.Subjects.route) {
                SubjectScreen(viewModel = subjectViewModel)
            }
            // Rota 2: Tarefas
            composable(ScreenRoute.Tasks.route) {
                TaskScreen(
                    taskViewModel = taskViewModel,
                    subjectViewModel = subjectViewModel
                )
            }
            // Rota 3: Provas
            composable(ScreenRoute.Exams.route) {
                ExamScreen(
                    examViewModel = examViewModel,
                    subjectViewModel = subjectViewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Adicionámos o Dashboard no topo da lista
    val items = listOf(
        ScreenRoute.Dashboard,
        ScreenRoute.Subjects,
        ScreenRoute.Tasks,
        ScreenRoute.Exams
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}