package edu.bluejack23_1.next.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.next.model.Email

import kotlinx.coroutines.tasks.await

class EmailRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchQmanEmails(): List<Email> {
        val emails = mutableListOf<Email>()
        val querySnapshot = db.collection("qman").get().await()

        for (document in querySnapshot) {
            val email = document.toObject(Email::class.java)
            emails.add(email)
        }

        return emails
    }

}