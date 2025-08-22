package edu.bluejack23_1.next.views.requests

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.next.views.events.EventHomeActivity
import edu.bluejack23_1.next.views.requests.create.CreateRequestActivity
import edu.bluejack23_1.next.model.recyclerView.adapter.RequestsRecyclerViewAdapter
import edu.bluejack23_1.next.databinding.ActivityRequestHomeBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.Request
import edu.bluejack23_1.next.model.recyclerView.adapter.RequestRVAdapter
import android.view.View
import edu.bluejack23_1.next.viewmodels.events.EventHomeViewModel
import edu.bluejack23_1.next.viewmodels.requests.RequestHomeViewModel

class RequestHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestHomeBinding
    private lateinit var requestRV: RecyclerView

    private val viewModel by viewModels<RequestHomeViewModel>{
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RequestHomeViewModel(this@RequestHomeActivity) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestHomeBinding.inflate(layoutInflater)
        binding.apply {
            viewModel = this@RequestHomeActivity.viewModel
            lifecycleOwner = this@RequestHomeActivity
        }
        setContentView(binding.root)

        binding.adapter = RequestRVAdapter(listOf())

        Helper.checkUserLogOut(this, this@RequestHomeActivity)

        requestRV = binding.requestRV
        requestRV.layoutManager = LinearLayoutManager(this)

        setUpContent()
        setUpButtonAction()
        setUpObserver()
    }

    private fun setUpContent() {
        requestRV = binding.requestRV
        binding.invisibleCircle.bringToFront()
    }

    private fun setUpButtonAction() {
        binding.addButton.setOnClickListener {
            Helper.expandCircleAnimation(
                listOf(binding.addButton, binding.addIcon, binding.pageHeaderTV, binding.requestRV),
                binding.invisibleCircle,
                19f
            ) {
                val intent = Intent(this@RequestHomeActivity, CreateRequestActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }

        binding.eventButton.setOnClickListener {
            redirectToEventHomePage()
        }
    }

    private fun redirectToEventHomePage() {
        val intent = Intent(this@RequestHomeActivity, EventHomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }


    override fun onResume() {
        super.onResume()
        Helper.onResumeShrinkBack(
            binding.invisibleCircle,
            listOf(
                binding.addButton,
                binding.addIcon,
                binding.pageHeaderTV,
                binding.requestRV,
                binding.bottomBar
            ),
            false
        )
        val pref: SharedPreferences = this.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        viewModel.fetchRequests(username!!)
    }

    private fun setUpObserver() {
        viewModel.isEmptyViewVisible.observe(this) { result ->
            binding.emptyView.visibility = if (result) View.VISIBLE else View.GONE

        }
    }
}