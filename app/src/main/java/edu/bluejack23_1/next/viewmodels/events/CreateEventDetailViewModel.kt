package edu.bluejack23_1.next.viewmodels.events

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.repository.EventRepository
import kotlinx.coroutines.launch

class CreateEventDetailViewModel : ViewModel() {
    data class EventValidation(
        var isValid: Boolean,
        var isSubmitted: Boolean,
        var event: Event?,
        var imageURI: Uri?,
        var message: String?
    )

    private val _validationState = MutableLiveData<EventValidation>()
    val validationState: LiveData<EventValidation> get() = _validationState

    private val repository = EventRepository()


    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    fun onSubmitButtonClicked(eventNotes: String, context: Context) {
        if (eventNotes.isEmpty()) {
            _validationState.value = EventValidation(
                isValid = false, isSubmitted = false,
                event = _validationState.value?.event,
                imageURI = _validationState.value?.imageURI,
                message = "Please fill the notes"
            )
            return
        }

        if (eventNotes.length > 50) {
            _validationState.value = EventValidation(
                isValid = false, isSubmitted = false,
                event = _validationState.value?.event,
                imageURI = _validationState.value?.imageURI,
                message = "The notes exceed 50 characters"
            )
            return
        }

        var eventCopy = _validationState.value?.event?.copy()
        eventCopy?.notes = eventNotes
        _showProgressBar.value = true

        viewModelScope.launch {
            val result = repository.addEvent(eventCopy!!, _validationState.value?.imageURI, context)
            _showProgressBar.value = false

            if (result.isSuccess) {
                _validationState.value = EventValidation(
                    isValid = true, isSubmitted = true,
                    result.getOrNull(),
                    imageURI = _validationState.value?.imageURI,
                    message = null
                )
            } else {
                _validationState.value = EventValidation(
                    isValid = false, isSubmitted = false,
                    event = _validationState.value?.event,
                    imageURI = _validationState.value?.imageURI,
                    message = "Failed: ${result.exceptionOrNull()?.message}"
                )
            }
        }

    }

    fun initializeEvent(
        eventName: String,
        eventDate: String,
        eventLocation: String,
        eventParticipant: String,
        eventReward: String
    ) {
        _validationState.value = EventValidation(
            isValid = false, isSubmitted = false,
            event = Event(
                null, null, eventDate,
                null, eventLocation,
                eventParticipant.toInt(), null,
                eventReward, "Active",
                eventName
            ),
            imageURI = _validationState.value?.imageURI,
            message = null
        )
    }


    fun setImageURI(imageURI: Uri?) {
        _validationState.value = EventValidation(
            isValid = true, isSubmitted = false,
            event = _validationState.value?.event,
            imageURI = imageURI,
            message = ""
        )
    }
}