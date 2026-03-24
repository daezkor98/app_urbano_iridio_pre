# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install debug build on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Lint check
./gradlew lint

# Clean build
./gradlew clean build
```

## Architecture: MVP (Model-View-Presenter)

The app follows a strict MVP pattern with clear layer separation:

- **`ui/`** — Activities and Fragments that implement View interfaces. They delegate all logic to their Presenter.
- **`presenter/`** — ~57 Presenter classes containing business/UI logic. Each presenter holds a reference to a View interface and one or more Interactors.
- **`view/`** — ~50 View interfaces that define the contract between Presenter and UI (e.g., `DetalleRutaView`).
- **`model/interactor/`** — Business logic classes that call the data layer (REST, local DB) and return results to presenters.
- **`model/entity/`** — Domain/ORM entities persisted via Sugar ORM.
- **`data/`** — Separate Gradle module: repositories, API service interfaces, `PreferencesHelper`, and environment configuration. API base URLs are built dynamically via `ApiRest.buildUrbanoApiBaseUrl()`.
- **`urbanocore/`** — Separate Gradle module with shared Kotlin utilities.

### Key Flow Pattern

```
Activity/Fragment (View) → Presenter → Interactor → data/rest (Retrofit/Volley) or local DB (Sugar ORM)
                         ← Presenter ← Interactor ←
```

Presenters are instantiated in Activities/Fragments and receive the Activity as the View interface. Hilt (Dagger) is used for dependency injection throughout.

## Module Structure

- **`:app`** — Main application module (all UI, presenters, interactors)
- **`:data`** — Shared data layer (Retrofit services, repositories, SharedPreferences)
- **`:urbanocore`** — Shared Kotlin utilities

## Tech Stack

| Concern | Library |
|---|---|
| DI | Dagger Hilt 2.40.1 |
| Networking | Retrofit 2.9.0 + OkHttp3 (also legacy Volley for some endpoints) |
| Reactive | RxJava3 + Kotlin Coroutines |
| Local DB | Sugar ORM (`urbano.db`, schema v58) |
| Images | Glide 4.12.0 |
| Maps | Google Maps SDK + play-services-location |
| QR Scanning | ZXing |
| Firebase | Analytics, Crashlytics, Messaging, Performance |

## Build Variants

- **`debug`**: `USE_PRODUCTION=false`, ProGuard disabled, Crashlytics upload disabled.
- **`release`**: `USE_PRODUCTION=true`, ProGuard enabled, keystore at `D:\UrbanoApps\credentials\Key Store\Iridio3\urbano-iridio3.jks`.

## Important Conventions

- **Language**: App is Spanish-only (`resConfigs 'es'`). All variable names, comments, class names, and UI strings use Spanish.
- **Mixed Java/Kotlin**: Most code is Java; newer features/fragments tend to be Kotlin. Both coexist freely.
- **View Binding** is enabled — use `ActivityXxxBinding` / `FragmentXxxBinding` instead of `findViewById`.
- **Entities** must be kept in ProGuard rules (`com.urbanoexpress.iridio3.model.entity.**`) or Sugar ORM will break.
- **Two HTTP clients coexist**: Retrofit (modern endpoints) and Volley (legacy endpoints). Do not assume all API calls use Retrofit.

## Domain Context

This is a logistics/delivery app for urban couriers (Urbano Express). Key domain concepts:
- **Ruta** — A delivery route assigned to a driver for a day
- **Guía** — An individual shipment/package within a route
- **Parada** — A delivery stop (may contain multiple guías grouped by address)
- **Ruta Rural** — Rural route variant with different workflows
- **Gestión** — Actions taken on a guía (delivered, not delivered, collected, returned, etc.)
- **Liquidación** — End-of-day route settlement/closing process
