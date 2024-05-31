package com.agnidating.agni.ui.activities.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivitySplashBinding
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.ui.activities.getStarted.GetStarted
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.TimeComparator
import com.agnidating.agni.utils.changeStatusBarColorToTransParent
import com.agnidating.agni.utils.getTime
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by AJAY ASIJA
 */

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ScopedActivity() {
    private lateinit var binding: ActivitySplashBinding
    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColorToTransParent()
        binding=DataBindingUtil.setContentView(this, R.layout.activity_splash)


        var otp_share = com.agnidating.agni.utils.sharedPreference.Otp_pref(this)

        if (otp_share.get_time(CommonKeys.OTP_TIME,"").isNotEmpty())
        {
            if (TimeComparator.isNewTimeGreaterThan24Hours(otp_share.get_time(CommonKeys.OTP_TIME,""), getTime()))
            {
                otp_share.save_count(CommonKeys.OTP_COUNT,0)
                otp_share.save_time(CommonKeys.OTP_TIME,"")
            }
        }
        else if (otp_share.get_own_time(CommonKeys.OTP_OWN_TIME,"").isNotEmpty())
        {
            if (TimeComparator.isNewTimeGreaterThan24Hours(otp_share.get_own_time(CommonKeys.OTP_OWN_TIME,""), getTime()))
            {
                otp_share.save_count(CommonKeys.OTP_COUNT,0)
                otp_share.set_own_time(CommonKeys.OTP_OWN_TIME,"")
            }
        }

        Log.d("check_pref",otp_share.get_count(CommonKeys.OTP_COUNT,0).toString())
        Log.d("check_pref",otp_share.get_time(CommonKeys.OTP_TIME,""))

        lifecycleScope.launch {
            delay(5000)
            goToNextActivity()
        }
    }
    /**
     * move to appropriate activity
     */
    private fun goToNextActivity(){
        if (sharedPrefs.getBoolean(CommonKeys.LOGGED_IN)){
            val intent= Intent(this,DashboardActivity::class.java)
            startActivity(intent)
        }else{
           val intent= Intent(this,GetStarted::class.java)
            startActivity(intent)
        }
        finish()
    }

}