package edu.bluejack23_1.next.model.recyclerView.adapter

import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.EventItemBinding
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.model.recyclerView.BaseAdapter

class EventAdapter (
    list: List<Event>,
    private val eventListener : IEventListener
) : BaseAdapter<EventItemBinding, Event> (list) {

    override val layoutId: Int = R.layout.event_item

    override fun bind(binding: EventItemBinding, item: Event) {
        binding.apply {
            event = item
            listener = eventListener
            executePendingBindings()
        }
    }
}

interface IEventListener {
    fun onEventClicked(event: Event)
}