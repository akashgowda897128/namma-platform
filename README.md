Namma-Platform 🚆

Namma-Platform is an Android application developed to help passengers at small railway stations by providing clear train platform information, coach positions, and multilingual assistance. The app mainly focuses on improving accessibility for rural passengers, elderly travelers, and people who may struggle with railway announcements.

GitHub Repository: Namma-Platform Repository

📌 Features
🔍 Search trains using train number or train name
🚉 Display platform number and train details
📍 Coach sequence and general coach guidance
🌐 Kannada-first simple user interface
📡 Real-time train information using RapidAPI
📱 Clean and lightweight Android application
⚡ Fast and easy-to-use interface
🛠️ Tech Stack
Language: Kotlin
IDE: Android Studio
API Integration: RapidAPI (Train Details API)
UI Design: XML Layouts
Architecture: Android Native Development
Database: Room DB (Future Enhancement)
📂 Project Structure
app/
 ├── manifests/
 ├── java/com/example/nammaplatform/
 │     ├── MainActivity.kt
 │     ├── ApiService.kt
 │     ├── Adapter/
 │     └── Model/
 ├── res/
 │     ├── layout/
 │     ├── drawable/
 │     └── values/
 └── build.gradle
🚀 Getting Started
Prerequisites

Before running the project, make sure you have:

Android Studio installed
Android SDK 34 or above
Internet connection for API requests
RapidAPI Key
⚙️ Installation
1️⃣ Clone the Repository
git clone https://github.com/akashgowda897128/namma-platform.git
2️⃣ Open in Android Studio
Open Android Studio
Select Open Project
Choose the cloned folder
3️⃣ Add API Key

Add your RapidAPI key inside the API configuration file.

Example:

headers["X-RapidAPI-Key"] = "YOUR_API_KEY"
4️⃣ Run the Application
Connect Android device or emulator
Click ▶ Run in Android Studio
📱 Application Purpose

In many small railway stations, announcements are often unclear or available only in Hindi/English. Rural passengers and elderly travelers may face difficulties identifying the correct platform or general coach location.

Namma-Platform solves this problem by offering:

Easy train search
Platform details
Coach guidance
Local language support
User-friendly mobile interface
🔮 Future Enhancements
🔊 Voice announcements in Kannada
📍 Live train tracking
🌙 Dark mode support
🌐 Multi-language support
💾 Offline caching using Room Database
🔔 Push notifications for train updates
🤝 Contributing

Contributions are welcome.

Fork the repository
Create your feature branch
git checkout -b feature-name
Commit your changes
git commit -m "Added new feature"
Push to the branch
git push origin feature-name
Open a Pull Request
