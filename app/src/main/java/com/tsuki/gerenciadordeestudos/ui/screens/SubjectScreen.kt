package com.tsuki.gerenciadordeestudos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tsuki.gerenciadordeestudos.R
import com.tsuki.gerenciadordeestudos.data.entity.Subject
import com.tsuki.gerenciadordeestudos.ui.viewmodel.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(viewModel: SubjectViewModel) {

    val subjects by viewModel.allSubjects.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var itemToDelete by remember { mutableStateOf<Subject?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Matérias") },
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

                    // CARD ESTILO GOOGLE CLASSROOM
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp) // Um pouco mais de espaço entre os cards
                            .clickable { subjectToEdit = subject },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Adiciona uma sombrinha
                    ) {
                        Column {
                            // 1. O CABEÇALHO (BANNER COLORIDO COM IMAGEM)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(MaterialTheme.colorScheme.primary) // Fundo da cor primária do app
                            ) {
                                // Imagem de fundo (Aqui usei a imagem padrão do Android para testar)
                                // Depois, posso arrastar qualquer .jpg ou .png para a pasta res/drawable
                                // e trocar o R.drawable.ic_launcher_background pelo nome da imagem!
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_background),
                                    contentDescription = "Capa da Matéria",
                                    contentScale = ContentScale.Crop, // Corta a imagem para preencher o banner
                                    modifier = Modifier.fillMaxSize(),
                                    alpha = 0.2f // Deixa a imagem semi-transparente para o texto ficar legível
                                )

                                // O Nome da Matéria alinhado no canto inferior esquerdo do banner
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                )
                            }

                            // 2. O CORPO DO CARD (Descrição e Lixeira)
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = subject.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(onClick = { itemToDelete = subject }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Apagar Matéria",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // NOVA JANELA DE CONFIRMAÇÃO DE DELETAR
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza de que deseja apagar a matéria '${itemToDelete?.name}'? ATENÇÃO: Isso também vai apagar todas as tarefas e provas associadas a ela!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSubject(itemToDelete!!)
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

    // Janela Pop-up para Criar ou Editar (Continua igual)
    if (showDialog || subjectToEdit != null) {

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
                                viewModel.insertSubject(
                                    Subject(name = name, description = description, colorCode = "#FF5733")
                                )
                            } else {
                                viewModel.updateSubject(
                                    subjectToEdit!!.copy(name = name, description = description)
                                )
                            }
                            showDialog = false
                            subjectToEdit = null
                        }
                    }
                ) {
                    Text("Salvar")
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