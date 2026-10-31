# Integración de Cuentas para Menores - Guía Backend Firebase

Este documento detalla la estructura implementada en el cliente Android para la creación de credenciales de acceso para jugadores menores de edad, preparada para ser conectada con el backend de Firebase (Cloud Functions y Firestore).

## 1. Pantalla que inicia la creación de acceso
La creación de la cuenta se inicia desde el perfil del apoderado:
**Ruta:** Perfil del apoderado -> Privacidad de mis hijos (`ParentRoutes.CHILDREN_PRIVACY_LIST`) -> Seleccionar menor -> Pantalla de Detalle (`ChildScreen.kt` en el Tab `ParentRoutes.CHILD`).
- El botón **"Crear acceso para el menor"** despliega el modal `CreateChildAccessDialog` (ubicado en `ParentDialogs.kt`).

## 2. Repositorio / Interfaz a implementar
Para mantener la arquitectura limpia, se debe implementar una interfaz en el backend/dominio para aprovisionar las cuentas de manera asíncrona:

```kotlin
enum class ProvisioningResult {
    LOADING, SUCCESS, PERMISSION_DENIED, ALREADY_EXISTS, INVALID_INPUT, NETWORK_ERROR, BACKEND_NOT_CONFIGURED
}

data class ProvisioningState(
    val result: ProvisioningResult = ProvisioningResult.LOADING,
    val message: String? = null
)

interface ChildAccountProvisioningRepository {
    suspend fun provisionChildAccount(
        childId: String,          // ID del LinkedChild / Player
        email: String,            // Correo elegido por el apoderado
        passwordRaw: String       // Contraseña elegida por el apoderado (sobre HTTPS a Cloud Functions)
    ): ProvisioningState
}
```

## 3. Identificadores que deben existir
- **`parentUid`:** El identificador en Firebase Auth de la sesión activa (el apoderado responsable).
- **`childId`:** El ID estático del menor (compartido en `LinkedChild` y el `Player` en la base de datos general de los clubes).
- **`childUid`:** El nuevo `uid` de Firebase Auth que se generará tras ejecutar la Cloud Function.

## 4. Identificación del apoderado autenticado
La UI actual ya recupera la sesión desde el `AuthRepository.currentUser` y la pasa por el `ParentNavGraph`. El llamado a la función debe enviar el `request.auth.uid` como validador primario.

## 5. Validaciones de vínculo en el servidor (Cloud Function)
Antes de llamar a `admin.auth().createUser(...)`, la Cloud Function debe validar:
1.  **El invocador es un apoderado válido:** Tiene rol de Apoderado y un token de sesión legítimo.
2.  **El vínculo existe:** El `childId` proporcionado está en la lista de menores a cargo del `parentUid` en Firestore.
3.  **Prevención de duplicidad:** El `childId` no tiene ya un `uid` de Auth asignado (por ejemplo revisando un campo `linkedUid` en el documento del menor).

## 6. Respuesta esperada por la aplicación
La Cloud Function debe devolver un estado compatible con `ProvisioningResult` (éxito, denegado, ya existe, etc.). Actualmente, la UI responde a estados simulados (mostrando una alerta naranja si falla, y cerrando el modal si tiene éxito).

## 7. Actualización de estado de la pantalla
Una vez implementado, en `CreateChildAccessDialog` se deberá reemplazar la alerta estática actual por la lógica de invocación a un ViewModel o Repository que modifique el estado de la UI (por ejemplo, mostrando un `CircularProgressIndicator` en `LOADING` y un mensaje de éxito con cierre en `SUCCESS`).

## 8. Asociación del UID de Firebase
El `childUid` creado por la función de admin SDK debe inyectarse en el documento principal del menor en Firestore (ej. `linkedAuthId: childUid`).
Cuando el menor inicie sesión en la pantalla normal de Login, el sistema lo identificará como `UserRole.PLAYER` y cruzará su `uid` para traer los datos del jugador original sin haber creado una ficha doble.

## 9. Evitar duplicar cuentas
En el frontend, el diseño usa el mismo objeto `LinkedChild` actual. El backend debe hacer *fail-fast* si el niño ya posee credenciales.

## 10. Reglas de protección de menores en Firestore
Las reglas de seguridad (`firestore.rules`) **deben** imponerse firmemente para evitar vulnerabilidades REST:
```javascript
match /players/{playerId} {
  // Los menores no pueden modificar su propia privacidad
  allow update: if (request.auth.uid == resource.data.linkedParentUid) || 
                   (request.auth.uid == resource.data.linkedAuthId && resource.data.age >= 18);
  
  // Los entrenadores solo pueden leer datos
  allow read: if request.auth.token.role == 'TRAINER' && resource.data.teamId in request.auth.token.teams;
}
```

## 11. Pruebas de integración faltantes
- Llamado POST real a la Cloud Function enviando un Token JWT válido.
- Intentar usar la Cloud Function desde la cuenta de un DT (debe arrojar `PERMISSION_DENIED`).
- Inicio de sesión con el correo/clave del menor recién creado.

## 12. Funcionalidades Listas vs Pendientes
**Listas (Frontend Android Android):**
- UI y estado modal de creación de cuenta (`CreateChildAccessDialog`).
- Validaciones síncronas de Regex y fortaleza de clave de 6 caracteres.
- UI bloqueada para el menor (Privacidad administrada únicamente por el padre).
- Persistencia de datos separada.
- Modelo relacional (Padre/Madre/Apoderado explícito).
- Restricciones peruanas de celular.

**Pendientes (Backend Firebase):**
- La Cloud Function en NodeJS (`createChildAccount`).
- Reglas de Firestore para el modelo `PrivacySettings`.
- El mapeo en el Login convencional para leer el `linkedAuthId` del menor.
