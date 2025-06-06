# 🎓 CollegeApp – Admin & User

**CollegeApp** is an Android application built with **Kotlin** and **Jetpack Compose**, designed to streamline college administration and enhance interaction between students, faculty, and administrators. It features distinct interfaces for Admins and Users, powered by Firebase.

---

## 🛠️ Features

### 🔐 Admin Panel
- 🖼️ **Manage Banner**: Upload, view, and delete promotional banners.
- 📢 **Manage Notice**: Add, view, and delete notices with titles, links, and images.
- 🖼️ **Manage Gallery**: Create and manage categories like:
  - Campus Photos
  - Events
  - Sports
  - Labs
  - Library
  - Annual Function
- 👨‍🏫 **Manage Faculty**: Add faculty departments and individual profiles with:
  - Name
  - Email
  - Position
  - Department
  - Profile image
- 🏫 **Manage College Info**: Update name, address, contact details, website, description, and image.

### 👨‍🎓 User Panel
- 🏠 **Home Screen**: Shows banner slider, college info, and latest notices.
- 👩‍🏫 **Faculty**: Browse faculty profiles organized by departments.
- 🖼️ **Gallery**: View categorized image galleries.
- ℹ️ **About Us**: Displays detailed college information.
- 📂 **Navigation Drawer**:
  - 🌐 Website
  - 📄 Notices
  - 📘 Notes
  - 📞 Contact Us

---

## 🔧 Technologies Used

- 💻 **Kotlin** – Core programming language
- 🧩 **Jetpack Compose** – Modern declarative UI toolkit
- ☁️ **Firebase**:
  - 🔥 Firestore – NoSQL cloud database
  - 🗂️ Storage – For image uploads
  - 🔐 Authentication – (Optional, included as a dependency)
  - 📊 Analytics – User behavior tracking
- 🌀 **Coil** – Image loading library for Compose
- 🧭 **Jetpack Navigation Compose** – Screen navigation
- 📚 **Accompanist Pager** – Banner slider support
- ⚙️ **Gradle Kotlin DSL** – Project configuration

---

## 📂 Project Structure

CollegeApp/
├── admin/screens/ # Admin panel UI
├── screens/ # User panel UI
├── itemview/ # Reusable UI components
├── models/ # Data models (FacultyModel, NoticeModel, etc.)
├── navigation/ # Navigation graph and routes
├── viewmodel/ # ViewModels for Firebase operations
├── utils/Constant.kt # Global constants (e.g., isAdmin flag)
├── ui/theme/ # Theme (colors, typography)
├── res/ # App resources
├── MainActivity.kt # App entry point
├── build.gradle.kts # App-level Gradle config


---

## 🚀 Setup Instructions

### 🖥️ Prerequisites
- Android Studio (Bumblebee 2021.1.1 or later)
- JDK 11 or later
- Kotlin plugin installed

### 🛠️ Steps to Run the Project

1. **Clone the repository**:
   ```bash
   git clone [repository_url]
   cd CollegeApp

2.Open in Android Studio.

3.Firebase Setup:

Create a Firebase project at https://console.firebase.google.com

Add an Android app to Firebase.

Download google-services.json and place it in the app/ directory.

4.Sync Gradle:

Android Studio will prompt you to sync.

If not, click "Sync Project with Gradle Files" from the toolbar.

5.Run the app:

Choose a physical/emulated device.

Click the ▶️ Run button

👥 Admin/User Role Switching
The application behavior is determined by the isAdmin flag:

kotlin
Copy
Edit
const val isAdmin = true // Set to false for user mode
true → Launches Admin Dashboard

false → Launches User Panel

🎥 Demo & 📦 APK Download
📺 App Demo
See CollegeApp in action:

👉 Watch Screen Recording Demo(https://drive.google.com/drive/folders/140a2_bBSuqccb8sLNzdF8l_deb1Tzxh1?usp=drive_link)

📲 Download APK
Install and explore the app:

📥 Download CollegeApp APK(https://drive.google.com/drive/folders/1S5P0DuwGd68kjY46gkD8XVa1lPlMZDkw?usp=drive_link)

⚠️ Make sure to enable "Install from unknown sources" in your device settings.

🐞 Error Logging
Basic error logs (e.g., .kotlin/errors/errors-*.log) help debug issues like unexpected Kotlin daemon termination.

💬 Contributions
Feel free to fork the repo, improve the code, fix bugs, or add new features!
Pull requests are always welcome. 🙌

📄 License
This project is for educational and personal use. Licensing terms can be added here.

