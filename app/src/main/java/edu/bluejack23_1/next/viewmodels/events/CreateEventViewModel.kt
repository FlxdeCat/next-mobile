package edu.bluejack23_1.next.viewmodels.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.viewmodels.requests.CreateExtraClassRequestViewModel

class CreateEventViewModel : ViewModel(){
    data class ValidationResult(
        val isValid: Boolean,
        val message: String? = null
    )

    private val _validationState = MutableLiveData<ValidationResult>()
    val validationState: LiveData<ValidationResult> get() = _validationState

    fun onNextButtonClicked(eventName: String, eventDate: String, participantNeeded: String, location: String, reward: String) {
        if(eventName.isEmpty() || eventDate.isEmpty() || participantNeeded.isEmpty() || location.isEmpty() || reward.isEmpty()) {
            _validationState.value = ValidationResult(
                false,
                "All fields must be filled"
            )
            return
        }

        if(participantNeeded.toIntOrNull() == null) {
            _validationState.value = ValidationResult(
                false,
                "Participant must be filled with numeric values"
            )
            return
        }

        if(!Helper.isDateFuture(eventDate)){
            _validationState.value = ValidationResult(
                false,
                "Date must be located in the future"
            )
            return
        }

        _validationState.value = ValidationResult(true)
    }
}