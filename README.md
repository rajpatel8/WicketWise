# 🏏 Cricket Coaching Video Annotation Platform

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Material Design](https://img.shields.io/badge/UI-Material%20Design-blue.svg)](https://material.io/)

A native Android application designed to revolutionize cricket coaching through advanced video annotation, real-time feedback, and performance analytics.

## 🚀 Features

### ✅ Current Implementation (Phase 1)
- **🔐 Secure Authentication** - Email/password validation with error handling
- **🎨 Material Design UI** - Cricket-themed interface with modern aesthetics  
- **📱 Responsive Layout** - Optimized for various Android screen sizes
- **🧭 Smooth Navigation** - Seamless flow between application screens
- **⚡ Performance Optimized** - Fast loading and smooth animations

### 🚧 Coming Soon (Future Phases)
- **🎥 Video Capture & Recording** - Real-time cricket session recording
- **📝 Frame-by-Frame Annotations** - Precise coaching feedback tools
- **🏏 Smart Player Tracking** - AI-powered movement analysis
- **📊 Performance Analytics** - Detailed coaching insights and metrics
- **☁️ Cloud Synchronization** - Multi-device access and backup
- **🎯 Drill Recommendations** - Personalized training suggestions

## 🛠️ Technology Stack

- **Platform:** Native Android
- **Language:** Java
- **IDE:** Android Studio
- **UI Framework:** Material Components
- **Architecture:** MVP (Model-View-Presenter)
- **Minimum SDK:** API 24 (Android 7.0)
- **Target SDK:** API 35 (Android 15)

## 📋 Prerequisites

- **Android Studio** Arctic Fox or later
- **JDK 11** or higher
- **Android SDK** with API level 24+
- **Git** for version control

## 🔧 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/cricket-coaching-app.git
cd cricket-coaching-app
```

### 2. Open in Android Studio
- Launch Android Studio
- Select "Open an existing Android Studio project"
- Navigate to the cloned directory and select it

### 3. Sync Project
- Android Studio will automatically prompt to sync Gradle files
- Click "Sync Now" when prompted
- Wait for dependencies to download

### 4. Run the Application
- Connect an Android device or start an emulator
- Click the "Run" button (green play icon)
- Select your target device

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/lords/becomebetter/
│   │   ├── MainActivity.java          # Login screen logic
│   │   └── DashboardActivity.java     # Dashboard screen logic
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml      # Login screen layout
│   │   │   └── activity_dashboard.xml # Dashboard screen layout
│   │   ├── values/
│   │   │   ├── colors.xml            # Cricket-themed color palette
│   │   │   └── strings.xml           # App text resources
│   │   └── drawable/                 # Icons and graphics
│   └── AndroidManifest.xml           # App configuration
├── build.gradle                      # Module dependencies
└── README.md                         # This file
```

## 🎨 Design System

### Color Palette
- **Primary Green:** `#2E7D32` - Main cricket theme color
- **Dark Green:** `#1B5E20` - Accent and emphasis
- **Light Accent:** `#4CAF50` - Interactive elements
- **Background:** `#F5F5F5` - Clean, professional backdrop

### Typography
- **Headers:** Bold, 28-32sp for titles
- **Body Text:** Regular, 16sp for content
- **Captions:** Light, 14sp for supporting text

## 🧪 Testing

### Manual Testing Checklist
- [ ] Login with valid credentials
- [ ] Login with invalid email format
- [ ] Login with short password (<6 characters)
- [ ] Navigation to dashboard screen
- [ ] Back navigation from dashboard
- [ ] App rotation handling
- [ ] Network connectivity scenarios

### Running Tests
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📖 Usage

### Login Flow
1. Launch the application
2. Enter a valid email address
3. Enter a password (minimum 6 characters)
4. Tap "Login" to proceed to dashboard

### Dashboard Features
- View development status message
- Access future feature roadmap
- Navigate back to login screen

## 🐛 Known Issues

- [ ] No current known issues

## 📝 Changelog

### v1.0.0 (May 2025)
- ✅ Initial release with authentication system
- ✅ Material Design UI implementation
- ✅ Basic navigation between screens
- ✅ Input validation and error handling

## 🔮 Roadmap

### Phase 2 (June 2025)
- Video capture functionality
- Basic annotation tools
- File management system

Next phase TBD

## 👥 Team

- **Project Lead:** Rajkumar Patel
- **Business Partner:** [www.becomebetter.ca](www.becomebetter.ca)
- **Institution:** University of Windsor - COMP 8967

## 📞 Support

For support and questions:
- **Email:** patel9qb@uwindsor.ca
- **Issues:** Contact project lead directly

## 📄 License

This project is developed as part of COMP 8967 Internship Project at University of Windsor.

## 🚀 Getting Started Quick Commands

```bash
# Clone and setup
git clone https://github.com/your-username/cricket-coaching-app.git
cd cricket-coaching-app

# Open in Android Studio and run!
```

**Ready to revolutionize cricket coaching? Let's build something amazing! 🏏⚡**

---

*Made with ❤️ for cricket coaches worldwide*
