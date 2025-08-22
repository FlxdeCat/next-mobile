package edu.bluejack23_1.next.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EventRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun addEvent(event: Event, imageURI: Uri?, context: Context): Result<Event> {
        val eventRef = db.collection("events").document()

        val pref: SharedPreferences =
            context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        event.id = eventRef.id
        event.issuer = username

        return try {
            if (imageURI != null) {
                val eventBannerRef = storage.reference.child("eventBanner/${event.id}")

                eventBannerRef.putFile(imageURI).await()
                val downloadUrl = eventBannerRef.downloadUrl.await()

                event.bannerImageLink = downloadUrl.toString()
            }

            eventRef.set(event).await()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAllEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val querySnapshot = db.collection("events").get().await()

        for (document in querySnapshot) {
            val event = document.toObject(Event::class.java)
            events.add(event)
        }

        return events.sortedBy { it.status }
    }

    suspend fun fetchParticipantCount(eventId: String): Int {
        val documentSnapshot = db.collection("participants").document(eventId).get().await()
        return documentSnapshot.data?.size ?: 0
    }

    suspend fun participateInEvent(eventId: String?, context: Context): Boolean {
        val participantsRef = db.collection("participants").document(eventId!!)

        val pref: SharedPreferences =
            context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        return try {
            val docSnapshot = participantsRef.get().await()

            if (username != null && username != "") {
                if (docSnapshot.exists()) {
                    participantsRef.update(username, true)
                } else {
                    participantsRef.set(
                        hashMapOf(
                            username to true
                        )
                    )
                }
                true
            } else {
                Log.d("Error in participating in event: Error username", username.toString())
                false
            }
        } catch (e: Exception) {
            Log.d("Error in participating in event", e.toString())
            false
        }
    }

    suspend fun checkParticipated(username: String, eventId: String): Boolean {
        val documentSnapshot = db.collection("participants").document(eventId).get().await()
        val data = documentSnapshot.data
        return data?.let {
            it.containsKey(username) && it[username] == true
        } ?: false
    }

    suspend fun closeEvent(id: String?): Boolean {
        return try {
            db.collection("events").document(id!!).update("status", "Closed").await()
            true
        } catch (e: Exception) {
            Log.d("Error in closing event", e.toString())
            false
        }
    }

    fun deleteEvent(id: String?): Boolean {
        return try {
            db.collection("events").document(id!!).delete()
            true
        } catch (e: Exception) {
            Log.d("Error in deleting event", e.toString())
            false
        }
    }

    suspend fun getParticipantsTemp(eventId: String) : List<User>{
        val participants = mutableListOf<User>()
        val querySnapshot = db.collection("participants").document(eventId).get().await()
        for ( document in querySnapshot.data!!.keys) {
            val user = User(document)
            participants.add(user)
        }
        return participants
    }

    suspend fun getParticipants(eventId: String): List<User> {
        return withContext(Dispatchers.IO) {
            val participants = mutableListOf<User>()
            val querySnapshot = db.collection("participants").document(eventId).get().await()
            querySnapshot.data?.keys?.map { initial ->
                async {
                    val userResponse = Helper.fetchParticipantDataAPI(initial)
                    if (userResponse == null) {
                        Log.d("FETCH_API", "Failed to fetch user data!")
                    } else {
                        val binusianResponse = Helper.fetchParticipantEmailDataAPI(userResponse.BinusianId!!)
                        if (binusianResponse == null) {
                            Log.d("FETCH_API", "Failed to fetch binusian data!")
                        } else {
                            val user = User(
                                id = userResponse.Username,
                                profilePicture = "https://bluejack.binus.ac.id/lapi/api/Account/GetThumbnail?id=" + userResponse.PictureId,
                                name = userResponse.Name,
                                email = Helper.getBinusianEmail(binusianResponse.Emails!!)
                            )
                            participants.add(user)
                        }
                    }
                }
            }?.awaitAll()
            participants
        }
    }

    suspend fun deleteParticipant(eventId: String, username: String): Boolean {
        return try {
            val updates = hashMapOf<String, Any>(
                username to FieldValue.delete()
            )
            db.collection("participants").document(eventId).update(updates).await()
            true
        } catch (e: Exception) {
            Log.d("Error in deleting participant", e.toString())
            false
        }
    }

}
