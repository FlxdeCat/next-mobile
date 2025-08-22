package edu.bluejack23_1.next.viewmodels.events

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.repository.EventRepository
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {
    data class EventValidation(
        var isAvailable: Boolean,
        var event: Event?,
        var message: String?,
        var isError: Boolean
    )

    private val _validationState = MutableLiveData<EventValidation>()
    val validationState: LiveData<EventValidation> get() = _validationState

    private val repository = EventRepository()

    fun onParticipateButtonClicked(context: Context) {
        if (!_validationState.value!!.isAvailable) {
            return
        }

        viewModelScope.launch {
            val participants = repository.fetchParticipantCount(_validationState.value?.event?.id!!)
            if (participants >= _validationState.value?.event?.needed!!) {
                _validationState.value = EventValidation(
                    _validationState.value!!.isAvailable,
                    _validationState.value?.event,
                    "Slot is full",
                    true
                )
                return@launch
            }

            val result = repository.participateInEvent(_validationState.value?.event?.id, context)

            if (result) {
                _validationState.value = EventValidation(
                    false, _validationState.value?.event, null, false
                )
            } else {
                _validationState.value = EventValidation(
                    _validationState.value!!.isAvailable,
                    _validationState.value?.event,
                    "Error on participating in the event",
                    true
                )
            }
        }
    }

    fun initializeData(
        event: Event?,
        context: Context
    ) {
        Log.d("Eventnya di init", event.toString())
        val pref: SharedPreferences =
            context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        _validationState.value = EventValidation(
            false, event, null, false
        )

        viewModelScope.launch {
            // Fetch whether the user has participated
            val isParticipated = repository.checkParticipated(username!!, event!!.id!!)
            val anySlotLeft =
                repository.fetchParticipantCount(_validationState.value?.event?.id!!) < _validationState.value?.event?.needed!!

            if (!isParticipated && anySlotLeft && event.status == "Active") {
                _validationState.value = EventValidation(
                    true, _validationState.value?.event, null, false
                )
            }
        }

    }
}