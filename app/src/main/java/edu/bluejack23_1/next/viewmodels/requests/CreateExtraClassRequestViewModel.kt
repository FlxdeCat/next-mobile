package edu.bluejack23_1.next.viewmodels.requests

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.Request
import edu.bluejack23_1.next.repositories.EmailRepository
import edu.bluejack23_1.next.repositories.RequestRepository
import kotlinx.coroutines.launch

class CreateExtraClassRequestViewModel: ViewModel() {
    data class ValidationResult(
        val isValid: Boolean,
        val message: String? = null
    )

    private val _validationState = MutableLiveData<ValidationResult>()
    val validationState: LiveData<ValidationResult> get() = _validationState

    private val repository = RequestRepository()


    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    fun onNextButtonClicked(activity: Activity, partner: String, course: String, date: String, shift: String, code: String, students: String, location: String, context: Context) {
        if(partner == "" || course == "" || code == "" || students == "" || date.isEmpty() || location.isEmpty()) {
            _validationState.value = ValidationResult(
                false,
                "All fields must be filled"
            )
            return
        }

        if(code.length >= 50 || location.length >= 50){
            _validationState.value = ValidationResult(
                false,
                "Class Code and Location must be less than 50 characters"
            )
            return
        }

        if(shift.toIntOrNull() == null) {
            _validationState.value = ValidationResult(
                false,
                "Shift must be filled with numeric values"
            )
            return
        }

        if(shift.toInt() < 1 || shift.toInt() > 6) {
            _validationState.value = ValidationResult(
                false,
                "Shift must be between 1 and 6"
            )
            return
        }

        if(students.toIntOrNull() == null) {
            _validationState.value = ValidationResult(
                false,
                "Student count must be filled with numeric values"
            )
            return
        }

        if(!Helper.isDateFuture(date)){
            _validationState.value = ValidationResult(
                false,
                "Date must be located in the future"
            )
            return
        }

        _showProgressBar.value = true

        viewModelScope.launch {
            val result = repository.addRequest(Request(null, "Extra Class", null, date, null, null, partner, course, shift, code, students, location), context)

            _showProgressBar.value = false
            if (result.isSuccess) {
                _validationState.value = ValidationResult(
                    true, null
                )
            } else {
                _validationState.value = ValidationResult(
                    false, "Failed: ${result.exceptionOrNull()?.message}"
                )
            }

            val repository = EmailRepository()
            val emails = repository.fetchQmanEmails()

            Helper.sendExtraClassRequestOulookEmail(
                activity,
                partner,
                course,
                date,
                shift,
                code,
                students,
                location,
                emails)
        }
    }

}