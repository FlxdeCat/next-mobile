package edu.bluejack23_1.next.model.interfaces

import edu.bluejack23_1.next.model.SemesterResponse
import retrofit2.Call
import retrofit2.http.GET

interface SemesterAPIInterface {
    @GET("Semester/Active")
    fun getActiveSemester(): Call<SemesterResponse>
}