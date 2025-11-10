package com.flashmaster.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flashmaster.app.data.dao.FlashcardDao
import com.flashmaster.app.data.dao.NoteDao
import com.flashmaster.app.data.dao.SubjectDao
import com.flashmaster.app.data.dao.TopicDao
import com.flashmaster.app.data.model.Flashcard
import com.flashmaster.app.data.model.Note
import com.flashmaster.app.data.model.Subject
import com.flashmaster.app.data.model.Topic

@Database(
    entities = [Subject::class, Topic::class, Flashcard::class, Note::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlashMasterDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun noteDao(): NoteDao
}
