package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserAPIInterface {
    @GET("Assistant/GetBinusianByUsername")
    fun getUser(
        @Query("username") username: String
    ): Call<UserResponse>
}