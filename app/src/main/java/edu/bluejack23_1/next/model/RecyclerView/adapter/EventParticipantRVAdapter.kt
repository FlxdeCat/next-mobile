package edu.bluejack23_1.next.model.recyclerView.adapter

import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.ParticipantEventItemBinding
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.model.User
import edu.bluejack23_1.next.model.recyclerView.BaseAdapter

class EventParticipantRVAdapter (
    list: List<User>,
    private val participantListener : IParticipantListener
) : BaseAdapter<ParticipantEventItemBinding, User>(list) {

    override val layoutId: Int = R.layout.participant_event_item

    override fun bind(binding: ParticipantEventItemBinding, item: User) {
        binding.apply {
            user = item
            listener = participantListener
            executePendingBindings()
        }
    }
}

interface IParticipantListener {
    fun onDeleteClicked(userId: String)
}