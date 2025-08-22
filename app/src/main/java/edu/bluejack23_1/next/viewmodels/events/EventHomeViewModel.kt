package edu.bluejack23_1.next.viewmodels.events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.model.UserAPIFetchCallback
import edu.bluejack23_1.next.model.UserResponse
import edu.bluejack23_1.next.model.recyclerView.adapter.IEventListener
import edu.bluejack23_1.next.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

open class EventHomeViewModel @Inject constructor() : ViewModel(), IEventListener {
    data class EventValidation(
        var isValid: Boolean,
        var event: Event?
    )

    private val _isEmptyViewVisible = MutableLiveData(false)
    val isEmptyViewVisible: LiveData<Boolean> = _isEmptyViewVisible

    data class FetchEventError(
        var isError: Boolean,
        var message: String
    )

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

    private val _showProgressBar = MutableLiveData(false)

    private val _selectedEvent = MutableLiveData<EventValidation>()
    val selectedEvent: LiveData<EventValidation> = _selectedEvent

    val showProgressBar: LiveData<Boolean> = _showProgressBar
    private val eventRepository = EventRepository()

    override fun onEventClicked(event: Event) {
        _selectedEvent.value = EventValidation(
            true,
            event
        )

        return
    }

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            fetchAllEvents()
                .onStart {
                    _showProgressBar.postValue(true)
                }.catch {
                    _showProgressBar.postValue(false)
                }
                .collect { list ->
                    _eventList.value = list
                    _isEmptyViewVisible.value = list.isEmpty()
                    _showProgressBar.postValue(false)
                }
        }
    }

    private fun fetchAllEvents() = flow {
        val events = eventRepository.fetchAllEvents()

        for (event in events) {
            val count = eventRepository.fetchParticipantCount(event.id ?: "")
            event.participantsCount = count
        }
        emit(events)

    }.flowOn(Dispatchers.IO)
}