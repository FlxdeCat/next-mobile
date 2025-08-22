package edu.bluejack23_1.next.components

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import edu.bluejack23_1.next.R

class BackTopBar : LinearLayout {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        LayoutInflater.from(context).inflate(R.layout.back_topbar, this, true)

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.BackTopBar, 0, 0)

        try {
            val title = typedArray.getString(R.styleable.BackTopBar_pageTitleBTB)
            title?.let {
                findViewById<TextView>(R.id.PageTV).text = it
            }
        } finally {
            typedArray.recycle()
        }

        val backBtn = findViewById<ImageView>(R.id.navbarBackBtn)

        backBtn.setOnClickListener {
            if (context is Activity) {
                context.onBackPressed()
                context.overridePendingTransition(0, 0)
            }
        }

    }
}
