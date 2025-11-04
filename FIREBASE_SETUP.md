# Firebase Setup Guide for FlashMaster

Follow these steps carefully to configure Firebase for your FlashMaster app.

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project" or "Create a project"
3. Enter project name: **FlashMaster** (or your preferred name)
4. Accept the terms and click "Continue"
5. Disable Google Analytics (optional) and click "Create project"
6. Wait for project creation to complete, then click "Continue"

## Step 2: Add Android App to Firebase Project

1. In your Firebase project dashboard, click the **Android icon** to add an Android app
2. Register your app with these details:
   - **Android package name**: `com.flashmaster.app`
   - **App nickname** (optional): FlashMaster
   - **Debug signing certificate SHA-1**: (Get this from step 3 below)
3. Click "Register app"

## Step 3: Get SHA-1 Certificate Fingerprint

Open a terminal in your project directory and run:

### For Windows (PowerShell):
```powershell
cd $env:USERPROFILE\.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### For Mac/Linux:
```bash
cd ~/.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copy the **SHA-1** fingerprint (looks like: `A1:B2:C3:D4:...`) and paste it in Firebase Console under:
- Project Settings → Your apps → Add fingerprint

## Step 4: Download google-services.json

1. In the Firebase Console, after registering your app, click **"Download google-services.json"**
2. Replace the placeholder `google-services.json` file in your project with the downloaded one:
   ```
   flashcard-app-kotlin/app/google-services.json
   ```

## Step 5: Enable Google Sign-In Authentication

1. In Firebase Console, go to **Authentication** section from the left menu
2. Click **"Get started"** if this is your first time
3. Go to the **"Sign-in method"** tab
4. Click on **"Google"** in the list of providers
5. Toggle the **"Enable"** switch to ON
6. Select a **Project support email** from the dropdown
7. Click **"Save"**

## Step 6: Get Web Client ID

1. Still in Firebase Console, go to **Project Settings** (gear icon)
2. Scroll down to **"Your apps"** section
3. Click on your Android app
4. Scroll down to find **"Web client ID"** under "SDK setup and configuration"
5. Copy the Web Client ID (looks like: `123456789-abc123.apps.googleusercontent.com`)

## Step 7: Update Your Code with Web Client ID

1. Open the file: `app/src/main/java/com/flashmaster/app/ui/screen/LoginScreen.kt`
2. Find this line:
   ```kotlin
   .requestIdToken("YOUR_WEB_CLIENT_ID") // You'll need to replace this
   ```
3. Replace `YOUR_WEB_CLIENT_ID` with the Web Client ID you copied
4. Save the file

## Step 8: Enable Firestore Database

1. In Firebase Console, go to **Firestore Database** from the left menu
2. Click **"Create database"**
3. Select **"Start in test mode"** (for development)
4. Choose a location closest to you
5. Click **"Enable"**

## Step 9: (Optional) Configure Firestore Security Rules

In Firestore → Rules tab, replace the default rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

This ensures users can only access their own data.

## Step 10: Build and Run

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect your Android device or start an emulator
4. Click **Run** (green play button)
5. Test the Google Sign-In feature

## Troubleshooting

### "Status 10" Error during Google Sign-In
- Make sure the SHA-1 fingerprint is correctly added in Firebase Console
- Verify that the Web Client ID in `LoginScreen.kt` matches the one from Firebase

### "google-services.json not found"
- Ensure the file is placed in: `flashcard-app-kotlin/app/google-services.json`
- Make sure the filename is exact (all lowercase, with hyphen)

### Build fails with "Could not resolve com.google.gms"
- Check your internet connection
- In Android Studio: File → Sync Project with Gradle Files

### App crashes on launch
- Check Logcat in Android Studio for error messages
- Ensure all Firebase dependencies are properly synced

## Testing Google Sign-In

1. Launch the app
2. Click "Continue with Google"
3. Select your Google account
4. Grant permissions
5. You should be redirected to the Subjects screen

## Next Steps

After successful setup:
- Create subjects with different colors
- Add topics to subjects
- Create flashcards for your topics
- Use the Study mode with flip animations
- Your data will automatically sync to Firebase Firestore

## Support

If you encounter any issues:
1. Check the Firebase Console for any error logs
2. Review the Logcat in Android Studio
3. Ensure all steps were followed correctly
4. Make sure your device/emulator has internet access
