package com.flashmaster.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flashmaster.app.data.dao.FlashcardDao
import com.flashmaster.app.data.dao.NoteDao
import com.flashmaster.app.data.dao.SubjectDao
import com.flashmaster.app.data.dao.TopicDao
import com.flashmaster.app.data.database.FlashMasterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create notes table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    topicId INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    originalText TEXT NOT NULL,
                    summary TEXT,
                    fileName TEXT,
                    fileType TEXT NOT NULL,
                    processingStatus TEXT NOT NULL,
                    userId TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    syncedToCloud INTEGER NOT NULL,
                    FOREIGN KEY(topicId) REFERENCES topics(id) ON DELETE CASCADE
                )
            """.trimIndent())
            
            // Create index on topicId for notes
            database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_topicId ON notes(topicId)")
            
            // Add new columns to flashcards table
            database.execSQL("ALTER TABLE flashcards ADD COLUMN isAiGenerated INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE flashcards ADD COLUMN sourceNoteId INTEGER")
        }
    }

    @Provides
    @Singleton
    fun provideFlashMasterDatabase(@ApplicationContext context: Context): FlashMasterDatabase {
        return Room.databaseBuilder(
            context,
            FlashMasterDatabase::class.java,
            "flashmaster_database"
        )
        .addMigrations(MIGRATION_1_2)
        .build()
    }

    @Provides
    fun provideSubjectDao(database: FlashMasterDatabase): SubjectDao {
        return database.subjectDao()
    }

    @Provides
    fun provideTopicDao(database: FlashMasterDatabase): TopicDao {
        return database.topicDao()
    }

    @Provides
    fun provideFlashcardDao(database: FlashMasterDatabase): FlashcardDao {
        return database.flashcardDao()
    }
    
    @Provides
    fun provideNoteDao(database: FlashMasterDatabase): NoteDao {
        return database.noteDao()
    }
}
