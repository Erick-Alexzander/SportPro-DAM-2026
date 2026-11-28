package devs.grupo5.sportpro.data.model

/**
 * Documento `parentAccess/{parentUid}`.
 *
 * Lo escribe únicamente la Cloud Function de vinculación (Admin SDK), nunca el cliente.
 * Debe existir incluso con listas vacías para que las reglas puedan comprobar el acceso.
 */
data class ParentAccess(
    val parentUid: String = "",
    val playerIds: List<String> = emptyList(),
    val teamIds: List<String> = emptyList()
)
