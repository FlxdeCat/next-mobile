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

class CreatePermissionRequestViewModel: ViewModel() {
    data class ValidationResult(
        val isValid: Boolean,
        val message: String? = null
    )

    private val _validationState = MutableLiveData<ValidationResult>()
    val validationState: LiveData<ValidationResult> get() = _validationState

    private val repository = RequestRepository()


    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    fun onNextButtonClicked(activity: Activity, type: String, date: String, reason: String, tasks: String, context: Context) {
        if(type == "" || type == "Please choose permission" || date.isEmpty() || reason.isEmpty() || tasks.isEmpty()) {
            _validationState.value = ValidationResult(
                false,
                "All fields must be filled"
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

        if(reason.length >= 100 || tasks.length >= 100) {
            _validationState.value = ValidationResult(
                false,
                "Reason and Tasks must be less than 100 characters"
            )
            return
        }


        _showProgressBar.value = true

        viewModelScope.launch {
            val result = repository.addRequest(Request(null, type, null, date, reason, tasks, null, null, null, null, null, null), context)
            _showProgressBar.value = false

            if (result.isSuccess) {
                _validationState.value = ValidationResult(
                    true,null
                )
            } else {
                _validationState.value = ValidationResult(
                    false,"Failed: ${result.exceptionOrNull()?.message}"
                )
            }

            val repository = EmailRepository()
            val emails = repository.fetchQmanEmails()

            Helper.sendPermissionRequestOulookEmail(
                activity,
                type,
                date,
                reason,
                tasks,
                emails)
        }

    }
}