# FunNotify SDK [![](https://jitpack.io/v/Funsol-Projects/FunNotify-SDK.svg)](https://jitpack.io/#Funsol-Projects/FunNotify-SDK)

<div align="center">


![FunNotify SDK](https://img.shields.io/badge/FunNotify-SDK-blue?style=for-the-badge)
![Android](https://img.shields.io/badge/Android-24%2B-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple?style=for-the-badge&logo=kotlin)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**A Firebase Cloud Messaging (FCM) SDK for Android with built-in cross-promotion support. This SDK simplifies FCM integration and provides automatic handling of notifications and cross-promotion campaigns.**

</div>



## Features

- **Firebase Cloud Messaging Integration** - Easy setup and token management
- **Cross-Promotion Support** - Automatic handling of package name-based cross-promotion campaigns
- **Smart Notification Handling** - Built-in notification display with image loading
- **Token Management** - Automatic token refresh and listener support
- **Simple API** - Clean, easy-to-use interface for quick integration
- **Image Support** - Automatic image loading for rich notifications
- **WorkManager Integration** - Efficient background processing for notification images


## Integration Setup

This SDK requires Firebase to be properly integrated in your Android application. Before using this SDK, ensure the following:

### 1. Firebase Project Setup

1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app to the Firebase project
3. Download the `google-services.json` file from Firebase Console

### 2. Add google-services.json

Place the downloaded `google-services.json` file in your app module's root directory (`app/google-services.json`).

### 3. Configure Build Files

Add the Google Services plugin to your project-level `build.gradle.kts`:

```kotlin
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:x.y.z")
    }
}
```

Apply the plugin in your app-level `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

### 4. Required Dependencies

The SDK requires the following dependencies to be added to your `build.gradle.kts`:

**SDK library**

```kotlin
dependencies {
    implementation("com.github.Funsol-Projects:FunNotify-SDK:v1.0.1")
}
```
**Other libraries**

```kotlin
dependencies {
    // WorkManager for background processing
    implementation("androidx.work:work-runtime-ktx")
    
    // Picasso for image loading
    implementation("com.squareup.picasso:picasso")
    
    // Firebase Messaging
    implementation(platform("com.google.firebase:firebase-bom"))
    implementation("com.google.firebase:firebase-messaging")
}
```


**Note:** Use version catalogs or dependency management to specify versions rather than hardcoding them. The SDK is compatible with recent stable versions of these libraries.

## Quick Start

### 1. Initialize the SDK

In your `Application` class:

```kotlin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize FCM SDK
        val fcmManager = FunFCM()
        fcmManager.setup(this, "your-topic-name")
        
        // Optional: Listen for token updates
        fcmManager.setTokenListener { token ->
            Log.d("FCM", "FCM Token: $token")
            // Send token to your server
            sendTokenToServer(token)
        }
    }
}
```

### 2. Register Application Class

In your `AndroidManifest.xml`:

```xml
<application>
    android:name=".MyApplication"/>
</application>
```

```xml
<service
    android:name="com.funsol.fcm.FunFirebaseMessagingService"
    android:directBootAware="true"
    android:exported="true"
    tools:node="replace">
    <intent-filter android:priority="-500">
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>

<receiver
    android:name="com.google.firebase.iid.FirebaseInstanceIdReceiver"
    android:exported="true"
    android:permission="com.google.android.c2dm.permission.SEND"
    tools:node="replace">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>
</receiver>
```

That's it! The SDK will automatically handle incoming FCM messages and display notifications.

## Usage

### Getting FCM Token

```kotlin
val fcmManager = FunFCM()
fcmManager.getToken(
    onSuccess = { token ->
        Log.d("FCM", "FCM Token: $token")
        // Send token to your server
        sendTokenToServer(token)
    },
    onFailure = { exception ->
        Log.e("FCM", "Failed to get token", exception)
    }
)
```

### Subscribing to Topics

```kotlin
val fcmManager = FunFCM()

// Subscribe to a topic (done automatically in setup)
fcmManager.setup(context, "topic-name")

// Unsubscribe from a topic
fcmManager.removeSubscription("topic-name")
```

### Token Listener

The SDK automatically handles token refresh. Set a listener to be notified:

```kotlin
fcmManager.setTokenListener { token ->
    // New token received
    Log.d("FCM", "New token: $token")
    sendTokenToServer(token)
}

