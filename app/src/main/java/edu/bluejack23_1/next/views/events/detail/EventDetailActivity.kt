package edu.bluejack23_1.next.views.events.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.ActivityEventDetailBinding
import edu.bluejack23_1.next.model.Event
import edu.bluejack23_1.next.viewmodels.events.EventDetailViewModel

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding

    private val viewModel: EventDetailViewModel by lazy {
        ViewModelProvider(this)[EventDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val event = intent.getSerializableExtra("event_data") as? Event
        viewModel.initializeData(event, this)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_event_detail
        )

        binding.apply {
            viewModel = this@EventDetailActivity.viewModel
        }

        setContentView(binding.root)

        if (event?.status != "Active") {
            binding.statusTV.setTextColor(resources.getColor(R.color.red))
            binding.participateBtn.backgroundTintList =
                ContextCompat.getColorStateList(this@EventDetailActivity, R.color.dark_gray)
        } else {
            binding.participateBtn.backgroundTintList =
                ContextCompat.getColorStateList(this@EventDetailActivity, R.color.green)
        }

        setUpButtonAction()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.validationState.observe(this) { result ->
            result?.let {
                if (it.isError) {
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } else if (!it.isAvailable) {
                    binding.participateBtn.backgroundTintList =
                        ContextCompat.getColorStateList(this@EventDetailActivity, R.color.dark_gray)
                    binding.participateBtn.isEnabled = false
                    binding.participantsCountTV.text = "${it.event?.participantsCount?.plus(1)} participated"
                } else if (it.isAvailable) {
                    binding.participateBtn.backgroundTintList =
                        ContextCompat.getColorStateList(this@EventDetailActivity, R.color.green)
                    binding.participateBtn.isEnabled = true
                    binding.participantsCountTV.text = "${it.event?.participantsCount} participated"
                }
            }
        }
    }

    private fun setUpButtonAction() {
        binding.participateBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@EventDetailActivity)
            builder.setMessage("Are you sure you want to participate in this event?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.onParticipateButtonClicked(this)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }
}