package devs.grupo5.sportpro.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa la vinculación final y aprobada entre una cuenta de usuario
 * y una ficha deportiva.
 * Este mismo modelo se usa tanto en playerAccountLinks/{playerId}
 * como en userPlayerLinks/{userUid} para garantizar integridad relacional.
 */
data class PlayerAccountLink(
    val playerId: String = "",
    val userUid: String = "",
    val trainerUid: String = "",
    val inviteId: String = "",
    @ServerTimestamp
    val linkedAt: Timestamp? = null
)
