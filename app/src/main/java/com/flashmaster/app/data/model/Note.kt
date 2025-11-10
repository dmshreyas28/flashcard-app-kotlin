package com.flashmaster.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topicId: Long,
    val title: String,
    val originalText: String, // Full note text extracted from file
    val summary: String? = null, // AI-generated summary
    val fileName: String? = null,
    val fileType: String, // "pdf", "txt", "image"
    val processingStatus: ProcessingStatus = ProcessingStatus.PENDING,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncedToCloud: Boolean = false
)

enum class ProcessingStatus {
    PENDING,      // Uploaded, not processed yet
    PROCESSING,   // AI is working on it
    COMPLETED,    // Flashcards generated successfully
    FAILED        // Error occurred during processing
}
