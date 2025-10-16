# ============================================================================
# Script de Verificacion del Instalador - CommandHub
# ============================================================================

Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "   VERIFICACION DE CONFIGURACION DEL INSTALADOR - CommandHub   " -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

$errores = 0
$advertencias = 0
$exitosos = 0

function Test-ArchivoRequerido {
    param(
        [string]$Ruta,
        [string]$Descripcion,
        [bool]$Requerido = $true
    )
    
    if (Test-Path $Ruta) {
        Write-Host "[OK] " -ForegroundColor Green -NoNewline
        Write-Host "$Descripcion" -ForegroundColor White
        Write-Host "     $Ruta" -ForegroundColor DarkGray
        $script:exitosos++
        return $true
    }
    else {
        if ($Requerido) {
            Write-Host "[ERROR] " -ForegroundColor Red -NoNewline
            Write-Host "$Descripcion - NO ENCONTRADO" -ForegroundColor Red
            Write-Host "        $Ruta" -ForegroundColor DarkGray
            $script:errores++
        }
        else {
            Write-Host "[OPCIONAL] " -ForegroundColor Yellow -NoNewline
            Write-Host "$Descripcion - no encontrado" -ForegroundColor Yellow
            Write-Host "           $Ruta" -ForegroundColor DarkGray
            $script:advertencias++
        }
        return $false
    }
}

# 1. VERIFICAR ICONOS
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " ICONOS DE LA APLICACION" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

Test-ArchivoRequerido -Ruta "composeApp\app_icon.ico" -Descripcion "Icono Windows (.ico)" -Requerido $true
Test-ArchivoRequerido -Ruta "composeApp\src\jvmMain\composeResources\drawable\app_icon.png" -Descripcion "Icono interno (.png)" -Requerido $true
Test-ArchivoRequerido -Ruta "composeApp\app_icon.icns" -Descripcion "Icono macOS (.icns)" -Requerido $false

# 2. VERIFICAR CONFIGURACION
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " ARCHIVOS DE CONFIGURACION" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

Test-ArchivoRequerido -Ruta "composeApp\build.gradle.kts" -Descripcion "Configuracion Gradle" -Requerido $true
Test-ArchivoRequerido -Ruta "LICENSE" -Descripcion "Archivo de licencia" -Requerido $true
Test-ArchivoRequerido -Ruta "README.md" -Descripcion "Documentacion principal" -Requerido $true

# 3. VERIFICAR GRADLE Y JAVA
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " HERRAMIENTAS DE DESARROLLO" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    if ($javaVersion -match 'version "(\d+)') {
        $version = [int]$matches[1]
        if ($version -ge 17) {
            Write-Host "[OK] " -ForegroundColor Green -NoNewline
            Write-Host "Java $version instalado (requiere 17+)" -ForegroundColor White
            $script:exitosos++
        } else {
            Write-Host "[ERROR] " -ForegroundColor Red -NoNewline
            Write-Host "Java $version - Se requiere Java 17 o superior" -ForegroundColor Red
            $script:errores++
        }
    }
} catch {
    Write-Host "[ERROR] " -ForegroundColor Red -NoNewline
    Write-Host "Java no encontrado - Instala JDK 17 o superior" -ForegroundColor Red
    $script:errores++
}

if (Test-Path "gradlew.bat") {
    Write-Host "[OK] " -ForegroundColor Green -NoNewline
    Write-Host "Gradle Wrapper configurado" -ForegroundColor White
    $script:exitosos++
} else {
    Write-Host "[ERROR] " -ForegroundColor Red -NoNewline
    Write-Host "gradlew.bat no encontrado" -ForegroundColor Red
    $script:errores++
}

# 4. LEER CONFIGURACION DEL BUILD.GRADLE.KTS
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " CONFIGURACION DEL INSTALADOR" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

