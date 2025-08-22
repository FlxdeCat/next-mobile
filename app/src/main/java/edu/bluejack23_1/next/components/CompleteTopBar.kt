package edu.bluejack23_1.next.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.views.ProfileActivity
import edu.bluejack23_1.next.views.auth.LoginActivity

class CompleteTopBar : LinearLayout {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        LayoutInflater.from(context).inflate(R.layout.basic_complete_topbar, this, true)

        val typedArray = context.theme.obtainStyledAttributes(attrs,
            R.styleable.CompleteTopBar, 0, 0)

        try {
            val title = typedArray.getString(R.styleable.CompleteTopBar_pageTitle)
            title?.let {
                findViewById<TextView>(R.id.PageTV).text = it
            }
        } finally {
            typedArray.recycle()
        }

        val logoutButton = findViewById<ImageView>(R.id.logoutButton)
        val profileButton = findViewById<ImageView>(R.id.profileButton)

        val pref: SharedPreferences = context.getSharedPreferences("NeXtSharedPref", Context.MODE_PRIVATE)
        val username = pref.getString("username", "")

        if(username != "AST" && username != "OP") {
            Helper.setThumbnailUser((context as Activity), username!!, profileButton)
        }

        logoutButton.setOnClickListener {
            pref.edit().remove("username").apply()

            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            (context as Activity).finish()
        }

        profileButton.setOnClickListener {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }

    }
}
