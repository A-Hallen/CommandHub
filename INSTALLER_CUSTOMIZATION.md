# 📦 Guía de Personalización del Instalador

Esta guía te explica cómo personalizar completamente los instaladores de CommandHub para que se vean profesionales y únicos.

---

## 🎯 Qué se ha Configurado

El proyecto ya incluye configuraciones profesionales para:

### ✅ Windows (MSI)
- ✅ Icono personalizado del ejecutable
- ✅ Menú de inicio con grupo "CommandHub"
- ✅ Acceso directo en el escritorio
- ✅ Información de vendor y copyright
- ✅ UUID único para actualizaciones
- ✅ Instalación para todos los usuarios

### ✅ Linux (DEB)
- ✅ Icono en formato PNG
- ✅ Categorías correctas (Development, Utility)
- ✅ Acceso directo en el menú de aplicaciones
- ✅ Información del mantenedor

### ✅ macOS (DMG)
- ✅ Bundle ID único
- ✅ Categoría de desarrollador
- ✅ Nombre en el Dock
- ✅ Versiones de paquete

---

## 🎨 Personalización de Iconos

### Windows (.ico)

**Ubicación**: `composeApp/app_icon.ico`

**Requisitos**:
- Formato: ICO multipunto
- Tamaños recomendados: 16x16, 32x32, 48x48, 64x64, 128x128, 256x256
- Profundidad: 32-bit con transparencia

**Herramientas para crear ICO**:

