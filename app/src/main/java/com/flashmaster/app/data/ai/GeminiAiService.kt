package com.flashmaster.app.data.ai

import com.flashmaster.app.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class FlashcardPair(
    val front: String,
    val back: String
)

data class AiResponse(
    val summary: String,
    val flashcards: List<FlashcardPair>
)

@Singleton
class GeminiAiService @Inject constructor() {
    
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash", // Fast & free tier
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 2048
        }
    )
    
    suspend fun generateFlashcardsAndSummary(
        noteText: String,
        topicName: String
    ): Result<AiResponse> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(
                    Exception("GEMINI_API_KEY is not configured. Please add it to local.properties")
                )
            }
            
            val prompt = buildPrompt(noteText, topicName)
            val response = model.generateContent(prompt)
            val responseText = response.text ?: return@withContext Result.failure(
                Exception("Empty response from AI")
            )
            
            // Parse JSON response
            val parsed = parseAiResponse(responseText)
            Result.success(parsed)
            
        } catch (e: Exception) {
            Result.failure(Exception("AI processing failed: ${e.message}", e))
        }
    }
    
    private fun buildPrompt(noteText: String, topicName: String): String {
        return """
            You are an expert educator creating study materials for the topic: "$topicName"
            
            Based on the following notes, generate:
            1. A concise summary (2-3 paragraphs)
            2. 10-15 high-quality flashcards for studying
            
            Notes:
            $noteText
            
            Respond ONLY with valid JSON in this exact format:
            {
              "summary": "Your comprehensive summary here...",
              "flashcards": [
                {
                  "front": "Question or concept",
                  "back": "Answer or explanation"
                }
              ]
            }
            
            Guidelines:
            - Make flashcards specific and testable
            - Cover key concepts, definitions, and important facts
            - Keep questions clear and concise
            - Provide complete answers with context
            - Use the topic name for context
            - Ensure JSON is properly formatted
        """.trimIndent()
    }
    
    private fun parseAiResponse(jsonText: String): AiResponse {
        try {
            // Clean up response (AI sometimes adds markdown code blocks)
            val cleanJson = jsonText
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            val json = JSONObject(cleanJson)
            val summary = json.getString("summary")
            val flashcardsArray = json.getJSONArray("flashcards")
            
            val flashcards = mutableListOf<FlashcardPair>()
            for (i in 0 until flashcardsArray.length()) {
                val card = flashcardsArray.getJSONObject(i)
                flashcards.add(
                    FlashcardPair(
                        front = card.getString("front"),
                        back = card.getString("back")
                    )
                )
            }
            
            if (flashcards.isEmpty()) {
                throw Exception("No flashcards generated")
            }
            
            return AiResponse(summary, flashcards)
        } catch (e: Exception) {
            throw Exception("Failed to parse AI response: ${e.message}", e)
        }
    }
}
