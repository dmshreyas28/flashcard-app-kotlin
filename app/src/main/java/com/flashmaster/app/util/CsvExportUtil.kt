package com.flashmaster.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.flashmaster.app.data.model.Flashcard
import com.opencsv.CSVWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

object CsvExportUtil {

    suspend fun exportFlashcardsToCSV(
        context: Context,
        flashcards: List<Flashcard>,
        topicName: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val fileName = "flashcards_${topicName.replace(" ", "_")}_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)
            
            CSVWriter(FileWriter(file)).use { writer ->
                // Write header
                writer.writeNext(arrayOf("Front", "Back", "Created At"))
                
                // Write flashcard data
                flashcards.forEach { flashcard ->
                    writer.writeNext(
                        arrayOf(
                            flashcard.front,
                            flashcard.back,
                            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                                .format(java.util.Date(flashcard.createdAt))
                        )
                    )
                }
            }
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shareCSVFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Flashcards CSV"))
    }
}
