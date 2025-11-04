package com.flashmaster.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashmaster.app.data.model.Subject
import com.flashmaster.app.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            _isLoading.value = true
            subjectRepository.getAllSubjects().collect { subjectList ->
                _subjects.value = subjectList
                _isLoading.value = false
            }
        }
    }

    fun addSubject(name: String, color: String) {
        viewModelScope.launch {
            val subject = Subject(
                name = name,
                color = color,
                userId = ""
            )
            subjectRepository.insertSubject(subject)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(subject)
        }
    }
}
