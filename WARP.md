# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

MusicPlayer is a modern Android music player built with Jetpack Compose, Material Design 3 Expressive, and Clean Architecture. The app follows MVVM pattern with feature-based modularization.

**Key Technologies:**
- Kotlin 2.2.21 with Java 21
- Jetpack Compose (BOM 2025.12.00) with Material3 (1.5.0-alpha10)
- Navigation3 (1.1.0-alpha01) for type-safe navigation
- Media3 ExoPlayer (1.8.0) for audio playback
- Room Database (2.8.4) for local persistence
- Koin (4.1.1) for dependency injection
- DataStore (1.2.0) for preferences
- Ktor (3.3.3) for API calls (Deezer integration)

**Build Requirements:**
- Min SDK: 30 (Android 11)
- Target SDK: 36 (Android 15)
- Compile SDK: 36
- JDK: 21

## Common Development Commands

### Build & Run
```powershell
# Build the project
.\gradlew.bat build

# Build release APK (with minification)
.\gradlew.bat assembleRelease
# Output: app\release\app-release.apk

# Build debug APK
.\gradlew.bat assembleDebug

# Build staging variant
.\gradlew.bat assembleStaging

# Build benchmark variant (for performance profiling)
.\gradlew.bat assembleBenchmark

# Install and run debug build on connected device
.\gradlew.bat installDebug
```

### Code Quality
```powershell
# Format code with ktlint
.\gradlew.bat ktlintFormat

# Check code style
.\gradlew.bat ktlintCheck

# Validate Koin configuration (will detect DI issues at build time)
.\gradlew.bat kspDebugKotlin
```

### Testing
```powershell
# Run unit tests
.\gradlew.bat test

# Run instrumented tests on connected device
.\gradlew.bat connectedAndroidTest

# Run specific test class
.\gradlew.bat test --tests younesbouhouche.musicplayer.ExampleUnitTest
```

### Clean & Rebuild
```powershell
# Clean build artifacts
.\gradlew.bat clean

# Clean and rebuild
.\gradlew.bat clean build
```

## Architecture Overview

### Layer Structure

The app follows **Clean Architecture** with strict separation between layers:

**Presentation → Domain ← Data**

- **Presentation Layer**: Jetpack Compose UI, ViewModels, UI state management with StateFlow
- **Domain Layer**: Business logic, use cases, domain models, events, and repository interfaces
- **Data Layer**: Repository implementations, Room database, DataStore, MediaStore scanner, and API clients

### Feature Modules

Each feature is self-contained with its own data/domain/presentation structure:

- **main**: Core library functionality (songs, albums, artists, playlists)
- **player**: Audio playback, queue management, MediaSession integration
- **settings**: App preferences and configuration
- **permissions**: Runtime permission handling
- **dialog**: Dialog activities (for launcher integration)
- **glance**: Home screen widgets

### Core Components

**Core** contains shared functionality across features:
- `core/data`: Database entities, DAOs, extension functions, MediaStore scanner
- `core/domain`: Domain models (Song, Album, Artist, Playlist, Queue), repository interfaces, mappers, PlayerManager/PlayerFactory
- `core/presentation`: Reusable UI components, Material Theme configuration

### Key Architectural Patterns

#### Dependency Injection (Koin)
All dependencies are defined in `di/modules/`:
- `AppModule.kt`: HttpClient, includes other modules
- `DatabaseModule.kt`: Room database and DAOs
- `RepoModule.kt`: Repository implementations
- `ViewModelModule.kt`: All ViewModels
- `UtilsModule.kt`: PlayerManager, PlayerFactory, MediaStoreScanner, etc.
- `UseCaseModule.kt`: Use case instances
- `DialogModule.kt`: Dialog-specific dependencies

**Important**: The app uses Kotzilla plugin for Koin configuration validation at build time. If you add new Koin dependencies, build the project to verify configuration.

#### Navigation Architecture
Uses **Navigation3** (type-safe navigation) with Material Motion transitions:
- Top-level navigation: `navigation/AppNavGraph.kt` manages three graphs (Permissions, Main, Settings)
- Graph routes: `navigation/routes/Graph.kt` defines serializable route keys
- Entry point: `navigation/MainApp.kt` initializes nav backstack
- Feature navigation: Each feature has its own navigation graph

#### Player Architecture
**Dual-component player system**:

1. **PlayerManager** (`core/domain/player/PlayerManager.kt`):
   - Manages ExoPlayer instance lifecycle and state
   - Handles playback controls, queue management
   - Maintains player state via PlayerStateManager
   - Coordinates with repositories (music, queue, preferences)

