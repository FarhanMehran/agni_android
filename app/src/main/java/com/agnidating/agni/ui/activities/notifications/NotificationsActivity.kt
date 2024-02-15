package com.agnidating.agni.ui.activities.notifications

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityNotificationsBinding
import com.agnidating.agni.model.notification.Data
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * created by AJAY ASIJA on 04/28/2022
 */
@AndroidEntryPoint
class NotificationsActivity:ScopedActivity() {
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var binding: ActivityNotificationsBinding
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listeners()
        getNotifications()
        bindObserver()
    }

    /**
     * initialize view and adapters
     */
    private fun initView(data: List<Data>) {
        notificationAdapter=NotificationAdapter(data){
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.rvNotifications.adapter=notificationAdapter
    }

    /**
     * set up click listeners [listeners]
     */
    private fun listeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }


    /**
     * hit notification api
     */
    private fun getNotifications() {
        viewModel.getNotifications()
    }

    /**
     * bind live data observers [bindObserver]
     */
    var data = ArrayList<Data>()
    private fun bindObserver() {
        viewModel.notificationLiveData.observe(this){
            when (it) {
                is ResultWrapper.Loading -> {
                   showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    data.addAll(it.data.data)
                    binding.rlNoNotification.isVisible=data.isNullOrEmpty()
                    initView(data)
                }
                is ResultWrapper.Error -> {
                    hideProgress()
                 }
            }
        }
    }

    override fun onBackPressed() {
        val intent=Intent(this,DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

}