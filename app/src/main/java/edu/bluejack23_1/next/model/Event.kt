package edu.bluejack23_1.next.model

import com.google.firebase.database.Exclude
import edu.bluejack23_1.next.model.recyclerView.ListAdapterItem
import java.io.Serializable

data class Event (
    override var id: String? = "",
    var bannerImageLink: String? = "",
    var date: String? = "",
    var issuer: String? = "",
    var location: String? = "",
    var needed: Int? = 0,
    var notes: String? = "",
    var reward: String? = "",
    var status: String? = "",
    var title: String? = "",
    @Exclude
    var participantsCount: Int = 0,
    var issuerPictureLink: String? = ""
) : ListAdapterItem, Serializable
