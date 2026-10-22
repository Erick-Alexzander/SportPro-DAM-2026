package devs.grupo5.sportpro.presentation.parent.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentEmerald
import devs.grupo5.sportpro.presentation.parent.theme.ParentGreenContainer
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentStatusBrown
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurfaceVariant
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary
import devs.grupo5.sportpro.presentation.parent.theme.ParentWarning

/**
 * Click con efecto de presión equivalente a `active:scale-95`.
 */
@Composable
fun Modifier.pressableClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "pressScale"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Métrica destacada del menor (goles, asistencias, partidos jugados, % asistencia).
 */
@Composable
fun ParentMetricTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ParentNeonGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = ParentTextSecondary,
                maxLines = 1
            )
        }
    }
}

/**
 * Insignia de estado de la mensualidad (Pendiente / Pagado).
 */
@Composable
fun ParentStatusPill(status: PaymentStatus) {
    val background = when (status) {
        PaymentStatus.PENDIENTE -> ParentStatusBrown
        PaymentStatus.PAGADO -> ParentGreenContainer
    }
    val content = when (status) {
        PaymentStatus.PENDIENTE -> ParentWarning
        PaymentStatus.PAGADO -> ParentEmerald
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .border(1.dp, content.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = content
        )
    }
}

/**
 * Estado vacío reutilizable del módulo del apoderado.
 */
@Composable
fun ParentEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ParentGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ParentNeonGreen,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                fontSize = 13.sp,
                color = ParentTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParentNeonGreen,
                        contentColor = Color(0xFF00210B)
                    )
                ) {
                    Text(
                        text = actionLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Fila de detalle con etiqueta y valor.
 */
@Composable
fun ParentDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = ParentTextPrimary,
    onValueClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = ParentTextMuted
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            modifier = if (onValueClick != null) {
                Modifier.pressableClick(onClick = onValueClick)
            } else {
                Modifier
            }
        )
        if (trailing != null) {
            Spacer(modifier = Modifier.size(6.dp))
            trailing()
        }
    }
}

/**
 * Separador oscuro sutil entre filas de datos de una tarjeta.
 */
@Composable
fun ParentDetailDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        color = ParentBorder.copy(alpha = 0.7f),
        thickness = 1.dp
    )
}

/**
 * Etiqueta redondeada (relación del apoderado, estados informativos).
 */
@Composable
fun ParentBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ParentNeonGreen
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Selector horizontal del menor visible cuando la cuenta tiene más de un menor vinculado.
 */
@Composable
fun ParentChildSelector(
    children: List<LinkedChild>,
    selectedChildId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = children, key = { it.id }) { child ->
            val isSelected = child.id == selectedChildId
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) ParentGreenContainer else ParentSurface)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) ParentNeonGreen else ParentBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .pressableClick { onSelect(child.id) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ParentNeonGreen.copy(alpha = 0.18f) else ParentSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = child.initials,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) ParentNeonGreen else ParentTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = child.fullName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) ParentTextPrimary else ParentTextSecondary
                    )
                    Text(
                        text = "${child.category} · #${child.jerseyNumber}",
                        fontSize = 11.sp,
                        color = ParentTextMuted
                    )
                }
            }
        }
    }
}
