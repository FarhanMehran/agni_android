package com.agnidating.agni.ui.activities.writeBio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.getStarted.GetStarted
import com.agnidating.agni.ui.activities.welcome.WelcomeActivity
import com.agnidating.agni.utils.*


class WriteBio : ScopedActivity() {
    lateinit var binding: com.agnidating.agni.databinding.ActivityWriteBioBinding
    private val viewModel:BioViewModel by viewModels()
    private var update=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_write_bio)
        update=intent.getBooleanExtra(CommonKeys.FOR_UPDATE,false)
        if (update)  binding.btNext.text=getString(R.string.save)
        binding.etPhone.setText(intent.getStringExtra(CommonKeys.BIO)?:"")
        listeners()
        bindObserver()
    }

    private fun bindObserver() {
        viewModel.updateLiveData.observe(this){
            when(it){
                is ResultWrapper.Loading->showProgress()
                is ResultWrapper.Success->{
                   if (update){
                       onBackPressed()
                   }else{
                       hideProgress()
                       startNewActivityWithFinish(WelcomeActivity::class.java,null)
                   }
                }
                is ResultWrapper.Error->{
                    showToast(it.error?.message!!)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listeners() {
        binding.btNext.setOnClickListener {
            if (isValid()){
                viewModel.updateBio(binding.etPhone.text.toString())
            }
        }
        binding.ivBack.setOnClickListener {
           if (update.not()){
               errorDialog("You are being redirected to the Login screen.Do you want to continue?",true){
                   startActivity(Intent(this, GetStarted::class.java))
                   finishAffinity()
               }
           }else{
               binding.btNext.performClick()
           }
        }
        binding.etPhone.setOnTouchListener(View.OnTouchListener { v, event ->
            if (binding.etPhone.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
    }
    private fun isValid(): Boolean {
        var valid =checkNetwork()
        if (binding.etPhone.text.toString().trim().isEmpty()){
            valid=false
            showToast("Please Enter bio")
        }
        return valid
    }
}