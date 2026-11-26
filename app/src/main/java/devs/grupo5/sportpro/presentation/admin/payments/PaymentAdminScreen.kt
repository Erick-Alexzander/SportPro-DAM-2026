package devs.grupo5.sportpro.presentation.admin.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.repository.FirebasePaymentRepository
import devs.grupo5.sportpro.presentation.admin.moderation.DarkBackground
import devs.grupo5.sportpro.presentation.admin.moderation.NeonGreen
import kotlinx.coroutines.launch

@Composable
fun PaymentAdminScreen(
    adminUid: String,
    onBackClick: () -> Unit = {},
    paymentRepository: FirebasePaymentRepository = FirebasePaymentRepository.getInstance()
) {
    var guardians by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var payments by remember { mutableStateOf<Map<String, MonthlyPayment>>(emptyMap()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        paymentRepository.getGuardians()
            .onSuccess { list ->
                guardians = list
                val loaded = mutableMapOf<String, MonthlyPayment>()
                list.forEach { guardian ->
                    paymentRepository.getPayment(guardian.uid)
                        .onSuccess { loaded[guardian.uid] = it }
                }
                payments = loaded
            }
            .onFailure { error = it.message }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SportPro Admin",
                        fontSize = 12.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Panel de Pagos",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            error?.let {
                Text(text = "Error: $it", color = Color(0xFFFF9100), fontSize = 13.sp)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(guardians) { guardian ->
                    val payment = payments[guardian.uid] ?: MonthlyPayment()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B231F))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            val name = listOf(guardian.firstName, guardian.lastName)
                                .filter { it.isNotBlank() }
                                .joinToString(" ")
                                .ifBlank { guardian.email }
                            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(text = guardian.email, color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = "Mensualidad ${payment.period} · Estado: ${payment.status.label}",
                                color = NeonGreen,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PaymentStatus.entries.forEach { status ->
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                paymentRepository
                                                    .updateStatus(adminUid, guardian.uid, status)
                                                    .onSuccess {
                                                        paymentRepository.getPayment(guardian.uid)
                                                            .onSuccess { updated ->
                                                                payments = payments + (guardian.uid to updated)
                                                            }
                                                    }
                                                    .onFailure { error = it.message }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (payment.status == status) NeonGreen else Color(0xFF2E3B34),
                                            contentColor = if (payment.status == status) Color.Black else Color.White
                                        )
                                    ) {
                                        Text(text = status.label)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
