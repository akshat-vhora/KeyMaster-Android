# PanelApp

Android application for managing panel/game keys and admin operations.

## Features

- **Authentication**: Secure login with session management
- **Key Management**: Generate, list, edit, and delete keys
- **Game Management**: Create, update, and manage games
- **Admin Console**: User management, referrals, bulk operations
- **Referral System**: Create and manage referral codes

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM with StateFlow
- **Networking**: Ktor Client
- **Serialization**: Kotlinx Serialization
- **Dependency Injection**: Manual (via Factory)
- **Build**: Gradle (KTS)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/akshatmodz/panel/
│   │   ├── data/
│   │   │   ├── Models.kt          # Data models & DTOs
│   │   │   ├── RemoteClient.kt    # API client
│   │   │   └── SessionManager.kt  # Session persistence
│   │   ├── ui/
│   │   │   ├── app/
│   │   │   │   └── PanelRoot.kt   # Main navigation & screens
│   │   │   └── theme/
│   │   │       └── Theme.kt       # Material3 theming
│   │   ├── MainActivity.kt
│   │   └── PanelApp.kt            # Application class
│   └── res/                       # Resources
└── build.gradle.kts
```

## Getting Started

### Prerequisites

- Android Studio Ladybug or later
- JDK 17+
- Android SDK 34+

### Setup

1. Clone the repository:
```bash
git clone https://github.com/akshat-vhora/KeyMaster-Android.git
```

2. Open in Android Studio

3. Configure `local.properties` with your SDK path

4. Sync Gradle and run

### Build Variants

- `debug` - Development build with logging
- `release` - Production build (signed)

## API Integration

The app communicates with a Supabase backend via Edge Functions. Configure the base URL in `RemoteClient.kt`.

## License

MIT