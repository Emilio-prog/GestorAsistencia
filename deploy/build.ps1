# ============================================================
#  Build de GestorAsistencia  ->  instalador sin falsos positivos
#
#  Sustituye el antiguo launcher de launch4j por el launcher NATIVO
#  de jpackage, que es un binario estandar y reconocido.
#
#  Uso:  powershell -File deploy\build.ps1
#  Requisitos: JDK 21 (jpackage en PATH) + Inno Setup (ISCC.exe)
# ============================================================

$ErrorActionPreference = 'Stop'
$Deploy   = $PSScriptRoot
$Project  = Split-Path $Deploy -Parent
$Jar      = Join-Path $Project 'target\gestionasistencia-0.0.1-SNAPSHOT.jar'
$Jre      = Join-Path $Deploy  'custom-jre'
$Icon     = Join-Path $Deploy  'icon.ico'
$ImageDir = Join-Path $Deploy  'app-image'
$InputDir = Join-Path $Deploy  '_jpinput'

Write-Host '== 1/4  Comprobaciones ==' -ForegroundColor Cyan
if (-not (Test-Path $Jar)) { throw "No existe el jar: $Jar  (ejecuta antes: .\mvnw.cmd -DskipTests clean package)" }
if (-not (Test-Path $Jre)) { throw "No existe custom-jre en: $Jre" }
$jpackage = (Get-Command jpackage -ErrorAction SilentlyContinue).Source
if (-not $jpackage) { throw 'jpackage no esta en el PATH. Instala/activa el JDK 21.' }

Write-Host '== 2/4  Preparando entrada ==' -ForegroundColor Cyan
if (Test-Path $InputDir) { Remove-Item $InputDir -Recurse -Force }
if (Test-Path $ImageDir) { Remove-Item $ImageDir -Recurse -Force }
New-Item -ItemType Directory -Path $InputDir | Out-Null
Copy-Item $Jar $InputDir

Write-Host '== 3/4  jpackage (app-image nativa) ==' -ForegroundColor Cyan
& $jpackage `
    --type app-image `
    --name GestorAsistencia `
    --input $InputDir `
    --main-jar (Split-Path $Jar -Leaf) `
    --runtime-image $Jre `
    --icon $Icon `
    --dest $ImageDir `
    --vendor 'ISEN - DAM' `
    --app-version '1.0'
if ($LASTEXITCODE -ne 0) { throw "jpackage fallo (exit $LASTEXITCODE)" }
try { Remove-Item $InputDir -Recurse -Force -ErrorAction Stop }
catch { Write-Host "   (aviso: no se pudo borrar $InputDir ahora; se puede borrar a mano)" -ForegroundColor Yellow }
Write-Host "   -> $ImageDir\GestorAsistencia\GestorAsistencia.exe" -ForegroundColor Green

Write-Host '== 4/4  Instalador (Inno Setup) ==' -ForegroundColor Cyan
$iscc = @(
    'C:\Program Files\Inno Setup 7\ISCC.exe',
    'C:\Program Files (x86)\Inno Setup 6\ISCC.exe',
    "$env:LOCALAPPDATA\Programs\Inno Setup 6\ISCC.exe"
) | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $iscc) { throw 'No se encontro ISCC.exe (Inno Setup).' }
& $iscc (Join-Path $Deploy 'installer-setup.iss')
if ($LASTEXITCODE -ne 0) { throw "ISCC fallo (exit $LASTEXITCODE)" }

Write-Host ''
Write-Host 'LISTO. Instalador en: deploy\Output\GestorAsistencia-Setup.exe' -ForegroundColor Green
