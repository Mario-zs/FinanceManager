#  Finance Manager

Aplicación de escritorio desarrollada en Java para la gestión simple de finanzas personales.  
Permite registrar ingresos y egresos, calcular saldo dinámico, aplicar filtros por fecha y exportar reportes en PDF.

---

##  Descripción

**Finance Manager** es una aplicación orientada a la administración básica de finanzas personales.  
El sistema permite a los usuarios registrarse, iniciar sesión y llevar un control detallado de sus movimientos financieros mediante una base de datos local.

- **MySQL (v1.0)** - Requiere un servidor de base de datos local.
- **SQLite (v1.1)** - Base de datos embebida que se crea automáticamente al ejecutar la aplicación.

El proyecto fue desarrollado siguiendo prácticas como:

- Separación entre lógica de negocio e interfaz gráfica
- Uso de transacciones
- Manejo de excepciones
- Encriptación de contraseñas
- Prevención de SQL Injection
- Generación profesional de reportes PDF

---

##  Funcionalidades

###  Gestión de usuarios
- Registro con validación de datos
- Contraseñas encriptadas con SHA-256 + SALT
- Validación de usuario duplicado
- Inicio de sesión seguro

###  Gestión financiera
- Registro de ingresos y egresos
- Cálculo automático del saldo actual
- Cálculo total de ingresos
- Cálculo total de egresos
- Saldo calculado dinámicamente desde la base de datos

###  Filtros
- Filtrado por tipo (Ingreso / Egreso)
- Filtrado por mes
- Filtrado por año

###  Reportes
- Exportación de historial en PDF
- Resumen financiero incluido
- Filtros aplicados visibles en el reporte
- Paginación automática
- Comentarios por movimiento

---

##  Tecnologías utilizadas

- **Java SE**
- **Swing (Interfaz gráfica)**
- **MySQL (Base de datos relacional)**
- **SQLite (Base de datos embebida)**
- **JDBC**
- **Apache PDFBox (Generación de PDF)**

---

##  Arquitectura del proyecto

El proyecto está dividido en dos capas principales:

###  `clases`
Contiene la lógica de negocio y acceso a datos:

- `Conexion` → Gestión de conexión a base de datos (MySQL / SQLite)
- `Seguridad` → Hash SHA-256 para contraseñas
- `Usuarios` → Registro y autenticación
- `Movimientos` → Gestión de ingresos y egresos
- `Movimiento` → Modelo de datos
- `ReportePDF` → Generación de reportes

###  `ventanas`
Contiene la interfaz gráfica (Swing):

- `Inicio`
- `Registrarse`
- `Login`
- `Principal`
- `HistorialMovimientos`
- Otras ventanas auxiliares

---

##  Seguridad

- Las contraseñas **no se almacenan en texto plano**
- Se utiliza **SHA-256 + SALT**
- Uso de `PreparedStatement` para prevenir SQL Injection
- Eliminación de contraseña de memoria después de su uso
- Transacciones para mantener integridad en el registro

---

##  Base de Datos

El proyecto incluye dos opciones de base de datos.

### MySQL (v 1.0)

El repositorio incluye un script SQL para crear la base de datos:

### 📁 `bd_fm.sql`

Contiene:

- Creación de la base de datos
- Tablas `usuarios` y `movimientos`
- Llaves primarias
- Llaves foráneas
- Restricciones `UNIQUE`
- Tipos de datos adecuados

Esta versión requiere un servidor MySQL local.

---

### SQLite (v1.1)

La versión SQLite **no requiere instalación de servidor de base de datos**.

Al ejecutar el archivo `.jar`, la aplicación crea automáticamente una carpeta

**data/**

Dentro de ella se genera el archivo:

**bd_fm.db**

Este archivo contiene todos los datos de los usuarios.

La base de datos se almacena **localmente en la misma carpeta donde se ejecuta la aplicación**.

La estructura de tablas se genera automáticamente desde el código si la base de datos no existe.

---

##  Instalación y ejecución

Existen dos formas de ejecutar la aplicación dependiendo de la versión utilizada.

---

### Opción — SQLite (v1.1)

No requiere configuración de base de datos.

1. Descargar el archivo `.jar` desde la sección **Releases**
2. Ejecutar el archivo

La base de datos se creará automáticamente en el primer inicio.

---

### Opción — MySQL (v1.0)

1️⃣ Crear la base de datos ejecutando:

```sql
bd_fm.sql
```

2️⃣ Configurar la conexión en Conexion.java:

```Java
private static final String URL = "jdbc:mysql://localhost/bd_fm?serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";
```

3️⃣ Ejecutar la aplicación desde el IDE o desde el .jar.

---

##  Capturas de pantalla

###  Pantalla de Inicio
![Inicio](images/screenshots/inicio.png)

---

### Iniciar sesión
![Iniciar sesión](images/screenshots/iniciar.png)

---

### Registrarse
![Registrarse](images/screenshots/registrarse.png)

---

###  Panel Principal
![Principal](images/screenshots/principal.png)

---

###  Agregar Movimiento
![Agregar Movimiento](images/screenshots/agregar.png)

---

###  Historial de Movimientos
![Historial](images/screenshots/historial.png)

---

###  Detalles movimiento
![Detalles movimiento](images/screenshots/detalles.png)

---

###  Reporte PDF
![Reporte PDF](images/screenshots/pdf.png)


---


##  Versiones del proyecto

| Versión | Base de datos | Descripción |
|--------|---------------|-------------|
| v1.1 | SQLite | No requiere servidor de base de datos. |
| v1.0 | MySQL | Versión original que utiliza servidor MySQL local. |

La versión SQLite simplifica la instalación y permite ejecutar la aplicación sin dependencias externas.

---


##  Licencia

### MIT License
Puedes usar, modificar y distribuir este código siempre que incluyas la nota de copyright y licencia.
Desarrollado por Mario Alberto Melgarejo Villaseñor © 2026
¡Gracias por usar Finance Manager!