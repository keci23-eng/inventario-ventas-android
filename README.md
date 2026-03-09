# Inventario Ventas Android

Una aplicación Android integral para administrar operaciones de inventario y ventas. Este proyecto está construido con Kotlin y sigue las prácticas modernas de desarrollo en Android.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Compilación](#compilación)
- [Uso](#uso)
- [Configuración](#configuración)
- [Contribución](#contribución)
- [Licencia](#licencia)

## ✨ Características

- **Gestión de Inventario**: Rastrear y administrar el inventario de productos
- **Operaciones de Ventas**: Registrar y monitorear transacciones de ventas
- **Interfaz Amigable**: UI intuitiva para una fácil navegación
- **Actualizaciones en Tiempo Real**: Mantener el inventario sincronizado en todas las operaciones
- **Persistencia de Datos**: Almacenamiento local confiable para datos de inventario y ventas

## 📋 Requisitos

- **Android SDK**: API Level 21 (Android 5.0) o superior
- **Kotlin**: 1.7.0 o posterior
- **Gradle**: 7.0 o posterior
- **Java**: JDK 11 o superior

## 🚀 Instalación

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/keci23-eng/inventario-ventas-android.git
   cd inventario-ventas-android
   ```

2. **Abrir en Android Studio**:
   - Abre Android Studio
   - Selecciona "Abrir un proyecto existente de Android Studio"
   - Navega al directorio clonado y ábrelo

3. **Sincronizar Gradle**:
   - Android Studio te pedirá automáticamente que sincronices los archivos de Gradle
   - Alternativamente, usa `./gradlew build` desde la terminal

## 📁 Estructura del Proyecto

```
inventario-ventas-android/
├── app/                      # Módulo principal de la aplicación
├── gradle/                   # Contenedor de Gradle y configuración
├── build.gradle.kts          # Configuración de compilación a nivel de proyecto
├── settings.gradle.kts       # Configuración de Gradle
├── gradle.properties         # Propiedades de Gradle
├── gradlew                   # Script de contenedor de Gradle (Linux/Mac)
├── gradlew.bat              # Script de contenedor de Gradle (Windows)
└── README.md                # Este archivo
```

## 🔨 Compilación

### Compilación de Depuración
```bash
./gradlew assembleDebug
```

### Compilación de Lanzamiento
```bash
./gradlew assembleRelease
```

### Ejecutar Pruebas
```bash
./gradlew test
```

### Compilar e Instalar en Dispositivo/Emulador
```bash
./gradlew installDebug
```

## 💻 Uso

1. **Iniciar la Aplicación**: Abre la aplicación en tu dispositivo Android o emulador
2. **Administrar Inventario**: 
   - Añade, edita y elimina productos de tu inventario
   - Rastrea cantidades y detalles de productos
3. **Registrar Ventas**: 
   - Crea transacciones de ventas
   - Actualiza el inventario automáticamente cuando se completen las ventas
4. **Ver Reportes**: Monitorea estadísticas de ventas e inventario

## ⚙️ Configuración

Archivos de configuración clave:

- **gradle.properties**: Propiedades de Gradle a nivel de proyecto y versiones del SDK
- **settings.gradle.kts**: Configuración de módulos y dependencias
- **build.gradle.kts**: Configuración de compilación y dependencias

## 🤝 Contribución

¡Las contribuciones son bienvenidas! Para contribuir:

1. Haz un fork del repositorio
2. Crea una rama de características (`git checkout -b feature/CaracterísticaIncreíble`)
3. Confirma tus cambios (`git commit -m 'Añadir CaracterísticaIncreíble'`)
4. Sube a la rama (`git push origin feature/CaracterísticaIncreíble`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la [Licencia MIT](LICENSE) (si es aplicable). Por favor, verifica el repositorio para obtener información de la licencia.

## 👤 Autor

**keci23-eng**
- GitHub: [@keci23-eng](https://github.com/keci23-eng)

---

**Última Actualización**: Marzo 2026