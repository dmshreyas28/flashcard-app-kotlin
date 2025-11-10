# FlashMaster - Smart Flashcard Learning App

A beautiful Android application built with Kotlin and Jetpack Compose that helps you master your studies with smart flashcards.

## Features

✨ **Beautiful UI**
- Material 3 Design with smooth animations
- Light and Dark theme support
- Color-coded subjects for better organization

🤖 **AI-Powered Flashcard Generation** ⭐ NEW!
- Upload notes (PDF, text files, or images)
- AI automatically generates flashcards from your notes
- Get AI-generated summaries for quick review
- OCR text extraction from images
- Powered by Google Gemini AI

🎴 **Flashcard Management**
- Create subjects with custom colors
- **Edit and delete subjects**
- Organize topics within subjects
- **Edit and delete topics**
- Add unlimited flashcards per topic
- **Edit and delete individual flashcards**
- Search functionality for quick access

📖 **Study Mode**
- Smooth 3D card flip animations
- Random order study mode for better learning
- Track progress with card counter
- Navigate between cards easily

🔐 **Authentication**
- Google Sign-In integration
- Secure user authentication with Firebase

💾 **Data Storage**
- Local storage with Room Database (SQLite)
- Offline support - works without internet
- Cloud backup with Firebase Firestore
- Automatic data synchronization

📤 **Sharing & Export**
- Export flashcards to CSV format
- Share CSV files with other apps
- **Three-dot menu for quick actions**
- **Delete confirmations to prevent accidents**

## Tech Stack

- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose with Material 3
- **Compose BOM**: 2024.04.01 (latest stable APIs)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt/Dagger
- **Local Database**: Room Database (SQLite)
- **AI Integration**: Google Gemini API for flashcard generation
- **Text Extraction**: PDFBox (PDF), ML Kit (OCR)
- **Cloud Storage**: Firebase Firestore
- **Authentication**: Firebase Auth with Google Sign-In
- **Navigation**: Jetpack Navigation Compose
- **Async Processing**: Kotlin Coroutines & Flow

## Prerequisites

- Android Studio Hedgehog | 2023.1.1 or newer
- JDK 17
- Android SDK (API level 26 or higher)
- Firebase account
- **Google Gemini API key** (for AI features - see [AI_SETUP.md](AI_SETUP.md))

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Skyrocket345/flashcard-app-kotlin.git
cd flashcard-app-kotlin
```

### 2. AI Setup (Required for AI Features)

**Follow the detailed guide in [AI_SETUP.md](AI_SETUP.md)**

Quick steps:
1. Get a free API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Add to `local.properties`:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```
3. Sync Gradle

### 3. Firebase Setup

