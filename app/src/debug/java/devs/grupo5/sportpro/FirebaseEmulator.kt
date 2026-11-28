package devs.grupo5.sportpro

/**
 * En ejecuciones normales de la aplicación (Debug y Release), se conecta directamente
 * al proyecto de Firebase en la nube (proyecto-72df1).
 *
 * La configuración de emuladores para pruebas de reglas de Firestore se mantiene
 * completamente aislada en la suite de pruebas automatizadas.
 */
object FirebaseEmulator {
    fun configureIfDebug() {
        // Desactivado en la ejecución normal de la aplicación para utilizar
        // el proyecto real de Firebase en la nube en lugar de emuladores locales.
    }
}
