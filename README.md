# Android Flashlight

A simple native Android flashlight app written in Kotlin.

## Features

- One-tap flashlight ON/OFF control
- Shows whether the flashlight is on or off
- Works offline
- No accounts, advertising, or analytics
- Uses the phone's built-in LED flash
- Turns the torch off when the app leaves the foreground

## Requirements

- Android 6.0 (API 23) or newer
- Android Studio
- An Android phone with an LED flash for real-device testing

The app requests the Android **Camera** permission because Android requires it to control the camera flash. It does not capture photos or record video.

## Project structure

```
FlashlightAndroid/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── README.md
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        └── java/com/example/flashlight/MainActivity.kt
```

## Open and run

1. Open Android Studio.
2. Choose **Open** and select the project folder.
3. Let Gradle sync.
4. Connect an Android phone with USB debugging enabled.
5. Press **Run** to install and launch the app.

To build an APK, choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

## Important

This source project has not yet been compiled or tested on a physical device. A physical phone with a camera LED flash is needed to verify the actual flashlight behavior.
