package edu.bluejack23_1.next.model.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.RequestItemBinding
import edu.bluejack23_1.next.model.Request

class RequestsRecyclerViewAdapter(
    private var requestList: List<Request>
) : RecyclerView.Adapter<RequestsRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(binding: edu.bluejack23_1.next.databinding.RequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var requestTypeTV: TextView = itemView.findViewById(R.id.request_typeTV)
        private var requestDateTV: TextView = itemView.findViewById(R.id.request_dateTV)
        private var requestDescTV: TextView = itemView.findViewById(R.id.request_event_descTV)

        override fun toString(): String {
            return super.toString() + " '" + requestDateTV.text + "'"
        }

        fun bind(request: Request) {
            requestTypeTV.text = request.type
            requestDateTV.text = request.date
            requestDescTV.text = request.reason
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RequestItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = requestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

}