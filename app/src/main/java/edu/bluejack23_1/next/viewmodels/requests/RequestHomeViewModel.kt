package edu.bluejack23_1.next.viewmodels.requests

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack23_1.next.model.Request
import edu.bluejack23_1.next.repositories.RequestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class RequestHomeViewModel @Inject constructor(private val context: Context) : ViewModel(){
    data class RequestValidation(
        var isValid: Boolean,
        var event: Request?
    )

    private val _requestList = MutableLiveData<List<Request>>()
    val requestList: MutableLiveData<List<Request>> = _requestList

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    private val _isEmptyViewVisible = MutableLiveData(false)
    val isEmptyViewVisible: LiveData<Boolean> = _isEmptyViewVisible

    private val requestRepository = RequestRepository()

    init {
        val pref: SharedPreferences = context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        fetchRequests(username!!)
    }

    fun fetchRequests(username: String) {
        viewModelScope.launch {
            fetchAllRequests(username)
                .onStart {
                    _showProgressBar.value = true
                }
                .catch {
                    _showProgressBar.value = false
                }
                .collect { list ->
                    _requestList.value = list
                    _isEmptyViewVisible.value = list.isEmpty()
                    _showProgressBar.value = false
                }
        }
    }

    private fun fetchAllRequests(username: String) = flow {
        val requests = requestRepository.getRequestsByUsername(username)
        emit(requests)
    }.flowOn(Dispatchers.IO)

}