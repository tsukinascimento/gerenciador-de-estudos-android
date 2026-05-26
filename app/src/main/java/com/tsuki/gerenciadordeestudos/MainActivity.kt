package com.tsuki.gerenciadordeestudos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tsuki.gerenciadordeestudos.ui.screens.MainScreen
import com.tsuki.gerenciadordeestudos.ui.theme.GerenciadorDeEstudosTheme
import com.tsuki.gerenciadordeestudos.ui.viewmodel.ExamViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.ExamViewModelFactory
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModelFactory
import com.tsuki.gerenciadordeestudos.ui.viewmodel.TaskViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as StudyApplication

        // Inicializamos os 3 cérebros da aplicação
        val subjectViewModel: SubjectViewModel by viewModels {
            SubjectViewModelFactory(app.subjectRepository)
        }
        val taskViewModel: TaskViewModel by viewModels {
            TaskViewModelFactory(app.taskRepository)
        }
        val examViewModel: ExamViewModel by viewModels {
            ExamViewModelFactory(app.examRepository)
        }

        setContent {
            GerenciadorDeEstudosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Agora chamamos o Menu Mestre em vez de apenas o ecrã de matérias
                    MainScreen(
                        subjectViewModel = subjectViewModel,
                        taskViewModel = taskViewModel,
                        examViewModel = examViewModel
                    )
                }
            }
        }
    }
}