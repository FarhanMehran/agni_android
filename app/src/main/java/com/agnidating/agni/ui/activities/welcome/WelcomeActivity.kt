package com.agnidating.agni.ui.activities.welcome

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityWelcomeBinding
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity


class WelcomeActivity : ScopedActivity() {
    lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.
        setContentView(this,R.layout.activity_welcome)
        binding.btNext.setOnClickListener {
            startActivity(Intent(this,DashboardActivity::class.java))
            finish()
        }

    }
}