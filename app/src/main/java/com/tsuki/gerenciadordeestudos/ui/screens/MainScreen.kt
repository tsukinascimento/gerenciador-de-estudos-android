package com.tsuki.gerenciadordeestudos.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    // navController é o "motorista" que sabe como viajar entre os ecrãs
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        // NavHost é o espaço no meio do ecrã onde o conteúdo vai aparecer
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.Subjects.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Rota 1: Matérias (O ecrã que já construímos!)
            composable(ScreenRoute.Subjects.route) {
                SubjectScreen(viewModel = subjectViewModel)
            }
            // Rota 2: Tarefas (Faremos a seguir)
            composable(ScreenRoute.Tasks.route) {
                TaskScreen(
                    taskViewModel = taskViewModel,
                    subjectViewModel = subjectViewModel
                )
            }
            // Rota 3: Provas (Faremos a seguir)
            composable(ScreenRoute.Exams.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ecrã de Provas em Construção \uD83D\uDEA7")
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
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
                        // Estas configurações evitam que o utilizador abra o mesmo ecrã 50 vezes
                        // e sobrecarregue a memória do telemóvel ao clicar muito rápido.
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}