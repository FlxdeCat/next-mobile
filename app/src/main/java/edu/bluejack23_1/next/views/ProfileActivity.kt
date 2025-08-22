package edu.bluejack23_1.next.views

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import edu.bluejack23_1.next.databinding.ActivityProfileBinding
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.LeaderAPIFetchCallback
import edu.bluejack23_1.next.model.LeaderResponse
import edu.bluejack23_1.next.model.UserAPIFetchCallback
import edu.bluejack23_1.next.model.UserResponse

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper.checkUserLogOut(this, this@ProfileActivity)

        binding.backButton.setOnClickListener {
            finish()
        }

        val pref: SharedPreferences = getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        if(username != "AST" && username != "OP"){
            fetchProfilePageData()
        }
        else{
            binding.nameTV.text = username.uppercase()
            binding.usernameTV.text = username.uppercase()
            binding.emailTV.text = ""

            binding.leaderTV.text = ""
            binding.leaderUsernameTV.text = ""
            binding.leaderNameTV.text = ""
            binding.leaderEmailTV.text = ""
            binding.leaderProfilePicture.setImageDrawable(null)
        }

    }

    private fun fetchProfilePageData(){
        val pref: SharedPreferences = getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        Helper.fetchUserDataAPI(object : UserAPIFetchCallback {
            override fun onUserFetched(userResponse: UserResponse?) {
                if(userResponse == null){
                    Toast.makeText(this@ProfileActivity, "Failed to fetch user data!", Toast.LENGTH_SHORT).show()
                }
                else{
                    binding.nameTV.text = userResponse.Name
                    binding.usernameTV.text = userResponse.Username

                    userResponse.PictureId?.let {
                        Helper.setThumbnailUser(this@ProfileActivity, userResponse.Username!!, binding.profilePicture)
                    } ?: run {
                        Toast.makeText(this@ProfileActivity, "Thumbnail not found!", Toast.LENGTH_SHORT).show()
                    }

                    userResponse.BinusianId?.let {
                        Helper.setEmailUser(this@ProfileActivity, userResponse.Username!!, binding.emailTV)
                    } ?: run {
                        Toast.makeText(this@ProfileActivity, "Binusian ID not found!", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }, username!!)

        Helper.fetchLeaderDataAPI(object : LeaderAPIFetchCallback {
            override fun onLeaderFetched(leaderResponse: LeaderResponse?) {
                if(leaderResponse == null){
                    binding.leaderTV.text = ""
                    binding.leaderUsernameTV.text = ""
                    binding.leaderNameTV.text = ""
                    binding.leaderEmailTV.text = ""
                    binding.leaderProfilePicture.setImageDrawable(null)
                    Toast.makeText(this@ProfileActivity, "Failed to fetch leader user data!", Toast.LENGTH_SHORT).show()
                }
                else{
                    binding.leaderUsernameTV.text = leaderResponse.username

                    if(leaderResponse.username != null) {
                        Helper.fetchUserDataAPI(object : UserAPIFetchCallback {
                            override fun onUserFetched(userResponse: UserResponse?) {
                                if (userResponse == null) {
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Failed to fetch leader data!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    binding.leaderNameTV.text = userResponse.Name

                                    userResponse.PictureId?.let {
                                        Helper.setThumbnailUser(
                                            this@ProfileActivity,
                                            userResponse.Username!!,
                                            binding.leaderProfilePicture
                                        )
                                    } ?: run {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Thumbnail leader not found!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    userResponse.BinusianId?.let {
                                        Helper.setEmailUser(
                                            this@ProfileActivity,
                                            userResponse.Username!!,
                                            binding.leaderEmailTV
                                        )
                                    } ?: run {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Binusian ID leader not found!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }

                            override fun onFailure(errorMessage: String) {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, leaderResponse.username!!)
                    }
                    else{
                        Toast.makeText(this@ProfileActivity, "Failed to get leader username!", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onLeaderNotFound() {
                binding.leaderTV.text = ""
                binding.leaderUsernameTV.text = ""
                binding.leaderNameTV.text = ""
                binding.leaderEmailTV.text = ""
                binding.leaderProfilePicture.setImageDrawable(null)
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }, username)
    }

}