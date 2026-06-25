# 📚 Documentación General del Proyecto — MediConnect

Este documento contiene el resumen técnico completo del proyecto "MediConnect", incluyendo la estructura de archivos de los proyectos Backend y Android, configuraciones de red, base de datos, credenciales y el catálogo de servicios REST.

---

## 🛠️ 1. Stack Tecnológico

### Backend & Base de Datos
| Componente | Tecnología / Versión | Propósito |
| :--- | :--- | :--- |
| **Lenguaje** | Java 17 | Lenguaje de programación del servidor |
| **Framework** | Spring Boot 3.3.6 | Desarrollo de la API RESTful y seguridad |
| **Persistencia** | Spring Data JPA / Hibernate | Mapeo objeto-relacional (ORM) |
| **Seguridad** | Spring Security / JWT (HS256) | Autenticación y control de accesos |
| **Base de Datos** | PostgreSQL 16 (Alpine) | Motor de base de datos relacional |
| **Documentación** | Swagger UI / OpenAPI 3.0 | Pruebas de API y documentación de endpoints |

### Aplicación Móvil Android
| Componente | Tecnología / Versión | Propósito |
| :--- | :--- | :--- |
| **Lenguaje** | Kotlin | Desarrollo nativo Android |
| **Arquitectura** | MVVM (Model-View-ViewModel) | Separación de lógica de negocio y presentación |
| **Inyección de DI** | Dagger Hilt (2.50) | Gestión del ciclo de vida de dependencias |
| **Cliente HTTP** | Retrofit 2 / OkHttp 4 | Consumo y parsing de servicios web |
| **Caché Local** | Room Database | Base de datos local SQLite para soporte offline |
| **Seguridad** | EncryptedSharedPreferences / Biometrics | Cifrado de credenciales y login por huella |
| **Generador QR** | ZXing Core | Generación de códigos QR de check-in |
| **Multimedia** | Coil | Carga asíncrona de imágenes de perfil |

---

## 🌐 2. Parámetros de Red, Infraestructura y Despliegue

El sistema está desplegado en el clúster remoto **epis-k3s** dentro del namespace universitario asignado.

*   **Namespace de Kubernetes**: `2023800251`
*   **Servidor Control Plane (Clúster)**: `172.16.10.31:6443` (Acceso vía túnel VPN WireGuard)
*   **Puntos de Entrada del Backend (Externos)**:
    *   **URL Base API (Móvil)**: `http://172.16.10.31:30251/api/`
    *   **Puerto de Exposición (NodePort)**: `30251`
    *   **Swagger UI**: `http://172.16.10.31:30251/swagger-ui/index.html`
*   **Conexiones de Red Internas (Overlay K8s)**:
    *   **IP directa del Pod de PostgreSQL**: `10.42.0.44:5432` (Usado en el ConfigMap de la API para garantizar el enrutamiento directo en el clúster)
    *   **ClusterIP del Servicio Postgres**: `10.43.149.205`
*   **Parámetros de Base de Datos**:
    *   **Base de datos**: `mediconnect`
    *   **Usuario**: `mediconnect`
    *   **Contraseña**: `mediconnect123`

---

## 📂 3. Estructura General del Proyecto (Árbol de Archivos)

El repositorio local está ubicado en `/home/m4fb/Documents/ProjFinalTM` y se organiza de la siguiente manera:

