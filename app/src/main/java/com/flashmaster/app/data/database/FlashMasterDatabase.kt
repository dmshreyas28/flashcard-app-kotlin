package com.flashmaster.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flashmaster.app.data.dao.FlashcardDao
import com.flashmaster.app.data.dao.SubjectDao
import com.flashmaster.app.data.dao.TopicDao
import com.flashmaster.app.data.model.Flashcard
import com.flashmaster.app.data.model.Subject
import com.flashmaster.app.data.model.Topic

@Database(
    entities = [Subject::class, Topic::class, Flashcard::class],
    version = 1,
    exportSchema = false
)
abstract class FlashMasterDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun flashcardDao(): FlashcardDao
}
