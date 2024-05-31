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
        var otp_share = com.agnidating.agni.utils.sharedPreference.Otp_pref(this)
        var otp_count = otp_share.get_count(CommonKeys.OTP_COUNT,0)

        binding.btGetStarted.setOnClickListener {
            val intent=Intent(this,YourPhoneActivity::class.java)
            intent.putExtra(CommonKeys.STATUS,"1")
            intent.putExtra(CommonKeys.OTP_COUNT_INTENT_KEY,otp_count)
            startActivity(intent)
        }
        binding.consThree.setOnClickListener {
            val intent=Intent(this,YourPhoneActivity::class.java)
            intent.putExtra(CommonKeys.STATUS,"0")
            intent.putExtra(CommonKeys.OTP_COUNT_INTENT_KEY,otp_count)
            startActivity(intent)
        }
    }
}