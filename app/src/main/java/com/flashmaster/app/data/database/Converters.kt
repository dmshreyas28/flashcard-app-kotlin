package com.flashmaster.app.data.database

import androidx.room.TypeConverter
import com.flashmaster.app.data.model.ProcessingStatus

class Converters {
    @TypeConverter
    fun fromProcessingStatus(status: ProcessingStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toProcessingStatus(status: String): ProcessingStatus {
        return ProcessingStatus.valueOf(status)
    }
}
