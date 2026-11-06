package devs.grupo5.sportpro.presentation.parent.calendar

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ParentEventType(val label: String) {
    ENTRENAMIENTO("Entrenamiento"),
    PARTIDO("Partido")
}

private val MONTH_ABBREVIATIONS = listOf(
    "ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
    "JUL", "AGO", "SET", "OCT", "NOV", "DIC"
)
private val EVENT_TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("es-PE"))

/**
 * Evento del calendario visto por el apoderado (entrenamientos y partidos del menor).
 * La fecha es real: día, mes y hora se derivan de [startAt] para que el calendario no quede
 * congelado en un mes del pasado.
 */
data class ParentEvent(
    val id: String,
    val type: ParentEventType,
    val startAt: LocalDateTime,
    val title: String,
    val subtitle: String,
    val place: String,
    val category: String = ""
) {
    val day: String
        get() = startAt.dayOfMonth.toString().padStart(2, '0')

    val month: String
        get() = MONTH_ABBREVIATIONS[startAt.monthValue - 1]

    val time: String
        get() = EVENT_TIME.format(startAt)

    fun isPast(now: LocalDateTime = LocalDateTime.now()): Boolean = startAt.isBefore(now)
}

internal val parentEventFilters = listOf("Todos", "Entrenamientos", "Partidos")

/**
 * Agenda de ejemplo calculada desde la fecha actual. Se mantiene como datos locales hasta
 * conectar el calendario con la colección `trainings` de Firestore.
 */
internal fun parentEventsFor(
    childCategory: String,
    today: LocalDate = LocalDate.now()
): List<ParentEvent> {
    val category = childCategory.ifBlank { "Sub-15" }

    fun at(daysFromToday: Long, hour: Int, minute: Int = 0): LocalDateTime =
        today.plusDays(daysFromToday).atTime(hour, minute)

    return listOf(
        ParentEvent(
            id = "pe1",
            type = ParentEventType.ENTRENAMIENTO,
            startAt = at(1, 15, 30),
            title = "Técnica individual: regate y conducción",
            subtitle = "$category · 90 min · 12 ejercicios",
            place = "Cancha Academia SportPro",
            category = category
        ),
        ParentEvent(
            id = "pe2",
            type = ParentEventType.PARTIDO,
            startAt = at(2, 10),
            title = "vs. Deportes Centro",
            subtitle = "$category · Local · Estadio Municipal",
            place = "Estadio Municipal",
            category = category
        ),
        ParentEvent(
            id = "pe3",
            type = ParentEventType.ENTRENAMIENTO,
            startAt = at(4, 16),
            title = "Trabajo táctico en posición defensiva",
            subtitle = "$category · 90 min · 4 ejercicios",
            place = "Cancha Academia SportPro",
            category = category
        ),
        ParentEvent(
            id = "pe4",
            type = ParentEventType.PARTIDO,
            startAt = at(7, 12),
            title = "vs. Club Deportivo Norte",
            subtitle = "$category · Visitante · Complejo Norte",
            place = "Complejo Norte",
            category = category
        )
    ).sortedBy { it.startAt }
}
