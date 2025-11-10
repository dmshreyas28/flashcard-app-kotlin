# Navigation Integration Guide

This guide shows you how to integrate the new `UploadNoteScreen` into your existing app navigation.

## 🎯 Goal

Add an "Upload Notes" button to your Topic screen that navigates to the AI upload feature.

## 📍 Step 1: Find Your Navigation File

Your navigation is likely in one of these locations:
- `app/src/main/java/com/flashmaster/app/ui/navigation/NavGraph.kt`
- `app/src/main/java/com/flashmaster/app/ui/navigation/Navigation.kt`
- Inside `MainActivity.kt`

## 📝 Step 2: Add the Route

### Find your NavHost composable
Look for something like:
```kotlin
NavHost(
    navController = navController,
    startDestination = "home"
) {
    // Your existing routes...
}
```

### Add the UploadNoteScreen route
Add this inside your `NavHost`:

```kotlin
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flashmaster.app.ui.screen.UploadNoteScreen

// Inside your NavHost:
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

## 🔘 Step 3: Add Upload Button to Topic Screen

Find your Topic screen file (e.g., `TopicScreen.kt`, `TopicsScreen.kt`, or `FlashcardsScreen.kt`).

### Option A: Add to FloatingActionButton

If you have a FAB with a dropdown menu:

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload

// Inside your @Composable function:
FloatingActionButton(
    onClick = { /* show menu */ }
) {
    Icon(Icons.Default.Add, "Add")
}

// Add a new menu item:
DropdownMenuItem(
    text = { Text("Upload Notes (AI)") },
    leadingIcon = {
        Icon(Icons.Default.Upload, "Upload")
    },
    onClick = {
        navController.navigate("upload_note/$topicId/$topicName")
        // Close menu
    }
)
```

### Option B: Add to TopAppBar Actions

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload

TopAppBar(
    title = { Text(topicName) },
    actions = {
        // Your existing actions...
        
        IconButton(
            onClick = {
                navController.navigate("upload_note/$topicId/$topicName")
            }
        ) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = "Upload Notes"
            )
        }
    }
)
```

### Option C: Add as a Card/Button in the Screen

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Upload

// Inside your Column/LazyColumn:
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    onClick = {
        navController.navigate("upload_note/$topicId/$topicName")
    }
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = "Generate Flashcards with AI",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Upload notes and let AI create flashcards",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

## 🧪 Step 4: Test Navigation

1. Build and run the app
2. Navigate to a topic
3. Click the upload button you added
4. Verify the UploadNoteScreen appears
5. Test the back navigation

## 🎨 Step 5: Customize (Optional)

### Change Button Icon
Replace `Icons.Default.Upload` with:
- `Icons.Default.AutoAwesome` - AI sparkle
- `Icons.Default.CloudUpload` - Cloud upload
- `Icons.Default.NoteAdd` - Add note
- `Icons.Default.DocumentScanner` - Scanner

### Add Badge for "NEW" Feature
```kotlin
BadgedBox(
    badge = {
        Badge { Text("NEW") }
    }
) {
    Icon(Icons.Default.Upload, "Upload")
}
```

## 📱 Complete Example

Here's a complete example of a Topic screen with the upload button:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    topicId: Long,
    topicName: String,
    navController: NavController,
    viewModel: TopicViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topicName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Upload Notes button
                    IconButton(
                        onClick = {
                            navController.navigate("upload_note/$topicId/$topicName")
                        }
                    ) {
                        Icon(Icons.Default.Upload, "Upload Notes")
                    }
                    
                    // Your other actions...
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add flashcard manually */ }
            ) {
                Icon(Icons.Default.Add, "Add Flashcard")
            }
        }
    ) { padding ->
        // Your content...
    }
}
```

## 🔍 Troubleshooting

### "Cannot resolve symbol 'UploadNoteScreen'"

**Solution:** Make sure you have the import:
```kotlin
import com.flashmaster.app.ui.screen.UploadNoteScreen
```

### Navigation doesn't work

**Solution:** 
1. Check that `navController` is accessible in your composable
2. Verify the route string matches exactly: `"upload_note/{topicId}/{topicName}"`
3. Make sure you're passing actual values: `"upload_note/$topicId/$topicName"`

### Topic name has special characters

**Solution:** URL encode the topic name:
```kotlin
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

val encodedTopicName = URLEncoder.encode(topicName, StandardCharsets.UTF_8.toString())
navController.navigate("upload_note/$topicId/$encodedTopicName")

// In the route:
val topicName = URLDecoder.decode(
    backStackEntry.arguments?.getString("topicName") ?: "",
    StandardCharsets.UTF_8.toString()
)
```

## ✅ Checklist

- [ ] Found navigation file
- [ ] Added route to NavHost
- [ ] Imported UploadNoteScreen
- [ ] Added button to Topic screen
- [ ] Tested navigation forward
- [ ] Tested navigation back
- [ ] Customized icon/text (optional)

---

Once navigation is set up, follow [AI_SETUP.md](AI_SETUP.md) to configure the Gemini API key!
