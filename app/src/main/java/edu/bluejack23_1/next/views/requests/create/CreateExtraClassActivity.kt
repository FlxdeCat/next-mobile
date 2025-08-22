package edu.bluejack23_1.next.views.requests.create

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.next.databinding.ActivityCreateExtraClassBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.ClassAPIFetchCallback
import edu.bluejack23_1.next.model.ClassInformation
import edu.bluejack23_1.next.viewmodels.requests.CreateExtraClassRequestViewModel

class CreateExtraClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateExtraClassBinding
    private lateinit var selectedCourse: String
    private lateinit var selectedPartner: String
    private lateinit var selectedClass: String
    private lateinit var selectedStudent: String
    private lateinit var viewModel: CreateExtraClassRequestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateExtraClassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CreateExtraClassRequestViewModel::class.java]

        selectedPartner = ""
        selectedCourse = ""
        selectedClass = ""
        selectedStudent = ""

        setUpContent()
        observeViewModel()

        setUpButtonAction()
    }

    private fun setUpContent() {
        val pref: SharedPreferences = getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        val transactionList = mutableSetOf<String>()
        transactionList.add("Please choose a class transaction")

        if(username == "AST" || username == "OP"){
            transactionList.add("-")

            Helper.setUpSpinner(
                binding.transactionSpinner,
                transactionList.toList(),
                this@CreateExtraClassActivity
            ) { s: String ->
                if (s == "-") {
                    selectedCourse = "-"
                    selectedPartner = "-"
                    selectedClass = "-"
                    selectedStudent = "0"
                }
            }
        }
        else {
            Helper.fetchClassTransactionDataAPI(object : ClassAPIFetchCallback {
                override fun onClassFetched(classLists: List<ClassInformation>?) {
                    if (classLists == null) {
                        Toast.makeText(
                            this@CreateExtraClassActivity,
                            "Failed to fetch class transaction data!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        for (classList in classLists) {
                            transactionList.add("${classList.Class} | ${classList.Subject} | ${classList.Assistant} | ${classList.TotalStudent}")
                        }

                        Helper.setUpSpinner(
                            binding.transactionSpinner,
                            transactionList.toList(),
                            this@CreateExtraClassActivity
                        ) { s: String ->
                            if(s != "Please choose a class transaction"){
                                val transaction = s.split(" | ")
                                selectedCourse = transaction[1]
                                selectedClass = transaction[0]
                                selectedStudent = transaction[3]
                                val assistants = transaction[2].split(", ").toMutableList()
                                selectedPartner = if (assistants.size == 1 && assistants[0] == username){
                                    "-"
                                } else {
                                    assistants.remove(username)
                                    assistants[0]
                                }
                            }
                        }
                    }
                }

                override fun onFailure(errorMessage: String) {
                    Toast.makeText(this@CreateExtraClassActivity, errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }, username!!)
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
                this@CreateExtraClassActivity,
                selectedPartner,
                selectedCourse,
                binding.requestDateText.text.toString(),
                binding.requestShiftText.text.toString(),
                selectedClass,
                selectedStudent,
                binding.requestLocationText.text.toString(),
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