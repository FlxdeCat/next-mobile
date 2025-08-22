package edu.bluejack23_1.next.model

interface UserAPIFetchCallback {
    fun onUserFetched(userResponse: UserResponse?)
    fun onFailure(errorMessage: String)
}

interface LeaderAPIFetchCallback {
    fun onLeaderFetched(leaderResponse: LeaderResponse?)
    fun onLeaderNotFound()
    fun onFailure(errorMessage: String)
}

interface ClassAPIFetchCallback {
    fun onClassFetched(classLists: List<ClassInformation>?)
    fun onFailure(errorMessage: String)
}

interface EmailAPIFetchCallback {
    fun onEmailFetched(classLists: BinusianResponse?)
    fun onFailure(errorMessage: String)
}