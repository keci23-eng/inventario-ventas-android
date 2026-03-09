# Inventario Ventas Android

A comprehensive Android application for managing inventory and sales operations. This project is built with Kotlin and follows modern Android development practices.

## 📋 Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Building](#building)
- [Usage](#usage)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

- **Inventory Management**: Track and manage product inventory
- **Sales Operations**: Record and monitor sales transactions
- **User-Friendly Interface**: Intuitive UI for easy navigation
- **Real-time Updates**: Keep inventory synchronized across operations
- **Data Persistence**: Reliable local storage for inventory and sales data

## 📋 Requirements

- **Android SDK**: API Level 21 (Android 5.0) or higher
- **Kotlin**: 1.7.0 or later
- **Gradle**: 7.0 or later
- **Java**: JDK 11 or higher

## 🚀 Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/keci23-eng/inventario-ventas-android.git
   cd inventario-ventas-android
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and open it

3. **Sync Gradle**:
   - Android Studio will automatically prompt you to sync Gradle files
   - Alternatively, use `./gradlew build` from the terminal

## 📁 Project Structure

```
inventario-ventas-android/
├── app/                      # Main application module
├── gradle/                   # Gradle wrapper and configuration
├── build.gradle.kts          # Project-level build configuration
├── settings.gradle.kts       # Gradle settings
├── gradle.properties         # Gradle properties
├── gradlew                   # Gradle wrapper script (Linux/Mac)
├── gradlew.bat              # Gradle wrapper script (Windows)
└── README.md                # This file
```

## 🔨 Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
```

### Build and Install on Device/Emulator
```bash
./gradlew installDebug
```

## 💻 Usage

1. **Launch the Application**: Open the app on your Android device or emulator
2. **Manage Inventory**: 
   - Add, edit, and delete products from your inventory
   - Track product quantities and details
3. **Record Sales**: 
   - Create sales transactions
   - Update inventory automatically when sales are completed
4. **View Reports**: Monitor sales and inventory statistics

## ⚙️ Configuration

Key configuration files:

- **gradle.properties**: Project-wide Gradle properties and SDK versions
- **settings.gradle.kts**: Module configuration and dependencies
- **build.gradle.kts**: Build configuration and dependencies

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the [MIT License](LICENSE) (if applicable). Please check the repository for license information.

## 👤 Author

**keci23-eng**
- GitHub: [@keci23-eng](https://github.com/keci23-eng)

## 📞 Support

For support, please open an issue in the [GitHub Issues](https://github.com/keci23-eng/inventario-ventas-android/issues) section.

---

**Last Updated**: March 2026