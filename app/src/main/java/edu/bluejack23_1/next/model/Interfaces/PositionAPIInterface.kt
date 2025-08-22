package edu.bluejack23_1.next.model.interfaces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PositionAPIInterface {
    @GET("Assistant/GetAssistantRoles")
    fun getPositions(
        @Query("username") username: String
    ): Call<List<String>>
}