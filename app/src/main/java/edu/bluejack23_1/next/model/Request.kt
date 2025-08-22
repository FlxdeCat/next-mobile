package edu.bluejack23_1.next.model

import edu.bluejack23_1.next.model.recyclerView.ListAdapterItem
import java.io.Serializable

data class Request (
    override var id: String? = "",
    var type: String? = "",
    var username: String? = "",
    var date: String? = "",

    var reason: String? = "",
    var tasks: String? = "",

    var partner: String? = "",
    var course: String? = "",
    var shift: String? = "",
    var class_code: String? = "",
    var student: String? = "",
    var location: String? = ""
) : ListAdapterItem, Serializable