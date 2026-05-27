package com.tsuki.gerenciadordeestudos.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tsuki.gerenciadordeestudos.data.entity.Exam
import com.tsuki.gerenciadordeestudos.data.receiver.ExamNotificationReceiver
import com.tsuki.gerenciadordeestudos.ui.viewmodel.ExamViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// NOVO: Função responsável por ir ao sistema do telemóvel e cravar o alarme
fun scheduleExamNotification(context: Context, examTitle: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Criamos uma carta (Intent) com o destino do nosso Mensageiro
    val intent = Intent(context, ExamNotificationReceiver::class.java).apply {
        putExtra("EXAM_TITLE", examTitle) // Colocamos o nome da prova na carta
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        System.currentTimeMillis().toInt(), // ID único para não sobrepor alarmes
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // MAGIA DE TESTE: O alarme vai tocar 5 SEGUNDOS depois de salvar a prova!
    // (Para um app real, usaríamos: tempoDaProva - 86400000 para avisar 1 dia antes)
    val triggerTime = System.currentTimeMillis() + 5000

    try {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    } catch (e: SecurityException) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(examViewModel: ExamViewModel, subjectViewModel: SubjectViewModel) {

    val exams by examViewModel.allExams.collectAsState()
    val subjects by subjectViewModel.allSubjects.collectAsState()

    // Apanhamos o "Contexto" (A ligação do ecrã com o sistema Android)
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var examToEdit by remember { mutableStateOf<Exam?>(null) }
    var itemToDelete by remember { mutableStateOf<Exam?>(null) }

    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
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
                items(items = exams, key = { it.id }) { exam ->
                    val subjectName = subjects.find { it.id == exam.subjectId }?.name ?: "Sem matéria"

                    // SWIPE-TO-DISMISS APLICADO ÀS PROVAS
                    val dismissState = rememberSwipeToDismissBoxState()

                    LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                            itemToDelete = exam
                            dismissState.reset()
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                                label = "cor_fundo"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 8.dp)
                                    .background(color, shape = MaterialTheme.shapes.medium)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Apagar", tint = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        },
                        content = {
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
                                        Icon(Icons.Filled.Delete, contentDescription = "Apagar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

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
                ) { Text("Apagar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    if (showDialog || examToEdit != null) {
        var title by remember(examToEdit) { mutableStateOf(examToEdit?.title ?: "") }
        var selectedDateMillis by remember(examToEdit) { mutableLongStateOf(examToEdit?.examDate ?: System.currentTimeMillis()) }
        var expanded by remember { mutableStateOf(false) }
        var selectedSubject by remember(examToEdit) { mutableStateOf(subjects.find { it.id == examToEdit?.subjectId }) }
        var showDatePicker by remember { mutableStateOf(false) }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        showDatePicker = false
                    }) { Text("Confirmar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) { DatePicker(state = datePickerState) }
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
                    Spacer(modifier = Modifier.height(16.dp))

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
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && selectedSubject != null) {
                            if (examToEdit == null) {
                                examViewModel.insertExam(
                                    Exam(title = title, examDate = selectedDateMillis, subjectId = selectedSubject!!.id)
                                )
                                // NOVO: CHAMAMOS A FUNÇÃO DO ALARME ASSIM QUE SALVAMOS!
                                scheduleExamNotification(context, title)
                            } else {
                                examViewModel.updateExam(
                                    examToEdit!!.copy(title = title, examDate = selectedDateMillis, subjectId = selectedSubject!!.id)
                                )
                            }
                            showDialog = false
                            examToEdit = null
                        }
                    }
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    examToEdit = null
                }) { Text("Cancelar") }
            }
        )
    }
}