```text
ProjFinalTM/
├── .gitignore                          # Exclusiones de Git para Maven y Android
├── guia_diseno_y_credenciales.txt      # Guía rápida para edición visual
├── resumen_proyecto_completo.md        # Esta documentación general
│
├── mediconnect-api/                    # --- SUBPROYECTO BACKEND (SPRING BOOT) ---
│   ├── Dockerfile                      # Compilación multi-stage y empaquetado Docker
│   ├── pom.xml                         # Dependencias Maven (Spring Boot, Security, JWT, Postgres)
│   ├── k8s/                            # Manifiestos de Despliegue en Kubernetes
│   │   ├── postgres-pvc.yaml           # Almacenamiento persistente de 5GB para Postgres
│   │   ├── postgres-configmap.yaml     # Variables de base de datos (DB, USER, PASSWORD)
│   │   ├── postgres-deployment.yaml    # Configuración de réplica única y probes de Postgres
│   │   ├── postgres-service.yaml       # Exposición interna (ClusterIP: 5432)
│   │   ├── api-configmap.yaml          # Ajustes del API (Puerto 8080, DB_URL, JWT secret)
│   │   ├── api-deployment.yaml         # Despliegue de la API, probes de salud y límites de memoria
│   │   ├── api-service.yaml            # Exposición externa (NodePort: 30251)
│   │   └── deploy.sh                   # Script automático de despliegue ordenado
│   └── src/main/
│       ├── java/com/mediconnect/api/
│       │   ├── controller/             # Controladores REST (Auth, User, Doctor, Cita, Receta, Historial)
│       │   ├── dto/                    # Clases DTO de transferencia agrupados por entidad
│       │   ├── entity/                 # Entidades JPA (User, Doctor, Paciente, Cita, Receta, Historial, etc.)
│       │   ├── repository/             # Interfaces de Spring Data JPA para PostgreSQL
│       │   ├── security/               # Configuración WebSecurity, filtros y proveedor de tokens JWT
│       │   └── service/                # Capa lógica de negocio del sistema
│       └── resources/
│           └── application-prod.yml    # Propiedades de producción y Dialectos JPA
│
└── mediconnect-android/                # --- SUBPROYECTO CLIENTE (ANDROID NATIVO) ---
    ├── build.gradle.kts                # Configuración de compilación raíz
    ├── settings.gradle.kts             # Nombre del proyecto e inclusión de módulos
    ├── local.properties                # Ruta local del Android SDK (/home/m4fb/Android/Sdk)
    ├── gradle.properties               # Ajustes JVM y ruta del JDK 17 portable de respaldo
    └── app/
        ├── build.gradle.kts            # Plugins (Kapt, Ksp, Hilt) y dependencias de UI y datos
        └── src/main/
            ├── AndroidManifest.xml     # Permisos (INTERNET, BIOMETRICS, CAMERA) y lanzador principal
            ├── java/com/mediconnect/app/
            │   ├── MediConnectApp.kt   # Clase de aplicación inicializadora de Hilt
            │   ├── MainActivity.kt     # Actividad contenedora única
            │   ├── di/
            │   │   └── AppModule.kt    # Módulo de provisión Hilt (Retrofit, Room, SharedPreferences)
            │   ├── data/
            │   │   ├── local/          # Base de datos Room (AppDatabase, Cache entities, DAOs)
            │   │   ├── remote/         # Capa de red (MediConnectApi, Dtos y AuthInterceptor)
            │   │   └── repository/     # Repositorio mediador con caché offline (patrón Repository)
            │   ├── ui/                 # Componentes gráficos de la app (ViewModels y Fragments)
            │   │   ├── auth/           # Login (con Huella digital) y Registro
            │   │   ├── dashboard/      # Panel de accesos con banner de estado Offline
            │   │   ├── doctors/        # Directorio de búsqueda y agendamiento
            │   │   ├── appointments/   # Mis Citas, cancelaciones y generador QR para Check-in
            │   │   ├── prescriptions/  # Recetas y medicamento detallado
            │   │   ├── history/        # Historial clínico e ingresos
            │   │   ├── notifications/  # Alertas internas del usuario
            │   │   └── profile/        # Edición de perfil y configuraciones de seguridad
            │   └── util/
            │       └── Constants.kt    # Constantes globales (BASE_URL, PREFS, KEYS)
            └── res/
                ├── layout/             # Diseños de pantalla XML (basados en LinearLayout)
                ├── navigation/
                │   └── nav_graph.xml   # Gráfico de flujo y transiciones de pantalla
                └── values/
                    ├── colors.xml      # Paleta de colores hexadecimales del aplicativo
                    ├── strings.xml     # Textos y etiquetas en español
                    └── themes.xml      # Tema visual primario de la app
```

