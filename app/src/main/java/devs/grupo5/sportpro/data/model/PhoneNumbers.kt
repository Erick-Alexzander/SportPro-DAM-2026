package devs.grupo5.sportpro.data.model

/** Código de país que usa la academia en los formularios. */
const val PERU_COUNTRY_CODE = "+51"

private val PERU_MOBILE = Regex("""^9\d{8}$""")

/**
 * Extrae el número móvil peruano de 9 dígitos a partir de cualquier formato guardado.
 *
 * Acepta:
 * - `987654321`
 * - `+51 987 654 321`
 * - `51987654321`
 * - números antiguos con otro código de país (por ejemplo `+56 9 4321 0987`),
 *   tomando los últimos 9 dígitos si empiezan con 9.
 *
 * Devuelve `null` si no se puede interpretar como móvil peruano.
 */
fun String.peruvianMobileDigits(): String? {
    val digits = filter(Char::isDigit)
    if (digits.isEmpty()) return null

    val candidates = buildList {
        if (digits.length == 9) add(digits)
        if (digits.length == 11 && digits.startsWith("51")) add(digits.drop(2))
        if (digits.length > 9) add(digits.takeLast(9))
    }

    return candidates.firstOrNull { PERU_MOBILE.matches(it) }
}

fun String.isValidPeruvianMobile(): Boolean = peruvianMobileDigits() != null

/** Número nacional de 9 dígitos listo para un campo de texto, o cadena vacía. */
fun String.nationalPeruvianMobile(): String = peruvianMobileDigits().orEmpty()

/** Formato visible uniforme: `+51 987 654 321`. Si no es válido, devuelve el texto original. */
fun String.formatPeruvianMobile(): String {
    val digits = peruvianMobileDigits() ?: return trim()
    return "$PERU_COUNTRY_CODE ${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6, 9)}"
}
