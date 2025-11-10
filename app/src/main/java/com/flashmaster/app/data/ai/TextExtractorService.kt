package com.flashmaster.app.data.ai

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class TextExtractorService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    init {
        // Initialize PDFBox
        PDFBoxResourceLoader.init(context)
    }
    
    suspend fun extractText(uri: Uri, fileType: String): Result<String> {
        return try {
            val text = when (fileType.lowercase()) {
                "pdf" -> extractFromPdf(uri)
                "txt", "text" -> extractFromTxt(uri)
                "jpg", "jpeg", "png", "image" -> extractFromImage(uri)
                else -> return Result.failure(Exception("Unsupported file type: $fileType"))
            }
            
            if (text.isBlank()) {
                Result.failure(Exception("No text found in file"))
            } else {
                Result.success(text.trim())
            }
        } catch (e: Exception) {
            Result.failure(Exception("Text extraction failed: ${e.message}", e))
        }
    }
    
    private suspend fun extractFromPdf(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val document = PDDocument.load(input)
                val stripper = PDFTextStripper()
                val text = stripper.getText(document)
                document.close()
                text
            } ?: throw Exception("Cannot open PDF file")
        } catch (e: Exception) {
            throw Exception("PDF extraction failed: ${e.message}", e)
        }
    }
    
    private suspend fun extractFromTxt(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader().readText()
            } ?: throw Exception("Cannot open text file")
        } catch (e: Exception) {
            throw Exception("Text file extraction failed: ${e.message}", e)
        }
    }
    
    private suspend fun extractFromImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        continuation.resume(visionText.text)
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(
                            Exception("OCR failed: ${e.message}", e)
                        )
                    }
            }
        } catch (e: Exception) {
            throw Exception("Image text extraction failed: ${e.message}", e)
        }
    }
}
