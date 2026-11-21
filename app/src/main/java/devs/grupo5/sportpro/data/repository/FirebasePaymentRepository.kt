package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.currentPaymentPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebasePaymentRepository private constructor() : PaymentRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _payment = MutableStateFlow(MonthlyPayment())
    override val payment: StateFlow<MonthlyPayment> = _payment.asStateFlow()

    private var listener: ListenerRegistration? = null
    private var observedUid: String? = null

    override fun observePayment(guardianUid: String): StateFlow<MonthlyPayment> {
        if (observedUid == guardianUid) return payment
        listener?.remove()
        observedUid = guardianUid
        _payment.value = MonthlyPayment()
        listener = firestore.collection(COLLECTION).document(guardianUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val stored = snapshot?.toObject(MonthlyPayment::class.java)
                _payment.value = resolvePayment(stored)
            }
        return payment
    }

    override suspend fun getPayment(guardianUid: String): Result<MonthlyPayment> = try {
        val snapshot = firestore.collection(COLLECTION).document(guardianUid).get().await()
        Result.success(resolvePayment(snapshot.toObject(MonthlyPayment::class.java)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getGuardians(): Result<List<UserProfile>> = try {
        val snapshot = firestore.collection("users")
            .whereEqualTo("role", "PARENT")
            .get()
            .await()
        Result.success(snapshot.documents.mapNotNull { doc ->
            doc.toObject(UserProfile::class.java)?.copy(uid = doc.id)
        })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun submitReceipt(guardianUid: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        if (uid != guardianUid) return Result.failure(Exception("Solo puedes enviar tu propio comprobante"))

        return try {
            val docRef = firestore.collection(COLLECTION).document(guardianUid)
            if (docRef.get().await().exists()) {
                docRef.update("receiptRegistered", true).await()
            } else {
                docRef.set(
                    mapOf(
                        "period" to currentPaymentPeriod(),
                        "status" to PaymentStatus.PENDIENTE.name,
                        "receiptRegistered" to true
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStatus(
        adminUid: String,
        guardianUid: String,
        status: PaymentStatus
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        if (uid != adminUid) return Result.failure(Exception("No autorizado"))

        return try {
            val adminRole = firestore.collection("users").document(adminUid).get().await().getString("role")
            if (adminRole != "ADMIN") return Result.failure(Exception("No autorizado"))

            firestore.collection(COLLECTION).document(guardianUid).set(
                mapOf(
                    "period" to currentPaymentPeriod(),
                    "status" to status.name,
                    "updatedBy" to adminUid,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun resolvePayment(stored: MonthlyPayment?): MonthlyPayment =
        if (stored == null || stored.period != currentPaymentPeriod()) MonthlyPayment() else stored

    companion object {
        private const val COLLECTION = "payments"

        @Volatile
        private var instance: FirebasePaymentRepository? = null

        fun getInstance(): FirebasePaymentRepository = instance ?: synchronized(this) {
            instance ?: FirebasePaymentRepository().also { instance = it }
        }
    }
}
