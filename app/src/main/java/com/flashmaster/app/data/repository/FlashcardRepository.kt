package com.flashmaster.app.data.repository

import com.flashmaster.app.data.dao.FlashcardDao
import com.flashmaster.app.data.model.Flashcard
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepository @Inject constructor(
    private val flashcardDao: FlashcardDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    fun getFlashcardsByTopic(topicId: Long): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsByTopic(topicId)
    }

    suspend fun getRandomFlashcardsByTopic(topicId: Long): List<Flashcard> {
        return flashcardDao.getRandomFlashcardsByTopic(topicId)
    }

    suspend fun getFlashcardById(id: Long): Flashcard? {
        return flashcardDao.getFlashcardById(id)
    }

    suspend fun insertFlashcard(flashcard: Flashcard): Long {
        val id = flashcardDao.insertFlashcard(flashcard.copy(userId = authRepository.getUserId()))
        syncFlashcardToCloud(flashcard.copy(id = id))
        return id
    }

    suspend fun updateFlashcard(flashcard: Flashcard) {
        flashcardDao.updateFlashcard(flashcard.copy(updatedAt = System.currentTimeMillis()))
        syncFlashcardToCloud(flashcard)
    }

    suspend fun deleteFlashcard(flashcard: Flashcard) {
        flashcardDao.deleteFlashcard(flashcard)
        deleteFlashcardFromCloud(flashcard.id)
    }

    fun getFlashcardCount(topicId: Long): Flow<Int> {
        return flashcardDao.getFlashcardCount(topicId)
    }

    private suspend fun syncFlashcardToCloud(flashcard: Flashcard) {
        try {
            if (authRepository.isUserLoggedIn()) {
                firestore.collection("users")
                    .document(authRepository.getUserId())
                    .collection("flashcards")
                    .document(flashcard.id.toString())
                    .set(flashcard)
                    .await()
                
                flashcardDao.updateFlashcard(flashcard.copy(syncedToCloud = true))
            }
        } catch (e: Exception) {
            // Handle sync error silently
        }
    }

    private suspend fun deleteFlashcardFromCloud(flashcardId: Long) {
        try {
            if (authRepository.isUserLoggedIn()) {
                firestore.collection("users")
                    .document(authRepository.getUserId())
                    .collection("flashcards")
                    .document(flashcardId.toString())
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            // Handle deletion error silently
        }
    }
}
