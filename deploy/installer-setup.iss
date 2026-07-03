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

[Code]
// La app lee la cadena de conexion de MongoDB desde la variable de entorno
// MONGODB_URI (ver application.properties). Para que la app funcione nada
// mas instalarse -sin que quien instale tenga que tocar nada tecnico- el
// propio instalador la pide en una pantalla y la deja puesta en el sistema.
//
// La cadena NUNCA se escribe en este script ni se compila dentro del .exe:
// se introduce en el momento de instalar. Tambien se puede pasar en modo
// desatendido con el parametro /MONGOURI="mongodb+srv://..." (por ejemplo
// para instalaciones silenciosas o para pruebas automatizadas).

var
  MongoPage: TInputQueryWizardPage;
  MongoUriParam: string;

function SendMessageTimeout(hWnd: Longint; Msg: Longint; wParam: Longint;
  lParam: AnsiString; fuFlags, uTimeout: Longint; var lpdwResult: Longint): Longint;
  external 'SendMessageTimeoutA@user32.dll stdcall';

procedure InitializeWizard;
begin
  MongoUriParam := ExpandConstant('{param:MONGOURI|}');
  if MongoUriParam = '' then
  begin
    MongoPage := CreateInputQueryPage(wpSelectDir,
      'Configuracion de la base de datos',
      'Cadena de conexion de MongoDB Atlas',
      'Introduce la cadena de conexion que te ha facilitado el desarrollador ' +
      '(empieza por mongodb+srv://). Se guardara como variable de entorno del ' +
      'sistema y la aplicacion la usara automaticamente, sin pasos adicionales.');
    MongoPage.Add('Cadena de conexion:', True);
  end;
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  Result := True;
  if (MongoPage <> nil) and (CurPageID = MongoPage.ID) then
  begin
    if Trim(MongoPage.Values[0]) = '' then
    begin
      MsgBox('Debes introducir la cadena de conexion de MongoDB para continuar.', mbError, MB_OK);
      Result := False;
    end;
  end;
end;

function GetMongoUriValue: string;
begin
  if MongoUriParam <> '' then
    Result := MongoUriParam
  else
    Result := MongoPage.Values[0];
end;

procedure SetPersistentEnvVar(Name, Value: string);
var
  ResultCode: Longint;
begin
  RegWriteStringValue(HKEY_LOCAL_MACHINE,
    'SYSTEM\CurrentControlSet\Control\Session Manager\Environment', Name, Value);
  // Avisa a los procesos existentes (Explorer, etc.) del cambio de entorno
  SendMessageTimeout($FFFF, $1A, 0, 'Environment', 0, 5000, ResultCode);
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
    SetPersistentEnvVar('MONGODB_URI', GetMongoUriValue);
end;
