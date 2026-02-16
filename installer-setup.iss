; Script de instalación para GestorAsistencia
; Generado para el proyecto de 2ª Evaluación - Acceso a Datos y Desarrollo de Interfaces

#define MyAppName "Gestor de Asistencia"
#define MyAppVersion "1.0"
#define MyAppPublisher "IES - Desarrollo de Aplicaciones Multiplataforma"
#define MyAppExeName "GestorAsistencia.exe"

[Setup]
; Información básica de la aplicación
AppId={{8F2A3B1C-9D4E-5F6A-7B8C-9D0E1F2A3B4C}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={autopf}\GestorAsistencia
DefaultGroupName={#MyAppName}
; Ruta donde se guardará el instalador generado
OutputDir=target\installer
OutputBaseFilename=GestorAsistencia-Setup
; Icono del instalador
SetupIconFile=icon.ico
Compression=lzma2/max
SolidCompression=yes
WizardStyle=modern
; Requiere privilegios de administrador
PrivilegesRequired=admin

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "desktopicon"; Description: "Crear icono en el escritorio"; GroupDescription: "Iconos adicionales:"

[Files]
; Copiar el ejecutable
Source: "target\GestorAsistencia.exe"; DestDir: "{app}"; Flags: ignoreversion
; Copiar el JAR
Source: "target\gestionasistencia-0.0.1-SNAPSHOT.jar"; DestDir: "{app}"; Flags: ignoreversion
; Copiar el JRE personalizado completo
Source: "target\custom-jre\*"; DestDir: "{app}\custom-jre"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
; Acceso directo en el menú de inicio
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
; Acceso directo en el escritorio (opcional)
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
; Desinstalador en el menú de inicio
Name: "{group}\Desinstalar {#MyAppName}"; Filename: "{uninstallexe}"

[Run]
; Ejecutar la aplicación al finalizar la instalación
Filename: "{app}\{#MyAppExeName}"; Description: "Ejecutar {#MyAppName}"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
; Limpiar archivos temporales al desinstalar
Type: filesandordirs; Name: "{app}"
