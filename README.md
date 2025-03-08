# Android Task Manager App

## Design Rationale

### Architecture

#### MVVM (Model-View-ViewModel)
- **Why**: Separates UI logic (View in Compose) from business logic (ViewModel), improving testability and maintainability.
- **Implementation**: The `TaskViewModel` manages state (e.g., `filteredTasks`, `is_loading`) as `StateFlow`, which is observed by composables like `HomeScreen`.

#### Dependency Injection with Hilt
- **Why**: Simplifies providing dependencies (e.g., `TaskViewModel`, `DataStore`) across the app, reducing boilerplate and enhancing modularity.
- **Implementation**: Hilt injects the `TaskViewModel` into composables, with test overrides for fakes.

#### Single Activity with Navigation Compose
- **Why**: Leverages Jetpack Navigation for a seamless, type-safe navigation experience within a single activity, aligning with modern Android practices.
- **Implementation**: `NavHost` navigates between `HomeScreen`, `TaskDetailScreen`, etc.

---

## UI Design

### Jetpack Compose
- **Why**: Offers a declarative, reactive UI framework that reduces code complexity and improves performance over XML layouts.
- **Implementation**: `HomeScreen` uses `Scaffold`, `LazyColumn`, and `SwipeableTaskItem` for a dynamic, interactive task list.

### Material 3
- **Why**: Provides a modern, consistent design system with adaptive components (e.g., `TopAppBar`, `Card`).
- **Implementation**: Used in `TaskDetailScreen` (e.g., `Card` for task details) and `HomeScreen` (e.g., `TopAppBar`).

---

## State Management
- **Why**: Reactive state with `StateFlow` ensures the UI updates automatically when data changes, enhancing user experience.
- **Implementation**: `taskList` in `HomeScreen` syncs with `filteredTasks` via `LaunchedEffect`.

---

## User Feedback
- **Why**: Loading states (shimmer), empty states, and snackbars improve usability by providing clear feedback.
- **Implementation**: `ShimmerTaskList`, `EmptyStateUI`, and `SnackbarHost` with undo actions in `HomeScreen`.

---

## Testing

### UI Tests with Compose Testing
- **Why**: Ensures UI behaves as expected across states (loading, empty, populated) and interactions (swipes, clicks).
- **Implementation**: `HomeScreenTest` uses `createAndroidComposeRule` to test navigation, swipe actions, and state changes.

### Fake ViewModel
- **Why**: Avoids Mockito issues and keeps production code clean by using a `TaskViewModelInterface` with a `FakeTaskViewModel` for tests.
- **Implementation**: Tests inject `FakeTaskViewModel` to control state without altering `TaskViewModel`.

---

## Key Design Decisions

- **Swipeable Actions**: Inspired by popular task apps (e.g., Todoist), swipe-to-delete/complete with undo provides a fast, forgiving UX.
- **Drag-and-Drop**: Enhances flexibility in task prioritization, implemented via `dragContainer` in `LazyColumn`.
- **Sort/Filter Menus**: Allows users to customize task views (e.g., by due date or priority), improving usability for large task lists.
- **Completion Percentage**: Visual feedback via `CircularProgressBarContainer` motivates users to complete tasks.

---

## Installation

Clone this repository and follow these steps:

1. Open the project in Android Studio.
2. Build and run the app on an emulator or physical device.
3. Enjoy the task manager app!

---

## Sample Db data 
1. Open app inspection 
2. Choose data base option
3. Go to the table name "tasks"
4. open query editor and paste the query from [here](/sample_db_data)
5. Click Run to execute the query.
6. Verify that the data is successfully inserted into the tasks table.
   





