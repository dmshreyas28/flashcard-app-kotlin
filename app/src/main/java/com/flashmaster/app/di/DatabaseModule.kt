package com.flashmaster.app.di

import android.content.Context
import androidx.room.Room
import com.flashmaster.app.data.dao.FlashcardDao
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

    @Provides
    @Singleton
    fun provideFlashMasterDatabase(@ApplicationContext context: Context): FlashMasterDatabase {
        return Room.databaseBuilder(
            context,
            FlashMasterDatabase::class.java,
            "flashmaster_database"
        ).build()
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
}
