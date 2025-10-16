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
            packageName = "org.hallen.commandhub"
            packageVersion = "1.0.0"
            
            // Incluir todas las dependencias runtime
            includeAllModules = true
            
            windows {
                // Icono para Windows (.ico) - aparecerá en la barra de tareas y el ejecutable
                // Coloca tu archivo app_icon.ico en la carpeta composeApp/
                val customIconFile = project.file("app_icon.ico")
                if (customIconFile.exists()) {
                    iconFile.set(customIconFile)
                    println("Usando icono personalizado: ${customIconFile.absolutePath}")
                } else {
                    println("ADVERTENCIA: No se encontró app_icon.ico en ${customIconFile.absolutePath}")
                    println("La aplicación usará el icono por defecto de Windows")
                }
            }
            
            linux {
                // Configuración de Linux si es necesario
            }
            
            macOS {
                // Configuración de macOS si es necesario
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
