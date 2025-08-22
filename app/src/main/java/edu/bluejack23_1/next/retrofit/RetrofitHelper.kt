package edu.bluejack23_1.next.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private var instance: Retrofit? = null
    private val BASE_URL = "https://bluejack.binus.ac.id/lapi/api/"

    fun getInstance(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance!!
    }

}

object RetrofitLeaderHelper {

    private var instance: Retrofit? = null
    private val BASE_URL = "https://academic-slc.apps.binus.ac.id/scheduler-api/"

    fun getInstance(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance!!
    }

}