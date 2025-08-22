package edu.bluejack23_1.next.views.events.create

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.databinding.ActivityCreateEventDetailBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.viewmodels.events.CreateEventDetailViewModel
import edu.bluejack23_1.next.views.events.EventHomeActivity

class CreateEventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventDetailBinding
    private lateinit var imageResultActivity: ActivityResultLauncher<Intent>
    private lateinit var viewModel: CreateEventDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the attribute from the last activity
        val eventName = intent.getStringExtra("eventName")
        val eventDate = intent.getStringExtra("eventDate")
        val eventLocation = intent.getStringExtra("eventLocation")
        val eventParticipant = intent.getStringExtra("eventParticipant")
        val eventReward = intent.getStringExtra("eventReward")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event_detail)
        viewModel = ViewModelProvider(this)[CreateEventDetailViewModel::class.java]
        binding.viewModel = viewModel


        viewModel.initializeEvent(
            eventName!!,
            eventDate!!,
            eventLocation!!,
            eventParticipant!!,
            eventReward!!
        )

        setUpContent()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.validationState.observe(this) { result ->
            result?.let {
                if (it.isSubmitted) {
                    Toast.makeText(this, "Create Event Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, EventHomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finishAndRemoveTask()
                } else if (!it.isValid) {
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("Changes in Live Data", it.toString())
                }
            }
        }
    }

    private fun setUpContent() {
        Helper.initializeFadeInElement(listOf(binding.linearLayout))
        setUpButtonAction()

        binding.imageField.setOnClickListener {
            checkExternalStoragePermission()
        }

        binding.clearImageInput.setOnClickListener {
            binding.bannerImageIV.setImageURI(null)
            binding.bannerImageIV.visibility = View.GONE
            binding.clearImageInput.visibility = View.GONE
            binding.clearImageInput.isClickable = false
        }

        imageResultActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                try {
                    if (res.data != null) {
                        val imageURI = res.data?.data
                        viewModel.setImageURI(imageURI)
                        binding.bannerImageIV.setImageURI(imageURI)
                        binding.bannerImageIV.visibility = View.VISIBLE

                        binding.clearImageInput.visibility = View.VISIBLE
                        binding.clearImageInput.focusable = View.FOCUSABLE
                        binding.clearImageInput.isClickable = true
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    private fun checkExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                101
            )
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        imageResultActivity.launch(galleryIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        }
    }

    private fun setUpButtonAction() {
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(0, 0)
        }

        binding.nextBtn.setOnClickListener {
            viewModel.onSubmitButtonClicked(binding.eventNotesET.text.toString(), this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(0, 0)
    }

}