import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            
            // Serialization
            implementation(libs.kotlinx.serialization.json)
            
            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            
            // SQLDelight
            implementation(libs.sqldelight.coroutines)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            
            // SQLDelight JVM Driver - importante usar implementation, no runtimeOnly
            implementation(libs.sqldelight.driver.sqlite)
            
            // Asegurar que Xerial SQLite JDBC se incluye explícitamente
            implementation("org.xerial:sqlite-jdbc:3.46.1.0")
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.hallen.commandhub.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            
            // Información básica de la aplicación
            packageName = "CommandHub"
            packageVersion = "1.0.0"
            description = "Gestor moderno de comandos de terminal con interfaz gráfica"
            copyright = "© 2025 Hallen. All rights reserved."
            vendor = "Adrian Hallen"
            licenseFile.set(project.rootProject.file("LICENSE"))
            
            // Incluir todas las dependencias runtime
            includeAllModules = true
            
            // Módulos adicionales de Java que se necesitan
            modules(
                "java.sql",
                "java.naming",
                "jdk.unsupported"
            )
            
            // ═══════════════════════════════════════════════════════════════
            // 🪟 WINDOWS - Configuración del instalador MSI
            // ═══════════════════════════════════════════════════════════════
            windows {
                // Icono del ejecutable y barra de tareas
                val customIconFile = project.file("app_icon.ico")
                if (customIconFile.exists()) {
                    iconFile.set(customIconFile)
                    println("✅ Icono Windows configurado: ${customIconFile.absolutePath}")
                } else {
                    println("⚠️  No se encontró app_icon.ico - usando icono por defecto")
                }
                
                // Crear menú de inicio
                menu = true
                menuGroup = "CommandHub"
                
                // Crear acceso directo en el escritorio
                shortcut = true
                dirChooser = true
                
                // Información del instalador
                upgradeUuid = "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d"
                
                // Configuración del MSI
                perUserInstall = false  // Instalación para todos los usuarios
                
                // Vendor y metadatos
                menuGroup = "CommandHub"
            }
            
            // ═══════════════════════════════════════════════════════════════
            // 🐧 LINUX - Configuración del paquete DEB
            // ═══════════════════════════════════════════════════════════════
            linux {
                // Icono de la aplicación (PNG)
                val linuxIconFile = project.file("src/jvmMain/composeResources/drawable/app_icon.png")
                if (linuxIconFile.exists()) {
                    iconFile.set(linuxIconFile)
                    println("✅ Icono Linux configurado: ${linuxIconFile.absolutePath}")
                }
                
                // Nombre del paquete (sin espacios, lowercase)
                packageName = "commandhub"
                
                // Crear acceso directo en el menú
                shortcut = true
                
                // Categorías según freedesktop.org
                appCategory = "Development;Utility;TerminalEmulator"
                
                // Release number para actualizaciones
                debMaintainer = "contact@example.com"
                debPackageVersion = "1"
                
                // Menú de aplicaciones
                menuGroup = "Development"
            }
            
            // ═══════════════════════════════════════════════════════════════
            // 🍎 macOS - Configuración del paquete DMG
            // ═══════════════════════════════════════════════════════════════
            macOS {
                // Bundle identifier único
                bundleID = "org.hallen.commandhub"
                
                // Icono de la aplicación (.icns)
                val macIconFile = project.file("app_icon.icns")
                if (macIconFile.exists()) {
                    iconFile.set(macIconFile)
                    println("✅ Icono macOS configurado: ${macIconFile.absolutePath}")
                } else {
                    println("💡 Tip: Crea app_icon.icns para personalizar el icono en macOS")
                }
                
                // Configuración del DMG
                dmgPackageVersion = "1.0.0"
                dmgPackageBuildVersion = "1"
                
                // Agregar al dock
                dockName = "CommandHub"
                
                // Información adicional
                appCategory = "public.app-category.developer-tools"
                
                // Entitlements para permisos (si es necesario en el futuro)
                // entitlementsFile.set(project.file("entitlements.plist"))
                // runtimeEntitlementsFile.set(project.file("runtime-entitlements.plist"))
            }
        }
        
        buildTypes.release {
            proguard {
                // Deshabilitar ProGuard para evitar que elimine clases necesarias
                isEnabled.set(false)
            }
        }
    }
}

sqldelight {
    databases {
        create("CommandHubDatabase") {
            packageName.set("org.hallen.commandhub.data.database")
        }
    }
}

// Tarea para crear un uber JAR (Fat JAR) con todas las dependencias incluidas
tasks.register<Jar>("uberJar") {
    group = "build"
    description = "Crea un JAR ejecutable con todas las dependencias incluidas"
    
    archiveBaseName.set("commandhub-uber")
    archiveVersion.set("1.0.0")
    
    manifest {
        attributes(
            "Main-Class" to "org.hallen.commandhub.MainKt"
        )
    }
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    // Obtener el output de compilación JVM
    val jvmJar = tasks.named<Jar>("jvmJar")
    dependsOn(jvmJar)
    from(zipTree(jvmJar.get().archiveFile))
    
    // Incluir todas las dependencias de runtime
    from({
        configurations.named("jvmRuntimeClasspath").get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
    
    // Excluir archivos de firma que pueden causar problemas
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}
