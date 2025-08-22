package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.LeaderResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LeaderAPIInterface {
    @POST("leader")
    fun getLeader(
        @Body() requestBody: RequestBody
    ): Call<LeaderResponse>
}