if (Test-Path "composeApp\build.gradle.kts") {
    $buildGradle = Get-Content "composeApp\build.gradle.kts" -Raw
    
    if ($buildGradle -match 'packageName = "([^"]+)"') {
        Write-Host "[OK] " -ForegroundColor Green -NoNewline
        Write-Host "Package Name: " -ForegroundColor White -NoNewline
        Write-Host $matches[1] -ForegroundColor Cyan
        $script:exitosos++
    }
    
    if ($buildGradle -match 'packageVersion = "([^"]+)"') {
        Write-Host "[OK] " -ForegroundColor Green -NoNewline
        Write-Host "Version: " -ForegroundColor White -NoNewline
        Write-Host $matches[1] -ForegroundColor Cyan
        $script:exitosos++
    }
    
    if ($buildGradle -match 'vendor = "([^"]+)"') {
        Write-Host "[OK] " -ForegroundColor Green -NoNewline
        Write-Host "Vendor: " -ForegroundColor White -NoNewline
        Write-Host $matches[1] -ForegroundColor Cyan
        $script:exitosos++
    }
    
    if ($buildGradle -match 'description = "([^"]+)"') {
        Write-Host "[OK] " -ForegroundColor Green -NoNewline
        Write-Host "Descripcion: " -ForegroundColor White -NoNewline
        Write-Host $matches[1] -ForegroundColor Cyan
        $script:exitosos++
    }
    
    if ($buildGradle -match 'copyright = "([^"]+)"') {
        Write-Host "[OK] " -ForegroundColor Green -NoNewline
        Write-Host "Copyright: " -ForegroundColor White -NoNewline
        Write-Host $matches[1] -ForegroundColor Cyan
        $script:exitosos++
    }
    
    if ($buildGradle -match 'upgradeUuid = "([^"]+)"') {
        $uuid = $matches[1]
        Write-Host "[OK] " -ForegroundColor Green -NoNewline
        Write-Host "UUID Windows: " -ForegroundColor White -NoNewline
        Write-Host $uuid -ForegroundColor Cyan
        
        if ($uuid -eq "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d") {
            Write-Host "     [AVISO] " -ForegroundColor Yellow -NoNewline
            Write-Host "Estas usando el UUID de ejemplo. Genera uno unico." -ForegroundColor Yellow
            $script:advertencias++
        } else {
            $script:exitosos++
        }
    }
}

# 5. VERIFICAR IMAGENES ADICIONALES (OPCIONAL)
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " RECURSOS VISUALES ADICIONALES (Opcional)" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

Test-ArchivoRequerido -Ruta "composeApp\installer-background.bmp" -Descripcion "Fondo instalador Windows" -Requerido $false
Test-ArchivoRequerido -Ruta "composeApp\installer-banner.bmp" -Descripcion "Banner instalador Windows" -Requerido $false
Test-ArchivoRequerido -Ruta "composeApp\dmg-background.png" -Descripcion "Fondo DMG macOS" -Requerido $false

# RESUMEN FINAL
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " RESUMEN DE VERIFICACION" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "   Verificaciones exitosas: " -NoNewline -ForegroundColor White
Write-Host $exitosos -ForegroundColor Green
Write-Host "   Advertencias: " -NoNewline -ForegroundColor White
Write-Host $advertencias -ForegroundColor Yellow
Write-Host "   Errores: " -NoNewline -ForegroundColor White
Write-Host $errores -ForegroundColor Red
Write-Host ""

if ($errores -eq 0) {
    Write-Host "================================================================" -ForegroundColor Green
    Write-Host "  TODO LISTO PARA GENERAR EL INSTALADOR" -ForegroundColor Green
    Write-Host "================================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Comandos disponibles:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "   Windows MSI:  " -ForegroundColor White -NoNewline
    Write-Host ".\gradlew.bat packageMsi" -ForegroundColor Yellow
    Write-Host "   macOS DMG:    " -ForegroundColor White -NoNewline
    Write-Host "./gradlew packageDmg" -ForegroundColor Yellow
    Write-Host "   Linux DEB:    " -ForegroundColor White -NoNewline
    Write-Host "./gradlew packageDeb" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "   Todas las plataformas: " -ForegroundColor White -NoNewline
    Write-Host ".\gradlew.bat packageDistributionForCurrentOS" -ForegroundColor Yellow
    Write-Host ""
} elseif ($errores -le 2) {
    Write-Host "================================================================" -ForegroundColor Yellow
    Write-Host "  CONFIGURACION BASICA OK - Hay mejoras sugeridas" -ForegroundColor Yellow
    Write-Host "================================================================" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Puedes generar el instalador, pero considera:" -ForegroundColor Yellow
    Write-Host "  * Generar un UUID unico para Windows" -ForegroundColor White
    Write-Host "  * Agregar recursos visuales adicionales" -ForegroundColor White
    Write-Host ""
    Write-Host "Ver guia completa: " -NoNewline -ForegroundColor White
    Write-Host "INSTALLER_CUSTOMIZATION.md" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host "================================================================" -ForegroundColor Red
    Write-Host "  HAY ERRORES QUE DEBEN CORREGIRSE" -ForegroundColor Red
    Write-Host "================================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Corrige los errores marcados arriba antes de continuar." -ForegroundColor Red
    Write-Host "Consulta la guia: " -NoNewline -ForegroundColor White
    Write-Host "INSTALLER_CUSTOMIZATION.md" -ForegroundColor Cyan
    Write-Host ""
}

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""