**IMPORTANT**: You must configure Firebase before running the app. Follow the detailed guide in [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

Quick steps:
1. Create a Firebase project
2. Add your Android app to Firebase
3. Download `google-services.json`
4. Enable Google Sign-In authentication
5. Enable Firestore database
6. Update the Web Client ID in `LoginScreen.kt`

### 3. Open in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned directory
4. Wait for Gradle sync to complete

### 4. Build and Run

1. Connect an Android device or start an emulator
2. Click the Run button (▶️)
3. The app will build and install on your device

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/flashmaster/app/
│       │   ├── data/
│       │   │   ├── dao/           # Room DAOs
│       │   │   ├── database/      # Database setup
│       │   │   ├── model/         # Data models
│       │   │   └── repository/    # Repository layer
│       │   ├── di/                # Dependency Injection
│       │   ├── ui/
│       │   │   ├── navigation/    # Navigation setup
│       │   │   ├── screen/        # Compose screens
│       │   │   ├── theme/         # App theming
│       │   │   └── viewmodel/     # ViewModels
│       │   ├── FlashMasterApplication.kt
│       │   └── MainActivity.kt
│       ├── res/                   # Resources
│       └── AndroidManifest.xml
├── build.gradle.kts
└── google-services.json           # Firebase config (required)
```

## How to Use

### Creating Your First Subject

1. Launch the app and sign in with Google
2. Tap the **+** floating action button
3. Enter a subject name (e.g., "Mathematics")
4. Choose a color for your subject
5. Tap "Add"

### Adding Topics

1. Tap on a subject card
2. Tap the **+** button
3. Enter a topic name (e.g., "Calculus")
4. Tap "Add"

### Creating Flashcards

1. Navigate to a topic
2. Tap the **+** button
3. Enter the front text (question/term)
4. Enter the back text (answer/definition)
5. Tap "Add"

### Studying with Flashcards

1. Open a topic with flashcards
2. Tap the **Study** icon (🎓) in the top bar
3. Tap on a card to flip it
4. Use "Previous" and "Next" buttons to navigate
5. Cards are shown in random order for better learning

### Editing Items

1. **Edit Subject**: Tap the ⋮ menu on any subject card → Edit → Update name/color
2. **Edit Topic**: Tap the ⋮ menu on any topic card → Edit → Update name
3. **Edit Flashcard**: Tap the ⋮ menu on any flashcard → Edit → Update front/back text

### Deleting Items

1. Tap the ⋮ menu on any subject, topic, or flashcard
2. Select "Delete"
3. Confirm deletion in the popup dialog
4. **Note**: Deleting a subject removes all its topics and flashcards. Deleting a topic removes all its flashcards.

### Searching Topics

1. Go to the Topics screen
2. Use the search bar at the top
3. Type to filter topics across all subjects

## Building for Release

### Generate a Signed APK

1. In Android Studio: Build → Generate Signed Bundle / APK
2. Select "APK" and click "Next"
3. Create a new keystore or use an existing one
4. Fill in the required information
5. Choose "release" build variant
6. Click "Finish"

### ProGuard

ProGuard rules are already configured in `app/proguard-rules.pro` for:
- Firebase
- Room Database
- Hilt Dependency Injection

## Dependencies

Key libraries used:

```kotlin
- Jetpack Compose (UI)
- Material 3 (Design)
- Room Database (Local storage)
- Firebase Auth & Firestore (Cloud)
- Hilt (Dependency Injection)
- Navigation Compose (Navigation)
- Coroutines (Async operations)
- OpenCSV (CSV export)
```

## Features in Detail

### 3D Card Flip Animation
The flashcard study mode features a smooth 3D flip animation using Compose's `graphicsLayer` modifier with rotation transformations.

### Color-Coded Subjects
16 beautiful preset colors to organize your subjects visually. Each subject maintains its unique color throughout the app.

### Offline First Architecture
All data is stored locally first using Room Database, ensuring the app works perfectly offline. When online, data syncs to Firebase Firestore for backup and cross-device access.

### Material 3 Design
Built with the latest Material 3 design system (Compose BOM 2024.04.01), supporting both light and dark themes with dynamic colors on Android 12+. Features smooth animations and modern UI patterns including AutoMirrored icons for RTL language support.

## Troubleshooting

### Build Errors
- Ensure you have JDK 17 installed
- Try: File → Invalidate Caches / Restart
- Clean and rebuild: Build → Clean Project, then Build → Rebuild Project

### Google Sign-In Not Working
- Check SHA-1 fingerprint is added to Firebase
- Verify Web Client ID in LoginScreen.kt
- Ensure device has Google Play Services

### Data Not Syncing
- Check internet connection
- Verify Firestore is enabled in Firebase Console
- Check Firebase Console for any error logs

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the MIT License.

## Author

Created by **Shreyas** ([@dmshreyas28](https://github.com/dmshreyas28))

## Acknowledgments

- Material Design team for the beautiful design system
- Firebase team for the excellent backend services
- Android community for amazing libraries and support
- Jetpack Compose team for the modern UI toolkit

---

**Happy Learning with FlashMaster! 📚✨**

*Last Updated: November 2025*
