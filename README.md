# eCommerce-BraveKart 🛒

Welcome to **BraveKart**, an Android eCommerce application built natively with Kotlin and Jetpack Compose!

## 🚀 Features Implemented Today
* **Firebase Authentication:** Secure Email/Password login and signup flow with form validation.
* **Firestore Integration:** 
  * Users collections to store profile information.
  * Realtime `products` catalog fetching.
  * Realtime `orders` tracking that dynamically updates UI based on order status (Pending, Approved, Delivered).
* **Jetpack Compose UI:** Modern, responsive UI with Material 3 design principles.
* **State Management & Coroutines:** Asynchronous database calls, loading spinners, and toast notifications for seamless UX.
* **Bottom Navigation:** Scaffold containing three main tabs:
  1. **Catalog:** Browse products and "Buy Now" directly to the database.
  2. **Orders:** Track your purchase history and order status.
  3. **Profile:** View user profile data and logout.

## 🛠️ Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Backend:** Firebase (Authentication & Firestore)
* **Architecture:** Modern Android Architecture (MVVM ready)

## 📌 Getting Started
1. Clone the repository.
2. Ensure you have the `google-services.json` file added to your `app/` directory.
3. Sync Gradle and run the app on an Android Emulator or physical device.
