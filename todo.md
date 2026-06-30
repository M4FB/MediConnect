# TODO - Mejoras y Nuevas Características para MediConnect

Lista de tareas técnicas para implementar en la aplicación Android y el backend.

## 1. Entrada de Fecha y Hora con Pickers (Android)
*   En `AppointmentCreateFragment`, cambiar los campos de texto `etApptDate` y `etApptTime` por diálogos nativos del sistema.
*   Implementar `DatePickerDialog` para la fecha y `TimePickerDialog` para la hora.
*   Asegurar que el formato final concatenado siempre sea ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss`) para evitar errores 400 en el servidor.

## 2. Refresco de Token JWT Silencioso (Android)
*   En `AuthInterceptor`, interceptar el error 401.
*   En vez de desloguear directamente, intentar una llamada síncrona a `/api/auth/refresh` usando el `refresh_token` almacenado.
*   Si la llamada tiene éxito, guardar los nuevos tokens en `EncryptedSharedPreferences` y reintentar la petición original.
*   Si el refresh token también expiró, proceder con el deslogueo y redirección a la pantalla de login.

## 3. Validaciones Locales de Formularios (Android)
*   **Login**: Validar que el campo sea un email estructurado y la contraseña no esté vacía antes de enviar la petición.
*   **Registro**:
    *   Comprobar que el email sea válido.
    *   Comprobar que las contraseñas coincidan y tengan una longitud mínima (ej. 6 caracteres).
    *   Asegurar que los campos obligatorios no estén vacíos.
*   **Crear Cita**: Validar que se haya seleccionado un doctor y que la fecha/hora seleccionada sea posterior a la actual.

## 4. Descarga de Receta Médica en PDF (Android)
*   En `PrescriptionDetailFragment`, agregar un botón de descarga.
*   Al presionarlo, generar un PDF localmente usando la API nativa de Android (`PdfDocument`) o una biblioteca ligera.
*   El PDF debe contener el membrete de la clínica, los datos del doctor, paciente, diagnóstico, medicamentos prescritos y dosis.
*   Solicitar y manejar permisos de almacenamiento para versiones anteriores de Android si aplica, o guardarlo directamente en el directorio público de descargas usando `MediaStore` (para APIs modernas).

## 5. Alarmas Locales de Medicación (Android)
*   En base a las recetas activas del paciente, permitir al usuario programar alarmas de recordatorio en el móvil.
*   Usar `AlarmManager` en conjunto con un `BroadcastReceiver` para disparar notificaciones locales a la hora exacta de la toma de cada medicamento.

## 6. Sincronización Diferida Offline con WorkManager (Android)
*   Crear una tabla intermedia de "peticiones pendientes" en Room para registrar acciones de escritura (como agendar una cita o cancelar una cita) cuando no haya conexión a internet.
*   Implementar un `Worker` que se ejecute en segundo plano cuando el dispositivo recupere conexión a internet estable.
*   El Worker debe procesar secuencialmente la cola de peticiones pendientes enviándolas al servidor y eliminándolas de Room una vez confirmadas.
