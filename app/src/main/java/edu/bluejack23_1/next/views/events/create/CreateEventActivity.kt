package edu.bluejack23_1.next.views.events.create

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.next.databinding.ActivityCreateEventBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.viewmodels.events.CreateEventViewModel


class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding
    private lateinit var viewModel: CreateEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CreateEventViewModel::class.java]

        setUpContent()
        observeViewModel()
    }

    private fun setUpContent() {
        setUpButtonActions()

        Helper.initializeFadeInElement(listOf(binding.linearLayout))
        Helper.setUpDatePicker(binding.eventDateText, supportFragmentManager)
    }

    private fun setUpButtonActions() {
        binding.nextBtn.setOnClickListener {
            viewModel.onNextButtonClicked(
                binding.eventNameET.text.toString(),
                binding.eventDateText.text.toString(),
                binding.participantsET.text.toString(),
                binding.locationET.text.toString(),
                binding.rewardET.text.toString()
            )
        }

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(0, 0)
        }
    }

    private fun observeViewModel() {
        viewModel.validationState.observe(this) { result ->
            result?.let {
                if (it.isValid) {
                    navigateToDetailPage()
                } else {
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun navigateToDetailPage() {
        Helper.expandCircleAnimation(
            listOf(binding.linearLayout),
            binding.circleBackground,
            1.09f
        ) {
            val intent = Intent(this@CreateEventActivity, CreateEventDetailActivity::class.java)
            intent.putExtra("eventName", binding.eventNameET.text.toString())
            intent.putExtra("eventDate", binding.eventDateText.text.toString())
            intent.putExtra("eventParticipant", binding.participantsET.text.toString())
            intent.putExtra("eventLocation", binding.locationET.text.toString())
            intent.putExtra("eventReward", binding.rewardET.text.toString())
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        Helper.onResumeShrinkBack(
            binding.circleBackground,
            listOf(binding.linearLayout),
            true
        )
    }
}