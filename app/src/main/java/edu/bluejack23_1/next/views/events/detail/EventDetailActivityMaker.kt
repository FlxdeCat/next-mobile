package edu.bluejack23_1.next.views.events.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.ActivityEventDetailMakerBinding
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.model.recyclerView.adapter.EventParticipantRVAdapter
import edu.bluejack23_1.next.viewmodels.events.EventDetailMakerViewModel

class EventDetailActivityMaker : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailMakerBinding
    private lateinit var participantRV: RecyclerView

    private val viewModel by viewModels<EventDetailMakerViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EventDetailMakerViewModel(
                    (intent.getSerializableExtra("event_data") as Event).id!!
                ) as T
            }
        }
    }

//    private val viewModel: EventDetailMakerViewModel by lazy {
//        ViewModelProvider(this)[EventDetailMakerViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val event = intent.getSerializableExtra("event_data") as? Event
        viewModel.initializeData(event)

        binding = ActivityEventDetailMakerBinding.inflate(layoutInflater)

        binding.apply {
            viewModel = this@EventDetailActivityMaker.viewModel
            lifecycleOwner = this@EventDetailActivityMaker
        }

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_event_detail_maker
        )


        binding.apply {
            viewModel = this@EventDetailActivityMaker.viewModel
            lifecycleOwner = this@EventDetailActivityMaker
        }
        setContentView(binding.root)

        binding.adapter = EventParticipantRVAdapter(listOf(), viewModel)

        participantRV = binding.participantRV
        participantRV.layoutManager = LinearLayoutManager(this)
        observeViewModel()

        if (event?.status != "Active") {
            binding.statusTV.setTextColor(resources.getColor(R.color.red))
        }
        setUpButtonAction()
    }

    private fun setUpButtonAction() {
        binding.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@EventDetailActivityMaker)
            builder.setMessage("Are you sure you want to delete this event?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.onDeleteButtonClicked()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        binding.closeBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@EventDetailActivityMaker)
            builder.setMessage("Are you sure you want to close this event?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.onCloseEventButtonClicked()
                    Log.d("Eventnya di close", viewModel.validationState.value?.event.toString())
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun observeViewModel() {
        viewModel.validationState.observe(this) { result ->
            result?.let {
                if (it.isError) {
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }  else if (it.isDeleted){
                    onBackPressedDispatcher.onBackPressed()
                    overridePendingTransition(0, 0)
                } else if (!it.isAvailable) {
                    binding.closeBtn.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.dark_gray)
                    binding.closeBtn.isEnabled = false
                    binding.statusTV.setTextColor(resources.getColor(R.color.red))
                    binding.statusTV.text = "Closed"
                } else {
                    binding.closeBtn.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.red)
                }
            }
        }
    }

}