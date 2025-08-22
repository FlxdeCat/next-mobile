package edu.bluejack23_1.next.model.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ThumbnailAPIInterface {
    @GET("Account/GetThumbnail")
    fun getThumbnail(
        @Query("id") id: String
    ): Call<ResponseBody>
}