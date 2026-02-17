# Manual de Instalación - Gestor de Asistencia

## 📋 Requisitos Previos

### **✅ NO se requiere instalar Java**
La aplicación incluye su propio entorno de ejecución Java (custom-jre). El instalador copia automáticamente todos los archivos necesarios para ejecutar la aplicación sin requerir que el usuario tenga Java instalado en su sistema.

---

### **MongoDB Atlas (Base de Datos en la Nube)**
La aplicación utiliza **MongoDB Atlas** como base de datos. **NO** es necesario instalar MongoDB localmente.

**Configuración de MongoDB Atlas:**
- La aplicación se conecta automáticamente a MongoDB Atlas
- La cadena de conexión está pre-configurada en `application.properties`
- La base de datos se llama: `centro_educativo`
- **Puerto:** No aplica (conexión cloud a través de internet)
- **Usuario:** `admin_profesor` (ya configurado)

**Colecciones que la aplicación utiliza:**
- `usuarios` - Datos de administradores y profesores
- `alumnos` - Información de estudiantes
- `asignaturas` - Materias del centro educativo
- `registros_asistencia` - Control de asistencia diaria

**⚠️ IMPORTANTE - Importación de Datos Iniciales:**
- Los datos de prueba (usuarios, alumnos y asignaturas) se deben importar manualmente usando la herramienta **mongoimport**
- Consulta la carpeta `/database` del proyecto para encontrar los archivos JSON y las instrucciones detalladas
- Se requiere conexión a Internet para acceder a MongoDB Atlas
- La base de datos ya está configurada y lista para recibir datos

---

## 🚀 Instalación

### **Opción 1: Instalador Automático (Recomendado)**

1. Ejecutar el archivo: `GestorAsistencia-Setup.exe`
2. Seguir el asistente de instalación
3. Seleccionar la carpeta de instalación (por defecto: `C:\Program Files\GestorAsistencia`)
4. (Opcional) Crear icono en el escritorio
5. Finalizar la instalación

El instalador copiará automáticamente:
- `GestorAsistencia.exe` (ejecutable)
- `gestionasistencia-0.0.1-SNAPSHOT.jar` (aplicación completa)

### **Opción 2: Instalación Manual**

1. Crear una carpeta para la aplicación (ej: `C:\GestorAsistencia`)
2. Copiar los siguientes archivos a la carpeta:
   - `GestorAsistencia.exe`
   - `gestionasistencia-0.0.1-SNAPSHOT.jar`
3. Ejecutar `GestorAsistencia.exe`

---

## ▶️ Ejecución de la Aplicación

### **Desde el Instalador:**
- Doble clic en el icono del escritorio (si se creó)
- O buscar "Gestor de Asistencia" en el Menú Inicio

### **Ejecución Manual:**
- Doble clic en `GestorAsistencia.exe`
- O desde línea de comandos:
  ```bash
  java -jar gestionasistencia-0.0.1-SNAPSHOT.jar
  ```

---

## 📦 Importación de Datos de Prueba

### **Usando mongoimport**

Antes de usar la aplicación por primera vez, debes importar los datos de prueba a MongoDB Atlas usando la herramienta **mongoimport**.

**Requisitos:**
1. Tener instalado **MongoDB Database Tools** (incluye mongoimport)
   - Descargar desde: https://www.mongodb.com/try/download/database-tools

2. Tener la cadena de conexión (URI) de MongoDB Atlas

**Archivos de datos disponibles (carpeta `/database`):**
- `asignaturas.json` - 5 asignaturas de ejemplo
- `usuarios.json` - 2 usuarios (admin + profesor)
- `alumnos.json` - 12 alumnos en 2 grupos

**Comandos de importación:**

```bash
# Importar asignaturas
mongoimport --uri="<MONGODB_URI>" --db=gestorasistencia --collection=asignaturas --file=database/asignaturas.json --jsonArray

# Importar usuarios
mongoimport --uri="<MONGODB_URI>" --db=gestorasistencia --collection=usuarios --file=database/usuarios.json --jsonArray

# Importar alumnos
mongoimport --uri="<MONGODB_URI>" --db=gestorasistencia --collection=alumnos --file=database/alumnos.json --jsonArray
```

Reemplaza `<MONGODB_URI>` con tu cadena de conexión real.

**📖 Para instrucciones detalladas**, consulta el archivo: `database/README.md`

---

## 👤 Acceso a la Aplicación

### **Usuario Administrador por Defecto:**
- **Email:** `admin@gestorasistencia.com`
- **Contraseña:** `admin123`
- **Rol:** ADMIN

### **Usuario Profesor de Prueba:**
- **Email:** `profesor@gestorasistencia.com`
- **Contraseña:** `profesor123`
- **Rol:** PROFESOR

### **Datos de Prueba (después de importar con mongoimport):**
- 2 usuarios (1 admin + 1 profesor)
- 12 alumnos de prueba en 2 grupos (2DAM-A y 2DAM-B)
- 5 asignaturas de ejemplo del ciclo DAM

---

## 🛠️ Solución de Problemas

### **Problema: "No se puede conectar a MongoDB"**
**Solución:**
1. Verificar conexión a Internet
2. Comprobar que MongoDB Atlas está accesible
3. La aplicación mostrará el error en la consola si hay problemas de conexión

### **Problema: "La aplicación no inicia"**
**Solución:**
1. Verificar que todos los archivos se instalaron correctamente
2. Comprobar que existe la carpeta `custom-jre` junto al ejecutable
3. Ejecutar el instalador de nuevo si falta algún archivo
4. Verificar que el archivo `.jar` está en la misma carpeta que el `.exe`

---

## 📂 Archivos del Proyecto

### **Carpeta /deploy (Entrega):**
```
/deploy/
├── GestorAsistencia-Setup.exe         ← Instalador de Inno Setup
├── GestorAsistencia.exe               ← Ejecutable de Launch4j
├── installer-setup.iss                ← Script de Inno Setup
├── launch4j-config.xml                ← Configuración de Launch4j
├── README_INSTALACION.md              ← Este manual
└── icon.ico                           ← Icono de la aplicación
```

### **Archivos Generados en /target:**
```
/target/
├── gestionasistencia-0.0.1-SNAPSHOT.jar    ← Fat JAR con dependencias
├── GestorAsistencia.exe                    ← Ejecutable Windows
└── installer/
    └── GestorAsistencia-Setup.exe          ← Instalador final
```

---

## 🏗️ Información Técnica

### **Tecnologías Utilizadas:**
- **Java:** 21 (LTS)
- **Framework:** Spring Boot 3.2.0
- **Base de Datos:** MongoDB Atlas (Cloud)
- **Interfaz:** JavaFX 21
- **Build Tool:** Maven 3.9.x
- **Empaquetado:** Launch4j + Inno Setup

### **Configuración de MongoDB:**
- **Base de datos:** `centro_educativo`
- **Modo de conexión:** MongoDB Atlas (Cloud - SRV)
- **Driver:** mongodb-driver-sync 4.11.1
- **Autenticación:** Usuario/Contraseña (configurado en application.properties)

---

## 📞 Soporte

Para problemas técnicos o consultas:
- **Proyecto Académico:** Desarrollo de Aplicaciones Multiplataforma (DAM)
- **Asignatura:** Acceso a Datos y Desarrollo de Interfaces Gráficas
- **Curso:** 2º DAM - 2ª Evaluación 2025-2026

---

**Versión:** 1.0
**Fecha:** Febrero 2026
**Autor:** Emilio - 2º DAM
