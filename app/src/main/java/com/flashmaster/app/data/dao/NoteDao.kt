package com.flashmaster.app.data.dao

import androidx.room.*
import com.flashmaster.app.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE topicId = :topicId ORDER BY createdAt DESC")
    fun getNotesForTopic(topicId: Long): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllNotesForUser(userId: String): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long
    
    @Update
    suspend fun update(note: Note)
    
    @Delete
    suspend fun delete(note: Note)
    
    @Query("DELETE FROM notes WHERE topicId = :topicId")
    suspend fun deleteNotesForTopic(topicId: Long)
    
    @Query("DELETE FROM notes WHERE userId = :userId")
    suspend fun deleteAllNotesForUser(userId: String)
}
