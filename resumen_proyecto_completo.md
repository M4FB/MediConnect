# Resumen Tecnico del Proyecto - MediConnect

Apuntes sobre la arquitectura, configuracion y estructura de archivos de la API y la aplicacion Android para la entrega de Tecnologias Moviles.

---

## 1. Tecnologias y stack de desarrollo

### API y base de datos (Backend)
*   **Servidor**: Java 17 con Spring Boot 3.3.6.
*   **Base de datos**: PostgreSQL 16 corriendo sobre una imagen Alpine.
*   **ORM / Acceso a datos**: Spring Data JPA con Hibernate.
*   **Seguridad**: Spring Security usando tokens JWT (firmados con algoritmo HS256) y contraseñas hasheadas en BD mediante BCrypt.
*   **Documentacion**: Swagger UI expuesto con OpenAPI 3.0 para pruebas de endpoints.
*   **Infraestructura**: Dockerfile para empaquetado y manifiestos de Kubernetes (K3s). La persistencia de la base de datos se maneja con un volumen persistente (PVC) de 5 GB.

### Aplicacion Android (Cliente)
*   **Lenguaje**: Kotlin nativo (compatible desde SDK 24 hasta SDK 34).
*   **Diseño de arquitectura**: Patron MVVM (Model-View-ViewModel) con arquitectura limpia.
*   **Inyeccion de dependencias**: Dagger Hilt (version 2.50).
*   **Llamadas de red**: Retrofit 2 con OkHttp 4 (incluye interceptor para adjuntar el JWT y redirigir al Login si expira la sesion).
*   **Base de datos local (Offline cache)**: Room para almacenar doctores y citas cuando no hay conexion.
*   **Seguridad local**: EncryptedSharedPreferences para guardar el token de forma segura e integracion con la huella digital (BiometricPrompt).
*   **Generador QR**: Libreria ZXing para renderizar el codigo de check-in de las citas.
*   **Imagenes**: Coil para cargar las fotos de perfil de los usuarios.

---

## 2. Configuracion de red y despliegue (Kubernetes)

El proyecto esta desplegado en el cluster K3s de la universidad y se conecta mediante WireGuard.

*   **Namespace asignado**: "2023800251"
*   **Servidor del cluster (Control Plane)**: 172.16.10.31:6443 (requiere conexion WireGuard activa).
*   **Acceso externo al API**:
    *   **URL base para Retrofit**: http://172.16.10.31:30251/api/
    *   **Puerto externo (NodePort)**: 30251
    *   **Swagger interactivo**: http://172.16.10.31:30251/swagger-ui/index.html
*   **Enrutamiento interno en K8s**:
    *   **IP directa del Pod de Postgres**: 10.42.0.44:5432 (se usa esta IP en el ConfigMap de la API para evitar problemas con el DNS del cluster).
    *   **ClusterIP de Postgres**: 10.43.149.205
*   **Datos de conexion a la base de datos**:
    *   **Base de datos**: mediconnect
    *   **Usuario**: mediconnect
    *   **Contraseña**: mediconnect123

---

## 3. Estructura de archivos de los proyectos

Ubicacion de las carpetas principales dentro de /home/m4fb/Documents/ProjFinalTM:

```text
ProjFinalTM/
├── .gitignore                          # Descarta archivos de compilacion de Maven y Android
├── guia_diseno_y_credenciales.txt      # Archivo guia para modificar el diseño visual
├── resumen_proyecto_completo.md        # Este archivo de apuntes
│
├── mediconnect-api/                    # --- BACKEND (SPRING BOOT) ---
│   ├── Dockerfile                      # Archivo de construccion de imagen
│   ├── pom.xml                         # Administrador de dependencias Maven
│   ├── k8s/                            # Manifiestos de despliegue en Kubernetes
│   │   ├── postgres-pvc.yaml           # Reserva de 5GB de disco para la BD
│   │   ├── postgres-configmap.yaml     # Datos de conexion de Postgres
│   │   ├── postgres-deployment.yaml    # Levanta el pod de la BD
│   │   ├── postgres-service.yaml       # Expone Postgres dentro del cluster
│   │   ├── api-configmap.yaml          # Configuracion de conexion del backend
│   │   ├── api-deployment.yaml         # Levanta la aplicacion Spring y define limites de RAM
│   │   ├── api-service.yaml            # Expone la API al exterior en el puerto 30251
│   │   └── deploy.sh                   # Script para desplegar todo en orden
│   └── src/main/
│       ├── java/com/mediconnect/api/
│       │   ├── controller/             # Puntos de entrada REST (Auth, Citas, Doctores, etc.)
│       │   ├── dto/                    # Objetos para transferencia de datos en JSON
│       │   ├── entity/                 # Tablas mapeadas con JPA
│       │   ├── repository/             # Consultas a la base de datos PostgreSQL
│       │   ├── security/               # Filtros JWT y configuracion de acceso web
│       │   └── service/                # Implementacion de la logica del sistema
│       └── resources/
│           └── application-prod.yml    # Propiedades del entorno de ejecucion
│
└── mediconnect-android/                # --- APP ANDROID (KOTLIN) ---
    ├── build.gradle.kts                # Gradle raiz del proyecto
    ├── settings.gradle.kts             # Nombre del proyecto y modulos incluidos
    ├── local.properties                # Ruta del SDK de Android del sistema
    ├── gradle.properties               # Configuracion del daemon de gradle y JDK de respaldo
    └── app/
        ├── build.gradle.kts            # Dependencias del modulo movil (Room, Hilt, Retrofit)
        └── src/main/
            ├── AndroidManifest.xml     # Permisos del sistema y actividades
            ├── java/com/mediconnect/app/
            │   ├── MediConnectApp.kt   # Inicializador de Dagger Hilt
            │   ├── MainActivity.kt     # Actividad base con NavHost
            │   ├── di/
            │   │   └── AppModule.kt    # Proveedor de Singletons (BD, API, SharedPrefs)
            │   ├── data/
            │   │   ├── local/          # Base de datos Room (Entidades de cache y DAOs)
            │   │   ├── remote/         # Interfaz Retrofit, DTOs y interceptor JWT
            │   │   └── repository/     # Logica del repositorio con soporte offline
            │   ├── ui/                 # Pantallas de la app (ViewModels y Fragments)
            │   │   ├── auth/           # Login y registro de usuarios
            │   │   ├── dashboard/      # Menu con botones y banner de red
            │   │   ├── doctors/        # Busqueda de doctores y reserva de citas
            │   │   ├── appointments/   # Mis citas, cancelaciones e ingreso por QR
            │   │   ├── prescriptions/  # Recetas medicas asignadas
            │   │   ├── history/        # Historial clinico y registros
            │   │   ├── notifications/  # Notificaciones locales
            │   │   └── profile/        # Edicion de datos del perfil
            │   └── util/
            │       └── Constants.kt    # Constantes (BASE_URL, SharedPrefs keys)
            └── res/
                ├── layout/             # Archivos XML de la interfaz visual
                ├── navigation/
                │   └── nav_graph.xml   # Flujo de pantallas
                └── values/
                    ├── colors.xml      # Colores del tema de la app
                    ├── strings.xml     # Textos en español
                    └── themes.xml      # Estilo general de los componentes
```

