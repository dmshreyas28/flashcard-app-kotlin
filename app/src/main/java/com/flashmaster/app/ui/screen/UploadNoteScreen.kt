package com.flashmaster.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flashmaster.app.ui.viewmodel.UploadNoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadNoteScreen(
    topicId: Long,
    topicName: String,
    onNavigateBack: () -> Unit,
    viewModel: UploadNoteViewModel = hiltViewModel()
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = viewModel.getFileName(it)
        }
    }
    
    // Navigate back on success after showing message
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess && uiState.uploadedNote != null) {
            kotlinx.coroutines.delay(2000) // Show success message for 2 seconds
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Notes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            Text(
                text = "Upload your notes and AI will automatically generate flashcards!",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "Topic: $topicName",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // File type cards
            Text(
                text = "Choose file type:",
                style = MaterialTheme.typography.titleSmall
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FileTypeCard(
                    icon = Icons.Default.PictureAsPdf,
                    label = "PDF",
                    onClick = { filePickerLauncher.launch("application/pdf") },
                    enabled = !uiState.isProcessing
                )
                FileTypeCard(
                    icon = Icons.Default.Description,
                    label = "Text",
                    onClick = { filePickerLauncher.launch("text/plain") },
                    enabled = !uiState.isProcessing
                )
                FileTypeCard(
                    icon = Icons.Default.Image,
                    label = "Image",
                    onClick = { filePickerLauncher.launch("image/*") },
                    enabled = !uiState.isProcessing
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Selected file display
            selectedFileUri?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.InsertDriveFile,
                            "File",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = selectedFileName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Upload button
            Button(
                onClick = {
                    selectedFileUri?.let { uri ->
                        viewModel.uploadNote(
                            uri = uri,
                            fileName = selectedFileName,
                            topicId = topicId,
                            topicName = topicName
                        )
                    }
                },
                enabled = selectedFileUri != null && !uiState.isProcessing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (uiState.isProcessing) "Processing..." else "Generate Flashcards with AI")
            }
            
            // Status messages
            uiState.message?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isSuccess) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Info card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            "Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "How it works:",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    InfoStep(number = "1", text = "Upload your notes (PDF, text, or image)")
                    InfoStep(number = "2", text = "AI extracts and analyzes the content")
                    InfoStep(number = "3", text = "Flashcards are automatically generated")
                    InfoStep(number = "4", text = "A summary is created for quick review")
                    InfoStep(number = "5", text = "Edit flashcards if needed")
                }
            }
            
            // Tips card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lightbulb,
                            "Tips",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Tips:",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "• Clear, well-organized notes work best\n" +
                        "• Minimum 50 characters of text required\n" +
                        "• Images should have clear, readable text\n" +
                        "• Processing takes 10-30 seconds",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun FileTypeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(100.dp),
        enabled = enabled
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(48.dp),
                tint = if (enabled) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun InfoStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
