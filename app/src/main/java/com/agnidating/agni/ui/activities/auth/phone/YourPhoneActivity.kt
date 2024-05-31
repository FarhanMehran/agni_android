package com.agnidating.agni.ui.activities.auth.phone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityYourPhoneBinding
import com.agnidating.agni.model.countries.Country
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.auth.AuthViewModel
import com.agnidating.agni.ui.activities.auth.verifyPhone.VerifyPhoneActivity
import com.agnidating.agni.ui.activities.auth.selectCountry.SelectCountry
import com.agnidating.agni.utils.*
import com.agnidating.agni.utils.sharedPreference.Otp_pref
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

/**
 * Created by AJAY ASIJA
 */
@AndroidEntryPoint
class YourPhoneActivity : ScopedActivity(){
    private  var devToken=""
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityYourPhoneBinding
    private var country: Country? = null
    private var status: String? = null
    private var count_otp : Int? = null
    lateinit var otp_share : Otp_pref

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel: AuthViewModel by viewModels()
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_your_phone)
        getDeviceToken {
            devToken=it
        }


        otp_share = com.agnidating.agni.utils.sharedPreference.Otp_pref(this)
        status=intent.getStringExtra(CommonKeys.STATUS)
        count_otp=intent.getIntExtra(CommonKeys.OTP_COUNT_INTENT_KEY,0)


        when(count_otp)
        {
            2 -> start_phone_timer(180000)
            3 -> start_phone_timer(300000)
        }

        createLaunchers()
        getIntentData()
        iniView()
        onClickListener()
        bindObserver()
    }


    private fun start_phone_timer(mills : Long) {
        binding.countTimer.visible()
        binding.btNext.setBackgroundResource(R.drawable.disable_btn_bg)
        binding.btNext.isEnabled=false
        object : CountDownTimer(mills, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {

                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.countTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.btNext.setBackgroundResource(R.drawable.buttons)
                binding.btNext.isEnabled=true
                binding.countTimer.gone()
            }
        }.start()
    }

    /**
     * if User comes to update phone no show previous phone no
     */
    @SuppressLint("SetTextI18n")
    private fun iniView() {
        binding.tvPrivacyPolicy.setSpannedString("privacy policies","Terms"){
            if (it == "Terms"){
                openChromeTab("https://agnidating.net/termCondition")
            }else{
                openChromeTab("https://agnidating.net/privacyPolicy")
            }
        }
        if (isUpdate) {
            binding.tvTitle.text="Phone Number"
            binding.btNext.text="save"
            binding.etPhone.setText(intent?.getStringExtra(CommonKeys.PHONE).toString())
            binding.tvCountryCode.text =
                "+" + intent?.getStringExtra(CommonKeys.COUNTRY_CODE).toString()
            binding.ccp.setCountryForPhoneCode(
                intent?.getStringExtra(CommonKeys.COUNTRY_CODE).toString().toInt()
            )
            binding.ivFlag.setImageResource(binding.ccp.selectedCountryFlagResourceId)
        }
    }

    /**
     * register Launchers for activity result [createLaunchers]
     */

    private fun createLaunchers() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                onBackPressed()
            }
        }
    }


    /**
     * bind all observers here
     */
    private fun bindObserver() {
        viewModel.registerLiveData.observe(this) {
            when (it) {
                is ResultWrapper.Loading -> {
                    showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    sharedPrefs.save(CommonKeys.TOKEN, "Bearer ${it.data.token}")
                    if(status=="0"){
                        it.data.data.phoneOtp.logs()
                    }
//                    it.data.data.phoneOtp.logs()
                    startNewActivityWithBundle(VerifyPhoneActivity::class.java,Bundle().apply {
                        putBoolean(CommonKeys.FOR_UPDATE, false)
                        putString(CommonKeys.STATUS, status)
                        putString(CommonKeys.PHONE, binding.etPhone.text.toString())
                        putString(CommonKeys.COUNTRY_CODE, binding.ccp.selectedCountryCode)
                        putString(CommonKeys.NAME_CODE, binding.ccp.selectedCountryNameCode)
                    })
                }
                is ResultWrapper.Error -> {
                    it.error?.message?.logs()
                    hideProgress()
                    if(it.error?.created_time != null)
                    {
                        start_phone_timer(300000)
                        showToast(it.error?.message.toString()+" "+ it.error?.created_time.toString())
                    }
                    else
                    {
                        showToast(it.error?.message.toString())
                    }
                }
            }
        }
        viewModel.phoneUpdateLiveData.observe(this) {
            when (it) {
                is ResultWrapper.Loading -> {
                    showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    launcher.launch(
                        Intent(this, VerifyPhoneActivity::class.java).apply {
                            putExtras(Bundle().apply {
                                putString(CommonKeys.STATUS, status)
                                putBoolean(CommonKeys.FOR_UPDATE, isUpdate)
                                putString(CommonKeys.PHONE, binding.etPhone.text.toString())
                                putString(CommonKeys.COUNTRY_CODE, binding.ccp.selectedCountryCode)
                                putString(CommonKeys.NAME_CODE, binding.ccp.selectedCountryNameCode)
                            })
                        }
                    )
                }
                is ResultWrapper.Error -> {
                    hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

    /**
     * Handle all click listeners
     */
    @SuppressLint("SetTextI18n")
    private fun onClickListener() {
        binding.btNext.setOnClickListener {
            if (isValid()) {
                val map = HashMap<String, String>()
                map["countrycode"] = binding.ccp.selectedCountryCode
                map["code"] = binding.ccp.selectedCountryNameCode
                map["phone"] = binding.etPhone.text.toString().replace(" ","").replace("-","")
                if (isUpdate) {
                    viewModel.updatePhone(map)
                } else {
                    map["state"] = "NO"
                    map["devId"] = getDeviceIds()
                    map["devType"] = "android"
                    map["status"] = status.toString()
                    map["devToken"] = devToken
                    map["unique_id"] = getDeviceIds()+377.toString()

                    Log.d("device_id",map.toString())
                    viewModel.register(map)
                }
            }
        }
        binding.btBack.setOnClickListener {
            onBackPressed()
        }
        binding.consCountry.setOnClickListener {
            val selectCountry = SelectCountry(country)
            selectCountry.show(supportFragmentManager, "")
            selectCountry.callBack = { item ->
                country = item
                binding.ccp.setCountryForNameCode(item.nameCode)
                binding.ivFlag.setImageResource(binding.ccp.selectedCountryFlagResourceId)
                binding.tvCountryCode.text = "+" + country!!.phoneCode
                selectCountry.dismiss()
            }
        }
        binding.etPhone.doAfterTextChanged {
            if (it.toString().isNotEmpty()){
                binding.tvError.gone()
            }
        }
    }

    /**
     * get data from intent
     */
    private fun getIntentData() {
        isUpdate = intent?.getBooleanExtra(CommonKeys.FOR_UPDATE, false)!!
    }


    /**
     * Check validation
     */
    private fun isValid(): Boolean {
        var valid = true
        if (binding.ccp.isValidFullNumber.not()) {
            binding.tvError.visible()
            valid = false
        }
        else if (isOnline().not()) {
            showToast("Please check your internet connection")
            valid = false
        }
        else if (binding.cbTerms.isChecked.not()) {
            showToast("Please accept privacy policies and terms")
            valid = false
        }

        return valid
    }
}