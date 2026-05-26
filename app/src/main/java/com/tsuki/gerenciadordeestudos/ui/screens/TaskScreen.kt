package com.tsuki.gerenciadordeestudos.ui.screens

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

    // Controla se a janela de NOVA tarefa está aberta
    var showDialog by remember { mutableStateOf(false) }

    // NOVO: Guarda qual tarefa o utilizador quer editar (se for nulo, significa que está a criar)
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    var itemToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarefas") },
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
                items(tasks) { task ->
                    val subjectName = subjects.find { it.id == task.subjectId }?.name ?: "Sem matéria"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { taskToEdit = task } // NOVO: Tocar no cartão abre o modo de edição
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // NOVO: Botão de concluir tarefa (Update do isCompleted)
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

                            // O "weight(1f)" empurra o botão de lixo lá para o canto direito
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

                            // NOVO: Botão de Deletar Tarefa
                            IconButton(onClick = { itemToDelete = task }) { // Abre o diálogo de confirmação
                                Icon(Icons.Filled.Delete, contentDescription = "Apagar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza de que deseja apagar este item? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskViewModel.deleteTask(itemToDelete!!)
                        itemToDelete = null
                    }
                ) { Text("Apagar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancelar") }
            }
        )
    }
    // A Janela Pop-up agora serve tanto para Criar quanto para Editar
    if (showDialog || taskToEdit != null) {

        // NOVO: O "remember(taskToEdit)" diz para preencher os campos se a tarefa existir, ou deixar em branco se for nova
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
                                // Criar
                                taskViewModel.insertTask(
                                    Task(
                                        title = title,
                                        description = description,
                                        subjectId = selectedSubject!!.id,
                                        dueDate = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                // NOVO: Atualiza a existente
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
                    Text("Guardar")
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
