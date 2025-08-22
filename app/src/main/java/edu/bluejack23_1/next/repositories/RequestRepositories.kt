package edu.bluejack23_1.next.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.model.Request
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class RequestRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addRequest(request: Request, context: Context): Result<Request> {
        val requestRef = db.collection("requests").document()

        val pref: SharedPreferences = context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        request.id = requestRef.id
        request.username = username!!.uppercase()

        return try {
            requestRef.set(request).await()
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    

    suspend fun getRequestsByUsername(username: String): List<Request> {
        val requests = mutableListOf<Request>()
        val querySnapshot = db.collection("requests").whereEqualTo("username", username).get().await()

        for (document in querySnapshot) {
            val request = document.toObject(Request::class.java)
            requests.add(request)
        }

        return requests.sortedByDescending { request ->
            LocalDate.parse(request.date, DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH))
        }
    }

}