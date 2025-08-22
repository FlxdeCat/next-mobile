package edu.bluejack23_1.next.model.recyclerView.adapter

import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.RequestItemBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.Request
import edu.bluejack23_1.next.model.recyclerView.BaseAdapter

class RequestRVAdapter (
    list: List<Request>
) : BaseAdapter<RequestItemBinding, Request>(list) {

    override val layoutId: Int = R.layout.request_item

    override fun bind(binding: RequestItemBinding, item: Request) {
        binding.apply {
            request = item
            helper = Helper
            executePendingBindings()
        }
    }
}