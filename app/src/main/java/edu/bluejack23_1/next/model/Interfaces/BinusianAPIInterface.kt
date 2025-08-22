package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.BinusianResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BinusianAPIInterface {
    @GET("Assistant/GetBinusianByBinusianId")
    fun getBinusian(
        @Query("binusianId") binusianId: String
    ): Call<BinusianResponse>
}