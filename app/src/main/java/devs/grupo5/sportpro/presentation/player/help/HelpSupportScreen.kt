package devs.grupo5.sportpro.presentation.player.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private data class FaqItem(
    val id: String,
    val question: String,
    val answer: String
)

private val faqs = listOf(
    FaqItem(
        id = "faq1",
        question = "¿Cómo confirmo mi asistencia a un partido o entrenamiento?",
        answer = "Abre la convocatoria desde Inicio o desde la notificación y pulsa \"Disponible\" o " +
            "\"No disponible\" antes de la fecha límite indicada."
    ),
    FaqItem(
        id = "faq2",
        question = "¿Quién puede ver mi información de contacto?",
        answer = "Depende de tu configuración en Perfil › Privacidad. Si el campo está en \"Solo autorizados\", " +
            "únicamente el administrador y tu apoderado registrado pueden verlo."
    ),
    FaqItem(
        id = "faq3",
        question = "¿Cómo recupero mi contraseña?",
        answer = "En la pantalla de inicio de sesión usa la opción \"¿Olvidaste tu contraseña?\" o contacta a " +
            "soporte de SportPro para restablecerla."
    )
)

@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit = {},
    onContactSupportClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            bottomBar = {
                PlayerBottomNavigationBar(
                    currentTab = PlayerTab.PROFILE,
                    onTabSelected = onTabSelected
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    HelpTopBar(onBackClick = onBackClick)
                }

                item {
                    Text(
                        text = "Preguntas frecuentes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(faqs, key = { it.id }) { faq ->
                    FaqCard(faq = faq)
                }

                item {
                    Text(
                        text = "Contacto directo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    ContactSupportCard(onContactSupportClick = onContactSupportClick)
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun HelpTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = PlayerTextPrimary
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Ayuda y soporte",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerTextPrimary
        )
    }
}

@Composable
private fun FaqCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = PlayerNeonGreen,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(if (expanded) 180f else 0f)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(PlayerBorder)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 13.sp,
                        color = PlayerTextSecondary,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactSupportCard(onContactSupportClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PlayerGreenContainer)
                        .border(1.dp, PlayerBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎧", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Soporte SportPro",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Te respondemos en menos de 24 horas hábiles.",
                        fontSize = 12.sp,
                        color = PlayerTextMuted,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onContactSupportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlayerNeonGreen,
                    contentColor = Color(0xFF00210B)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Color(0xFF00210B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Contactar con soporte de SportPro",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00210B)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun HelpSupportScreenPreview() {
    HelpSupportScreen()
}
