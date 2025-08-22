package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.BinusianResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ParticipantEmailAPIInterface {
    @GET("Assistant/GetBinusianByBinusianId")
    suspend fun getBinusian(
        @Query("binusianId") binusianId: String
    ): Response<BinusianResponse>
}