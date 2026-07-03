; Script de instalacion para GestorAsistencia
; Generado para el proyecto de 2a Evaluacion - Acceso a Datos y Desarrollo de Interfaces
; IMPORTANTE: Este archivo .iss esta en la carpeta /deploy.
;
; El ejecutable ya NO se genera con launch4j (o el launcher C# de otros
; proyectos): se usa el launcher NATIVO de jpackage (deploy\app-image\GestorAsistencia),
; que es un binario estandar y no dispara la heuristica de antivirus como
; los lanzadores que arrancan otro proceso oculto.

#define MyAppName "Gestor de Asistencia"
#define MyAppVersion "1.0"
#define MyAppPublisher "ISEN - Desarrollo de Aplicaciones Multiplataforma"
#define MyAppExeName "GestorAsistencia.exe"
#define ImageDir "app-image\GestorAsistencia"

[Setup]
; Informacion basica de la aplicacion
AppId={{8F2A3B1C-9D4E-5F6A-7B8C-9D0E1F2A3B4C}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={autopf}\GestorAsistencia
DefaultGroupName={#MyAppName}
; Ruta donde se guardara el instalador generado (relativa a este .iss)
OutputDir=Output
OutputBaseFilename=GestorAsistencia-Setup
; Icono del instalador (relativa a este .iss)
SetupIconFile=icon.ico
UninstallDisplayIcon={app}\{#MyAppExeName}
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
; Copia recursiva de TODA la app-image de jpackage (exe nativo + app\ + runtime\)
Source: "{#ImageDir}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
; Acceso directo en el menu de inicio
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
; Acceso directo en el escritorio (opcional)
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
; Desinstalador en el menu de inicio
Name: "{group}\Desinstalar {#MyAppName}"; Filename: "{uninstallexe}"

[Run]
; Ejecutar la aplicacion al finalizar la instalacion
Filename: "{app}\{#MyAppExeName}"; Description: "Ejecutar {#MyAppName}"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
; Limpiar archivos temporales al desinstalar
Type: filesandordirs; Name: "{app}"
