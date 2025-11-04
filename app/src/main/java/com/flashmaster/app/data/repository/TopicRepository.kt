package com.flashmaster.app.data.repository

import com.flashmaster.app.data.dao.TopicDao
import com.flashmaster.app.data.model.Topic
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRepository @Inject constructor(
    private val topicDao: TopicDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    fun getTopicsBySubject(subjectId: Long): Flow<List<Topic>> {
        return topicDao.getTopicsBySubject(subjectId)
    }

    fun searchTopics(searchQuery: String): Flow<List<Topic>> {
        return topicDao.searchTopics(authRepository.getUserId(), searchQuery)
    }

    suspend fun getTopicById(id: Long): Topic? {
        return topicDao.getTopicById(id)
    }

    suspend fun insertTopic(topic: Topic): Long {
        val id = topicDao.insertTopic(topic.copy(userId = authRepository.getUserId()))
        syncTopicToCloud(topic.copy(id = id))
        return id
    }

    suspend fun updateTopic(topic: Topic) {
        topicDao.updateTopic(topic.copy(updatedAt = System.currentTimeMillis()))
        syncTopicToCloud(topic)
    }

    suspend fun deleteTopic(topic: Topic) {
        topicDao.deleteTopic(topic)
        deleteTopicFromCloud(topic.id)
    }

    private suspend fun syncTopicToCloud(topic: Topic) {
        try {
            if (authRepository.isUserLoggedIn()) {
                firestore.collection("users")
                    .document(authRepository.getUserId())
                    .collection("topics")
                    .document(topic.id.toString())
                    .set(topic)
                    .await()
                
                topicDao.updateTopic(topic.copy(syncedToCloud = true))
            }
        } catch (e: Exception) {
            // Handle sync error silently
        }
    }

    private suspend fun deleteTopicFromCloud(topicId: Long) {
        try {
            if (authRepository.isUserLoggedIn()) {
                firestore.collection("users")
                    .document(authRepository.getUserId())
                    .collection("topics")
                    .document(topicId.toString())
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            // Handle deletion error silently
        }
    }
}
