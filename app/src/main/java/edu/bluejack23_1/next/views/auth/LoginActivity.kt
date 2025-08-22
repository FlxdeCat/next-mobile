package edu.bluejack23_1.next.views.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack23_1.next.views.events.EventHomeActivity
import edu.bluejack23_1.next.databinding.ActivityLoginBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.AccountResponse
import edu.bluejack23_1.next.model.interfaces.LoginAPIInterface
import edu.bluejack23_1.next.retrofit.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper.checkUserLogIn(this, this@LoginActivity)

        binding.loginButton.setOnClickListener {
            binding.loginButton.isEnabled = false
            val username = binding.usernameET.text.toString()
            val password = binding.passwordET.text.toString()
            login(username, password)
        }
    }

    private fun login(username: String, password: String){
        val pref: SharedPreferences = getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)

        if((username == "ast" || username == "op") && password == "dummy"){
            val editor = pref.edit()
            editor.putString("username", username.uppercase())
            editor.apply()
            startActivity(Intent(this@LoginActivity, EventHomeActivity::class.java))
            binding.loginButton.isEnabled = true
            finish()
        }
        else {
            val loginAPI = RetrofitHelper.getInstance().create(LoginAPIInterface::class.java)
            val call = loginAPI.login(username, password)
            call.enqueue(object : Callback<AccountResponse> {
                override fun onResponse(
                    call: Call<AccountResponse>,
                    response: Response<AccountResponse>
                ) {
                    binding.loginButton.isEnabled = true
                    if (response.code() == 200) {
                        val editor = pref.edit()
                        editor.putString("username", username.uppercase())
                        editor.apply()
                        startActivity(Intent(this@LoginActivity, EventHomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Login!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    binding.loginButton.isEnabled = true
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}