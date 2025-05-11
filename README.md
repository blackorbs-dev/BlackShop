# 🛒 BlackShop

BlackShop is a modern e-commerce Android application built using the latest Android tech stack. It supports user authentication, product listing, cart management, and state-of-the-art architecture components like ViewModel, Navigation Component, FirebaseAuth, and Koin for Dependency Injection.

---

## 📱 Features

- User Login & Sign-Up (with FirebaseAuth)
- Custom reusable Views
- Product listing with RecyclerView
- Add-to-cart & Checkout functionality
- Navigation between fragments using Jetpack Navigation
- Fully tested with Espresso, JUnit & Roboelectric
- Dependency Injection with Koin

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Clean Architecture
- **UI**: Android Views (XML) with Animations
- **Navigation**: Jetpack Navigation Component
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Firebase**: FirebaseAuth
- **Testing**:
  - Unit testing with `JUnit`, `Roboelectric`
  - UI testing with `Espresso`, `MockK`, `KoinTest`

---

## 📷 Screen Record

https://github.com/user-attachments/assets/f7d81ddb-a742-44fd-a3ca-92f5b46dbc81

---

## 🚀 Getting Started

### Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a new project.
3. Add an android app to the project using package id `blackorbs.dev.blackshop`
4. Enable Email/Password Authentication.
5. Download `google-services.json` and place it in `app/`.

### Clone the repo

```bash
git clone https://github.com/yourusername/blackshop.git
cd blackshop
```

---

## 🧪 Running Tests

### Unit Tests

```bash
./gradlew test
```

### UI Tests

```bash
./gradlew connectedAndroidTest
```

> Make sure animations are disabled on your emulator or device.

---

## 💡 Tips

- Disable animations in Android Settings > Developer Options before running UI tests.

---

