package com.flashmaster.app.data.repository

import android.net.Uri
import com.flashmaster.app.data.ai.GeminiAiService
import com.flashmaster.app.data.ai.TextExtractorService
import com.flashmaster.app.data.dao.FlashcardDao
import com.flashmaster.app.data.dao.NoteDao
import com.flashmaster.app.data.model.Flashcard
import com.flashmaster.app.data.model.Note
import com.flashmaster.app.data.model.ProcessingStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val flashcardDao: FlashcardDao,
    private val textExtractor: TextExtractorService,
    private val aiService: GeminiAiService
) {
    
    fun getNotesForTopic(topicId: Long): Flow<List<Note>> {
        return noteDao.getNotesForTopic(topicId)
    }
    
    fun getAllNotesForUser(userId: String): Flow<List<Note>> {
        return noteDao.getAllNotesForUser(userId)
    }
    
    suspend fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)
    }
    
    suspend fun processNoteFile(
        uri: Uri,
        fileName: String,
        fileType: String,
        topicId: Long,
        topicName: String,
        userId: String
    ): Result<Note> {
        return try {
            // Step 1: Extract text from file
            val textResult = textExtractor.extractText(uri, fileType)
            if (textResult.isFailure) {
                return Result.failure(textResult.exceptionOrNull()!!)
            }
            
            val extractedText = textResult.getOrThrow()
            
            // Validate extracted text length
            if (extractedText.length < 50) {
                return Result.failure(Exception("Text too short. Please provide more content (minimum 50 characters)."))
            }
            
            // Step 2: Save note in PENDING state
            val note = Note(
                topicId = topicId,
                title = fileName,
                originalText = extractedText,
                fileName = fileName,
                fileType = fileType,
                processingStatus = ProcessingStatus.PENDING,
                userId = userId
            )
            val noteId = noteDao.insert(note)
            val savedNote = note.copy(id = noteId)
            
            // Step 3: Process with AI (in background)
            processWithAi(savedNote, topicName)
            
            Result.success(savedNote)
            
        } catch (e: Exception) {
            Result.failure(Exception("Failed to process note: ${e.message}", e))
        }
    }
    
    private suspend fun processWithAi(note: Note, topicName: String) {
        try {
            // Update status to PROCESSING
            noteDao.update(note.copy(processingStatus = ProcessingStatus.PROCESSING))
            
            // Call AI service
            val aiResult = aiService.generateFlashcardsAndSummary(
                note.originalText,
                topicName
            )
            
            if (aiResult.isSuccess) {
                val response = aiResult.getOrThrow()
                
                // Save summary
                noteDao.update(
                    note.copy(
                        summary = response.summary,
                        processingStatus = ProcessingStatus.COMPLETED,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                
                // Save generated flashcards
                response.flashcards.forEach { pair ->
                    val flashcard = Flashcard(
                        topicId = note.topicId,
                        front = pair.front,
                        back = pair.back,
                        userId = note.userId,
                        isAiGenerated = true,
                        sourceNoteId = note.id
                    )
                    flashcardDao.insertFlashcard(flashcard)
                }
                
            } else {
                // Mark as failed
                noteDao.update(
                    note.copy(
                        processingStatus = ProcessingStatus.FAILED,
                        summary = "Error: ${aiResult.exceptionOrNull()?.message}"
                    )
                )
            }
            
        } catch (e: Exception) {
            // Mark as failed with error message
            try {
                noteDao.update(
                    note.copy(
                        processingStatus = ProcessingStatus.FAILED,
                        summary = "Error: ${e.message}"
                    )
                )
            } catch (updateError: Exception) {
                // Log error but don't throw
            }
        }
    }
    
    suspend fun retryProcessing(note: Note, topicName: String) {
        processWithAi(note, topicName)
    }
    
    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }
}
