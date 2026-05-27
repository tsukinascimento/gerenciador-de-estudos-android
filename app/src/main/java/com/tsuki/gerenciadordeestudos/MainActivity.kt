package com.tsuki.gerenciadordeestudos

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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

    // NOVO: Criamos o "Lançador" que vai exibir a janela de permissão do sistema
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Se o utilizador negar, não enviamos notificações.
        // O sistema Android gere a resposta automaticamente, por isso não precisamos de código extra aqui.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // NOVO: Assim que o aplicativo abre, verificamos a versão do Android.
        // Se for 13 (TIRAMISU) ou superior, pedimos a permissão na tela.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val app = application as StudyApplication

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