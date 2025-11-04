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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flashmaster.app.ui.viewmodel.TopicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    subjectId: Long,
    onBackClick: () -> Unit,
    onTopicClick: (Long) -> Unit,
    viewModel: TopicViewModel = hiltViewModel()
) {
    val topics by viewModel.topics.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var topicToEdit by remember { mutableStateOf<com.flashmaster.app.data.model.Topic?>(null) }
    var topicToDelete by remember { mutableStateOf<com.flashmaster.app.data.model.Topic?>(null) }

    LaunchedEffect(subjectId) {
        viewModel.loadTopicsBySubject(subjectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Topics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Topic")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search topics...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (topics.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No topics yet. Add one to get started!")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(topics) { topic ->
                        TopicCard(
                            topic = topic,
                            onClick = { onTopicClick(topic.id) },
                            onEdit = { topicToEdit = it },
                            onDelete = { topicToDelete = it }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            var topicName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Topic") },
                text = {
                    OutlinedTextField(
                        value = topicName,
                        onValueChange = { topicName = it },
                        label = { Text("Topic Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (topicName.isNotBlank()) {
                                viewModel.addTopic(subjectId, topicName)
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

        // Edit topic dialog
        topicToEdit?.let { topic ->
            EditTopicDialog(
                topic = topic,
                onDismiss = { topicToEdit = null },
                onConfirm = { name ->
                    viewModel.updateTopic(topic.copy(name = name))
                    topicToEdit = null
                }
            )
        }

        // Delete confirmation dialog
        topicToDelete?.let { topic ->
            AlertDialog(
                onDismissRequest = { topicToDelete = null },
                title = { Text("Delete Topic") },
                text = { Text("Are you sure you want to delete \"${topic.name}\"? All flashcards in this topic will also be deleted.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteTopic(topic)
                            topicToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { topicToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun TopicCard(
    topic: com.flashmaster.app.data.model.Topic,
    onClick: () -> Unit,
    onEdit: (com.flashmaster.app.data.model.Topic) -> Unit,
    onDelete: (com.flashmaster.app.data.model.Topic) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Topic, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = topic.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
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
                            onEdit(topic)
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            onDelete(topic)
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                    )
                }
            }
        }
    }
}

@Composable
fun EditTopicDialog(
    topic: com.flashmaster.app.data.model.Topic,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var topicName by remember { mutableStateOf(topic.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Topic") },
        text = {
            OutlinedTextField(
                value = topicName,
                onValueChange = { topicName = it },
                label = { Text("Topic Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (topicName.isNotBlank()) {
                        onConfirm(topicName)
                    }
                },
                enabled = topicName.isNotBlank()
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