---

## 📞 4. Catálogo de Endpoints de la API REST

Todos los endpoints (excepto `/auth/*` de inicio) requieren la cabecera `Authorization: Bearer <token>`.

| Módulo | Método HTTP | Ruta del Endpoint | Descripción |
| :--- | :--- | :--- | :--- |
| **Autenticación** | `POST` | `/api/auth/login` | Login del usuario. Retorna JWT access + refresh tokens. |
| | `POST` | `/api/auth/register` | Registro de nuevos usuarios (rol Paciente por defecto). |
| | `POST` | `/api/auth/refresh` | Renovación del token de acceso expirado. |
| **Usuarios** | `GET` | `/api/users/me` | Obtiene el perfil del usuario autenticado. |
| | `PUT` | `/api/users/me` | Actualiza la información del perfil del usuario. |
| | `PUT` | `/api/users/me/password` | Cambia la contraseña del usuario (valida contraseña anterior). |
| **Doctores** | `GET` | `/api/doctors` | Lista doctores con filtros opcionales de búsqueda y especialidad. |
| | `GET` | `/api/doctors/{id}` | Obtiene el perfil detallado de un doctor. |
| | `GET` | `/api/doctors/{id}/horarios-disponibles` | Lista horarios disponibles del doctor para una fecha (`?fecha=`). |
| | `GET` | `/api/doctors/{id}/valoraciones` | Obtiene las reseñas e historial de calificaciones del doctor. |
| | `POST` | `/api/doctors/{id}/valoraciones` | Añade una valoración (1 a 5 estrellas) de un paciente al doctor. |
| **Citas** | `GET` | `/api/citas` | Lista las citas del usuario (filtrables por estado `?estado=`). |
| | `POST` | `/api/citas` | Reserva una nueva cita con un doctor para una fecha y hora. |
| | `PUT` | `/api/citas/{id}/cancelar` | Cancela una cita (requiere motivo de cancelación). |
| | `POST` | `/api/citas/{id}/check-in` | Realiza el ingreso a la cita médica utilizando su código QR. |
| **Recetas** | `GET` | `/api/recetas/mis-recetas` | Obtiene el listado de recetas emitidas para el paciente autenticado. |
| | `POST` | `/api/recetas` | Crea una receta con detalles de medicamentos (Solo Doctor). |
| **Historial** | `GET` | `/api/historial` | Obtiene el historial clínico acumulado del paciente. |
| | `POST` | `/api/historial` | Añade un registro o antecedente al historial médico. |
| **Notificaciones** | `GET` | `/api/notificaciones` | Lista todas las notificaciones del usuario. |
| | `PUT` | `/api/notificaciones/{id}/leer` | Marca una notificación como leída. |
| **Admin** | `POST` | `/api/admin/doctores` | Registra a un nuevo usuario con rol DOCTOR (Solo Admin). |

---

## 🔑 5. Credenciales de Prueba Precargadas

| Rol | Email / Usuario | Contraseña | Detalles del Perfil |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@mediconnect.com` | `admin123` | Acceso total al registro de doctores |
| **DOCTOR** | `dr.martinez@mediconnect.com` | `password123` | Carlos Martínez (Cardiología) |
| **DOCTOR** | `dra.garcia@mediconnect.com` | `password123` | Ana García (Dermatología) |
| **DOCTOR** | `dr.rodriguez@mediconnect.com` | `password123` | Miguel Rodríguez (Pediatría) |
| **PACIENTE** | `paciente1@email.com` | `password123` | Juan Pérez (Historial clínico y citas cargadas) |
| **PACIENTE** | `paciente2@email.com` | `password123` | María López (Historial clínico y citas cargadas) |
| **PACIENTE** | `paciente3@email.com` | `password123` | Roberto Sánchez |
| **PACIENTE** | `paciente4@email.com` | `password123` | Laura Fernández |
| **PACIENTE** | `paciente5@email.com` | `password123` | Diego Morales |
