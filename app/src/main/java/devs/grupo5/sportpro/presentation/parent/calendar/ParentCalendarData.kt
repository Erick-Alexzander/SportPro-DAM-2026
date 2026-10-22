package devs.grupo5.sportpro.presentation.parent.calendar

enum class ParentEventType(val label: String) {
    ENTRENAMIENTO("Entrenamiento"),
    PARTIDO("Partido")
}

/**
 * Evento del calendario visto por el apoderado (entrenamientos y partidos del menor).
 */
data class ParentEvent(
    val id: String,
    val type: ParentEventType,
    val day: String,
    val month: String,
    val time: String,
    val title: String,
    val subtitle: String,
    val place: String
)

internal val parentEventFilters = listOf("Todos", "Entrenamientos", "Partidos")

internal val parentEvents = listOf(
    ParentEvent(
        id = "pe1",
        type = ParentEventType.ENTRENAMIENTO,
        day = "04",
        month = "NOV",
        time = "15:30",
        title = "Técnica individual: regate y conducción",
        subtitle = "Sub-15 · 90 min · 12 ejercicios",
        place = "Cancha Academia SportPro"
    ),
    ParentEvent(
        id = "pe2",
        type = ParentEventType.PARTIDO,
        day = "09",
        month = "NOV",
        time = "10:00",
        title = "vs. Deportes Centro",
        subtitle = "Sub-15 · Local · Estadio Municipal",
        place = "Estadio Municipal"
    ),
    ParentEvent(
        id = "pe3",
        type = ParentEventType.ENTRENAMIENTO,
        day = "06",
        month = "NOV",
        time = "16:00",
        title = "Trabajo táctico en posición defensiva",
        subtitle = "Sub-15 · 90 min · 4 ejercicios",
        place = "Cancha Academia SportPro"
    ),
    ParentEvent(
        id = "pe4",
        type = ParentEventType.PARTIDO,
        day = "16",
        month = "NOV",
        time = "12:00",
        title = "vs. Club Deportivo Norte",
        subtitle = "Sub-15 · Visitante · Complejo Norte",
        place = "Complejo Norte"
    )
)
