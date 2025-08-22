package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ParticipantAPIInterface {
    @GET("Assistant/GetBinusianByUsername")
    suspend fun getUser(
        @Query("username") username: String
    ): Call<UserResponse>
}