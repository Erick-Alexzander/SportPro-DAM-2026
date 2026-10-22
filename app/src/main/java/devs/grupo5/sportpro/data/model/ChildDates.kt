package devs.grupo5.sportpro.data.model

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
 * Indica si el texto corresponde a una fecha de nacimiento válida (ISO o dd/MM/yyyy).
 */
fun String.isValidBirthDate(): Boolean {
    val value = trim()
    if (value.isEmpty()) return false
    if (ISO_DATE.matches(value)) return true
    val match = LATIN_DATE.matchEntire(value) ?: return false
    val (day, month, year) = match.destructured
    val dayValue = day.toIntOrNull() ?: return false
    val monthValue = month.toIntOrNull() ?: return false
    val yearValue = year.toIntOrNull() ?: return false
    return dayValue in 1..31 && monthValue in 1..12 && yearValue in 2000..2030
}
