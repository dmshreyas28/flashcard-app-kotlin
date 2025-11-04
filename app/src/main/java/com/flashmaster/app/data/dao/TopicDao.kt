package com.flashmaster.app.data.dao

import androidx.room.*
import com.flashmaster.app.data.model.Topic
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE subjectId = :subjectId ORDER BY updatedAt DESC")
    fun getTopicsBySubject(subjectId: Long): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE userId = :userId AND name LIKE '%' || :searchQuery || '%' ORDER BY updatedAt DESC")
    fun searchTopics(userId: String, searchQuery: String): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: Long): Topic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: Topic): Long

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("DELETE FROM topics WHERE id = :id")
    suspend fun deleteTopicById(id: Long)
}