// Remove listener
fcmManager.setTokenListener(null)
```

## FCM Message Format

The SDK expects FCM messages in **data-only format**. Send messages to a topic or specific device token.

### Example Message

```json
{
  "message": {
    "topic": "[STRING] com.example.fcmtest",
    "data": {
      "icon": "[STRING] YOUR_ICON_URL",
      "title": "[STRING] YOUR_TITLE",
      "short_desc": "[STRING] YOUR_DESCRIPTION",
      "long_desc": "[STRING] YOUR_DESCRIPTION",
      "feature": "[STRING] FEATURE_IMAGE_URL",
      "package": "[STRING] PACKAGE_FOR_PROMOTION / OWN_PACKAGE",
      "crossPromotion": "[BOOLEAN] -> {PROMOTION ENABLES -> AD ATTRIBUTE} false", 
      "genericStyle": "[BOOLEAN] -> {CUSTOM OR GENERIC NOTIFICATION STYLE} true",
      "payload": "{\"icon\":\"https://static.vecteezy.com/...png\",\"feature\":\"https://static.vecteezy.com/...png\",\"package\":\"com.tinder\",\"crossPromotion\":false,\"genericStyle\":false,\"cta\":\"Swipe Now!\",\"rating\":4.9}"
    }
  }
}
```

### Data Attributes

All attributes in the `data` object are sent as strings. Here's what each attribute does:

| Attribute | Required | Type | Description |
|-----------|----------|------|-------------|
| **icon** | Yes | String (URL) | URL to the notification icon image. This will be displayed in the notification. |
| **title** | Yes | String | The main title text displayed in the notification. |
| **short_desc** | Yes | String | Short description text shown in the collapsed notification view. |
| **long_desc** | No | String | Long description text (currently not displayed, reserved for future use). |
| **feature** | No | String (URL) | URL to a feature image for rich notifications. This image will be loaded asynchronously and displayed in the expanded notification. |
| **package** | Yes | String | Package name of the app to open when the notification is clicked. If the app is installed, it opens directly. If not installed, it opens the Play Store page. |
| **crossPromotion** | No | String (Boolean) | Set to `"true"` to use cross-promotion notification style with custom layout. Set to `"false"` or omit for standard notification style. |
| **genericStyle** | No | String (Boolean) | Set to `"true"` to use generic notification style (standard Android notification with image support). Set to `"false"` or omit for custom cross-promotion style. |
| **payload** | No | String (JSON) | Custom JSON string containing additional data. This payload will be passed to your app when the notification is clicked. You can retrieve it from the intent. |

### Payload Handling

The `payload` field allows you to send custom data with the notification. When a user clicks the notification, you can retrieve this payload from the intent in your activity.

**Retrieving Payload in Activity:**

```kotlin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Retrieve payload from intent
        val payload = intent.getStringExtra(ARG_PAYLOAD)
        
        if (payload != null) {
            // Parse JSON payload
            try {
                val jsonObject = JSONObject(payload)
                val icon = jsonObject.optString("icon")
                val feature = jsonObject.optString("feature")
                val packageName = jsonObject.optString("package")
                val cta = jsonObject.optString("cta")
                val rating = jsonObject.optDouble("rating")
                
                // Use the payload data as needed
                Log.d("Payload", "CTA: $cta, Rating: $rating")
            } catch (e: Exception) {
                Log.e("Payload", "Failed to parse payload: ${e.message}")
            }
        }
    }
}
```

**Payload Example:**
```json
{
  "icon": "https://example.com/icon.png",
  "feature": "https://example.com/feature.png",
  "package": "com.app",
  "crossPromotion": false,
  "genericStyle": false,
  "cta": "Swipe Now!",
  "rating": 4.9
}
```

## Notification Styles

### Cross-Promotion Style

Set `crossPromotion: "true"` and `genericStyle: "false"` for a custom cross-promotion notification layout with:
- Custom RemoteViews layout
- Icon and feature image support
- Special styling for promotional content

### Generic Style

Set `genericStyle: "true"` for a standard Android notification with:
- Standard notification layout
- Large icon support
- Big picture style for expanded view
- Optional "AD" prefix in title (when `crossPromotion: "true"`)

## Cross-Promotion Behavior

The SDK automatically handles cross-promotion based on the `package` field:

1. **If app is installed**: Opens the app directly when notification is clicked
2. **If app is not installed**: Opens Play Store page to install the app

The `package` field in the FCM message determines which app to promote or open.

## API Reference

### FcmManager Interface

Main interface for FCM operations.

#### Methods

- `setup(context: Context, topic: String)` - Initialize SDK and subscribe to topic
- `removeSubscription(topic: String)` - Unsubscribe from a topic
- `setTokenListener(listener: FcmTokenListener?)` - Set token update listener
- `getToken(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)` - Get current token

### FunFCM()

Implementation of `FcmManager`. Use this class to interact with the SDK.

```kotlin
val fcmManager = FunFCM()
```

### FcmTokenListener

Interface for receiving token updates.

```kotlin
interface FcmTokenListener {
    fun onTokenReceived(token: String)
}
```

## Troubleshooting

### Notifications not showing

1. Verify Firebase integration is complete:
   - Ensure `google-services.json` is present in `app/` directory
   - Confirm Google Services plugin is applied in build files
   - Check that Firebase dependencies are properly configured
2. Verify notification permissions are granted (Android 13+)
3. Check logcat for error messages
4. Ensure the FCM message uses data-only format (not notification payload)
5. Verify all required fields (`icon`, `title`, `short_desc`, `package`) are present

### Token not received

1. Verify Firebase integration:
   - Ensure `google-services.json` is correctly placed and configured
   - Check that Firebase Messaging dependency is included
   - Verify Google Services plugin is applied
2. Verify internet connection
3. Check logcat for Firebase errors
4. Ensure `setup()` is called in Application class

### Cross-promotion not working

1. Verify `package` field is present in FCM message
2. Check that package name is correct
3. Test with a valid package name
4. Ensure `crossPromotion` field is set to `"true"` string

### Images not loading

1. Verify image URLs are accessible
2. Check internet connection
3. Images are loaded asynchronously via WorkManager - allow time for loading
4. Check logcat for image loading errors

## ProGuard Rules

If you're using ProGuard, add these rules to your `proguard-rules.pro`:

```proguard
# FunNotify SDK
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider
-dontwarn java.lang.invoke.StringConcatFactory
-keep class com.funsol.fcm.FunFCM { *; }
```

## Support

For issues and questions, please open an issue on GitHub.

## Changelog

### Version 1.0.1
- Initial release
- FCM integration
- Cross-promotion support
- Token management
- Image loading for notifications
- WorkManager integration
- Payload support for custom data


## License

Copyright (c) 2025 Funsol Technologies Pvt Ltd

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---
