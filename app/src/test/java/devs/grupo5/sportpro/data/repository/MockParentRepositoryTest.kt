package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.LinkedChild
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MockParentRepositoryTest {

    private val repository = MockParentRepository.getInstance(null)

    @Before
    fun setUp() {
        repository.clear()
    }

    private fun child(
        id: String = "lc_new",
        firstName: String = "Felipe",
        lastName: String = "Muñoz",
        birthDate: String = "2009-06-22"
    ) = LinkedChild(id = id, firstName = firstName, lastName = lastName, birthDate = birthDate)

    @Test
    fun `registrar un menor nuevo devuelve Success`() {
        val result = repository.linkChild(child())

        assertTrue(result is ChildLinkResult.Success)
        assertEquals(1, repository.linkedChildren.value.size)
    }

    @Test
    fun `registrar dos veces el mismo menor devuelve Duplicate`() {
        repository.linkChild(child())

        val result = repository.linkChild(child(id = "lc_other"))

        assertTrue(result is ChildLinkResult.Duplicate)
        assertEquals(1, repository.linkedChildren.value.size)
    }

    @Test
    fun `la duplicidad ignora mayusculas espacios y formato de fecha`() {
        repository.linkChild(child())

        val result = repository.linkChild(
            child(id = "lc_other", firstName = "  felipe ", lastName = "MUÑOZ", birthDate = "22/06/2009")
        )

        assertTrue(result is ChildLinkResult.Duplicate)
        assertEquals(1, repository.linkedChildren.value.size)
    }

    @Test
    fun `otra fecha de nacimiento no se considera duplicado`() {
        repository.linkChild(child())

        val result = repository.linkChild(child(id = "lc_other", birthDate = "2010-01-15"))

        assertTrue(result is ChildLinkResult.Success)
        assertEquals(2, repository.linkedChildren.value.size)
    }

    @Test
    fun `editar el mismo menor no se considera duplicado`() {
        repository.linkChild(child())

        val result = repository.linkChild(child(firstName = "Felipe A."))

        assertTrue(result is ChildLinkResult.Success)
        assertEquals(1, repository.linkedChildren.value.size)
        assertEquals("Felipe A.", repository.linkedChildren.value.first().firstName)
    }
}
