# AI Integration Setup Guide

This guide will help you set up the AI-powered flashcard generation feature in FlashMaster.

## 📋 Overview

The AI integration allows users to:
- Upload notes (PDF, TXT, or images)
- Automatically generate flashcards from the content
- Get AI-generated summaries of their notes
- Process text from images using OCR

## 🔑 Step 1: Get Your Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click **"Create API Key"**
4. Choose **"Create API key in new project"** (or select an existing project)
5. Copy the generated API key

**Important:** Keep this key secure and never commit it to version control!

## ⚙️ Step 2: Configure the API Key

### Option A: Add to `local.properties` (Recommended)

1. Open `local.properties` in your project root
2. Add this line at the end:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```
3. Replace `your_actual_api_key_here` with your copied API key

### Option B: Set as Environment Variable

Alternatively, set it as an environment variable:
```bash
export GEMINI_API_KEY=your_actual_api_key_here
```

## 🏗️ Step 3: Sync and Build

1. In Android Studio, click **File → Sync Project with Gradle Files**
2. Wait for the sync to complete
3. Build the project: **Build → Make Project**

## 🧪 Step 4: Test the Integration

1. Run the app on a device or emulator
2. Sign in with Google
3. Create a subject and topic
4. Navigate to the topic
5. Tap the **Upload Notes** button (you'll need to add this to your navigation)
6. Upload a test file (try a simple text file first)
7. Wait for AI processing (10-30 seconds)
8. Check if flashcards are generated

## 📱 Step 5: Add Navigation Route

You need to integrate the `UploadNoteScreen` into your app's navigation.

### Find your Navigation file
Look for a file like `Navigation.kt` or `NavGraph.kt` in:
```
app/src/main/java/com/flashmaster/app/ui/navigation/
```

### Add the route
Add this composable to your navigation:

```kotlin
composable(
    route = "upload_note/{topicId}/{topicName}",
    arguments = listOf(
        navArgument("topicId") { type = NavType.LongType },
        navArgument("topicName") { type = NavType.StringType }
    )
) { backStackEntry ->
    val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
    val topicName = backStackEntry.arguments?.getString("topicName") ?: ""
    
    UploadNoteScreen(
        topicId = topicId,
        topicName = topicName,
        onNavigateBack = { navController.navigateUp() }
    )
}
```

### Add Upload Button to Topic Screen
In your `TopicScreen.kt` or similar, add a button to navigate:

```kotlin
// Example: Add to your TopicBar or Floating Action Button
IconButton(
    onClick = {
        navController.navigate("upload_note/$topicId/${topicName}")
    }
) {
    Icon(Icons.Default.Upload, "Upload Notes")
}
```

## 🎯 Step 6: Verify Dependencies

Make sure Gradle sync was successful and all dependencies were downloaded:

```kotlin
// These should be in your app/build.gradle.kts
implementation("com.google.ai.client.generativeai:generativeai:0.1.2")
implementation("com.tom-roush:pdfbox-android:2.0.27.0")
implementation("com.google.mlkit:text-recognition:16.0.0")
implementation("com.github.jeziellago:compose-markdown:0.3.6")
```

## 🔍 Troubleshooting

### "GEMINI_API_KEY is not configured" Error

**Solution:**
1. Check that `local.properties` has the key
2. Make sure there are no spaces around the `=` sign
3. Sync Gradle again
4. Clean and rebuild: **Build → Clean Project** then **Build → Rebuild Project**

### "Invalid API key" Error

**Solution:**
1. Verify the API key is correct (copy it again from Google AI Studio)
2. Make sure you enabled the API in Google Cloud Console
3. Check for any trailing spaces in the key

### PDF Text Extraction Fails

**Solution:**
1. Make sure the PDF contains selectable text (not scanned images)
2. Try with a simple text file first to verify the AI service works
3. For scanned PDFs, save pages as images and use image upload instead

### Image OCR Not Working

**Solution:**
1. Ensure images have clear, readable text
2. Try with high-contrast, well-lit images
3. Check that Google Play Services is installed on the device/emulator

### "No text found in file" Error

**Solution:**
- Make sure the uploaded file actually contains text
- For PDFs: some PDFs are image-only (scanned documents)
- For images: text must be clear and legible
- Minimum 50 characters required

### Build Errors After Adding Dependencies

**Solution:**
1. Make sure JitPack repository is in `settings.gradle.kts`:
   ```kotlin
   maven { url = uri("https://jitpack.io") }
   ```
2. Invalidate caches: **File → Invalidate Caches / Restart**
3. Delete `.gradle` folder and sync again

## 💰 Usage Limits & Costs

### Free Tier (Gemini)
- **15 requests per minute**
- **1,000,000 tokens per minute**
- Sufficient for ~200-300 note uploads per day per user

### Rate Limit Handling
The app will show an error if you hit rate limits. Wait a minute and try again.

### Cost Estimation
If you exceed free tier (unlikely for personal use):
- Gemini Pro: ~$0.01 per long note processing
- For 1000 users uploading 5 notes/day: ~$50/day

## 🎨 Customization

### Adjust Number of Flashcards
Edit `GeminiAiService.kt`:
```kotlin
private fun buildPrompt(noteText: String, topicName: String): String {
    return """
        ...
        2. 10-15 high-quality flashcards for studying  // Change this number
        ...
    """
}
```

### Change AI Model
In `GeminiAiService.kt`:
```kotlin
private val model = GenerativeModel(
    modelName = "gemini-1.5-flash", // Try: "gemini-1.5-pro" for better quality
    apiKey = apiKey,
    // ...
)
```

Models:
- `gemini-1.5-flash` - Fast, good quality (recommended)
- `gemini-1.5-pro` - Slower, best quality
- `gemini-1.0-pro` - Legacy, cheaper

### Adjust AI Creativity
In `GeminiAiService.kt`:
```kotlin
generationConfig = generationConfig {
    temperature = 0.7f  // 0.0 = deterministic, 1.0 = creative
    topK = 40
    topP = 0.95f
    maxOutputTokens = 2048
}
```

## 📊 Monitoring

### Check Processing Status
Notes have 4 states:
- `PENDING` - Uploaded, waiting to process
- `PROCESSING` - AI is working
- `COMPLETED` - Flashcards generated
- `FAILED` - Error occurred

### View in Database
Use Android Studio's **App Inspection → Database Inspector** to view:
- `notes` table - all uploaded notes
- `flashcards` table - check `isAiGenerated` and `sourceNoteId` columns

## 🚀 Next Steps

Once basic AI integration is working:

1. **Add notes list screen** - Show all uploaded notes per topic
2. **Display summaries** - Use the Markdown library to render summaries
3. **Retry failed processing** - Add button to retry failed notes
4. **Batch upload** - Allow multiple files at once
5. **Progress tracking** - Show real-time processing status
6. **Flashcard editing** - Let users refine AI-generated cards
7. **Quality feedback** - Let users rate AI-generated flashcards

## 📚 Additional Resources

- [Gemini API Documentation](https://ai.google.dev/docs)
- [ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition)
- [PDFBox Android](https://github.com/TomRoush/PdfBox-Android)
- [Compose Markdown](https://github.com/jeziellago/compose-markdown)

## 🆘 Need Help?

If you encounter issues:
1. Check the Logcat output in Android Studio
2. Look for error messages in the UI
3. Verify all setup steps were completed
4. Check the troubleshooting section above

---

**Happy AI-powered studying! 🎓✨**
