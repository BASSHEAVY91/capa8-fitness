# 4. Aplicación de Fitness Personal

Android application built with **Jetpack Compose** and **Kotlin** featuring a persistent side-navigation layout for tracking personal fitness goals.

## 📱 UI Layout

```
┌─────────────────────────────────────────────┐
│     4. Aplicación de Fitness Personal        │  ← Blue TopBar
├──────────┬──────────────────────────────────┤
│  Perfil  │  4. Aplicación de Fitness        │
│          │  Personal                        │
│  Fotos   │                                  │
│          │  Perfil: Estadísticas físicas     │
│  Video   │  y metas del usuario.            │
│          │                                  │
│  Web     │                                  │
└──────────┴──────────────────────────────────┘
 Side panel        Content area
```

## 🏗️ Project Structure

```
FitnessPersonalApp/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/bassheavy91/fitnesspersonalapp/
│       │   ├── MainActivity.kt                   # Single Activity entry point
│       │   ├── navigation/
│       │   │   └── NavRoutes.kt                  # Route definitions & SideNavItem model
│       │   └── ui/
│       │       ├── components/
│       │       │   ├── FitnessTopBar.kt           # Blue branded top bar
│       │       │   └── SideNavPanel.kt            # Persistent left-side menu
│       │       ├── screens/
│       │       │   ├── MainScreen.kt              # Root layout (TopBar + SideNav + Content)
│       │       │   ├── PerfilScreen.kt            # User stats & fitness goals
│       │       │   ├── FotosScreen.kt             # Progress photo gallery
│       │       │   ├── VideoScreen.kt             # Workout video library
│       │       │   └── WebScreen.kt               # Online fitness resources
│       │       └── theme/
│       │           ├── Color.kt                   # Brand color palette
│       │           ├── Theme.kt                   # MaterialTheme configuration
│       │           └── Type.kt                    # Typography scale
│       └── res/
│           └── values/
│               ├── strings.xml
│               └── themes.xml
├── gradle/
│   ├── libs.versions.toml                         # Version catalog
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── .gitignore
```

## 🚀 Getting Started

### Requirements
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11+
- Android SDK 35
- Minimum Android API 26 (Android 8.0)

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/bassheavy91/FitnessPersonalApp.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync complete.
4. Run on an emulator or physical device (API 26+).

## 🧩 Tech Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Kotlin 2.0                        |
| UI          | Jetpack Compose + Material 3      |
| Navigation  | State-driven (rememberSaveable)   |
| Build       | Gradle 8.7 with Version Catalog   |
| Min SDK     | 26 (Android 8.0 Oreo)             |
| Target SDK  | 35 (Android 15)                   |

## 📋 Screens

| Screen  | Description                                        |
|---------|----------------------------------------------------|
| Perfil  | Physical statistics and personal fitness goals     |
| Fotos   | Photographic record of physical progress           |
| Video   | Workout routines and exercise videos               |
| Web     | Online fitness and nutrition articles and guides   |

## 🗺️ Roadmap

- [ ] Perfil: User stats cards (weight, height, BMI, goal progress)
- [ ] Fotos: Before/after photo gallery with camera integration
- [ ] Video: Embedded video player with routine categories
- [ ] Web: WebView with curated fitness resource links
- [ ] Room database for local data persistence
- [ ] ViewModel + StateFlow architecture per screen

## 👤 Author

**bassheavy91** – [@bassheavy91](https://github.com/bassheavy91)

---
*Semestre VI – Herramientas de Programación Móvil*
