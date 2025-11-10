package com.flashmaster.app.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flashmaster.app.data.model.Note
import com.flashmaster.app.data.repository.AiRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UploadUiState(
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String? = null,
    val uploadedNote: Note? = null
)

@HiltViewModel
class UploadNoteViewModel @Inject constructor(
    application: Application,
    private val aiRepository: AiRepository,
    private val auth: FirebaseAuth
) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()
    
    fun getFileName(uri: Uri): String {
        val context = getApplication<Application>().applicationContext
        var name = "unknown"
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            // Fall back to using URI path
            name = uri.lastPathSegment ?: "unknown"
        }
        return name
    }
    
    fun uploadNote(
        uri: Uri,
        fileName: String,
        topicId: Long,
        topicName: String
    ) {
        viewModelScope.launch {
            _uiState.value = UploadUiState(isProcessing = true)
            
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _uiState.value = UploadUiState(
                    isProcessing = false,
                    isSuccess = false,
                    message = "❌ Error: User not authenticated"
                )
                return@launch
            }
            
            val fileType = fileName.substringAfterLast(".", "txt")
            
            val result = aiRepository.processNoteFile(
                uri = uri,
                fileName = fileName,
                fileType = fileType,
                topicId = topicId,
                topicName = topicName,
                userId = userId
            )
            
            if (result.isSuccess) {
                val note = result.getOrThrow()
                _uiState.value = UploadUiState(
                    isProcessing = false,
                    isSuccess = true,
                    message = "✅ Note uploaded! AI is generating flashcards...",
                    uploadedNote = note
                )
            } else {
                _uiState.value = UploadUiState(
                    isProcessing = false,
                    isSuccess = false,
                    message = "❌ Error: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun resetState() {
        _uiState.value = UploadUiState()
    }
}
