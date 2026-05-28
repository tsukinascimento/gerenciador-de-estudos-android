package com.tsuki.gerenciadordeestudos

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val app = application as StudyApplication

        val subjectViewModel: SubjectViewModel by viewModels { SubjectViewModelFactory(app.subjectRepository) }
        val taskViewModel: TaskViewModel by viewModels { TaskViewModelFactory(app.taskRepository) }
        val examViewModel: ExamViewModel by viewModels { ExamViewModelFactory(app.examRepository) }

        setContent {
            // Lógica do Tema: Descobre se o celular já está no modo escuro
            val systemTheme = isSystemInDarkTheme()
            // Variável que o nosso botão vai alterar
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            // Passamos a nossa variável para o tema oficial do app
            GerenciadorDeEstudosTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        subjectViewModel = subjectViewModel,
                        taskViewModel = taskViewModel,
                        examViewModel = examViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme } // O interruptor!
                    )
                }
            }
        }
    }
}