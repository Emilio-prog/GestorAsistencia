# Base de Datos - Datos de Ejemplo para GestorAsistencia

Esta carpeta contiene archivos JSON con datos de prueba para importar en MongoDB Atlas.

## Contenido

- **asignaturas.json**: 5 asignaturas del ciclo formativo DAM (curso 2025-2026)
- **usuarios.json**: 2 usuarios (1 ADMIN y 1 PROFESOR) para realizar el login
- **alumnos.json**: 12 alumnos de prueba repartidos en 2 grupos (2DAM-A y 2DAM-B)

## Credenciales de Acceso

### Usuario Administrador
- **Email**: admin@gestorasistencia.com
- **Contraseña**: admin123
- **Rol**: ADMIN

### Usuario Profesor
- **Email**: profesor@gestorasistencia.com
- **Contraseña**: profesor123
- **Rol**: PROFESOR

## Importación de Datos con mongoimport

La herramienta **mongoimport** permite importar datos desde archivos JSON a una base de datos MongoDB (local o en la nube como MongoDB Atlas).

### Requisitos Previos

1. Tener instalado **MongoDB Database Tools** (incluye mongoimport)
   - Descargar desde: https://www.mongodb.com/try/download/database-tools
   - O instalar con: `choco install mongodb-database-tools` (Windows con Chocolatey)

2. Tener una cadena de conexión a tu base de datos MongoDB Atlas
   - Formato: `mongodb+srv://usuario:password@cluster.mongodb.net/`

### Comandos de Importación

**IMPORTANTE**: Reemplaza `<MONGODB_URI>` con tu cadena de conexión real de MongoDB Atlas.

#### 1. Importar Asignaturas

```bash
mongoimport --uri="<MONGODB_URI>" --db=gestorasistencia --collection=asignaturas --file=database/asignaturas.json --jsonArray
```

#### 2. Importar Usuarios

```bash
mongoimport --uri="<MONGODB_URI>" --db=gestorasistencia --collection=usuarios --file=database/usuarios.json --jsonArray
```

#### 3. Importar Alumnos

```bash
mongoimport --uri="<MONGODB_URI>" --db=gestorasistencia --collection=alumnos --file=database/alumnos.json --jsonArray
```

### Ejemplo Completo (desde la raíz del proyecto)

Si tu URI de MongoDB Atlas es:
```
mongodb+srv://usuario:password@asistenciadam.tr1xlor.mongodb.net/
```

Ejecuta estos comandos desde PowerShell (en la raíz del proyecto):

```powershell
# Importar asignaturas
mongoimport --uri="mongodb+srv://usuario:password@asistenciadam.tr1xlor.mongodb.net/" --db=gestorasistencia --collection=asignaturas --file=database/asignaturas.json --jsonArray

# Importar usuarios
mongoimport --uri="mongodb+srv://usuario:password@asistenciadam.tr1xlor.mongodb.net/" --db=gestorasistencia --collection=usuarios --file=database/usuarios.json --jsonArray

# Importar alumnos
mongoimport --uri="mongodb+srv://usuario:password@asistenciadam.tr1xlor.mongodb.net/" --db=gestorasistencia --collection=alumnos --file=database/alumnos.json --jsonArray
```

### Opciones del Comando mongoimport

- `--uri`: Cadena de conexión a MongoDB Atlas (incluye usuario, password y cluster)
- `--db`: Nombre de la base de datos donde se importarán los datos
- `--collection`: Nombre de la colección donde se guardarán los documentos
- `--file`: Ruta al archivo JSON con los datos a importar
- `--jsonArray`: Indica que el archivo contiene un array JSON (múltiples documentos)

### Verificación

Después de importar, puedes verificar los datos en MongoDB Atlas:

1. Inicia sesión en https://cloud.mongodb.com
2. Ve a **Database** > **Browse Collections**
3. Selecciona la base de datos `gestorasistencia`
4. Verifica que existen las colecciones: `asignaturas`, `usuarios`, `alumnos`

O ejecuta la aplicación GestorAsistencia y haz login con las credenciales de admin o profesor.

## Estructura de los Datos

### Alumnos por Grupo

- **Grupo 2DAM-A**: 6 alumnos
  - Carlos Martínez Rodríguez
  - Laura Fernández Pérez
  - Javier López Sánchez
  - Ana González Ruiz
  - David Jiménez Torres
  - Isabel Moreno Díaz

- **Grupo 2DAM-B**: 6 alumnos
  - Miguel Romero Navarro
  - Carmen Muñoz Vázquez
  - Sergio Ramos Castro
  - Patricia Ortega Herrera
  - Raúl Gil Medina
  - Silvia Blanco Prieto

### Asignaturas Disponibles

1. Acceso a Datos
2. Desarrollo de Interfaces
3. Programación de Servicios y Procesos
4. Sistemas de Gestión Empresarial
5. Inglés Técnico

---

**Nota**: Las contraseñas en este proyecto de prueba están en texto plano. En un entorno de producción real, las contraseñas deben estar encriptadas usando algoritmos como BCrypt.
