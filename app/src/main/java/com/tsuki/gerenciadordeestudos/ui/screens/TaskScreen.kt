package com.tsuki.gerenciadordeestudos.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tsuki.gerenciadordeestudos.data.entity.Subject
import com.tsuki.gerenciadordeestudos.data.entity.Task
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel
import com.tsuki.gerenciadordeestudos.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(taskViewModel: TaskViewModel, subjectViewModel: SubjectViewModel) {

    val tasks by taskViewModel.allTasks.collectAsState()
    val subjects by subjectViewModel.allSubjects.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var itemToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.secondary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    ) { paddingValues ->

        if (tasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Nenhuma tarefa pendente. Excelente!")
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(items = tasks, key = { it.id }) { task ->
                    val subjectName = subjects.find { it.id == task.subjectId }?.name ?: "Sem matéria"

                    // NOVA FORMA OFICIAL DO COMPOSE PARA O SWIPE
                    val dismissState = rememberSwipeToDismissBoxState()

                    // Observador: Fica de olho no estado do arrasto
                    LaunchedEffect(dismissState.currentValue) {
                        // Se o cartão for arrastado para fora do lugar...
                        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                            itemToDelete = task // 1. Abre a janela de confirmação de lixo
                            dismissState.reset() // 2. Manda o cartão voltar para o lugar visualmente
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
                                    MaterialTheme.colorScheme.errorContainer
                                } else {
                                    Color.Transparent
                                }, label = "cor_fundo_swipe"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 8.dp)
                                    .background(color, shape = MaterialTheme.shapes.medium)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Apagar",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        },
                        content = {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { taskToEdit = task }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = {
                                        taskViewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                                    }) {
                                        Icon(
                                            imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                            contentDescription = "Concluir Tarefa",
                                            tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "$subjectName - ${task.description}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = { itemToDelete = task }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Apagar Tarefa",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // JANELA DE CONFIRMAÇÃO DE DELETAR
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza de que deseja apagar a tarefa '${itemToDelete?.title}'? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskViewModel.deleteTask(itemToDelete!!)
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

    // Janela Pop-up para Criar ou Editar
    if (showDialog || taskToEdit != null) {
        var title by remember(taskToEdit) { mutableStateOf(taskToEdit?.title ?: "") }
        var description by remember(taskToEdit) { mutableStateOf(taskToEdit?.description ?: "") }
        var expanded by remember { mutableStateOf(false) }
        var selectedSubject by remember(taskToEdit) {
            mutableStateOf(subjects.find { it.id == taskToEdit?.subjectId })
        }

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                taskToEdit = null
            },
            title = { Text(if (taskToEdit == null) "Nova Tarefa" else "Editar Tarefa") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título da Tarefa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição") },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && selectedSubject != null) {
                            if (taskToEdit == null) {
                                taskViewModel.insertTask(
                                    Task(
                                        title = title,
                                        description = description,
                                        subjectId = selectedSubject!!.id,
                                        dueDate = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                taskViewModel.updateTask(
                                    taskToEdit!!.copy(
                                        title = title,
                                        description = description,
                                        subjectId = selectedSubject!!.id
                                    )
                                )
                            }
                            showDialog = false
                            taskToEdit = null
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    taskToEdit = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}