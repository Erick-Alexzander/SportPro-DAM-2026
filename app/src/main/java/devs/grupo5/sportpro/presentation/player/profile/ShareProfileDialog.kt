package devs.grupo5.sportpro.presentation.player.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary

private const val PROFILE_LINK_PREFIX = "sportpro://player/profile/"

@Composable
fun ShareProfileDialog(
    uid: String,
    fullName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val link = remember(uid) { "$PROFILE_LINK_PREFIX$uid" }
    val qrBitmap = remember(link, uid) {
        if (uid.isBlank()) null else generateQrBitmap(link, 600)
    }

    val shareProfile = {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Perfil de $fullName en SportPro")
            putExtra(
                Intent.EXTRA_TEXT,
                "Mira el perfil de $fullName en SportPro: $link"
            )
        }
        context.startActivity(Intent.createChooser(intent, "Compartir perfil").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    val copyLink = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Perfil SportPro", link))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlayerSurface,
        titleContentColor = PlayerTextPrimary,
        textContentColor = PlayerTextSecondary,
        title = {
            Text(
                text = "Compartir perfil",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Código QR del perfil",
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PlayerTextPrimary)
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Text(
                        text = "No se pudo generar el código QR.",
                        color = PlayerTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = link,
                    fontSize = 12.sp,
                    color = PlayerNeonGreen,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                shareProfile()
                onDismiss()
            }) {
                Text(text = "Compartir", color = PlayerNeonGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                copyLink()
                onDismiss()
            }) {
                Text(text = "Copiar enlace", color = PlayerTextSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

private fun generateQrBitmap(content: String, size: Int): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(
                    x,
                    y,
                    if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (_: Exception) {
        null
    }
}
