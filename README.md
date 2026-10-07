# StackOverflow Users App

An Android application built with **Jetpack Compose**, **Kotlin Coroutines & Flow**, **Hilt**, **Retrofit**, and **Paging 3** using **Clean Architecture** and a multi-module project structure.

---

## 📱 Features

- **User Search Screen**:
  - Real-time user search with **300ms debouncing** to optimize API calls.
  - Infinite scroll / pagination powered by **Paging 3**.
  - Displays user avatar, reputation score, location, and badge counts (Gold, Silver, Bronze).
  - Empty states and error retry handling.

- **User Profile Screen**:
  - Detailed user profile view showing reputation, join date, location, and badge breakdown.
  - Fetches and renders user's **Top Tags** with score and answer count.
  - In-memory caching to minimize duplicate network requests.

- **Telemetry & Logging**:
  - Structured event and error logging for analytics and diagnostic tracing.

---

## 🛠 Tech Stack & Libraries

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
- **Architecture**: Clean Architecture + MVVM with Multi-Module setup (`:app`, `:core`, `:feature:user-search`, `:feature:user-profile`)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) + [OkHttp 4](https://square.github.io/okhttp/) + [Gson](https://github.com/google/gson)
- **Pagination**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- **Asynchronous Execution**: Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) / [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Testing**:
  - **Unit Testing**: JUnit 4, Kotlinx Coroutines Test
  - **UI Testing**: Compose UI Test JUnit 4, Hilt Testing

---

## 🏗 Architecture & Module Structure

The project follows the official **Android Architecture Guidelines** and modularization practices:

```text
StackOverflowUsersApp/
├── app/                        # Application entry point, MainActivity, AppNavHost
├── core/                       # Shared Data, Domain models, API contracts, Telemetry, Utils
└── feature/
    ├── user-search/            # User Search Screen, ViewModel, PagingSource, UseCases
    └── user-profile/           # User Profile Screen, Assisted ViewModel, UseCases
```

### Module Responsibilities:
- **`:core`**: Contains domain models (`User`, `TopTag`), network API contracts (`StackUsersApi`), repository implementation (`UserRepositoryImpl`), and telemetry services (`TelemetryLogger`).
- **`:feature:user-search`**: Encapsulates user search presentation, state management, Paging 3 integration, and search use cases.
- **`:feature:user-profile`**: Encapsulates user profile details, top tag fetching, assisted ViewModel factory, and profile presentation.
- **`:app`**: Binds feature modules together with `AppNavHost` and configures Hilt application entry point.

---
### Build & Run Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/anusreeravi/StackOverflowUsersApp.git
   cd StackOverflowUsersApp
   ```

2. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

4. **Run Instrumented UI Tests**:
   ```bash
   ./gradlew connectedAndroidTest
   ```

---

## 🧪 Testing

The repository includes unit and UI test coverage across all modules:
- **`:core`**: Tests for `UserRepositoryImpl` (caching, error handling, mapping) and `DateUtils`.
- **`:feature:user-search`**: Tests for `UserSearchViewModel`, `UserPagingSource`, `GetUserListUseCase`, and `UserSearchScreenTest`.
- **`:feature:user-profile`**: Tests for `UserProfileViewModel`, `GetUserProfileUseCase`, `GetTopTagsUseCase`, and `UserProfileScreenTest`.

Run all tests via Gradle:
```bash
./gradlew test
```

---

## 🧪Features Estimation for Child Tasks


1.Fetch User List Data from Stack Users API (3 story points)
2.Fetch User Profile Data from Stack Users API (3 story points)
3.Render SearchUser Compose Screen (3 story points)
4.Render UserProfile Compose Screen (3 story points)
5.Integrate API Result with Compose Screen and Add onClick Handler (2 story points)
6.Add Navigation from User List Screen to User ProfileScreen (2 story points)
7.Add Pagination for list of users (3 story points)
8.Automation (3 story points)
9.Ensure Accessibility Acceptance Criteria Met (2 story points)
10.Add Logger for Telemetry (2 story points)
11.Analytics (Not considered at the moment)
12.Database Room(Not considered at the moment due to timeframe)
