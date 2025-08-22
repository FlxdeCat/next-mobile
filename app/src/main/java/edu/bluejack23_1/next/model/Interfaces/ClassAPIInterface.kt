package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.ClassInformation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ClassAPIInterface {
    @GET("Assistant/GetClassTransactionByAssistantUsername")
    fun getClassTransactions(
        @Query("username") username: String,
        @Query("semesterId") semesterId: String
    ): Call<List<ClassInformation>>
}