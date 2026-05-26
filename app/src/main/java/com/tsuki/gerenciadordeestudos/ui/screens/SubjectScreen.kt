package com.tsuki.gerenciadordeestudos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tsuki.gerenciadordeestudos.data.entity.Subject
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(viewModel: SubjectViewModel) {

    val subjects by viewModel.allSubjects.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    // Variável para guardar a matéria a ser editada
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }

    var itemToDelete by remember { mutableStateOf<Subject?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matérias") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Matéria")
            }
        }
    ) { paddingValues ->

        if (subjects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Ainda não adicionou nenhuma matéria.")
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(subjects) { subject ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { subjectToEdit = subject } // Tocar no cartão abre a edição
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = subject.name, style = MaterialTheme.typography.titleLarge)
                                Text(text = subject.description, style = MaterialTheme.typography.bodyMedium)
                            }

                            // Botão de Eliminar
                            IconButton(onClick = { itemToDelete = subject }) { // Abre o diálogo de confirmação
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
                        viewModel.deleteSubject(itemToDelete!!)
                        itemToDelete = null
                    }
                ) { Text("Apagar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancelar") }
            }
        )
    }
    // Janela Pop-up para Criar ou Editar
    if (showDialog || subjectToEdit != null) {

        // Preenche os campos se estiver a editar, ou deixa em branco se for nova
        var name by remember(subjectToEdit) { mutableStateOf(subjectToEdit?.name ?: "") }
        var description by remember(subjectToEdit) { mutableStateOf(subjectToEdit?.description ?: "") }

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                subjectToEdit = null
            },
            title = { Text(if (subjectToEdit == null) "Nova Matéria" else "Editar Matéria") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome da Matéria") },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            if (subjectToEdit == null) {
                                // Criar nova matéria
                                viewModel.insertSubject(
                                    Subject(name = name, description = description, colorCode = "#FF5733")
                                )
                            } else {
                                // Atualizar matéria existente
                                viewModel.updateSubject(
                                    subjectToEdit!!.copy(name = name, description = description)
                                )
                            }
                            showDialog = false
                            subjectToEdit = null
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    subjectToEdit = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}