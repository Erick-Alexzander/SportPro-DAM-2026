package devs.grupo5.sportpro.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumbersTest {

    @Test
    fun `acepta un movil peruano en distintos formatos`() {
        assertEquals("987654321", "987654321".peruvianMobileDigits())
        assertEquals("987654321", "+51 987 654 321".peruvianMobileDigits())
        assertEquals("987654321", "51987654321".peruvianMobileDigits())
    }

    @Test
    fun `recupera numeros antiguos con otro codigo de pais`() {
        assertEquals("943210987", "+56 9 4321 0987".peruvianMobileDigits())
        assertTrue("+56 9 4321 0987".isValidPeruvianMobile())
    }

    @Test
    fun `rechaza numeros que no son moviles peruanos`() {
        assertNull("12345".peruvianMobileDigits())
        assertNull("123456789".peruvianMobileDigits())
        assertNull("".peruvianMobileDigits())
        assertFalse("876543210".isValidPeruvianMobile())
    }

    @Test
    fun `formatea el numero para mostrarlo`() {
        assertEquals("+51 943 210 987", "+56 9 4321 0987".formatPeruvianMobile())
        assertEquals("+51 987 654 321", "987654321".formatPeruvianMobile())
    }

    @Test
    fun `convierte el numero guardado a los nueve digitos del campo`() {
        assertEquals("943210987", "+51 943 210 987".nationalPeruvianMobile())
    }
}
