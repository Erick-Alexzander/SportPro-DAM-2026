package devs.grupo5.sportpro.data.model

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RelativeTimeTest {

    private val now = LocalDateTime.of(2026, 11, 5, 12, 0)

    @Test
    fun `muestra minutos y horas transcurridas`() {
        assertEquals("Hace 5 min", now.minusMinutes(5).relativeLabel(now))
        assertEquals("Hace 2 h", now.minusHours(2).relativeLabel(now))
    }

    @Test
    fun `muestra ayer y dias transcurridos`() {
        assertEquals("Ayer", now.minusDays(1).relativeLabel(now))
        assertEquals("Hace 3 días", now.minusDays(3).relativeLabel(now))
    }

    @Test
    fun `una fecha futura se muestra como ahora`() {
        assertEquals("Ahora", now.plusHours(1).relativeLabel(now))
    }

    @Test
    fun `una fecha antigua usa la fecha completa`() {
        val label = now.minusDays(40).relativeLabel(now)
        assertTrue(label.contains("2026"))
    }
}
