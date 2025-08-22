package edu.bluejack23_1.next.views.events

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.ActivityEventHomePageBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.recyclerView.adapter.EventAdapter
import edu.bluejack23_1.next.model.interfaces.PositionAPIInterface
import edu.bluejack23_1.next.retrofit.RetrofitHelper
import edu.bluejack23_1.next.viewmodels.events.EventHomeViewModel
import edu.bluejack23_1.next.views.events.create.CreateEventActivity
import edu.bluejack23_1.next.views.events.detail.EventDetailActivity
import edu.bluejack23_1.next.views.events.detail.EventDetailActivityMaker
import edu.bluejack23_1.next.views.requests.RequestHomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventHomePageBinding
    private lateinit var eventRV: RecyclerView

    private val viewModel by viewModels<EventHomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_home_page)
        binding.apply {
            viewModel = this@EventHomeActivity.viewModel
            lifecycleOwner = this@EventHomeActivity
        }
        setContentView(binding.root)

        binding.adapter = EventAdapter(listOf(), viewModel)

        Helper.checkUserLogOut(this, this@EventHomeActivity)

        eventRV = binding.eventRV
        eventRV.layoutManager = LinearLayoutManager(this)

        binding.invisibleCircle.bringToFront()
        binding.addButton.backgroundTintList =
            ContextCompat.getColorStateList(this@EventHomeActivity, R.color.dark_gray)
        setUpButtonAction()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.selectedEvent.observe(this) { result ->
            result?.let {
                if (it.isValid) {

                    val pref: SharedPreferences =
                        this.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
                    val username = pref.getString("username", "")

                    intent = if (username == it.event?.issuer) {
                        Intent(this@EventHomeActivity, EventDetailActivityMaker::class.java)
                    } else {
                        Intent(this@EventHomeActivity, EventDetailActivity::class.java)
                    }

                    intent.putExtra("event_data", it.event)
                    startActivity(intent)
                }
            }
        }

        viewModel.isEmptyViewVisible.observe(this) { result ->
            binding.emptyView.visibility = if (result) View.VISIBLE else View.GONE

        }
    }

    private fun setUpAddButtonAction() {
        val addButton = binding.addButton
        addButton.backgroundTintList =
            ContextCompat.getColorStateList(this@EventHomeActivity, R.color.black_blue_bg)
        addButton.setOnClickListener {
            Helper.expandCircleAnimation(
                listOf(
                    binding.addButton,
                    binding.addIcon,
                    binding.pageHeaderTV,
                    binding.eventRV,
                    binding.bottomBar
                ),
                binding.invisibleCircle,
                19f
            ) {
                val intent = Intent(this@EventHomeActivity, CreateEventActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun setUpButtonAction() {
        val pref: SharedPreferences = getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        if (username == "OP") {
            setUpAddButtonAction()
        } else if (username != "ast") {
            val positionAPI = RetrofitHelper.getInstance().create(PositionAPIInterface::class.java)
            val call = positionAPI.getPositions(username!!)
            call.enqueue(object : Callback<List<String>> {
                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (response.code() == 200 && response.body() != null) {
                        if (response.body().contains("Software Operation Management Officer")) {
                            setUpAddButtonAction()
                        }
                    } else {
                        Toast.makeText(
                            this@EventHomeActivity,
                            "Failed to fetch User Position data!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>?, t: Throwable) {
                    Toast.makeText(
                        this@EventHomeActivity,
                        t.message ?: "Unknown error!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        binding.requestButton.setOnClickListener {
            redirectToRequestHomePage()
        }
    }

    private fun redirectToRequestHomePage() {
        val intent = Intent(this@EventHomeActivity, RequestHomeActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        Helper.onResumeShrinkBack(
            binding.invisibleCircle,
            listOf(
                binding.addButton,
                binding.addIcon,
                binding.pageHeaderTV,
                binding.eventRV,
                binding.bottomBar
            ),
            false
        )
        viewModel.fetchEvents()
    }


}