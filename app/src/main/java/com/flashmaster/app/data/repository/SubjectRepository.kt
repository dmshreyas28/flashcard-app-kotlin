package com.flashmaster.app.data.repository

import com.flashmaster.app.data.dao.SubjectDao
import com.flashmaster.app.data.model.Subject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val subjectDao: SubjectDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects(authRepository.getUserId())
    }

    suspend fun getSubjectById(id: Long): Subject? {
        return subjectDao.getSubjectById(id)
    }

    suspend fun insertSubject(subject: Subject): Long {
        val userId = authRepository.getUserId()
        val subjectWithUser = subject.copy(userId = userId)
        val id = subjectDao.insertSubject(subjectWithUser)
        syncSubjectToCloud(subjectWithUser.copy(id = id))
        return id
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject.copy(updatedAt = System.currentTimeMillis()))
        syncSubjectToCloud(subject)
    }

    suspend fun deleteSubject(subject: Subject) {
        subjectDao.deleteSubject(subject)
        deleteSubjectFromCloud(subject.id)
    }

    private suspend fun syncSubjectToCloud(subject: Subject) {
        try {
            if (authRepository.isUserLoggedIn()) {
                firestore.collection("users")
                    .document(authRepository.getUserId())
                    .collection("subjects")
                    .document(subject.id.toString())
                    .set(subject)
                    .await()
                
                subjectDao.updateSubject(subject.copy(syncedToCloud = true))
            }
        } catch (e: Exception) {
            // Handle sync error silently for now
        }
    }

    private suspend fun deleteSubjectFromCloud(subjectId: Long) {
        try {
            if (authRepository.isUserLoggedIn()) {
                firestore.collection("users")
                    .document(authRepository.getUserId())
                    .collection("subjects")
                    .document(subjectId.toString())
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            // Handle deletion error silently
        }
    }
}
