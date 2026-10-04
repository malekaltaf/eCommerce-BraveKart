# BraveKart 🛒

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

Welcome to **BraveKart**, a modern Android eCommerce application built natively with **Kotlin** and **Jetpack Compose**. It leverages **Firebase** for backend services including authentication and real-time database management.

## 🚀 Features

* **Firebase Authentication:** Secure Email/Password login and signup flow with comprehensive form validation.
* **Firestore Integration:** 
  * **Users:** Securely store and retrieve user profile information.
  * **Products:** Real-time catalog fetching for a seamless browsing experience.
  * **Orders:** Real-time tracking of purchase history, with dynamic UI updates based on order status (Pending, Approved, Delivered).
* **User Profile:** 
  * Displays user's name fetched directly from the Firebase Auth Display Profile.
  * Shows the currently logged-in email address.
  * Secure logout functionality managed via session management.
* **Jetpack Compose UI:** A fully modern, responsive, and declarative UI designed following Material 3 principles.
* **State Management & Coroutines:** Smooth asynchronous database calls, intuitive loading spinners, and toast notifications for a polished user experience (UX).
* **Bottom Navigation:** An easy-to-use Scaffold containing three main tabs:
  1. 🛍️ **Catalog:** Browse available products and use the "Buy Now" feature to record purchases instantly.
  2. 📦 **Orders:** Track your purchase history and monitor current order statuses.
  3. 👤 **Profile:** View user details and manage your session.

## 📸 Screenshots

| Login | Catalog | Orders | Profile |
| :---: | :---: | :---: | :---: |
| <img src="https://via.placeholder.com/200x400.png?text=Login+Screen" width="200"/> | <img src="https://via.placeholder.com/200x400.png?text=Catalog+Screen" width="200"/> | <img src="https://via.placeholder.com/200x400.png?text=Orders+Screen" width="200"/> | <img src="https://via.placeholder.com/200x400.png?text=Profile+Screen" width="200"/> |

*(Replace placeholder images with actual app screenshots)*

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Backend:** Firebase (Authentication & Cloud Firestore)
* **Asynchronous Programming:** Kotlin Coroutines
* **Architecture:** Modern Android Architecture (MVVM ready)

## 📌 Getting Started

### Prerequisites
* Android Studio (Latest Version Recommended)
* A Firebase Project with Authentication (Email/Password) and Firestore enabled.

### Installation Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/eCommerce-BraveKart.git
   ```
2. **Open the project** in Android Studio.
3. **Setup Firebase:**
   * Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project (or use an existing one).
   * Register a new Android App with the package name used in this project (`com.malekaltaf.ecommerce_bravekart`).
   * Download the `google-services.json` file.
   * Place the `google-services.json` file in the `app/` directory of the project.
   * Ensure Authentication (Email/Password) and Firestore Database are enabled in your Firebase project.
4. **Sync Gradle:** Click on "Sync Project with Gradle Files" in Android Studio.
5. **Run the app:** Deploy the application to an Android Emulator or a physical device.

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).
