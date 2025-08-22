package edu.bluejack23_1.next.views.requests.create

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.next.databinding.ActivityCreatePermissionBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.viewmodels.requests.CreatePermissionRequestViewModel


class CreatePermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePermissionBinding
    private lateinit var selectedPermissionType: String
    private lateinit var viewModel: CreatePermissionRequestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CreatePermissionRequestViewModel::class.java]

        selectedPermissionType = ""

        setUpContent()
        observeViewModel()

        setUpButtonAction()
    }

    private fun setUpContent() {
        Helper.setUpSpinner(
            binding.permissionTypeSpinner,
            listOf<String>(
                "Please choose permission",
                "Out of Office Permission",
                "First 4 Hours Work Permission",
                "Last 4 Hours Work Permission",
                "Sickness Leave",
                "Unpaid Leave"
            ),
            this
        ) { s: String ->
            selectedPermissionType = s
        }

        Helper.setUpDatePicker(
            binding.requestDateText,
            supportFragmentManager
        )

        Helper.initializeFadeInElement(listOf(binding.linearLayout))
    }

    private fun setUpButtonAction() {
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(0, 0)
        }

        binding.nextBtn.setOnClickListener {
            viewModel.onNextButtonClicked(
                this@CreatePermissionActivity,
                selectedPermissionType,
                binding.requestDateText.text.toString(),
                binding.requestReasonText.text.toString(),
                binding.onProgressTaskText.text.toString(),
                this
            )
        }
    }

    private fun observeViewModel() {
        viewModel.validationState.observe(this) { result ->
            result?.let {
                if (it.isValid) {
                    Toast.makeText(this, "Create Request Success", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("closePreviousActivity", true)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(0, 0)
    }
}