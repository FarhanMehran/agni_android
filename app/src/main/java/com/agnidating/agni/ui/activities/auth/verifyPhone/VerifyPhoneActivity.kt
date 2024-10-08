package com.agnidating.agni.ui.activities.auth.verifyPhone

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityVerifyPhoneBinding
import com.agnidating.agni.databinding.ActivityYourPhoneBinding
import com.agnidating.agni.model.LoginResponse
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.addImages.AddImagesActivity
import com.agnidating.agni.ui.activities.auth.AuthViewModel
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileActivity
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.ui.activities.writeBio.WriteBio
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
class VerifyPhoneActivity : ScopedActivity() {
    private  var nameCode: String=""
    private var status: String=""
    private var devToken: String=""
    private var phone: String=""
    private var countryCode: String=""
    private lateinit var binding: ActivityVerifyPhoneBinding
    lateinit var otp_share : Otp_pref

    var count = 0;

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel: AuthViewModel by viewModels()
    private var isUpdate=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs.isSubscribed().toString().logs()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_phone)
       otp_share = com.agnidating.agni.utils.sharedPreference.Otp_pref(this)
        startTimer(60000)
        status=intent.extras?.getString(CommonKeys.STATUS)?:"0"
        clickListeners()
        bindObserver()
        getIntentData()
        getDeviceToken {
            devToken=it
        }
    }

        private fun startTimer(mills : Long) {
            binding.tvResendTimer.visible()
            binding.tvResend.setTextColor(ContextCompat.getColor(this@VerifyPhoneActivity,R.color.light_grey))
            binding.tvResend.isEnabled=false
            object : CountDownTimer(mills, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {

                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = (millisUntilFinished / 1000) % 60
                    binding.tvResendTimer.text = String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    binding.tvResend.setTextColor(ContextCompat.getColor(this@VerifyPhoneActivity,R.color.orange_pink))
                    binding.tvResend.isEnabled=true
                    binding.tvResendTimer.gone()
                }
            }.start()
        }

    /**
     * handle click listeners
     */
    private fun clickListeners() {
        binding.btGoNext.setOnClickListener {
            if (binding.etOtp.text.isNullOrEmpty().not()) {
                binding.tvResend.isEnabled.toString().logs()
               if (binding.tvResend.isEnabled){
                   "Otp has been expired please resend otp to continue".toast(this)
               }else{
                   val map = HashMap<String, String>()
                   map["otp"] = binding.etOtp.text.toString()
                   map["phone"] = phone.replace(" ","").replace("-","")
                   map["countrycode"] = countryCode
                   map["code"]=nameCode
                   map["status"]=status
                   map["state"] = "NO"
                   map["devId"] = getDeviceIds()
                   map["devType"] = "android"
                   map["devToken"] = devToken
                   if (isUpdate){
                       viewModel.verifyPhoneUpdate(map)
                   }else{
                       viewModel.verifyOtp(map)
                   }
               }
            }
            else{
                "Please Enter OTP".toast(this)
            }
        }
        binding.tvResend.setOnClickListener {
            if (status=="1"){
                val map = HashMap<String, String>()
                map["countrycode"] = countryCode
                map["code"] = nameCode
                map["phone"] = phone.replace(" ","").replace("-","")
                map["state"] = "NO"
                map["devId"] = getDeviceIds()
                map["devType"] = "android"
                map["status"] = status
                map["devToken"] = devToken
                map["unique_id"] = getDeviceIds()
                viewModel.registerResend(map)
            }else{
                val map = HashMap<String, String>()
                map["phone"] = phone.replace(" ","").replace("-","")
                map["countrycode"] = countryCode
                map["unique_id"] = getDeviceIds()
                viewModel.resendOtp(map)
            }
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * get data from intent
     */
    private fun getIntentData() {
        try {
            isUpdate= intent?.extras?.getBoolean(CommonKeys.FOR_UPDATE,false)!!
            phone= intent?.extras?.getString(CommonKeys.PHONE)!!
            countryCode= intent?.extras?.getString(CommonKeys.COUNTRY_CODE)!!
            nameCode= intent?.extras?.getString(CommonKeys.NAME_CODE)!!
        } catch (e: Exception) {

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
                    otp_share.set_own_time(CommonKeys.OTP_OWN_TIME,getTime())
                    handleResponse(it)
                    it.data.data.phoneOtp.logs()
                }
                is ResultWrapper.Error -> {
                    hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
        viewModel.resendLiveData.observe(this) {
            when (it) {
                is ResultWrapper.Loading -> {
                    showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    showToast("Otp Resent")

                    var count = it.data.reques_count

                    Log.d("count",count.toString())

                    if (count != null) {
                        otp_share.save_count(CommonKeys.OTP_COUNT,count)

                        when(count)
                        {
                            1 -> startTimer(60000)
                            2 -> startTimer(180000)
                            3 -> startTimer(300000)
                        }
                    }



                }
                is ResultWrapper.Error -> {
                    hideProgress()
                    if(it.error?.created_time != null)
                    {
                        startTimer(300000)
                        otp_share.save_time(CommonKeys.OTP_TIME, it.error?.created_time.toString())
                        showToast(it.error?.message.toString()+" "+ it.error?.created_time.toString())
                    }
                    else
                    {
                        showToast(it.error?.message.toString())
                    }
                }
            }
        }

        viewModel.verifyUpdateLiveData.observe(this) {
            when (it) {
                is ResultWrapper.Loading -> {
                    showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    setResult(RESULT_OK)
                    onBackPressed()
                }
                is ResultWrapper.Error -> {
                    hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

    /**
    * handle success response
    */
    private fun handleResponse(it: ResultWrapper.Success<LoginResponse>) {
        if(it.data.token!=null)
            sharedPrefs.save(CommonKeys.TOKEN, "Bearer ${it.data.token}")
        if (viewModel.getDestination(it.data.data) == null) {
            sharedPrefs.saveUser(it.data.data)
            sharedPrefs.setSubscribed(it.data.data.subscribed=="1")
            startNewActivityWithAllFinish(DashboardActivity::class.java,null)
        }
        else {
            if(viewModel.getDestination(it.data.data)==0){
                if (it.data.data.isComplete==8){
                    startNewActivityWithAllFinish(AddImagesActivity::class.java,null)
                }
                else if (it.data.data.isComplete==9){
                    startNewActivityWithAllFinish(WriteBio::class.java,null)
                }
            }else{
                val bundle = Bundle()
                bundle.putInt(
                    CommonKeys.DESTINATION,
                    viewModel.getDestination(it.data.data)!!
                )
                startNewActivityWithAllFinish(CompleteProfileActivity::class.java,bundle)
            }
        }
    }

}