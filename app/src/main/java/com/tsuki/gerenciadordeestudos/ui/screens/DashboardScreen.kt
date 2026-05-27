package com.tsuki.gerenciadordeestudos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tsuki.gerenciadordeestudos.ui.viewmodel.ExamViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    subjectViewModel: SubjectViewModel,
    taskViewModel: TaskViewModel,
    examViewModel: ExamViewModel
) {
    // Observa todos os dados em tempo real
    val subjects by subjectViewModel.allSubjects.collectAsState()
    val tasks by taskViewModel.allTasks.collectAsState()
    val exams by examViewModel.allExams.collectAsState()

    // Cálculos de Resumo
    val pendingTasksCount = tasks.count { !it.isCompleted }
    val completedTasksCount = tasks.count { it.isCompleted }

    // Encontra a prova que está mais próxima (filtrando datas antigas)
    val currentTime = System.currentTimeMillis()
    val nextExam = exams.filter { it.examDate >= currentTime - 86400000 } // Inclui provas de hoje
        .minByOrNull { it.examDate }

    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visão Geral", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumo dos seus Estudos",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Linha com os cartões de Matérias e Tarefas Pendentes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cartão 1: Matérias
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Matérias",
                    value = subjects.size.toString(),
                    icon = Icons.Filled.Book,
                    color = MaterialTheme.colorScheme.primary
                )

                // Cartão 2: Tarefas Pendentes
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Pendentes",
                    value = pendingTasksCount.toString(),
                    icon = Icons.Filled.CheckCircle,
                    color = if (pendingTasksCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            // Cartão 3: Desempenho (Tarefas Concluídas)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Tarefas Concluídas", style = MaterialTheme.typography.titleMedium)
                        Text("Mantenha o bom ritmo!", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = completedTasksCount.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Cartão 4: Próxima Prova
            Text(
                text = "Próxima Prova",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (nextExam != null) {
                val subjectName = subjects.find { it.id == nextExam.subjectId }?.name ?: "Sem matéria"
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Próxima Prova",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = nextExam.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "$subjectName • ${dateFormat.format(Date(nextExam.examDate))}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Nenhuma prova a aproximar-se. 🎉")
                    }
                }
            }
        }
    }
}

// Componente reutilizável para os pequenos cartões quadrados
@Composable
fun DashboardCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, color: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}