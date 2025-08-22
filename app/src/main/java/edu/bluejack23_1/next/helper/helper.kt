package edu.bluejack23_1.next.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import edu.bluejack23_1.next.cache.ThumbnailCache
import edu.bluejack23_1.next.model.interfaces.BinusianAPIInterface
import edu.bluejack23_1.next.model.BinusianEmails
import edu.bluejack23_1.next.model.BinusianResponse
import edu.bluejack23_1.next.model.ClassAPIFetchCallback
import edu.bluejack23_1.next.model.ClassInformation
import edu.bluejack23_1.next.model.Email
import edu.bluejack23_1.next.model.interfaces.ClassAPIInterface
import edu.bluejack23_1.next.model.LeaderAPIFetchCallback
import edu.bluejack23_1.next.model.interfaces.LeaderAPIInterface
import edu.bluejack23_1.next.model.interfaces.SemesterAPIInterface
import edu.bluejack23_1.next.model.LeaderResponse
import edu.bluejack23_1.next.model.interfaces.ThumbnailAPIInterface
import edu.bluejack23_1.next.model.UserAPIFetchCallback
import edu.bluejack23_1.next.model.interfaces.UserAPIInterface
import edu.bluejack23_1.next.model.SemesterResponse
import edu.bluejack23_1.next.model.UserResponse
import edu.bluejack23_1.next.retrofit.RetrofitHelper
import edu.bluejack23_1.next.retrofit.RetrofitLeaderHelper
import edu.bluejack23_1.next.views.auth.LoginActivity
import edu.bluejack23_1.next.views.events.EventHomeActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Helper {
    fun setUpSpinner(
        spinner: Spinner,
        options: List<String>,
        context: Context,
        onSelected: (String) -> Unit
    ) {
        val adapter = object :
            ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, options) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val selectedItem = options[position]
                    onSelected(selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    fun setUpDatePicker(
        trigger: TextInputEditText,
        supportFragmentManager: FragmentManager
    ) {
        trigger.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTitleText("Select event date")

            val picker = builder.build()
            picker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                val formattedText = sdf.format(Date(selection))
                trigger.setText(formattedText.toString())
            }

            picker.show(supportFragmentManager, picker.toString())
        }
    }

    fun expandCircleAnimation(
        elementToFadeOut: List<View>,
        circle: View,
        expandTo: Float,
        onAnimationEnd: () -> Unit
    ) {
        for (e in elementToFadeOut) {
            e.animate().alpha(0f).setDuration(500).start()
        }

        circle.isVisible = true

        val scaleX = ObjectAnimator.ofFloat(circle, "scaleX", expandTo)
        val scaleY = ObjectAnimator.ofFloat(circle, "scaleY", expandTo)

        val set = AnimatorSet()

        set.playTogether(scaleX, scaleY)
        set.duration = 300
        set.interpolator = AccelerateDecelerateInterpolator()

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                onAnimationEnd()
            }
        })

        set.start()
    }

    fun onResumeShrinkBack(
        circle: View,
        elementsToFadeIn: List<View>,
        toggleCircleView: Boolean,
    ) {
        // If the circle is currently visible and scaled up, play the shrinking animation
        if (circle.isVisible && circle.scaleX > 1f) {
            shrinkCircleAnimation(circle, elementsToFadeIn, toggleCircleView)
        }
    }

    private fun shrinkCircleAnimation(
        circle: View,
        elementsToFadeIn: List<View>,
        toggleCircleView: Boolean
    ) {
        // Fade components in
        for (element in elementsToFadeIn) {
            element.animate().alpha(1f).setDuration(300).start()
        }

        val scaleX = ObjectAnimator.ofFloat(circle, "scaleX", 1f)
        val scaleY = ObjectAnimator.ofFloat(circle, "scaleY", 1f)

        val set = AnimatorSet()
        set.playTogether(scaleX, scaleY)
        set.duration = 700
        set.interpolator = AccelerateDecelerateInterpolator()

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                circle.isVisible = toggleCircleView
            }
        })

        set.start()
    }

    fun initializeFadeInElement(elements: List<View>) {
        for(e in elements) {
            e.animate().alpha(1f).setDuration(500).start()
        }
    }

    //Jika sudah log in, maka redirect ke EventHomePage
    fun checkUserLogIn(context: Context, activity: AppCompatActivity) {
        val pref: SharedPreferences =
            context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        if (username != "") {
            context.startActivity(Intent(activity, EventHomeActivity::class.java))
            activity.finish()
        }
    }

    //Jika sudah log out, maka redirect ke LoginPage
    fun checkUserLogOut(context: Context, activity: AppCompatActivity) {
        val pref: SharedPreferences =
            context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        if (username.equals("")) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            activity.finish()
        }
    }

    fun fetchUserDataAPI(callback: UserAPIFetchCallback, username: String){
        val userAPI = RetrofitHelper.getInstance().create(UserAPIInterface::class.java)
        val call = userAPI.getUser(username)
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.code() == 200 && response.body() != null) {
                    callback.onUserFetched(response.body())
                } else {
                    callback.onUserFetched(null)
                }
            }

            override fun onFailure(call: Call<UserResponse>?, t: Throwable) {
                callback.onFailure(t.message ?: "Unknown error")
            }
        })
    }

    suspend fun fetchParticipantDataAPI(username: String): UserResponse? = suspendCoroutine { continuation ->
        val userAPI = RetrofitHelper.getInstance().create(UserAPIInterface::class.java)
        val call = userAPI.getUser(username)
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.code() == 200 && response.body() != null) {
                    continuation.resume(response.body())
                } else {
                    continuation.resume(null)
                }
            }

            override fun onFailure(call: Call<UserResponse>?, t: Throwable) {
                continuation.resume(null)
            }
        })
    }

    suspend fun fetchParticipantEmailDataAPI(binusianId: String): BinusianResponse? = suspendCoroutine { continuation ->
        val binusianAPI = RetrofitHelper.getInstance().create(BinusianAPIInterface::class.java)
        val call = binusianAPI.getBinusian(binusianId)
        call.enqueue(object : Callback<BinusianResponse> {
            override fun onResponse(call: Call<BinusianResponse>, response: Response<BinusianResponse>) {
                if (response.code() == 200 && response.body() != null) {
                    continuation.resume(response.body())
                } else {
                    continuation.resume(null)
                }
            }

            override fun onFailure(call: Call<BinusianResponse>?, t: Throwable) {
                continuation.resume(null)
            }
        })
    }

    fun setThumbnailUser(activity: Activity, username: String, iv: ImageView){
        val cacheBitmap: Bitmap? = ThumbnailCache.getCache(username)
        if(cacheBitmap != null){
            iv.setImageBitmap(cacheBitmap)
        }
        else {
            fetchUserDataAPI(object : UserAPIFetchCallback {
                override fun onUserFetched(userResponse: UserResponse?) {
                    if (userResponse == null) {
                        Toast.makeText(activity, "Failed to fetch user data!", Toast.LENGTH_SHORT).show()
                    } else if (userResponse.PictureId == null) {
                        Toast.makeText(activity,"User thumbnail not found!", Toast.LENGTH_SHORT).show()
                    } else {
                        val thumbnailAPI = RetrofitHelper.getInstance().create(ThumbnailAPIInterface::class.java)
                        val call = thumbnailAPI.getThumbnail(userResponse.PictureId!!)
                        call.enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.code() == 200 && response.body() != null) {
                                    val thumbnailBytes = response.body().bytes()
                                    val thumbnailBitmap = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.size)
                                    ThumbnailCache.insertCache(username, thumbnailBitmap)
                                    iv.setImageBitmap(thumbnailBitmap)
                                } else {
                                    Toast.makeText(activity, "Failed to fetch thumbnail data!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable) {
                                Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }

                override fun onFailure(errorMessage: String) {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }, username)
        }
    }

    fun fetchThumbnailID(username: String, callback: (String?) -> Unit){
        fetchUserDataAPI(object : UserAPIFetchCallback {
            override fun onUserFetched(userResponse: UserResponse?) {
                if (userResponse == null) {
                    Log.d("FETCH_THUMBNAIL_ID", "Failed to fetch user data!")
                    callback("-")
                } else if (userResponse.PictureId == null) {
                    Log.d("FETCH_THUMBNAIL_ID", "User thumbnail not found!")
                    callback("-")
                } else {
                    callback("https://bluejack.binus.ac.id/lapi/api/Account/GetThumbnail?id=" + userResponse.PictureId)
                }
            }

            override fun onFailure(errorMessage: String) {
                Log.d("FETCH_THUMBNAIL_ID", errorMessage)
            }
        }, username)
    }

    fun getBinusianEmail(emails: Array<BinusianEmails>): String? {
        var eduEmail: String? = null
        var acidEmail: String? = null
        var homeEmail: String? = null
        for (email in emails) {
            when (email.Type) {
                "CAWK" -> eduEmail = email.Email
                "CAMP" -> acidEmail = email.Email
                "HOME" -> homeEmail = email.Email
            }
        }
        return eduEmail ?: acidEmail ?: homeEmail
    }

    fun setEmailUser(activity: Activity, username: String, tv: TextView){
        fetchUserDataAPI(object : UserAPIFetchCallback {
            override fun onUserFetched(userResponse: UserResponse?) {
                if (userResponse == null) {
                    Toast.makeText(activity, "Failed to fetch user data!", Toast.LENGTH_SHORT).show()
                } else if (userResponse.BinusianId == null) {
                    Toast.makeText(activity,"User Binusian ID not found!", Toast.LENGTH_SHORT).show()
                } else {
                    val binusianAPI = RetrofitHelper.getInstance().create(BinusianAPIInterface::class.java)
                    val call = binusianAPI.getBinusian(userResponse.BinusianId!!)
                    call.enqueue(object : Callback<BinusianResponse> {
                        override fun onResponse(call: Call<BinusianResponse>, response: Response<BinusianResponse>) {
                            if (response.code() == 200 && response.body() != null && response.body().Emails != null) {
                                tv.text = getBinusianEmail(response.body().Emails!!)
                            } else {
                                Toast.makeText(activity, "Failed to fetch User Binusian data!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<BinusianResponse>?, t: Throwable) {
                            Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }, username)
    }

    fun fetchLeaderDataAPI(callback: LeaderAPIFetchCallback, username: String){
        val leaderAPI = RetrofitLeaderHelper.getInstance().create(LeaderAPIInterface::class.java)
        val call = leaderAPI.getLeader(RequestBody.create(MediaType.parse("application/json"), "{\"username\": \"$username\"}"))
        call.enqueue(object : Callback<LeaderResponse> {
            override fun onResponse(call: Call<LeaderResponse>, response: Response<LeaderResponse>) {
                if (response.code() == 200 && response.body() != null) {
                    callback.onLeaderFetched(response.body())
                } else if(response.code() == 400 && response.message() == "Leader not found") {
                    callback.onLeaderNotFound()
                } else {
                    callback.onLeaderFetched(null)
                }
            }

            override fun onFailure(call: Call<LeaderResponse>?, t: Throwable) {
                callback.onFailure(t.message ?: "Unknown error")
            }
        })
    }

    fun fetchClassTransactionDataAPI(callback: ClassAPIFetchCallback, username: String){
        val semesterAPI = RetrofitHelper.getInstance().create(SemesterAPIInterface::class.java)
        val call = semesterAPI.getActiveSemester()
        call.enqueue(object : Callback<SemesterResponse> {
            override fun onResponse(call: Call<SemesterResponse>, response: Response<SemesterResponse>) {
                if (response.code() == 200 && response.body() != null && response.body().SemesterId != null) {
                    val classAPI = RetrofitHelper.getInstance().create(ClassAPIInterface::class.java)
                    val call = classAPI.getClassTransactions(username, response.body().SemesterId!!)
                    call.enqueue(object : Callback<List<ClassInformation>> {
                        override fun onResponse(call: Call<List<ClassInformation>>, response: Response<List<ClassInformation>>) {
                            if (response.code() == 200 && response.body() != null) {
                                callback.onClassFetched(response.body())
                            } else {
                                callback.onClassFetched(null)
                            }
                        }

                        override fun onFailure(call: Call<List<ClassInformation>>?, t: Throwable) {
                            callback.onFailure(t.message ?: "Unknown error")
                        }
                    })
                } else {
                    callback.onClassFetched(null)
                }
            }

            override fun onFailure(call: Call<SemesterResponse>?, t: Throwable) {
                callback.onFailure(t.message ?: "Unknown error")
            }
        })
    }

    fun isDateFuture(date: String): Boolean{
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val calendar = Calendar.getInstance()

        try {
            val parsedDate = dateFormat.parse(date)
            if (parsedDate != null) {
                Log.d("bluejack_date", "Parsed Date: $parsedDate")

                val currentDate = calendar.time
                Log.d("bluejack_date", "Current Date: $currentDate")

                val isFuture = parsedDate.after(currentDate)
                Log.d("bluejack_date", "Is Future Date: $isFuture")

                return isFuture
            }
        } catch (e: Exception) {
            Log.d("bluejack_date", "Invalid Date!")
            return false
        }
        return false
    }

    var partner_email = ""
    var leader_email = ""
    var leader_partner_email = ""

    fun sendExtraClassRequestOulookEmail(activity: Activity, partner: String, course: String, date: String, shift: String, class_code: String, student: String, location: String, emails: List<Email>){
        val pref: SharedPreferences = activity.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        partner_email = ""
        leader_email = ""
        leader_partner_email = ""

        if (username == "AST" || username == "OP"){
            sendOutlookEmailExtraClass(activity, username, partner, course, date, shift, class_code, student, location, emails)
            return
        } else if (partner == "-"){
            fetchLeaderData(activity, username!!, partner, course, date, shift, class_code, student, location, emails)
            return
        }

        fetchUserDataAPI(object : UserAPIFetchCallback {
            override fun onUserFetched(userResponse: UserResponse?) {
                if (userResponse == null) {
                    Toast.makeText(activity, "Failed to fetch partner data!", Toast.LENGTH_SHORT).show()
                } else if (userResponse.BinusianId == null) {
                    Toast.makeText(activity,"Partner Binusian ID not found!", Toast.LENGTH_SHORT).show()
                } else {
                    val binusianAPI = RetrofitHelper.getInstance().create(BinusianAPIInterface::class.java)
                    val call = binusianAPI.getBinusian(userResponse.BinusianId!!)
                    call.enqueue(object : Callback<BinusianResponse> {
                        override fun onResponse(call: Call<BinusianResponse>, response: Response<BinusianResponse>) {
                            if (response.code() == 200 && response.body() != null && response.body().Emails != null) {
                                partner_email = getBinusianEmail(response.body().Emails!!)!!
                                fetchLeaderData(activity, username!!, partner, course, date, shift, class_code, student, location, emails)
                            } else {
                                Toast.makeText(activity, "Failed to fetch User Binusian data!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<BinusianResponse>?, t: Throwable) {
                            Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }, partner)
    }

    fun fetchLeaderData(activity: Activity, username: String, partner: String, course: String, date: String, shift: String, class_code: String, student: String, location: String, emails: List<Email>) {
        val leaderAPI = RetrofitLeaderHelper.getInstance().create(LeaderAPIInterface::class.java)
        val call = leaderAPI.getLeader(RequestBody.create(MediaType.parse("application/json"), "{\"username\": \"$username\"}"))
        call.enqueue(object : Callback<LeaderResponse> {
            override fun onResponse(call: Call<LeaderResponse>, response: Response<LeaderResponse>) {
                if (response.code() == 200 && response.body() != null && response.body().username != null) {
                    fetchUserDataAPI(object : UserAPIFetchCallback {
                        override fun onUserFetched(userResponse: UserResponse?) {
                            if (userResponse == null) {
                                Toast.makeText(activity, "Failed to fetch User Leader data!", Toast.LENGTH_SHORT).show()
                            } else if (userResponse.BinusianId == null) {
                                Toast.makeText(activity,"User Leader Binusian ID not found!", Toast.LENGTH_SHORT).show()
                            } else {
                                val binusianAPI = RetrofitHelper.getInstance().create(BinusianAPIInterface::class.java)
                                val call = binusianAPI.getBinusian(userResponse.BinusianId!!)
                                call.enqueue(object : Callback<BinusianResponse> {
                                    override fun onResponse(call: Call<BinusianResponse>, response: Response<BinusianResponse>) {
                                        if (response.code() == 200 && response.body() != null && response.body().Emails != null) {
                                            leader_email = getBinusianEmail(response.body().Emails!!)!!
                                            fetchPartnerLeaderData(activity, partner, course, date, shift, class_code, student, location, username, emails)
                                        } else {
                                            Toast.makeText(activity, "Failed to fetch User Leader Binusian data!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<BinusianResponse>?, t: Throwable) {
                                        Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        }

                        override fun onFailure(errorMessage: String) {
                            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }, response.body().username!!)
                } else if (response.code() == 400) {
                    leader_email = ""
                    fetchPartnerLeaderData(activity, username, partner, course, date, shift, class_code, student, location, emails)
                } else {
                    Toast.makeText(activity, "Failed to fetch User Leader data!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LeaderResponse>?, t: Throwable) {
                Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun fetchPartnerLeaderData(activity: Activity, partner: String, course: String, date: String, shift: String, class_code: String, student: String, location: String, username: String, emails: List<Email>) {
        if (partner == "-"){
            leader_partner_email = ""
            sendOutlookEmailExtraClass(activity, username, partner, course, date, shift, class_code, student, location, emails)
            return
        }

        val leaderAPI = RetrofitLeaderHelper.getInstance().create(LeaderAPIInterface::class.java)
        val call = leaderAPI.getLeader(RequestBody.create(MediaType.parse("application/json"), "{\"username\": \"$partner\"}"))
        call.enqueue(object : Callback<LeaderResponse> {
            override fun onResponse(call: Call<LeaderResponse>, response: Response<LeaderResponse>) {
                if (response.code() == 200 && response.body() != null && response.body().username != null) {
                    fetchUserDataAPI(object : UserAPIFetchCallback {
                        override fun onUserFetched(userResponse: UserResponse?) {
                            if (userResponse == null) {
                                Toast.makeText(activity, "Failed to fetch User Leader data!", Toast.LENGTH_SHORT).show()
                            } else if (userResponse.BinusianId == null) {
                                Toast.makeText(activity,"User Leader Binusian ID not found!", Toast.LENGTH_SHORT).show()
                            } else {
                                val binusianAPI = RetrofitHelper.getInstance().create(BinusianAPIInterface::class.java)
                                val call = binusianAPI.getBinusian(userResponse.BinusianId!!)
                                call.enqueue(object : Callback<BinusianResponse> {
                                    override fun onResponse(call: Call<BinusianResponse>, response: Response<BinusianResponse>) {
                                        if (response.code() == 200 && response.body() != null && response.body().Emails != null) {
                                            leader_partner_email = getBinusianEmail(response.body().Emails!!)!!
                                            sendOutlookEmailExtraClass(activity, username, partner, course, date, shift, class_code, student, location, emails)
                                        } else {
                                            Toast.makeText(activity, "Failed to fetch User Leader Binusian data!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<BinusianResponse>?, t: Throwable) {
                                        Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        }

                        override fun onFailure(errorMessage: String) {
                            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }, response.body().username!!)
                } else if(response.code() == 400) {
                    leader_partner_email = ""
                    sendOutlookEmailExtraClass(activity, username, partner, course, date, shift, class_code, student, location, emails)
                } else {
                    Toast.makeText(activity, "Failed to fetch User Leader data!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LeaderResponse>?, t: Throwable) {
                Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun sendOutlookEmailExtraClass(activity: Activity, username: String, partner: String, course: String, date: String, shift: String, class_code: String, student: String, location: String, emails: List<Email>) {
        val emailIntent = Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", emails[0].email, null))
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emails[1].email))
        emailIntent.putExtra(Intent.EXTRA_CC, arrayOf(partner_email, leader_email, leader_partner_email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request Extra Class")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear QMan,\n" +
                "\n" +
                "Saya ${username.uppercase()} ingin mengajukan permohonan untuk extra class dengan detail sebagai berikut:\n" +
                "Mata Kuliah: $course\n" +
                "Kelas: $class_code\n" +
                "Asisten 1: ${username.uppercase()}\n" +
                "Asisten 2: $partner\n" +
                "Tanggal/Shift: $date, Shift $shift\n" +
                "Jumlah Mahasiswa: $student\n" +
                "Ruang/Lokasi: $location\n" +
                "\n" +
                "Terima Kasih")

        try {
            activity.startActivity(Intent.createChooser(emailIntent, "Send outlook email..."))
            activity.finish()
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(activity, "No email client!", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendPermissionRequestOulookEmail(activity: Activity, permission: String, date: String, reason: String, tasks: String, emails: List<Email>){
        val pref: SharedPreferences = activity.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        partner_email = ""
        leader_email = ""
        leader_partner_email = ""

        if (username == "AST" || username == "OP"){
            sendOutlookEmailPermission(activity, username, permission, date, reason, tasks, emails)
            return
        }

        val leaderAPI = RetrofitLeaderHelper.getInstance().create(LeaderAPIInterface::class.java)
        val call = leaderAPI.getLeader(RequestBody.create(MediaType.parse("application/json"), "{\"username\": \"$username\"}"))
        call.enqueue(object : Callback<LeaderResponse> {
            override fun onResponse(call: Call<LeaderResponse>, response: Response<LeaderResponse>) {
                if (response.code() == 200 && response.body() != null && response.body().username != null) {
                    fetchUserDataAPI(object : UserAPIFetchCallback {
                        override fun onUserFetched(userResponse: UserResponse?) {
                            if (userResponse == null) {
                                Toast.makeText(activity, "Failed to fetch User Leader data!", Toast.LENGTH_SHORT).show()
                            } else if (userResponse.BinusianId == null) {
                                Toast.makeText(activity,"User Leader Binusian ID not found!", Toast.LENGTH_SHORT).show()
                            } else {
                                val binusianAPI = RetrofitHelper.getInstance().create(BinusianAPIInterface::class.java)
                                val call = binusianAPI.getBinusian(userResponse.BinusianId!!)
                                call.enqueue(object : Callback<BinusianResponse> {
                                    override fun onResponse(call: Call<BinusianResponse>, response: Response<BinusianResponse>) {
                                        if (response.code() == 200 && response.body() != null && response.body().Emails != null) {
                                            leader_email = getBinusianEmail(response.body().Emails!!)!!
                                            sendOutlookEmailPermission(activity, username!!, permission, date, reason, tasks, emails)
                                        } else if(response.code() == 400 && response.message() == "Leader not found") {
                                            leader_email = ""
                                            sendOutlookEmailPermission(activity, username!!, permission, date, reason, tasks, emails)
                                        } else {
                                            Toast.makeText(activity, "Failed to fetch User Leader Binusian data!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<BinusianResponse>?, t: Throwable) {
                                        Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        }

                        override fun onFailure(errorMessage: String) {
                            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }, response.body().username!!)
                } else {
                    Toast.makeText(activity, "Failed to fetch User Leader data!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LeaderResponse>?, t: Throwable) {
                Toast.makeText(activity, t.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun sendOutlookEmailPermission(activity: Activity, username: String, permission: String, date: String, reason: String, tasks: String, emails: List<Email>) {
        val emailIntent = Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", emails[0].email, null))
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emails[1].email))
        emailIntent.putExtra(Intent.EXTRA_CC, arrayOf(leader_email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request Permission")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear QMan,\n" +
                "\n" +
                "Saya ${username.uppercase()} ingin mengajukan permohonan izin dengan detail sebagai berikut:\n" +
                "\n" +
                "Hari/Tanggal: $date\n" +
                "Jenis izin: $permission\n" +
                "Alasan izin: $reason\n" +
                "Task on process:\n$tasks\n" +
                "\n" +
                "Terima Kasih")
        try {
            activity.startActivity(Intent.createChooser(emailIntent, "Send outlook email..."))
            activity.finish()
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(activity, "No email client!", Toast.LENGTH_SHORT).show()
        }
    }

}