2. **MediaSessionManager** (`features/player/presentation/service/MediaSessionManager.kt`):
   - Creates and manages MediaSession for background playback
   - Integrates with MediaPlayerService (foreground service)
   - Handles system media controls and notifications
   - Restores player state on app restart

**PlayerFactory** (`core/domain/player/PlayerFactory.kt`): Singleton factory that provides ExoPlayer instance across the app.

**PlayerStateManager** (`core/domain/player/PlayerStateManager.kt`): Centralized state holder exposing `StateFlow<PlayerState>`.

#### Repository Pattern
Repositories mediate between data sources and domain layer:
- `MusicRepository`: MediaStore scanning, song/album/artist data
- `PlaylistRepository`: Playlist CRUD operations
- `QueueRepository`: Playback queue state
- `PreferencesRepository`: Settings persistence via DataStore
- `PlayerRepository`: Player-specific data operations

All repositories expose data as `Flow` for reactive UI updates.

#### Database Architecture
Room database with normalized schema:
- **Entities**: SongEntity, AlbumEntity, ArtistEntity, PlaylistEntity, QueueEntity, PlayHistEntity, SongStateEntity
- **Cross-reference tables**: PlaylistSongCrossRef, QueueSongCrossRef, PlayHistSongCrossRef
- **DAOs**: One per entity type, with Flow-based queries
- **Type converters**: UriConverter for Uri serialization

Database file: `AppDatabase.kt` (version 1)
Schema location: `app/schemas/`

#### Media Library Initialization
The app uses a **4-step loading process** tracked via `LoadingState`:

1. **Scan MediaStore**: Query all audio files from device storage
2. **Fetch covers**: Extract and cache album artwork using MediaMetadataRetriever (parallel processing)
3. **Build albums**: Group songs by album name
4. **Persist to Room**: Save all entities to local database

Progress is tracked with `LoadingState(step: Int, stepsCount: Int, progress: Int, progressMax: Int)`.

See: `core/data/local/MediaStoreScanner.kt`

#### Event-Driven Architecture
Domain events are defined as sealed classes/interfaces:
- `UiAction`: User-triggered actions sent to ViewModels
- `UiEvent`: ViewModel responses to the UI
- `PlaylistEvent`: Playlist-specific operations
- `MetadataEvent`: Song metadata editing events
- `SearchAction`: Search-related actions

ViewModels expose events via `SharedFlow` or `StateFlow`.

#### Cover Art Management
- Album artwork is extracted from audio files using `MediaMetadataRetriever`
- Covers are cached to `getExternalFilesDir(DIRECTORY_PICTURES)/covers/`
- Cache path stored in SongEntity as `coverPath`
- Coil loads covers via custom `AppSpecificStorageFetcher` for app-specific URIs

## Important Conventions

### Package Naming
- Root package: `younesbouhouche.musicplayer`
- Features: `features/{feature_name}/{layer}`
- Core: `core/{layer}`
- DI: `di/modules`
- Navigation: `navigation`

### ViewModel Naming
- Feature screens: `{FeatureName}ViewModel` (e.g., `AlbumsViewModel`)
- Shared/main: `MainViewModel`, `PlayerViewModel`
- Location: `features/{feature}/presentation/viewmodel/` or `features/{feature}/presentation/routes/{route}/`

### Composable Naming
- Screens: `{FeatureName}Screen` (e.g., `AlbumsScreen`)
- Reusable components: Descriptive names (e.g., `SongItem`, `CreatePlaylistDialog`)
- Location: `features/{feature}/presentation/` or `core/presentation/components/`

### State Management
- UI state: Data classes with `State` suffix (e.g., `AlbumsState`)
- ViewModels use `StateFlow` for state, `SharedFlow` for one-time events
- Compose observes state with `.collectAsState()` or `.collectAsStateWithLifecycle()`

### Kotlin Coroutines
- Repository functions are suspending functions or return Flow
- ViewModel operations use `viewModelScope.launch`
- Background work uses `Dispatchers.IO`, UI updates use `Dispatchers.Main`

## Working with This Codebase

### Adding a New Feature
1. Create feature package: `features/{feature_name}/{data,domain,presentation}`
2. Define domain models in `domain/models/`
3. Create repository interface in `core/domain/repositories/` or feature domain
4. Implement repository in `data/repository/`
5. Register repository in `di/modules/RepoModule.kt`
6. Create use cases in `domain/usecases/`, register in `UseCaseModule.kt`
7. Create ViewModel in `presentation/viewmodel/`, register in `ViewModelModule.kt`
8. Create UI in `presentation/`

