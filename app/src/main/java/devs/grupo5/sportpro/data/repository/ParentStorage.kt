package devs.grupo5.sportpro.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import devs.grupo5.sportpro.data.model.ChildStats
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.GuardianTitle
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.data.model.PaymentStatus
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persistencia local del módulo del apoderado. Equivale al `localStorage` del prototipo web:
 * conserva los datos de los menores vinculados (incluidos los datos físicos y de contacto), los del
 * apoderado responsable y el estado de la mensualidad entre sesiones de la aplicación.
 */
class ParentStorage(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadGuardian(): GuardianProfile? {
        val raw = prefs.getString(KEY_GUARDIAN, null) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            GuardianProfile(
                firstName = json.optString("firstName"),
                lastName = json.optString("lastName"),
                email = json.optString("email"),
                title = GuardianTitle.entries.firstOrNull { it.name == json.optString("title") }
                    ?: GuardianTitle.APODERADO
            )
        }.getOrNull()
    }

    fun saveGuardian(profile: GuardianProfile) {
        val json = JSONObject().apply {
            put("firstName", profile.firstName)
            put("lastName", profile.lastName)
            put("email", profile.email)
            put("title", profile.title.name)
        }
        prefs.edit { putString(KEY_GUARDIAN, json.toString()) }
    }

    /**
     * Menores vinculados. Migra el registro único de versiones anteriores si aún existe.
     */
    fun loadChildren(): List<LinkedChild> {
        val raw = prefs.getString(KEY_CHILDREN, null)
        if (raw != null) {
            return runCatching {
                val array = JSONArray(raw)
                (0 until array.length()).mapNotNull { index ->
                    array.optJSONObject(index)?.let { childFromJson(it) }
                }
            }.getOrDefault(emptyList())
        }

        val legacy = prefs.getString(KEY_CHILD, null) ?: return emptyList()
        return runCatching {
            val child = childFromJson(JSONObject(legacy))
            if (child != null) listOf(child) else emptyList()
        }.getOrDefault(emptyList())
    }

    fun saveChildren(children: List<LinkedChild>) {
        val array = JSONArray()
        children.forEach { array.put(childToJson(it)) }
        prefs.edit {
            putString(KEY_CHILDREN, array.toString())
            remove(KEY_CHILD)
        }
    }

    fun loadSelectedChildId(): String? {
        val id = prefs.getString(KEY_SELECTED_CHILD, null) ?: return null
        return id.ifBlank { null }
    }

    fun saveSelectedChildId(id: String?) {
        prefs.edit {
            if (id.isNullOrBlank()) remove(KEY_SELECTED_CHILD) else putString(KEY_SELECTED_CHILD, id)
        }
    }

    fun loadPayment(): MonthlyPayment? {
        val raw = prefs.getString(KEY_PAYMENT, null) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            MonthlyPayment(
                period = json.optString("period"),
                status = PaymentStatus.entries.firstOrNull { it.name == json.optString("status") }
                    ?: PaymentStatus.PENDIENTE,
                receiptRegistered = json.optBoolean("receiptRegistered")
            )
        }.getOrNull()
    }

    fun savePayment(payment: MonthlyPayment) {
        val json = JSONObject().apply {
            put("period", payment.period)
            put("status", payment.status.name)
            put("receiptRegistered", payment.receiptRegistered)
        }
        prefs.edit { putString(KEY_PAYMENT, json.toString()) }
    }

    private fun childFromJson(json: JSONObject): LinkedChild? {
        val stats = json.optJSONObject("stats")
        return LinkedChild(
            id = json.optString("id"),
            firstName = json.optString("firstName"),
            lastName = json.optString("lastName"),
            category = json.optString("category"),
            age = json.optInt("age"),
            jerseyNumber = json.optInt("jerseyNumber"),
            position = json.optString("position"),
            birthDate = json.optString("birthDate"),
            heightCm = json.optInt("heightCm"),
            weightKg = json.optInt("weightKg"),
            phone = json.optString("phone"),
            emergencyPhone = json.optString("emergencyPhone"),
            status = json.optString("status").ifBlank { "Activo" },
            stats = ChildStats(
                goals = stats?.optInt("goals") ?: 0,
                assists = stats?.optInt("assists") ?: 0,
                matchesPlayed = stats?.optInt("matchesPlayed") ?: 0,
                attendancePercent = stats?.optInt("attendancePercent") ?: 0
            )
        )
    }

    private fun childToJson(child: LinkedChild): JSONObject = JSONObject().apply {
        put("id", child.id)
        put("firstName", child.firstName)
        put("lastName", child.lastName)
        put("category", child.category)
        put("age", child.age)
        put("jerseyNumber", child.jerseyNumber)
        put("position", child.position)
        put("birthDate", child.birthDate)
        put("heightCm", child.heightCm)
        put("weightKg", child.weightKg)
        put("phone", child.phone)
        put("emergencyPhone", child.emergencyPhone)
        put("status", child.status)
        put(
            "stats",
            JSONObject().apply {
                put("goals", child.stats.goals)
                put("assists", child.stats.assists)
                put("matchesPlayed", child.stats.matchesPlayed)
                put("attendancePercent", child.stats.attendancePercent)
            }
        )
    }

    private companion object {
        const val PREFS_NAME = "sportpro_parent_module"
        const val KEY_GUARDIAN = "guardian"
        const val KEY_CHILD = "child"
        const val KEY_CHILDREN = "children"
        const val KEY_SELECTED_CHILD = "selectedChildId"
        const val KEY_PAYMENT = "payment"
    }
}
