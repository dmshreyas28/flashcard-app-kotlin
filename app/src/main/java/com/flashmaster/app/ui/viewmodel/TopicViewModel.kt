package com.flashmaster.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashmaster.app.data.model.Topic
import com.flashmaster.app.data.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Topic>>(emptyList())
    val searchResults: StateFlow<List<Topic>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadTopicsBySubject(subjectId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            topicRepository.getTopicsBySubject(subjectId).collect { topicList ->
                _topics.value = topicList
                _isLoading.value = false
            }
        }
    }

    fun searchTopics(query: String) {
        viewModelScope.launch {
            topicRepository.searchTopics(query).collect { results ->
                _searchResults.value = results
            }
        }
    }

    fun addTopic(subjectId: Long, name: String) {
        viewModelScope.launch {
            val topic = Topic(
                subjectId = subjectId,
                name = name,
                userId = ""
            )
            topicRepository.insertTopic(topic)
        }
    }

    fun updateTopic(topic: Topic) {
        viewModelScope.launch {
            topicRepository.updateTopic(topic)
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            topicRepository.deleteTopic(topic)
        }
    }
}
