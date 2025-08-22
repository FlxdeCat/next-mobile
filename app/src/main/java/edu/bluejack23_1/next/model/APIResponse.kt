package edu.bluejack23_1.next.model

import com.google.gson.annotations.SerializedName

class AccountResponse {
    @SerializedName("access_token")
    var access_token: String? = null
    @SerializedName("token_type")
    var token_type: String? = null
    @SerializedName("expired_in")
    var expired_in: Int? = null
    @SerializedName("refresh_token")
    var refresh_token: String? = null
}

class UserResponse {
    @SerializedName("Name")
    var Name: String? = null
    @SerializedName("BinusianId")
    var BinusianId: String? = null
    @SerializedName("Username")
    var Username: String? = null
    @SerializedName("PictureId")
    var PictureId: String? = null
}

class BinusianEmails {
    var Email: String? = null
    var Prefer: String? = null
    var Type: String? = null
}

class BinusianResponse {
    @SerializedName("Emails")
    var Emails: Array<BinusianEmails>? = null
    @SerializedName("Name")
    var Name: String? = null
    @SerializedName("PictureId")
    var PictureId: String? = null
    @SerializedName("Role")
    var Role: String? = null
    @SerializedName("UserId")
    var UserId: String? = null
    @SerializedName("Username")
    var Username: String? = null
}

class LeaderResponse {
    @SerializedName("initial")
    var initial: String? = null
    @SerializedName("generation")
    var generation: String? = null
    @SerializedName("username")
    var username: String? = null
}

class ClassInformation {
    var Assistant: String? = null
    var Campus: String? = null
    var Class: String? = null
    var Date: String? = null
    var Day: Int? = null
    var Id: String? = null
    var LecturerCode: String? = null
    var LecturerName: String? = null
    var Note: String? = null
    var Number: Int? = null
    var Realization: String? = null
    var Room: String? = null
    var SemesterId: String? = null
    var Session: String? = null
    var Shift: String? = null
    var Subject: String? = null
    var SubjectId: String? = null
    var TheoryClass: String? = null
    var TotalStudent: Int? = null
}

class SemesterResponse {
    @SerializedName("Description")
    var Description: String? = null
    @SerializedName("SemesterId")
    var SemesterId: String? = null
}
