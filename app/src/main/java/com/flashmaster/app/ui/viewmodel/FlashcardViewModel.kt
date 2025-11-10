package com.flashmaster.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashmaster.app.data.dao.TopicDao
import com.flashmaster.app.data.model.Flashcard
import com.flashmaster.app.data.model.Topic
import com.flashmaster.app.data.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val topicDao: TopicDao
) : ViewModel() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val _currentTopic = MutableStateFlow<Topic?>(null)
    val currentTopic: StateFlow<Topic?> = _currentTopic.asStateFlow()

    private val _randomFlashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val randomFlashcards: StateFlow<List<Flashcard>> = _randomFlashcards.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFlashcardsByTopic(topicId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            // Load topic info
            val topic = topicDao.getTopicById(topicId)
            _currentTopic.value = topic
            // Load flashcards
            flashcardRepository.getFlashcardsByTopic(topicId).collect { flashcardList ->
                _flashcards.value = flashcardList
                _isLoading.value = false
            }
        }
    }

    fun loadRandomFlashcards(topicId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val randomList = flashcardRepository.getRandomFlashcardsByTopic(topicId)
            _randomFlashcards.value = randomList
            _isLoading.value = false
        }
    }

    fun addFlashcard(topicId: Long, front: String, back: String) {
        viewModelScope.launch {
            val flashcard = Flashcard(
                topicId = topicId,
                front = front,
                back = back,
                userId = ""
            )
            flashcardRepository.insertFlashcard(flashcard)
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            flashcardRepository.updateFlashcard(flashcard)
        }
    }

    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            flashcardRepository.deleteFlashcard(flashcard)
        }
    }
}