1. **En línea**: [ConvertICO.com](https://convertio.co/es/png-ico/)
2. **GIMP**: Exportar como ICO con múltiples tamaños
3. **ImageMagick**:
   ```bash
   magick convert app_icon.png -define icon:auto-resize=256,128,64,48,32,16 app_icon.ico
   ```

### macOS (.icns)

**Ubicación**: `composeApp/app_icon.icns`

**Requisitos**:
- Formato: Apple Icon Image
- Tamaños: 16x16, 32x32, 64x64, 128x128, 256x256, 512x512, 1024x1024

**Crear ICNS desde PNG**:

```bash
# 1. Crear carpeta iconset
mkdir app_icon.iconset

# 2. Generar todos los tamaños (usa sips en macOS)
sips -z 16 16     app_icon.png --out app_icon.iconset/icon_16x16.png
sips -z 32 32     app_icon.png --out app_icon.iconset/icon_16x16@2x.png
sips -z 32 32     app_icon.png --out app_icon.iconset/icon_32x32.png
sips -z 64 64     app_icon.png --out app_icon.iconset/icon_32x32@2x.png
sips -z 128 128   app_icon.png --out app_icon.iconset/icon_128x128.png
sips -z 256 256   app_icon.png --out app_icon.iconset/icon_128x128@2x.png
sips -z 256 256   app_icon.png --out app_icon.iconset/icon_256x256.png
sips -z 512 512   app_icon.png --out app_icon.iconset/icon_256x256@2x.png
sips -z 512 512   app_icon.png --out app_icon.iconset/icon_512x512.png
sips -z 1024 1024 app_icon.png --out app_icon.iconset/icon_512x512@2x.png

# 3. Convertir a ICNS
iconutil -c icns app_icon.iconset
```

**Alternativa en Windows/Linux**:
- Usa [png2icns](https://github.com/bitboss-ca/png2icns) o servicios en línea

### Linux (.png)

**Ubicación**: `composeApp/src/jvmMain/composeResources/drawable/app_icon.png`

**Requisitos**:
- Formato: PNG con transparencia
- Tamaño recomendado: 512x512 o 1024x1024
- Profundidad: 32-bit RGBA

---

## 📝 Personalizar Metadatos

### Editar Información Básica

En `composeApp/build.gradle.kts`, modifica:

```kotlin
nativeDistributions {
    // Cambia estos valores
    packageName = "TuNombreDeApp"        // Nombre del paquete
    packageVersion = "1.0.0"              // Versión
    description = "Tu descripción aquí"   // Descripción
    copyright = "© 2025 Tu Nombre"        // Copyright
    vendor = "Tu Compañía"                // Vendor
}
```

### Cambiar el UUID de Windows (Importante)

**⚠️ IMPORTANTE**: Cambia el `upgradeUuid` por uno único para tu aplicación.

```kotlin
windows {
    upgradeUuid = "TU-UUID-UNICO-AQUI"
}
```

**Generar UUID único**:

**PowerShell (Windows)**:
```powershell
[guid]::NewGuid().ToString()
```

**Linux/macOS**:
```bash
uuidgen
```

**Online**: [UUID Generator](https://www.uuidgenerator.net/)

---

## 🪟 Personalización Avanzada de Windows

### Agregar Imagen de Fondo al Instalador

1. Crea una imagen BMP de 493x312 píxeles
2. Guárdala como `composeApp/installer-background.bmp`
3. Agrega en `build.gradle.kts`:

```kotlin
windows {
    val bgImage = project.file("installer-background.bmp")
    if (bgImage.exists()) {
        installerBackgroundImage.set(bgImage)
    }
}
```

### Agregar Banner al Instalador

1. Crea una imagen BMP de 493x58 píxeles
2. Guárdala como `composeApp/installer-banner.bmp`
3. Agrega:

```kotlin
windows {
    val bannerImage = project.file("installer-banner.bmp")
    if (bannerImage.exists()) {
        installerBannerImage.set(bannerImage)
    }
}
```

### Configurar Consola

Para que la app no muestre ventana de consola:

```kotlin
windows {
    console = false  // Sin consola en Windows
}
```

---

## 🍎 Personalización de macOS

### Agregar Imagen de Fondo al DMG

1. Crea una imagen PNG de 540x380 píxeles
2. Guárdala como `composeApp/dmg-background.png`
3. Agrega:

```kotlin
macOS {
    val dmgBg = project.file("dmg-background.png")
    if (dmgBg.exists()) {
        dmgPackageBackgroundImage.set(dmgBg)
    }
}
```

### Firmar la Aplicación (Para distribución oficial)

```kotlin
macOS {
    signing {
        sign.set(true)
        identity.set("Developer ID Application: Tu Nombre (TEAM_ID)")
    }
}
```

### Notarización (Para macOS 10.15+)

```kotlin
macOS {
    notarization {
        appleID.set("tu.email@icloud.com")
        password.set("@keychain:AC_PASSWORD")
        teamID.set("TU_TEAM_ID")
    }
}
```

---

## 🐧 Personalización de Linux

### Cambiar Categorías del Menú

Según [freedesktop.org](https://specifications.freedesktop.org/menu-spec/latest/apa.html):

```kotlin
linux {
    // Opciones comunes:
    appCategory = "Development"                    // Desarrollo
    appCategory = "Utility"                        // Utilidades
    appCategory = "System"                         // Sistema
    appCategory = "Development;Utility"            // Múltiples
    appCategory = "AudioVideo;Video;Player"        // Multimedia
}
```

### Agregar Dependencias del Paquete DEB

```kotlin
linux {
    debPackageDependencies.add("libc6")
    debPackageDependencies.add("libsqlite3-0")
}
```

---

## 🚀 Construir los Instaladores

### Construir para Windows (MSI)

```powershell
.\gradlew.bat packageMsi
```

**Salida**: `composeApp\build\compose\binaries\main\msi\CommandHub-1.0.0.msi`

### Construir para macOS (DMG)

```bash
./gradlew packageDmg
```

**Salida**: `composeApp/build/compose/binaries/main/dmg/CommandHub-1.0.0.dmg`

### Construir para Linux (DEB)

```bash
./gradlew packageDeb
```

**Salida**: `composeApp/build/compose/binaries/main/deb/commandhub_1.0.0-1_amd64.deb`

### Construir Todo

```bash
.\gradlew.bat packageDistributionForCurrentOS
```

---

## 📋 Checklist de Personalización

### Básico
- [ ] Cambiar `packageName` a tu nombre de app
- [ ] Cambiar `vendor` a tu nombre/empresa
- [ ] Cambiar `copyright` con tu información
- [ ] Actualizar `description`
- [ ] Generar y cambiar `upgradeUuid` único

### Iconos
- [ ] Crear/colocar `app_icon.ico` para Windows
- [ ] Crear/colocar `app_icon.icns` para macOS
- [ ] Verificar que existe `app_icon.png` para Linux

### Avanzado (Opcional)
- [ ] Crear imagen de fondo del instalador Windows
- [ ] Crear banner del instalador Windows
- [ ] Crear imagen de fondo DMG para macOS
- [ ] Configurar firmas de código para distribución

---

## 🎨 Consejos de Diseño

### Para el Icono de la Aplicación

1. **Simplicidad**: Iconos simples funcionan mejor en tamaños pequeños
2. **Contraste**: Asegúrate de que se vea bien en fondos claros y oscuros
3. **Padding**: Deja un margen de ~10% alrededor del diseño
4. **Prueba**: Visualiza en diferentes tamaños (16x16, 32x32, 256x256)

### Para Imágenes del Instalador

1. **Branding consistente**: Usa los colores de tu marca
2. **Legibilidad**: Texto claro y legible
3. **Profesionalismo**: Evita imágenes pixeladas o de baja calidad
4. **Simplicidad**: No sobrecargues con información

---

## 🔧 Troubleshooting

### El icono no aparece en Windows

1. Verifica que `app_icon.ico` exista en `composeApp/`
2. Asegúrate de que es formato ICO válido
3. Reconstruye: `.\gradlew.bat clean packageMsi`

### El instalador no se genera

1. Verifica que tienes WiX Toolset instalado (Windows)
2. Comprueba que Java 17+ está instalado
3. Revisa los logs: `.\gradlew.bat packageMsi --info`

### Errores de firma en macOS

- La firma requiere certificado de desarrollador Apple
- Para desarrollo local, desactiva: `sign.set(false)`

---

## 📚 Referencias

- [Compose Desktop Packaging](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Native_distributions_and_local_execution)
- [WiX Toolset](https://wixtoolset.org/) - Instaladores Windows
- [freedesktop.org](https://www.freedesktop.org/wiki/Specifications/) - Estándares Linux
- [Apple Developer](https://developer.apple.com/macos/distribution/) - Distribución macOS

---

## 🎁 Resultado Final

Después de aplicar todas las personalizaciones, tus instaladores tendrán:

✅ **Aspecto profesional** con iconos y branding  
✅ **Información completa** de vendor y copyright  
✅ **Accesos directos** en menú de inicio y escritorio  
✅ **Categorización correcta** en cada sistema operativo  
✅ **Experiencia de instalación** pulida y confiable

¡Tu aplicación se verá como un software comercial de alta calidad!
