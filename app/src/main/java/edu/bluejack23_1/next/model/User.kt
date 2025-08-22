package edu.bluejack23_1.next.model

import edu.bluejack23_1.next.model.recyclerView.ListAdapterItem

data class User(
    override var id: String? = "",
    val profilePicture: String? = "",
    val name: String? = "",
    val email: String? = "",
) : ListAdapterItem
