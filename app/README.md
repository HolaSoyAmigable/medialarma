# MediAlarma (app Android)

App móvil para registrar medicamentos. Se conecta a la API REST de MediAlarma.

## Funciones
- Registro, inicio y cierre de sesión (la sesión persiste al reabrir la app)
- Crear, listar, ver, editar y eliminar medicamentos

## Arquitectura
- UI con Jetpack Compose y patrón MVVM (StateFlow)
- Capa de datos: Repository, DTO y Mapper
- Inyección de dependencias manual con AppContainer
- Retrofit + OkHttp + kotlinx.serialization, DataStore para la sesión

## Cómo ejecutarla
1. Abrir el proyecto en Android Studio y sincronizar Gradle.
2. La URL de la API está en `app/build.gradle.kts` (`API_BASE_URL`).
3. Ejecutar en un emulador o teléfono con internet.