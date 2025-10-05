# 🎵 MusicPlayer

A modern, feature-rich Android music player built with Jetpack Compose and Material Design 3 Expressive.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2030+-green.svg)](https://android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material3-Latest-orange.svg)](https://m3.material.io)

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

This app follows clean architecture principles with MVVM pattern:

- **Presentation Layer**: Jetpack Compose UI with ViewModels
- **Domain Layer**: Business logic and use cases
- **Data Layer**: Room database, DataStore preferences, and repositories

### Tech Stack

#### Core
- **Kotlin 2.2**: Modern, concise programming language
- **Jetpack Compose**: Declarative UI framework
- **Material 3**: Latest Material Design components
- **ExoPlayer**: Advanced media playback
- **Coroutines & Flow**: Asynchronous programming

#### Architecture Components
- **Room Database**: Local data persistence
- **Navigation Compose**: Type-safe navigation
- **ViewModel**: Lifecycle-aware state management
- **DataStore**: Preferences storage
- **Koin**: Dependency injection

#### UI/UX Libraries
- **Material Kolor**: Color utilities
- **KMPalette**: Color extraction from images
- **Coil**: Image loading
- **Material Motion Compose**: Smooth animations
- **Reorderable**: Drag-and-drop functionality
- **WaveSlider**: Custom slider components

#### Media & Metadata
- **JAudioTagger**: Audio metadata editing
- **Media3 Session**: Media session management
- **Media3 UI**: Media controls UI

#### Networking
- **Ktor**: HTTP client for API calls
- **Gson**: JSON serialization
- **Converter Gson**: Retrofit converter

#### Other
- **Timber**: Logging
- **DiskLruCache**: Disk caching
- **ProfileInstaller**: Baseline profile support

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
│   ├── core/           # Core utilities and shared components
│   ├── di/             # Dependency injection modules
│   ├── dialog/         # Dialog activities
│   ├── glance/         # Widget implementation
│   ├── main/           # Main app features
│   │   ├── data/       # Data layer (repositories, database)
│   │   ├── domain/     # Domain layer (models, events, use cases)
│   │   └── presentation/ # UI layer (screens, components, viewmodels)
│   ├── settings/       # Settings feature
│   ├── ui/             # UI theme and styling
│   └── welcome/        # Welcome/onboarding screens
└── src/main/res/       # Resources (layouts, strings, drawables)
```

## 🎯 Key Components

### Main Screens
- **HomeScreen**: Quick access to favorites, history, and most played
- **LibraryScreen**: All music files in your library
- **AlbumsScreen**: Browse music by albums
- **ArtistsScreen**: Browse music by artists
- **PlaylistsScreen**: Manage your playlists
- **SearchScreen**: Search across all music content

### Dialogs
- **CreatePlaylistDialog**: Create new playlists
- **AddToPlaylistDialog**: Add songs to existing playlists
- **MusicCardBottomSheet**: Quick actions for songs

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

---

Made with ❤️ using Jetpack Compose
