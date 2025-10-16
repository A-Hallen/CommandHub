# 🚀 CommandHub

**Una aplicación de escritorio moderna para gestionar y ejecutar comandos de terminal con interfaz gráfica**

CommandHub es un gestor de comandos de terminal multiplataforma construido con **Kotlin Multiplatform** y **Compose for Desktop**. Permite guardar, organizar y ejecutar tus comandos favoritos con una interfaz moderna y funcional, eliminando la necesidad de recordar comandos complejos o navegar por el historial de terminal.

---

## ✨ Características

### 🎯 Gestión de Comandos
- **Guardar comandos** con nombre, descripción y etiquetas
- **Organizar por proyectos** y categorías
- **Sistema de favoritos** para acceso rápido
- **Búsqueda y filtrado** inteligente
- **Plantillas de comandos** con variables personalizables

### 🖥️ Terminal Integrado
- **Ejecución en tiempo real** con output en vivo
- **Soporte completo ANSI** - Colores y estilos de terminal
  - Colores básicos y brillantes (16 colores)
  - Paleta de 256 colores
  - TrueColor RGB (16 millones de colores)
  - Estilos: **Bold**, *Italic*, <u>Underline</u>, ~~Strikethrough~~
- **Selección y copia** de texto del output
- **Historial de ejecuciones** con timestamps
- **Detección automática** de shell del sistema

### 🎨 Interfaz Moderna
- **Tema claro/oscuro** con cambio dinámico
- **System Tray** - Minimizar a bandeja del sistema
- **Diseño adaptativo** con Material Design 3
- **Componentes reutilizables** y consistentes
- **Experiencia de usuario fluida** con animaciones suaves

### 💾 Persistencia
- **Base de datos SQLite** local
- **Backup automático** de configuraciones
- **Importar/Exportar** comandos en JSON
- **Sincronización** entre instancias (futuro)

---

## 🏗️ Arquitectura

El proyecto sigue los principios de **Clean Architecture** con separación clara entre capas:

```
├── Domain Layer (Lógica de Negocio)
│   ├── Models: Command, Project, Category, ExecutionResult
│   └── Repository Interfaces
│
├── Data Layer (Persistencia)
│   ├── SQLDelight Database
│   ├── Repository Implementations
│   └── Data Mappers
│
├── Presentation Layer (UI)
│   ├── Compose UI Components
│   ├── ViewModels
│   └── Theme System
│
└── Platform Layer (JVM)
    ├── System Tray Manager
    ├── Command Executor
    └── Database Driver
```

📖 **Documentación detallada**: [ARCHITECTURE.md](./ARCHITECTURE.md)

---

## 🛠️ Tecnologías

- **Lenguaje**: Kotlin 2.2.20
- **Framework UI**: Jetpack Compose Multiplatform 1.9.0
- **Base de datos**: SQLDelight 2.0.2
- **Inyección de dependencias**: Koin 4.0
- **Coroutines**: kotlinx.coroutines 1.10.2
- **Serialización**: kotlinx.serialization 1.8.0
- **Build System**: Gradle con Kotlin DSL

---

## 📦 Instalación

### Prerrequisitos

- **JDK 17 o superior** (recomendado: JDK 21)
- **Git** para clonar el repositorio
- **Windows/macOS/Linux** (soportado en las tres plataformas)

### Clonar el Repositorio

```bash
git clone https://github.com/yourusername/CommandHub.git
cd CommandHub
```

---

## 🚀 Ejecución

### Modo Desarrollo

**Windows:**
```powershell
.\gradlew.bat :composeApp:run
```

**macOS/Linux:**
```bash
./gradlew :composeApp:run
```

### Hot Reload (Desarrollo Rápido)

El proyecto incluye soporte para hot reload:

```bash
.\gradlew.bat composeApp:run --continuous
```

---

## 📦 Construcción

### Verificar Configuración del Instalador

Antes de generar los instaladores, verifica que todo esté configurado correctamente:

```powershell
.\verificar_instalador.ps1
```