---

## 4. Lista de endpoints del API REST

Los endpoints (excepto login y registro) requieren mandar el token en la cabecera `Authorization: Bearer <token>`.

| Modulo | Metodo | Ruta | Descripcion |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/login` | Login. Devuelve los tokens access y refresh. |
| | `POST` | `/api/auth/register` | Registro de pacientes nuevos. |
| | `POST` | `/api/auth/refresh` | Refresca el token cuando expira. |
| **Usuarios** | `GET` | `/api/users/me` | Retorna los datos del perfil actual. |
| | `PUT` | `/api/users/me` | Actualiza campos del perfil. |
| | `PUT` | `/api/users/me/password` | Cambia la contraseña (valida contraseña anterior). |
| **Doctores** | `GET` | `/api/doctors` | Lista doctores. Admite filtros de especialidad o busqueda por nombre. |
| | `GET` | `/api/doctors/{id}` | Retorna el detalle de un doctor en especifico. |
| | `GET` | `/api/doctors/{id}/horarios-disponibles` | Retorna horas disponibles para una fecha dada (?fecha=). |
| | `GET` | `/api/doctors/{id}/valoraciones` | Lista comentarios y estrellas del doctor. |
| | `POST` | `/api/doctors/{id}/valoraciones` | Agrega comentario y calificacion (1-5 estrellas) al doctor. |
| **Citas** | `GET` | `/api/citas` | Lista citas del usuario. Admite filtro de estado (?estado=). |
| | `POST` | `/api/citas` | Crea o reserva una cita con un doctor. |
| | `PUT` | `/api/citas/{id}/cancelar` | Cancela una cita pasandole un motivo. |
| | `POST` | `/api/citas/{id}/check-in` | Registra asistencia de la cita mandando el codigo QR. |
| **Recetas** | `GET` | `/api/recetas/mis-recetas` | Lista recetas medicas del paciente. |
| | `POST` | `/api/recetas` | Genera una receta de medicamentos (Uso del Doctor). |
| **Historial** | `GET` | `/api/historial` | Obtiene el historial medico del paciente. |
| | `POST` | `/api/historial` | Agrega una entrada nueva al historial. |
| **Alertas** | `GET` | `/api/notificaciones` | Retorna notificaciones del usuario. |
| | `PUT` | `/api/notificaciones/{id}/leer` | Marca una notificacion como leida. |
| **Admin** | `POST` | `/api/admin/doctores` | Registra a un nuevo doctor en el sistema (Uso exclusivo de ADMIN). |

---

## 5. Datos y usuarios de prueba

Cuentas precargadas por CommandLineRunner en la base de datos para probar flujos:

*   **Administrador**:
    *   Correo: `admin@mediconnect.com`
    *   Contraseña: `admin123`
*   **Doctores (Contraseña general: `password123`)**:
    *   Cardiologia: `dr.martinez@mediconnect.com`
    *   Dermatologia: `dra.garcia@mediconnect.com`
    *   Pediatria: `dr.rodriguez@mediconnect.com`
*   **Pacientes (Contraseña general: `password123`)**:
    *   Paciente 1: `paciente1@email.com` (tiene historial y citas previas)
    *   Paciente 2: `paciente2@email.com`
    *   Paciente 3: `paciente3@email.com`
    *   Paciente 4: `paciente4@email.com`
    *   Paciente 5: `paciente5@email.com`
