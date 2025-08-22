package edu.bluejack23_1.next.viewmodels.events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.model.User
import edu.bluejack23_1.next.model.recyclerView.adapter.IParticipantListener
import edu.bluejack23_1.next.repository.EventRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class EventDetailMakerViewModel @Inject constructor(private val eventId: String) : ViewModel(), IParticipantListener {
    data class EventValidation(
        var isAvailable: Boolean,
        var event: Event?,
        var message: String?,
        var isError: Boolean,
        val isDeleted: Boolean = false
    )

    private val _validationState = MutableLiveData<EventValidation>()
    val validationState: LiveData<EventValidation> get() = _validationState

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    private val repository = EventRepository()

    init {
        fetchParticipants()
    }

    private fun fetchParticipants() {
        _showProgressBar.value = true
        viewModelScope.launch {
            try {
                val participants = repository.getParticipants(eventId)
                _participantList.value = participants
            } catch (e: Exception) {
                e.message?.let { Log.d("FETCH_API", it) }
            } finally {
                _showProgressBar.value = false
            }
        }
    }

    fun initializeData(
        event: Event?,
    ) {
        _validationState.value = EventValidation(
            event?.status == "Active", event, null, false
        )
    }

    private val _participantList = MutableLiveData<List<User>>()
    val participantList: LiveData<List<User>> = _participantList

    fun onCloseEventButtonClicked() {
        if (!_validationState.value!!.isAvailable) {
            _validationState.value = EventValidation(
                _validationState.value!!.isAvailable,
                _validationState.value?.event,
                "The event is closed already",
                true
            )
            return
        }

        viewModelScope.launch {
            val result = repository.closeEvent(_validationState.value?.event?.id)

            if (result) {
                _validationState.value = EventValidation(
                    false, _validationState.value?.event, null, false
                )
            } else {
                _validationState.value = EventValidation(
                    _validationState.value!!.isAvailable,
                    _validationState.value?.event,
                    "Error on closing the event",
                    true
                )
            }
        }

    }

    fun onDeleteButtonClicked() {
//        For Deleting Event
        viewModelScope.launch {
            val result = repository.deleteEvent(_validationState.value?.event?.id)

            if (result) {
                _validationState.value = EventValidation(
                    false, _validationState.value?.event, null, false, isDeleted = true
                )
            } else {
                _validationState.value = EventValidation(
                    _validationState.value!!.isAvailable,
                    _validationState.value?.event,
                    "Error on deleting the event",
                    true
                )
            }
        }
    }

    override fun onDeleteClicked(userId: String) {
//        For deleting participant
        viewModelScope.launch {
            val result = repository.deleteParticipant(_validationState.value?.event?.id!!, userId)

            if (result) {
                fetchParticipants()
                _validationState.value = EventValidation(
                    _validationState.value!!.isAvailable,
                    _validationState.value?.event,
                    null,
                    false
                )
            } else {
                _validationState.value = EventValidation(
                    _validationState.value!!.isAvailable,
                    _validationState.value?.event,
                    "Error on deleting the participant",
                    true
                )
            }
        }
    }
}