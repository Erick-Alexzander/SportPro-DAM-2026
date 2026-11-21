package devs.grupo5.sportpro.data.model

/**
 * Compara la identidad de dos menores por nombre, apellido y fecha de nacimiento.
 */
fun LinkedChild.hasSameIdentityAs(other: LinkedChild): Boolean =
    firstName.trim().equals(other.firstName.trim(), ignoreCase = true) &&
        lastName.trim().equals(other.lastName.trim(), ignoreCase = true) &&
        birthDateIso == other.birthDateIso

/**
 * Indica si ya existe un menor con la misma identidad, excluyendo un id concreto
 * para no tratar la edición del propio menor como un duplicado.
 */
fun List<LinkedChild>.hasDuplicateOf(child: LinkedChild, excludedId: String? = null): Boolean =
    any { existing -> existing.id != excludedId && existing.hasSameIdentityAs(child) }