Este script verificará:
- ✅ Iconos presentes y configurados
- ✅ Archivos de configuración
- ✅ Java 17+ instalado
- ✅ Metadatos del paquete completos

### JAR Ejecutable (Uber JAR)

Crear un JAR con todas las dependencias incluidas:

```bash
.\gradlew.bat uberJar
```

El archivo se generará en: `composeApp/build/libs/commandhub-uber-1.0.0.jar`

**Ejecutar el JAR:**
```bash
java -jar composeApp/build/libs/commandhub-uber-1.0.0.jar
```

### Instaladores Nativos Personalizados

Los instaladores están completamente personalizados con:
- 🎨 Iconos personalizados
- 📝 Información de vendor y copyright
- 🔧 Menús de inicio y accesos directos
- 📦 Categorización correcta por plataforma

**Windows (MSI):**
```bash
.\gradlew.bat packageMsi
```
Genera un instalador MSI profesional con wizard de instalación.

**macOS (DMG):**
```bash
./gradlew packageDmg
```
Genera una imagen de disco DMG lista para distribución.

**Linux (DEB):**
```bash
./gradlew packageDeb
```
Genera un paquete Debian para distribución en Ubuntu/Debian.

Los instaladores se generarán en: `composeApp/build/compose/binaries/main/`

📖 **Guía de personalización**: [INSTALLER_CUSTOMIZATION.md](./INSTALLER_CUSTOMIZATION.md)

---

## 📖 Uso

### 1. Crear un Proyecto

1. Haz clic en **"Nuevo Proyecto"**
2. Ingresa el nombre y descripción
3. Define la ruta base del proyecto (opcional)

### 2. Agregar Comandos

1. Selecciona un proyecto
2. Haz clic en **"Nuevo Comando"**
3. Completa la información:
   - **Nombre**: Identificador del comando
   - **Comando**: El comando a ejecutar
   - **Descripción**: Qué hace el comando
   - **Categoría**: Organización (Build, Test, Deploy, etc.)
   - **Tags**: Etiquetas para búsqueda

### 3. Ejecutar Comandos

1. Selecciona un comando de la lista
2. Haz clic en **"Ejecutar"** o presiona `Ctrl+Enter`
3. Observa el output en tiempo real con colores ANSI
4. Selecciona y copia texto del output si es necesario

### 4. Gestionar Favoritos

- Marca comandos como favoritos con el ícono ⭐
- Accede rápidamente desde el panel de favoritos
- Filtra por favoritos en la vista principal

---

## 🎨 Personalización

### Cambiar Tema

- **Modo claro/oscuro**: Menú → Configuración → Apariencia
- **Tema automático**: Sigue la configuración del sistema (futuro)

### Icono de la Aplicación

El proyecto incluye configuración para iconos personalizados:

- **Windows**: Coloca `app_icon.ico` en `composeApp/`
- **Recursos internos**: `composeApp/src/jvmMain/composeResources/drawable/app_icon.png`

📖 **Guía completa**: [LOGO_SETUP.md](./LOGO_SETUP.md)

---

## 🧪 Testing

### Ejecutar Tests

```bash
.\gradlew.bat test
```

### Probar Terminal ANSI

El proyecto incluye scripts de prueba para el terminal:

📖 **Guía de pruebas**: [TERMINAL_TESTING.md](./TERMINAL_TESTING.md)

---

## 📁 Estructura del Proyecto

```
CommandHub/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/org/hallen/commandhub/
│   │   │   ├── data/           # Repositorios y persistencia
│   │   │   ├── domain/         # Modelos y lógica de negocio
│   │   │   ├── presentation/   # UI y ViewModels
│   │   │   ├── terminal/       # Parser ANSI y componentes
│   │   │   ├── theme/          # Sistema de temas
│   │   │   └── utils/          # Utilidades
│   │   └── jvmMain/kotlin/org/hallen/commandhub/
│   │       ├── platform/       # System Tray, ejecutor
│   │       ├── data/database/  # Driver SQLite
│   │       └── main.kt         # Entry point
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml      # Catálogo de dependencias
├── ARCHITECTURE.md             # Documentación de arquitectura
├── TERMINAL_IMPLEMENTATION_SUMMARY.md  # Detalles del terminal
└── README.md                   # Este archivo
```

