# Expense Tracker

Expense Tracker is an Android application designed to help users manage their finances by tracking their income and expenses. The app allows users to register, log in, and monitor their financial activities, providing an easy-to-use interface with various features such as expense and income tracking, password recovery, and more.

This project uses **MVVM architecture** for efficient management of UI and data, and integrates **Firebase** for user authentication and data storage.

---

## Features

- **User Registration and Authentication**: Users can create an account and log in using Firebase Authentication.
- **Dashboard**: Displays an overview of the user's financial situation, including total income, total expenses, and balance.
- **Expense Tracking**: Allows users to add, view, and delete expenses.
- **Income Tracking**: Lets users add, view, and delete sources of income.
- **Password Recovery**: Provides an option for users to reset their password.
- **Firebase Integration**: Uses Firebase Realtime Database for storing user data and Firebase Authentication for login and registration.

---

## Project Structure

- **Activities**: 
  - `MainActivity.java`: Entry point of the application.
  - `HomeActivity.java`: The main screen users see after logging in.
  - `ForgotPasswordActivity.java`: Allows users to reset their password.
  - `RegistrationActivity.java`: Handles user registration.
  - `SplashActivity.java`: Displays the splash screen when the app is launched.

- **Fragments**:
  - `DashBoardFragment.java`: Displays an overview of the user's income and expenses.
  - `ExpenseFragment.java`: Allows the user to manage and view their expenses.
  - `IncomeFragment.java`: Allows the user to manage and view their income.

- **Repositories**:
  - `ExpenseRepository.java`: Manages expense-related data.
  - `IncomeRepository.java`: Manages income-related data.

- **ViewModels**:
  - `ExpenseViewModel.java`: Provides data for the `ExpenseFragment` and interacts with `ExpenseRepository`.
  - `IncomeViewModel.java`: Provides data for the `IncomeFragment` and interacts with `IncomeRepository`.

---

## Firebase Integration

This project uses Firebase services to provide cloud-based storage and authentication:

- **Firebase Authentication**: Manages user authentication (registration, login, and password reset).
- **Firebase Realtime Database**: Stores data related to user expenses, income, and financial summary.

Make sure to set up Firebase in your Android project to enable these services. Follow the steps in the [Firebase setup documentation](https://firebase.google.com/docs/android/setup) for configuring Firebase with your Android project.

---

## Setup Instructions

1. **Clone the repository**:

    ```bash
    git clone https://github.com/KalyaniIyer/Expense-Tracker-KSKR.git
    cd Expense-Tracker-KSKR
    ```

2. **Open the project in [Android Studio](https://developer.android.com/studio)**.

3. **Configure Firebase**:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project and follow the steps to add your Android app.
   - Download the `google-services.json` file and add it to the `app/` directory of your Android project.
   - Enable Firebase Authentication and Realtime Database in the Firebase Console.

4. **Sync your project with Gradle**:
    - In Android Studio, go to `File` > `Sync Project with Gradle Files` to ensure that all dependencies are properly set up.

5. **Run the app** on an emulator or physical device.

---

## Dependencies

- **Firebase Authentication**
- **Firebase Realtime Database**
- **Android Jetpack** (MVVM Architecture)
- **LiveData, ViewModel** (optional for local database)
- **Material Components for Android**

---

## Screenshots

(Include any relevant screenshots of the app here, such as the login screen, dashboard, expense and income tracking screens, etc.)

---

## Contributing

Feel free to fork the repository, make changes, and submit pull requests. Contributions are always welcome.

---

## License

This project is open-source and available under the [MIT License](LICENSE).

---

## Acknowledgements

- Firebase for providing authentication and cloud storage services.
- Android Jetpack for enabling the MVVM architecture.
- Material Components for creating a beautiful user interface.

