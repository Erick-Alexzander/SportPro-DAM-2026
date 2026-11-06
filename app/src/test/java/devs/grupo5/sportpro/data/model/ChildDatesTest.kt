package devs.grupo5.sportpro.data.model

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChildDatesTest {

    private val today = LocalDate.of(2026, 11, 5)

    @Test
    fun `calcula la edad cumplida`() {
        assertEquals(17, "2009-06-22".ageInYears(today))
        assertEquals(15, "2010-11-06".ageInYears(today))
        assertEquals(0, "2026-11-05".ageInYears(today))
    }

    @Test
    fun `una fecha futura no tiene edad`() {
        assertNull("2027-01-01".ageInYears(today))
    }

    @Test
    fun `acepta menores de 0 a 17 anos`() {
        assertTrue("2026-11-05".isValidMinorBirthDate(today))
        assertTrue("2009-06-22".isValidMinorBirthDate(today))
        assertTrue("05/11/2015".isValidMinorBirthDate(today))
    }

    @Test
    fun `rechaza a quien ya cumplio 18 anos`() {
        assertFalse("2008-11-05".isValidMinorBirthDate(today))
        assertFalse("2000-01-01".isValidMinorBirthDate(today))
    }

    @Test
    fun `rechaza fechas inexistentes o en el futuro`() {
        assertFalse("2010-02-31".isValidMinorBirthDate(today))
        assertFalse("2026-11-06".isValidMinorBirthDate(today))
        assertFalse("".isValidMinorBirthDate(today))
    }

    @Test
    fun `normaliza la fecha latina a ISO`() {
        assertEquals("2015-11-05", "05/11/2015".normalizeBirthDate())
        assertEquals("2015-11-05", "2015-11-05".normalizeBirthDate())
    }
}