---

## 🗂️ Base de Datos

### Ubicación

- **Windows**: `C:\Users\{username}\.commandhub\commandhub.db`
- **macOS**: `~/.commandhub/commandhub.db`
- **Linux**: `~/.commandhub/commandhub.db`

### Esquema

```sql
-- Comandos guardados
CommandEntity (id, projectId, categoryId, name, command, description, tags, isFavorite, createdAt)

-- Proyectos
ProjectEntity (id, name, description, basePath, createdAt)

-- Categorías
CategoryEntity (id, name, description, color, icon)

-- Historial de ejecuciones
ExecutionEntity (id, commandId, output, exitCode, startTime, endTime)
```

---

## 🛣️ Roadmap

### ✅ Fase 1 - Completada
- [x] Arquitectura base con Clean Architecture
- [x] Base de datos SQLite con SQLDelight
- [x] UI básica con Compose Desktop
- [x] CRUD de comandos, proyectos y categorías
- [x] Sistema de temas claro/oscuro

### ✅ Fase 2 - Completada
- [x] Terminal integrado con ejecución de comandos
- [x] Parser ANSI completo (colores y estilos)
- [x] Selección y copia de texto
- [x] System Tray con minimizar a bandeja
- [x] Historial de ejecuciones

### 🚧 Fase 3 - En Progreso
- [ ] Input interactivo en terminal (stdin)
- [ ] Historial de comandos con ↑/↓
- [ ] Autocompletado de comandos
- [ ] Variables de entorno personalizadas
- [ ] Configuración de shells personalizados

### 📋 Fase 4 - Planificado
- [ ] Importar/Exportar comandos (JSON)
- [ ] Snippets de código con sintaxis highlight
- [ ] Buscar en output de terminal (Ctrl+F)
- [ ] Atajos de teclado personalizables
- [ ] Sincronización en la nube (opcional)

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Si deseas colaborar:

1. **Fork** el repositorio
2. Crea una **rama** para tu feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. **Push** a la rama (`git push origin feature/AmazingFeature`)
5. Abre un **Pull Request**

### Convenciones de Código

- Sigue las convenciones de Kotlin estándar
- Usa nombres descriptivos en inglés
- Documenta funciones públicas con KDoc
- Mantén la arquitectura limpia y separada por capas

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](./LICENSE) para más detalles.

---

## 📞 Contacto

**Desarrollador**: Adrian Hallen  
**Email**: hallen412120@gmail.com  
**GitHub**: [@A-Hallen](https://github.com/A-Hallen/)

---

## 🙏 Agradecimientos

- [JetBrains](https://www.jetbrains.com/) por Kotlin y Compose Multiplatform
- [Cash App](https://cashapp.github.io/sqldelight/) por SQLDelight
- [Insert Koin](https://insert-koin.io/) por Koin DI
- Comunidad de Kotlin por el apoyo continuo

---

## 📚 Documentación Adicional

- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - Detalles de arquitectura
- **[TERMINAL_IMPLEMENTATION_SUMMARY.md](./TERMINAL_IMPLEMENTATION_SUMMARY.md)** - Implementación del terminal
- **[TERMINAL_TESTING.md](./TERMINAL_TESTING.md)** - Guía de pruebas
- **[INSTALLER_CUSTOMIZATION.md](./INSTALLER_CUSTOMIZATION.md)** - Personalización del instalador
- **[RESUMEN_INSTALADOR.md](./RESUMEN_INSTALADOR.md)** - Estado actual del instalador
- **[LOGO_SETUP.md](./LOGO_SETUP.md)** - Configuración de iconos

---

<div align="center">

**Hecho con ❤️ usando Kotlin y Compose**

⭐ Si te gusta el proyecto, ¡dale una estrella en GitHub!

</div>
