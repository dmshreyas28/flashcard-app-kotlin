package com.flashmaster.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flashmaster.app.ui.viewmodel.FlashcardViewModel
import com.flashmaster.app.util.CsvExportUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    topicId: Long,
    onBackClick: () -> Unit,
    onStudyClick: () -> Unit,
    viewModel: FlashcardViewModel = hiltViewModel()
) {
    val flashcards by viewModel.flashcards.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showExportSuccess by remember { mutableStateOf(false) }
    var flashcardToEdit by remember { mutableStateOf<com.flashmaster.app.data.model.Flashcard?>(null) }
    var flashcardToDelete by remember { mutableStateOf<com.flashmaster.app.data.model.Flashcard?>(null) }

    LaunchedEffect(topicId) {
        viewModel.loadFlashcardsByTopic(topicId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcards", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (flashcards.isNotEmpty()) {
                        IconButton(onClick = onStudyClick) {
                            Icon(Icons.Default.School, contentDescription = "Study")
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Export to CSV") },
                                onClick = {
                                    showMenu = false
                                    scope.launch {
                                        val result = CsvExportUtil.exportFlashcardsToCSV(
                                            context,
                                            flashcards,
                                            "Topic_$topicId"
                                        )
                                        result.onSuccess { file ->
                                            CsvExportUtil.shareCSVFile(context, file)
                                            showExportSuccess = true
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
            }
        },
        snackbarHost = {
            if (showExportSuccess) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showExportSuccess = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text("Flashcards exported successfully!")
                }
            }
        }
    ) { paddingValues ->
        if (flashcards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No flashcards yet")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Text("Add Your First Flashcard")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flashcards) { flashcard ->
                    FlashcardCard(
                        flashcard = flashcard,
                        onEdit = { flashcardToEdit = it },
                        onDelete = { flashcardToDelete = it }
                    )
                }
            }
        }

        if (showAddDialog) {
            var front by remember { mutableStateOf("") }
            var back by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Flashcard") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = front,
                            onValueChange = { front = it },
                            label = { Text("Front") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = back,
                            onValueChange = { back = it },
                            label = { Text("Back") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (front.isNotBlank() && back.isNotBlank()) {
                                viewModel.addFlashcard(topicId, front, back)
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Edit flashcard dialog
        flashcardToEdit?.let { flashcard ->
            EditFlashcardDialog(
                flashcard = flashcard,
                onDismiss = { flashcardToEdit = null },
                onConfirm = { front, back ->
                    viewModel.updateFlashcard(flashcard.copy(front = front, back = back))
                    flashcardToEdit = null
                }
            )
        }

        // Delete confirmation dialog
        flashcardToDelete?.let { flashcard ->
            AlertDialog(
                onDismissRequest = { flashcardToDelete = null },
                title = { Text("Delete Flashcard") },
                text = { Text("Are you sure you want to delete this flashcard?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteFlashcard(flashcard)
                            flashcardToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { flashcardToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun FlashcardCard(
    flashcard: com.flashmaster.app.data.model.Flashcard,
    onEdit: (com.flashmaster.app.data.model.Flashcard) -> Unit,
    onDelete: (com.flashmaster.app.data.model.Flashcard) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Front:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        flashcard.front,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit(flashcard)
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete(flashcard)
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Back:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                flashcard.back,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EditFlashcardDialog(
    flashcard: com.flashmaster.app.data.model.Flashcard,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var front by remember { mutableStateOf(flashcard.front) }
    var back by remember { mutableStateOf(flashcard.back) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Flashcard") },
        text = {
            Column {
                OutlinedTextField(
                    value = front,
                    onValueChange = { front = it },
                    label = { Text("Front") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = back,
                    onValueChange = { back = it },
                    label = { Text("Back") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (front.isNotBlank() && back.isNotBlank()) {
                        onConfirm(front, back)
                    }
                },
                enabled = front.isNotBlank() && back.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
