package devs.grupo5.sportpro

import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

/**
 * Variante de debug: redirige los SDK de Firebase a los emuladores locales.
 *
 * - Emulador Android: usa 10.0.2.2 (alias del host de la máquina).
 * - Dispositivo físico: usa 127.0.0.1 y requiere `adb reverse`.
 */
object FirebaseEmulator {
    private const val AUTH_PORT = 9099
    private const val FIRESTORE_PORT = 8080
    private const val FUNCTIONS_PORT = 5002

    fun configureIfDebug() {
        // TEMPORAL: desactivado para probar contra el proyecto real.
        // Para volver a emuladores, elimina este return.
        return

        val host = emulatorHost()
        FirebaseAuth.getInstance().useEmulator(host, AUTH_PORT)
        FirebaseFirestore.getInstance().useEmulator(host, FIRESTORE_PORT)
        FirebaseFunctions.getInstance().useEmulator(host, FUNCTIONS_PORT)
    }

    private fun emulatorHost(): String {
        val isAndroidEmulator = Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
            Build.PRODUCT.contains("sdk_gphone") ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu")

        return if (isAndroidEmulator) "10.0.2.2" else "127.0.0.1"
    }
}
