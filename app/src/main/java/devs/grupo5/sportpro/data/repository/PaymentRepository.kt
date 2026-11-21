package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.data.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface PaymentRepository {
    val payment: StateFlow<MonthlyPayment>

    fun observePayment(guardianUid: String): StateFlow<MonthlyPayment>

    suspend fun getPayment(guardianUid: String): Result<MonthlyPayment>

    suspend fun getGuardians(): Result<List<UserProfile>>

    suspend fun submitReceipt(guardianUid: String): Result<Unit>

    suspend fun updateStatus(adminUid: String, guardianUid: String, status: PaymentStatus): Result<Unit>
}
