package com.flashmaster.app.data.dao

import androidx.room.*
import com.flashmaster.app.data.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId ORDER BY createdAt ASC")
    fun getFlashcardsByTopic(topicId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE topicId = :topicId ORDER BY RANDOM()")
    suspend fun getRandomFlashcardsByTopic(topicId: Long): List<Flashcard>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getFlashcardById(id: Long): Flashcard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard): Long

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteFlashcardById(id: Long)

    @Query("SELECT COUNT(*) FROM flashcards WHERE topicId = :topicId")
    fun getFlashcardCount(topicId: Long): Flow<Int>
}
