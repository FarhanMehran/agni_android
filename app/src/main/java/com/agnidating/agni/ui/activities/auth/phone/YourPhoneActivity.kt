package com.agnidating.agni.ui.activities.auth.phone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by AJAY ASIJA
 */
@AndroidEntryPoint
class YourPhoneActivity : ScopedActivity() {
    private  var devToken=""
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityYourPhoneBinding
    private var country: Country? = null
    private var status: String? = null

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
        status=intent.getStringExtra(CommonKeys.STATUS)
        createLaunchers()
        getIntentData()
        iniView()
        onClickListener()
        bindObserver()
    }

    /**
     * if User comes to update phone no show previous phone no
     */
    @SuppressLint("SetTextI18n")
    private fun iniView() {
        binding.tvPrivacyPolicy.setSpannedString("privacy policies","Terms"){
            if (it == "Terms"){
                openChromeTab("https://agnidating.co/termCondition")
            }else{
                openChromeTab("https://agnidating.co/privacyPolicy")
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
                    if(it.data.token!=null)
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
                    showToast(it.error?.message.toString())
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
                    map["devId"] = getDeviceId()
                    map["devType"] = "android"
                    map["status"] = status.toString()
                    map["devToken"] = devToken
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