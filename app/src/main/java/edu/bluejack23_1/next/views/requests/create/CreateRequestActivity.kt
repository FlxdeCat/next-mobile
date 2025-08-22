package edu.bluejack23_1.next.views.requests.create

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.ActivityCreateRequestBinding
import edu.bluejack23_1.next.helper.Helper
import javax.annotation.Nullable

class CreateRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRequestBinding

    private var requestTypeValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpContent()
        setUpButtonAction()
    }

    private fun setUpContent() {
        Helper.setUpSpinner(
            binding.eventTypeSpinner,
            resources.getStringArray(R.array.request_type_array).toList(), this
        ) { s: String ->
            requestTypeValue = s
        }

        Helper.initializeFadeInElement(listOf(binding.linearLayout))
    }

    private fun setUpButtonAction() {
        binding.nextBtn.setOnClickListener {
            if (requestTypeValue != "Permission" && requestTypeValue != "Extra Class") {
                Toast.makeText(
                    this@CreateRequestActivity,
                    "Please Select the Request Type",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Helper.expandCircleAnimation(
                    listOf(binding.linearLayout),
                    binding.circleBackground,
                    1.09f
                ) {
                    if (requestTypeValue == "Permission") {
                        val intent =
                            Intent(this@CreateRequestActivity, CreatePermissionActivity::class.java)
                        startActivityForResult(intent, 231)
                        overridePendingTransition(0, 0)
                    } else if (requestTypeValue == "Extra Class") {
                        val intent =
                            Intent(this@CreateRequestActivity, CreateExtraClassActivity::class.java)
                        startActivityForResult(intent, 231)
                        overridePendingTransition(0, 0)
                    }
                }
            }
        }

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
            listOf(binding.linearLayout), true
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 231) {
            if (resultCode == RESULT_OK && data != null) {
                val shouldClose = data.getBooleanExtra("closePreviousActivity", false)
                if (shouldClose) {
                    finish()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }


}