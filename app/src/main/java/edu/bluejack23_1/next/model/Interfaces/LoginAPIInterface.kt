package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.AccountResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginAPIInterface {
    @FormUrlEncoded
    @POST("Account/LogOn")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AccountResponse>
}