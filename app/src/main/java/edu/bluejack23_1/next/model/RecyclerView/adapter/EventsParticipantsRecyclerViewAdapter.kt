package edu.bluejack23_1.next.model.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.ActivityEventDetailBinding
import edu.bluejack23_1.next.model.User

class EventsParticipantsRecyclerViewAdapter(

    private var participantList: List<User>
) : RecyclerView.Adapter<EventsParticipantsRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ActivityEventDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var participantInitialTV: TextView = itemView.findViewById(R.id.participantInitialET)
        private var participantNameTV: TextView = itemView.findViewById(R.id.participantNameET)
        private var participantEmailTV: TextView = itemView.findViewById(R.id.participantEmailET)

        override fun toString(): String {
            return super.toString() + " '" + participantNameTV.text + "'"
        }

        fun bind(user: User) {
            participantInitialTV.text = user.id
            participantNameTV.text = user.name
            participantEmailTV.text = user.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ActivityEventDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(participantList[position])
    }

    override fun getItemCount(): Int = participantList.size
}

