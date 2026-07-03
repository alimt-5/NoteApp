# NoteApp

### About
**NoteApp** is a modern, lightweight note-taking application for Android. It provides a clean and intuitive interface for creating, viewing, and managing personal notes. Built with **Jetpack Compose** and **Material 3**, it delivers a smooth and adaptive user experience.

### Features
- ➕ Create new notes with auto‑direction text (RTL/LTR support)
- 📋 View all saved notes in a sorted list
- 🎨 Material 3 design with dynamic theming (Material You)
- 📱 Offline‑first architecture – all data stored locally
- 🧹 Clean and maintainable codebase

### Tech Stack
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Architecture:** MVVM + Clean Architecture
- **Dependency Injection:** Dagger Hilt
- **State Management:** StateFlow, SharedFlow
- **Local Database:** Room
- **Concurrency:** Kotlin Coroutines & Flow

### Architecture
The project follows a layered approach:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  (Screens, ViewModels, UI State/Events) │
├─────────────────────────────────────────┤
│            Domain Layer                 │
│        (Use Cases, Domain Models)       │
├─────────────────────────────────────────┤
│            Data Layer                   │
│    (Repository, Local/Remote Sources)   │
└─────────────────────────────────────────┘
```

### Project Structure
```
app/src/main/java/com/example/noteapp/
├── data/                  # Data sources (Room, Repository implementations)
├── di/                    # Dagger Hilt modules
├── presentation/          # UI layer
│   ├── screens/           # Compose screens (AddNote, NoteScreen, etc.)
│   ├── NotesState.kt      # UI state definitions
│   ├── NotesEvents.kt     # UI event definitions
│   └── NotesViewModel.kt  # ViewModel with state handling
├── ui/theme/              # Material 3 theming
├── MainActivity.kt        # Main activity
└── NoteApp.kt             # Application class
```

### Getting Started

#### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- JDK 17
- Android SDK 24+

#### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/alimt-5/NoteApp.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle files.
4. Run the app on an emulator or physical device.

### Contributing
Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---
