# 🎵 MusicPlayer

A modern, feature-rich Android music player built with Jetpack Compose and Material Design 3 Expressive.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2030+-green.svg)](https://android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.12.00-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material3-1.5.0--alpha10-orange.svg)](https://m3.material.io)
[![Navigation3](https://img.shields.io/badge/Navigation3-1.1.0--alpha01-purple.svg)](https://developer.android.com/jetpack/androidx/releases/navigation)

## ✨ Features

### 🎧 Music Management
- **Library Organization**: Browse your music by songs, albums, artists, and playlists
- **Search Functionality**: Quickly find songs, albums, artists, and playlists
- **Favorites**: Mark your favorite tracks for easy access
- **Recently Added**: View newly added music
- **Listen History**: Track your listening history
- **Most Played Artists**: See which artists you listen to most

### 🎮 Playback Control
- **Advanced Audio Player**: Powered by ExoPlayer for high-quality playback
- **Queue Management**: View, reorder, and manage your play queue
- **Playback Speed Control**: Adjust playback speed to your preference
- **Pitch Control**: Modify audio pitch
- **Sleep Timer**: Set a timer to stop playback automatically
- **Skip Silence**: Automatically skip silent portions in tracks
- **Repeat Modes**: Single, all, or no repeat
- **Shuffle Mode**: Random playback order
- **Play Next**: Add songs to play immediately after the current track

### 📑 Playlist Features
- **Create Playlists**: Build custom playlists from your music
- **Import Playlists**: Import existing playlists
- **Export Playlists**: Share your playlists
- **Reorder Tracks**: Drag and drop to reorganize playlist items
- **Playlist Management**: Rename, delete, and edit playlists
- **Custom Artwork**: Set custom images for playlists

### 🎨 Customization
- **Material You**: Dynamic color theming (Android 12+)
- **Theme Options**: Light, Dark, or Follow System
- **Extra Dark Mode**: Optimized for OLED displays
- **Color Palettes**: Multiple color schemes (Blue, Green, Red, Orange, Purple)
- **Picture-Adaptive Colors**: Player colors match album artwork
- **Customizable Player**: Toggle visibility of controls (repeat, shuffle, speed, pitch, timer, lyrics buttons)
- **Volume Slider**: Optional in-app volume control

### 🌍 Localization
- English
- French
- Arabic
- Hindi
- Spanish
- Italian

### 🎵 Additional Features
- **Lyrics Support**: View song lyrics (when available)
- **Metadata Editing**: Edit song information (title, artist, album, genre, composer, year)
- **File Details**: View complete file information
- **Share Music**: Share tracks with other apps
- **Bottom Sheet Actions**: Quick access to song options
- **App Widgets**: Glance-powered home screen widgets
- **Background Playback**: Continue listening while using other apps
- **Media Session Support**: Integration with system media controls

## 🏗️ Architecture

This app follows **Clean Architecture** principles with **MVVM pattern** and **feature-based modularization**:

```
app/
├── core/                    # Shared components
│   ├── data/               # Core data sources and utilities
│   ├── domain/             # Core domain models and contracts
│   └── presentation/       # Shared UI components and theme
├── features/               # Feature modules
│   ├── main/              # Main app feature (library, albums, artists, playlists)
│   │   ├── data/          # Repositories, database, and data sources
│   │   ├── domain/        # Use cases, models, and events
│   │   └── presentation/  # UI screens, components, and ViewModels
│   ├── player/            # Player feature (playback control and state)
│   │   ├── data/          # Player repository and MediaSession
│   │   ├── domain/        # Player state, events, and use cases
│   │   └── presentation/  # Player UI components
│   ├── settings/          # Settings feature
│   ├── permissions/       # Permission handling
│   ├── dialog/            # Dialog activities
│   └── glance/            # Widget implementation
├── di/                    # Dependency injection modules
└── navigation/            # App navigation with Navigation3
```

### Architectural Layers

- **Presentation Layer**: Jetpack Compose UI with ViewModels, reactive state management with StateFlow
- **Domain Layer**: Business logic, use cases, domain models, and events
- **Data Layer**: Room database, DataStore preferences, MediaStore scanner, and repositories

### Tech Stack

#### Core Technologies
- **Kotlin 2.2.21**: Modern, concise programming language with latest features
- **Jetpack Compose**: Declarative UI framework with Compose BOM 2025.12.00
- **Material Design 3**: Material 3 Expressive (1.5.0-alpha10) with adaptive components
- **Media3 ExoPlayer**: Advanced media playback engine (1.8.0)
- **Coroutines & Flow**: Asynchronous programming and reactive streams
- **Navigation3**: Type-safe navigation library (1.1.0-alpha01)

#### Architecture Components
- **Room Database (2.8.4)**: Local data persistence with SQLite
- **Koin (4.1.1)**: Lightweight dependency injection
  - `koin-android`
  - `koin-compose`
  - `koin-compose-viewmodel`
  - `koin-compose-viewmodel-navigation`
- **ViewModel**: Lifecycle-aware state management with ViewModelScope
- **DataStore (1.2.0)**: Modern preferences storage replacing SharedPreferences
- **ProfileInstaller (1.4.1)**: Baseline profile support for startup optimization

#### UI/UX Libraries
- **Material Kolor (4.0.5)**: Dynamic color generation and manipulation
- **KMPalette (3.1.0)**: Color extraction from images for adaptive theming
- **Coil (2.7.0)**: Image loading and caching
- **Material Motion Compose (1.1.3)**: Smooth Material Design transitions
- **Calvin Reorderable (3.0.0)**: Drag-and-drop reordering for LazyColumn/LazyRow
- **Wavy Slider (2.2.0)**: Custom animated slider components
- **LazyColumnScrollbar (2.2.0)**: Scrollbar for lazy lists
- **Compose DnD (0.4.0)**: Additional drag-and-drop functionality
- **Material Icons Extended (1.7.8)**: Comprehensive icon set
- **Material3 Adaptive Navigation Suite (1.4.0)**: Adaptive navigation patterns

#### Media & Metadata
- **JAudioTagger (3.0.1)**: Audio file metadata reading and editing
- **Media3 Session (1.8.0)**: Media session management for background playback
- **Media3 UI (1.8.0)**: Media controls UI components
- **Media3 ExoPlayer Dash (1.8.0)**: DASH streaming support

#### Networking & Serialization
- **Ktor (3.3.3)**: HTTP client for API calls (Deezer API integration)
- **Kotlinx Serialization (1.9.0)**: Kotlin-first serialization

#### Widget Support
- **Glance AppWidget (1.1.1)**: Modern widget framework
  - `glance`
  - `glance-appwidget`
  - `glance-material3`
  - `glance-material`

#### Development Tools
- **Timber (5.0.1)**: Extensible logging
- **DiskLruCache (1.7)**: Disk-based LRU cache
- **KSP (2.2.20-2.0.3)**: Kotlin Symbol Processing for code generation
- **Ktlint (14.0.1)**: Kotlin linter and formatter
- **Kotzilla (1.4.1)**: Koin configuration validation
- **Desugar JDK Libs (2.1.5)**: Java 8+ API desugaring for older Android versions

## 📋 Requirements

- **Minimum SDK**: Android 11 (API 30)
- **Target SDK**: Android 15 (API 36)
- **Compile SDK**: Android 15 (API 36)
- **Java Version**: 21

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or later
- JDK 21
- Android SDK 36

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/MyMusicPlayer.git
   cd MyMusicPlayer
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository

3. **Build the project**
   - Let Gradle sync and download dependencies
   - Build > Make Project (Ctrl+F9 / Cmd+F9)

4. **Run the app**
   - Connect an Android device or start an emulator
   - Run > Run 'app' (Shift+F10 / Ctrl+R)

### Building Release APK

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/release/app-release.apk`

## 📱 Permissions

The app requires the following permissions:
- **Read Media Audio**: To access and play music files on your device
- **Storage Access**: For Android 10 and below

## 🗂️ Project Structure

```
app/
├── src/main/java/younesbouhouche/musicplayer/
│   ├── core/                          # Shared core components
│   │   ├── data/                      # Core data layer
│   │   │   ├── database/             # Room database entities and DAOs
│   │   │   ├── ext/                  # Extension functions
│   │   │   └── local/                # MediaStore scanner
│   │   ├── domain/                    # Core domain layer
│   │   │   ├── ext/                  # Domain extensions
│   │   │   └── models/               # Shared domain models
│   │   └── presentation/              # Shared UI components
│   │       ├── components/           # Reusable UI components
│   │       └── theme/                # Material Theme configuration
│   │
│   ├── features/                      # Feature modules
│   │   ├── main/                     # Main app feature
│   │   │   ├── data/                 # Data layer
│   │   │   │   ├── db/              # Room database
│   │   │   │   ├── mappers/         # Data mappers
│   │   │   │   └── repository/      # Repository implementations
│   │   │   ├── domain/               # Domain layer
│   │   │   │   ├── events/          # Domain events (UiAction, UiEvent)
│   │   │   │   ├── models/          # Domain models (LoadingState, etc.)
│   │   │   │   └── usecases/        # Use cases
│   │   │   ├── presentation/         # Presentation layer
│   │   │   │   ├── components/      # UI components
│   │   │   │   ├── dialogs/         # Dialog composables
│   │   │   │   ├── navigation/      # Main navigation
│   │   │   │   ├── player/          # Player screen UI
│   │   │   │   ├── routes/          # Feature routes
│   │   │   │   │   ├── album/       # Albums screen
│   │   │   │   │   ├── artist/      # Artists screen
│   │   │   │   │   ├── home/        # Home screen
│   │   │   │   │   ├── library/     # Library screen
│   │   │   │   │   └── playlist/    # Playlists screen
│   │   │   │   ├── states/          # UI state models
│   │   │   │   ├── util/            # Presentation utilities
│   │   │   │   └── viewmodel/       # ViewModels
│   │   │   └── util/                 # Feature utilities
│   │   │
│   │   ├── player/                   # Player feature
│   │   │   ├── data/                 # Player data layer
│   │   │   │   └── repository/      # Player repository
│   │   │   ├── domain/               # Player domain layer
│   │   │   │   ├── events/          # Player events
│   │   │   │   ├── models/          # Player state models
│   │   │   │   └── usecases/        # Player use cases
│   │   │   └── presentation/         # Player UI components
│   │   │
│   │   ├── settings/                 # Settings feature
│   │   │   └── presentation/        # Settings screens
│   │   │
│   │   ├── permissions/              # Permission handling
│   │   │   └── presentation/        # Permission screens
│   │   │
│   │   ├── dialog/                   # Dialog activities
│   │   │   └── presentation/        # Dialog implementations
│   │   │
│   │   └── glance/                   # Widget implementation
│   │       └── presentation/        # Glance composables
│   │
│   ├── di/                           # Dependency injection
│   │   └── modules/                 # Koin modules
│   │
│   ├── navigation/                   # App-level navigation
│   │   ├── routes/                  # Navigation routes
│   │   ├── util/                    # Navigation utilities
│   │   ├── AppNavGraph.kt           # Main navigation graph
│   │   ├── EventHandler.kt          # Navigation event handling
│   │   └── MainApp.kt               # App entry point
│   │
│   └── MainActivity.kt               # Single activity
│
└── src/main/res/                     # Resources
    ├── drawable/                     # Vector drawables
    ├── font/                         # Custom fonts
    ├── layout/                       # XML layouts (for widgets)
    ├── mipmap-*/                    # App icons
    ├── values/                      # Default resources
    ├── values-ar/                   # Arabic translations
    ├── values-en/                   # English translations
    ├── values-fr/                   # French translations
    ├── values-hi/                   # Hindi translations
    └── xml/                         # Data extraction rules, file paths
```

## 🎯 Key Components

### Main Features

#### Navigation Structure
- **AppNavGraph**: Main app navigation using Navigation3 with Material Motion animations
  - **Permissions Graph**: Permission request handling
  - **Main Graph**: Primary app navigation with bottom navigation
  - **Settings Graph**: Settings and preferences

#### Main Screens (Bottom Navigation)
- **HomeScreen**: Quick access to favorites, recently added, listen history, and most played artists
- **Library**: All songs in your library with search and filter
- **Albums**: Browse and manage albums with grid/list views
- **Artists**: Browse artists and their songs
- **Playlists**: Create and manage custom playlists

#### Player Feature
- **PlayerScreen**: Full-screen player with album art, controls, and queue
- **MiniPlayer**: Compact player bar at the bottom of the screen
- **Queue Management**: View, reorder, and manage playback queue
- **Playback Controls**: Play, pause, skip, shuffle, repeat modes
- **Advanced Features**: Speed control, pitch adjustment, sleep timer, skip silence

#### Dialogs & Sheets
- **CreatePlaylistDialog**: Create new playlists with custom names
- **AddToPlaylistDialog**: Add songs to existing playlists
- **PlaybackParamsSheet**: Adjust playback speed, pitch, and other parameters
- **QueueSheet**: View and manage the current play queue
- **SongOptionsSheet**: Quick actions for individual songs (add to playlist, edit metadata, share, etc.)
- **MetadataEditorDialog**: Edit song information (title, artist, album, year, genre, composer)

#### Settings
- **Theme Settings**: Light/Dark/System theme, Extra Dark mode for OLED
- **Color Settings**: Material You dynamic colors, custom color palettes
- **Player Settings**: Customize player UI (toggle buttons, volume slider)
- **Library Settings**: Manage library scanning and metadata
- **About**: App information and developer details

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Translation
Help translate the app to your language! Check out the translation guide in the app settings.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Developer

**Younes Bouhouche**  
Mobile, Desktop & Web Developer

- GitHub: [@younesbouhouche](https://github.com/younesbouhouche)

## 🙏 Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Uses [ExoPlayer](https://github.com/google/ExoPlayer) for media playback
- Icons from [Material Icons](https://fonts.google.com/icons)
- Inspired by modern music player designs

## 📊 App Info

- **Package Name**: `younesbouhouche.musicplayer`
- **Version**: 1.0
- **Version Code**: 1
- **Min SDK**: 30 (Android 11)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36 (Android 15)

## 🔄 Recent Changes

### Architecture Refactoring (December 2025)
- ✅ **Fixed Room Database**: Corrected entities and relationships for proper data persistence
- ✅ **Cover Art Caching**: Implemented persistent cover art caching with MediaMetadataRetriever
- ✅ **Navigation3 Migration**: Updated from Navigation2 to Navigation3 for type-safe navigation
- ✅ **Package Reorganization**: Restructured files into appropriate feature-based packages
- ✅ **Code Cleanup**: Removed unnecessary data classes and redundant code
- ✅ **Loading States**: Added granular 4-step loading progress tracking for media scanning
- ✅ **Queue Management**: Implemented reactive Flow transformations for queue handling
- ✅ **Performance Optimization**: Parallel cover fetching with proper synchronization

---

Made with ❤️ using Jetpack Compose
