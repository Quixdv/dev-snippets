# Dev Snippets

A modern, offline-first Android developer tool for storing, organizing, searching and
sharing code snippets. Built with **Kotlin**, **Jetpack Compose**, **Material 3**,
**Room**, **Hilt** and **MVVM + Repository** architecture.

## Features

- Dark mode by default, with 4 selectable accent themes (Purple Neon, Ocean Blue,
  Forest Green, Sunset Orange)
- Categories: Android, Kotlin, Python, JavaScript, SQL, HTML/CSS, Shell, Custom
- Create, edit, delete, favorite and pin snippets
- Instant search across title, language, tags and code content
- Tag support
- Lightweight built-in syntax highlighter (no external dependency)
- One-tap copy to clipboard and share as text
- Recent snippets section and pinned section on Home
- Markdown-style free-text notes per snippet
- Line numbers in the code viewer (toggleable)
- Auto-save while editing (debounced, toggleable)
- JSON export/import for backup & restore, using Android's Storage Access Framework
- Optional biometric lock (fingerprint/face) on app start
- Statistics screen: total snippets, favorites, pinned, language breakdown
- 100% local storage (Room) — no account, no network required

## Architecture

```
app/
 └─ src/main/java/com/devsnippets/app/
     ├─ data/
     │   ├─ local/            Room database, DAO, entity, DataStore preferences
     │   ├─ mapper/           Entity <-> Domain model mappers
     │   └─ repository/       SnippetRepositoryImpl
     ├─ di/                   Hilt modules (Database, Repository)
     ├─ domain/
     │   ├─ model/            Snippet, SnippetLanguage
     │   └─ repository/       SnippetRepository interface
     ├─ ui/
     │   ├─ components/       Reusable Compose components (cards, chips, search bar...)
     │   ├─ navigation/       NavGraph + bottom navigation scaffold
     │   ├─ screens/          One package per feature screen (MVVM: Screen + ViewModel)
     │   └─ theme/            Color / Type / Shape / Theme (Material 3)
     ├─ util/                 Clipboard, Share, JSON import/export, Biometric, Files
     ├─ DevSnippetsApp.kt     @HiltAndroidApp Application class
     └─ MainActivity.kt       Single-activity host, biometric gate, root theme
```

- **MVVM**: each screen has a `XxxViewModel` exposing a `StateFlow<XxxUiState>`.
- **Repository pattern**: ViewModels never touch Room directly — only `SnippetRepository`.
- **Room**: single `snippets` table, reactive `Flow` queries power the UI.
- **DataStore**: lightweight key-value store for user settings (theme, auto-save, etc).
- **Hilt**: `DatabaseModule` (Room + DAO) and `RepositoryModule` (interface binding).
- **Navigation Compose**: typed routes defined in `Screen.kt`, hosted by
  `DevSnippetsNavHost` which also owns the bottom navigation bar and FAB.

## Building the project

1. Open the project root in **Android Studio Koala (2024.1)** or newer.
2. Let Gradle sync (requires JDK 17).
3. Run the `app` configuration on an emulator or device with **minSdk 26 (Android 8.0)**
   or newer.

No API keys, backend or third-party accounts are required — the app is fully offline.

### Requirements
- Android Studio Koala+ / Gradle 8.7 / Android Gradle Plugin 8.5.2
- Kotlin 1.9.24
- compileSdk / targetSdk 34, minSdk 26

## Publishing checklist

Before publishing to Google Play, remember to:
- Replace the placeholder vector launcher icon (`res/drawable/ic_launcher_*.xml`)
  with a final adaptive icon (Image Asset Studio recommended).
- Fill in a real `applicationId` if `com.devsnippets.app` is already taken.
- Generate and configure a release signing key (`signingConfigs` block in
  `app/build.gradle.kts`).
- Review `proguard-rules.pro` against your final dependency set after a release build.
- Add a privacy policy URL (the app collects no data, but Play Console still requires
  a data-safety declaration).

## License

You own all rights to the generated source code in this project.
