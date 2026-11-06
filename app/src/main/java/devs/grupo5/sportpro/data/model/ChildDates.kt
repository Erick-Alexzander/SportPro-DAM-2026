package devs.grupo5.sportpro.data.model

import java.time.LocalDate
import java.time.Period

/** Edad máxima permitida para una cuenta de menor (exclusiva). */
const val MINOR_MAX_AGE = 18

private val ISO_DATE = Regex("""^\d{4}-\d{2}-\d{2}$""")
private val LATIN_DATE = Regex("""^(\d{2})/(\d{2})/(\d{4})$""")

/**
 * Normaliza una fecha de nacimiento al formato ISO (yyyy-MM-dd).
 * Acepta el formato ISO y el formato latino (dd/MM/yyyy) usado en los formularios.
 */
fun String.normalizeBirthDate(): String {
    val value = trim()
    if (value.isEmpty()) return ""
    if (ISO_DATE.matches(value)) return value
    val match = LATIN_DATE.matchEntire(value) ?: return value
    val (day, month, year) = match.destructured
    return "$year-$month-$day"
}

/**
 * Convierte el texto en una fecha real. Devuelve `null` si el formato no es válido
 * o si la fecha no existe (por ejemplo, 31/02/2010).
 */
fun String.parseBirthDateOrNull(): LocalDate? {
    val value = trim()
    if (value.isEmpty()) return null
    return runCatching {
        when {
            ISO_DATE.matches(value) -> LocalDate.parse(value)
            LATIN_DATE.matches(value) -> {
                val (day, month, year) = LATIN_DATE.matchEntire(value)!!.destructured
                LocalDate.of(year.toInt(), month.toInt(), day.toInt())
            }
            else -> null
        }
    }.getOrNull()
}

/** Edad cumplida en años, o `null` si la fecha no es válida o está en el futuro. */
fun String.ageInYears(today: LocalDate = LocalDate.now()): Int? {
    val birthDate = parseBirthDateOrNull() ?: return null
    if (birthDate.isAfter(today)) return null
    return Period.between(birthDate, today).years
}

/**
 * Indica si el texto corresponde a una fecha de nacimiento válida (ISO o dd/MM/yyyy),
 * no futura y con un año razonable.
 */
fun String.isValidBirthDate(today: LocalDate = LocalDate.now()): Boolean {
    val birthDate = parseBirthDateOrNull() ?: return false
    return !birthDate.isAfter(today) && birthDate.year >= 1900
}

/**
 * Indica si la fecha corresponde a un menor de edad (0 a 17 años).
 * Es la validación que debe usar el módulo del apoderado.
 */
fun String.isValidMinorBirthDate(today: LocalDate = LocalDate.now()): Boolean {
    val age = ageInYears(today) ?: return false
    return age in 0 until MINOR_MAX_AGE
}
