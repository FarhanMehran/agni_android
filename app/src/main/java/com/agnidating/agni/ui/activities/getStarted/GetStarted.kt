package com.agnidating.agni.ui.activities.getStarted

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.agnidating.agni.R
import com.agnidating.agni.databinding.ActivityGetStartedBinding
import com.agnidating.agni.ui.activities.auth.phone.YourPhoneActivity
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.changeStatusBarColorToTransParent

/**
 * Created by AJAY ASIJA
 */
class GetStarted : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColorToTransParent()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_started)
        listener()
    }

    /**
     * Register all listener here
     */
    private fun listener() {
        binding.btGetStarted.setOnClickListener {
            val intent=Intent(this,YourPhoneActivity::class.java)
            intent.putExtra(CommonKeys.STATUS,"1")
            startActivity(intent)
        }
        binding.consThree.setOnClickListener {
            val intent=Intent(this,YourPhoneActivity::class.java)
            intent.putExtra(CommonKeys.STATUS,"0")
            startActivity(intent)
        }
    }
}