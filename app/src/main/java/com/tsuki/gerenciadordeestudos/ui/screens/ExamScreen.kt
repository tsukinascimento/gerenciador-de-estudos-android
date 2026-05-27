package com.tsuki.gerenciadordeestudos.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import com.tsuki.gerenciadordeestudos.data.entity.Exam
import com.tsuki.gerenciadordeestudos.ui.viewmodel.ExamViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(examViewModel: ExamViewModel, subjectViewModel: SubjectViewModel) {

    val exams by examViewModel.allExams.collectAsState()
    val subjects by subjectViewModel.allSubjects.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var examToEdit by remember { mutableStateOf<Exam?>(null) }
    var itemToDelete by remember { mutableStateOf<Exam?>(null) }

    // Formatador para transformar o código da data (Long) em algo legível como "25/12/2026"
    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Provas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.tertiary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Prova")
            }
        }
    ) { paddingValues ->

        if (exams.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Nenhuma prova agendada. Relaxe!")
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(exams) { exam ->
                    val subjectName = subjects.find { it.id == exam.subjectId }?.name ?: "Sem matéria"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { examToEdit = exam }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ícone de calendário decorativo
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Data da Prova",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = exam.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "$subjectName • ${dateFormat.format(Date(exam.examDate))}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { itemToDelete = exam }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Apagar Prova",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // JANELA DE CONFIRMAÇÃO DE DELETAR
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza de que deseja apagar a prova '${itemToDelete?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        examViewModel.deleteExam(itemToDelete!!)
                        itemToDelete = null
                    }
                ) {
                    Text("Apagar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Janela Pop-up para Criar ou Editar Prova
    if (showDialog || examToEdit != null) {
        var title by remember(examToEdit) { mutableStateOf(examToEdit?.title ?: "") }
        var selectedDateMillis by remember(examToEdit) { mutableLongStateOf(examToEdit?.examDate ?: System.currentTimeMillis()) }

        var expanded by remember { mutableStateOf(false) }
        var selectedSubject by remember(examToEdit) {
            mutableStateOf(subjects.find { it.id == examToEdit?.subjectId })
        }

        // Variável que controla se o calendário está aberto
        var showDatePicker by remember { mutableStateOf(false) }

        // O Calendário Interativo (DatePicker)
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        // Salva a data escolhida (ou a atual se der erro)
                        selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        showDatePicker = false
                    }) { Text("Confirmar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                examToEdit = null
            },
            title = { Text(if (examToEdit == null) "Nova Prova" else "Editar Prova") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Assunto / Título da Prova") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu de Matérias (Igual ao das Tarefas)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSubject?.name ?: "Selecione uma Matéria",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            if (subjects.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Crie uma matéria primeiro!") },
                                    onClick = { expanded = false }
                                )
                            } else {
                                subjects.forEach { subject ->
                                    DropdownMenuItem(
                                        text = { Text(subject.name) },
                                        onClick = {
                                            selectedSubject = subject
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // NOVO: Campo de Data (Clica e abre o calendário)
                    OutlinedTextField(
                        value = dateFormat.format(Date(selectedDateMillis)),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Data da Prova") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Filled.DateRange, contentDescription = "Selecionar Data")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true } // Abre o calendário ao tocar em qualquer lugar do campo
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && selectedSubject != null) {
                            if (examToEdit == null) {
                                examViewModel.insertExam(
                                    Exam(
                                        title = title,
                                        examDate = selectedDateMillis,
                                        subjectId = selectedSubject!!.id
                                    )
                                )
                            } else {
                                examViewModel.updateExam(
                                    examToEdit!!.copy(
                                        title = title,
                                        examDate = selectedDateMillis,
                                        subjectId = selectedSubject!!.id
                                    )
                                )
                            }
                            showDialog = false
                            examToEdit = null
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    examToEdit = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}