### Modifying Player Behavior
- Playback controls: Modify `PlayerManager` in `core/domain/player/`
- UI changes: Edit components in `features/player/presentation/`
- Background playback: Update `MediaPlayerService` or `MediaSessionManager`
- Player state: Extend `PlayerState` data class in `features/player/domain/models/`

### Adding Database Entities
1. Create entity in `core/data/database/entities/`
2. Add DAO in `core/data/database/dao/`
3. Register in `AppDatabase.kt` entities and abstract DAO property
4. Register DAO in `DatabaseModule.kt`
5. **Increment database version** in `AppDatabase.kt`
6. Rebuild project to generate schema in `app/schemas/`

### Working with Preferences
- Define preference key in `core/data/datastore/SettingsPreference.kt`
- Access via `PreferencesRepository.get(key)` (returns Flow) or `set(key, value)`
- UI uses `rememberDataFlow` helper to observe preferences

### Modifying Navigation
- Add route to `navigation/routes/` (must be serializable)
- Register serializer in `MainApp.kt` polymorphic block
- Add navigation logic in appropriate NavGraph
- Use `backStack.add(route)` to navigate, `backStack.popUntil { }` to pop

### API Integration (Deezer)
- HttpClient configured in `AppModule.kt` with Ktor
- Base URL: `BuildConfig.BASE_URL` (defined in build.gradle.kts)
- Artist picture fetching: `core/data/remote/ArtistsPictureFetcher.kt`
- Add new endpoints: Create data classes, retrofit-style suspend functions

## Build Variants

- **debug**: Development build, debuggable, no minification
- **release**: Production build with ProGuard/R8 minification
- **staging**: Release-like build with `.staging` suffix for testing
- **benchmark**: Special build for baseline profile generation and performance testing

ProGuard rules: `app/proguard-rules.pro`

## Localization

Translations are in `app/src/main/res/values-{lang}/`:
- `values/`: English (default)
- `values-fr/`: French
- `values-ar/`: Arabic
- `values-hi/`: Hindi
- `values-es/`: Spanish (implied from README)
- `values-it/`: Italian (implied from README)

When adding strings, always add to default `values/strings.xml` first.

## Widget Development

The app uses **Glance** for home screen widgets:
- Widget implementation: `features/glance/presentation/`
- Widget receiver: AndroidManifest.xml
- Update widget: `MyAppWidget().updateAll(context)`
- Glance uses Compose-like syntax but has limitations (no full Compose support)

## Logging

Uses **Timber** for logging:
```kotlin
Timber.tag("YourTag").d("Debug message")
Timber.tag("YourTag").e("Error message")
```

Timber is initialized in the Application class (`di/App.kt`).

## Performance Considerations

- **Cover fetching**: Done in parallel using `async`/`awaitAll` to avoid blocking
- **Flow transformations**: Use Flow operators for reactive queue/playlist updates
- **LazyColumn**: Used for all lists (songs, albums, artists)
- **ProfileInstaller**: Baseline profiles for startup optimization
- **Image caching**: Coil handles memory (25%) and disk (3%) caching
- **ProGuard**: Enabled in release builds to reduce APK size

## Known Architecture Details

### Single Activity Design
The app uses a single `MainActivity` that hosts all Compose navigation. No fragment usage.

### Media Session Lifecycle
- MediaSession is created by `MediaPlayerService` (foreground service)
- `MediaSessionManager.initialize()` called in `MainActivity.onCreate()`
- Player state restored from MediaController on app restart
- Service lifecycle tied to playback state

### Queue Management
Queue is reactive: managed through `QueueRepository` which exposes `Flow<Queue>`. Changes propagate to PlayerManager and UI automatically.

### Dynamic Theming
- Material You dynamic colors based on system (Android 12+)
- Custom color palettes (Blue, Green, Red, Orange, Purple)
- Picture-adaptive colors: Player UI adapts to album artwork colors
- Implementation: `core/presentation/theme/` using Material Kolor library

## Testing Notes

The project has minimal tests currently (`ExampleUnitTest`, `ExampleInstrumentedTest`). When adding tests:
- Unit tests: `app/src/test/`
- Instrumented tests: `app/src/androidTest/`
- Benchmark tests: `benchmark/src/main/`

## Additional Resources

- README.md: Comprehensive feature list and setup instructions
- BuildConfig fields: API base URL configured per build type
- Gradle version catalog: `gradle/libs.versions.toml`
