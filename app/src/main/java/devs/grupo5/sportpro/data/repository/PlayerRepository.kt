package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PlayerRepository {
    val players: StateFlow<List<Player>>
    fun getPlayerById(id: String): Player?
    fun getPlayersByIds(ids: List<String>): List<Player>
    fun addPlayer(player: Player)
    fun updatePlayer(player: Player)
}

class MockPlayerRepository private constructor() : PlayerRepository {

    private val initialPlayers = listOf(
        Player(
            id = "p1",
            fullName = "Carlos Mendoza",
            category = "Primera",
            position = "DEL",
            jerseyNumber = 9,
            age = 24,
            status = "Activo",
            dateOfBirth = "12/04/2000",
            contactEmail = "carlos.mendoza@sportpro.com",
            dominantFoot = "Derecho",
            size = "L",
            weight = 75.0,
            parentName = "Roberto Mendoza",
            parentRelation = "Padre",
            parentPhone = "+56911112222",
            emergencyPhone = "+56911113333"
        ),
        Player(
            id = "p2",
            fullName = "Diego Herrera",
            category = "Primera",
            position = "MED",
            jerseyNumber = 10,
            age = 22,
            status = "Activo",
            dateOfBirth = "05/08/2002",
            contactEmail = "diego.herrera@sportpro.com",
            dominantFoot = "Derecho",
            size = "M",
            weight = 70.0,
            parentName = "Juan Herrera",
            parentRelation = "Padre",
            parentPhone = "+56922223333",
            emergencyPhone = "+56922224444"
        ),
        Player(
            id = "p3",
            fullName = "Sebastián Torres",
            category = "Primera",
            position = "DEF",
            jerseyNumber = 4,
            age = 25,
            status = "Activo",
            dateOfBirth = "19/11/1999",
            contactEmail = "sebastian.torres@sportpro.com",
            dominantFoot = "Izquierdo",
            size = "L",
            weight = 80.0,
            parentName = "Carmen Torres",
            parentRelation = "Madre",
            parentPhone = "+56933334444",
            emergencyPhone = "+56933335555"
        ),
        Player(
            id = "p4",
            fullName = "Matías Rojas",
            category = "Sub-15",
            position = "POR",
            jerseyNumber = 1,
            age = 15,
            status = "Activo",
            dateOfBirth = "10/01/2009",
            contactEmail = "matias.rojas@sportpro.com",
            dominantFoot = "Derecho",
            size = "M",
            weight = 65.0,
            parentName = "Gonzalo Rojas",
            parentRelation = "Padre",
            parentPhone = "+56944445555",
            emergencyPhone = "+56944446666"
        ),
        Player(
            id = "p5",
            fullName = "Felipe Muñoz",
            category = "Sub-15",
            position = "MED",
            jerseyNumber = 8,
            age = 14,
            status = "Activo",
            dateOfBirth = "22/06/2010",
            contactEmail = "felipe.munoz@sportpro.com",
            dominantFoot = "Ambidiestro",
            size = "S",
            weight = 58.0,
            parentName = "Patricia Muñoz",
            parentRelation = "Madre",
            parentPhone = "+56955556666",
            emergencyPhone = "+56955557777"
        ),
        Player(
            id = "p6",
            fullName = "Nicolás Vega",
            category = "Sub-15",
            position = "DEL",
            jerseyNumber = 11,
            age = 15,
            status = "Lesionado",
            dateOfBirth = "03/03/2009",
            contactEmail = "nicolas.vega@sportpro.com",
            dominantFoot = "Derecho",
            size = "M",
            weight = 62.0,
            parentName = "Esteban Vega",
            parentRelation = "Padre",
            parentPhone = "+56966667777",
            emergencyPhone = "+56966668888"
        ),
        Player(
            id = "p7",
            fullName = "Andrés López",
            category = "Sub-10",
            position = "DEF",
            jerseyNumber = 2,
            age = 10,
            status = "Activo",
            dateOfBirth = "14/07/2014",
            contactEmail = "andres.lopez@sportpro.com",
            dominantFoot = "Derecho",
            size = "S",
            weight = 38.0,
            parentName = "Laura López",
            parentRelation = "Madre",
            parentPhone = "+56977778888",
            emergencyPhone = "+56977779999"
        ),
        Player(
            id = "p8",
            fullName = "Ignacio Soto",
            category = "Sub-10",
            position = "DEL",
            jerseyNumber = 7,
            age = 9,
            status = "Activo",
            dateOfBirth = "30/09/2015",
            contactEmail = "ignacio.soto@sportpro.com",
            dominantFoot = "Izquierdo",
            size = "XS",
            weight = 32.0,
            parentName = "Mauricio Soto",
            parentRelation = "Padre",
            parentPhone = "+56988889999",
            emergencyPhone = "+56988880000"
        )
    )

    private val _players = MutableStateFlow(initialPlayers)
    override val players: StateFlow<List<Player>> = _players.asStateFlow()

    override fun getPlayerById(id: String): Player? {
        return _players.value.find { it.id == id }
    }

    override fun getPlayersByIds(ids: List<String>): List<Player> {
        return _players.value.filter { it.id in ids }
    }

    override fun addPlayer(player: Player) {
        val newPlayer = if (player.id.isEmpty()) {
            player.copy(id = "p_${System.currentTimeMillis()}")
        } else {
            player
        }
        _players.value = _players.value + newPlayer
    }

    override fun updatePlayer(player: Player) {
        _players.value = _players.value.map {
            if (it.id == player.id) player else it
        }
    }

    companion object {
        val instance: MockPlayerRepository by lazy { MockPlayerRepository() }
    }
}
