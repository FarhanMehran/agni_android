package com.agnidating.agni.ui.activities.plans

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityPlanSelectionBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by AJAY ASIJA
 */

@AndroidEntryPoint
class PlanSelectionActivity : ScopedActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPlanSelectionBinding
    private lateinit var selectedPlan: RelativeLayout
    private lateinit var selectedRadio: RadioButton
    private val viewModel:PlansViewModel by viewModels()
    private var isConnected=false
    private var plan = "agni_monthly"

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.initBillingClient(this)
        initView()
        handleClick()
        bindObserver()
    }

    private fun bindObserver() {
        viewModel.connectStatus.observe(this){
            isConnected=it
        }
        viewModel.purchaseResponse.observe(this){
            when(it){
                is ResultWrapper.Error -> {
                    hideProgress()
                   showToast(it.error?.message.toString())
                }
                is ResultWrapper.Loading -> {
                    showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    sharedPrefs.setSubscribed(true)
                    onBackPressed()
                }
            }
        }
    }

    /**
     * change background and radio
     */
    private fun changeSelectedBackground(
        currentSelection: RelativeLayout,
        currentRadio: RadioButton
    ) {
        if (currentSelection != selectedPlan) {
            selectedPlan.setBackgroundResource(R.drawable.rectangle_border)
            selectedRadio.isChecked = false
            currentSelection.setBackgroundResource(R.drawable.selected_plan)
            currentRadio.isChecked = true
            selectedPlan = currentSelection
            selectedRadio = currentRadio
        }
    }

    /**
     * handle all click events
     */
    private fun handleClick() {
        binding.rlMonthly.setOnClickListener(this)
        binding.rlThreeMonth.setOnClickListener(this)
        binding.rlSixMonth.setOnClickListener(this)
        binding.rlYear.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.btnContinue.setOnClickListener(this)
    }

    /**
     * initialize views etc
     */
    private fun initView() {
        binding.tvActualPrice.paintFlags =
            binding.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        binding.tvActual3Months.paintFlags =
            binding.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvActual6Months.paintFlags =
            binding.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        selectedPlan = binding.rlMonthly
        selectedRadio = binding.rbMonth
        changeSelectedBackground(binding.rlMonthly, binding.rbMonth)
    }
    override fun onClick(v: View?) {
        when (v) {
            binding.rlMonthly -> {
                changeSelectedBackground(binding.rlMonthly, binding.rbMonth)
                plan = "agni_monthly"
            }
            binding.rlThreeMonth -> {
                changeSelectedBackground(binding.rlThreeMonth, binding.rbThreeMonth)
                plan = "agni_three_month"
            }
            binding.rlSixMonth -> {
                changeSelectedBackground(binding.rlSixMonth, binding.rbSixMonth)
                plan = "agni_six_month_subs"
            }
            binding.rlYear -> {
                changeSelectedBackground(binding.rlYear, binding.rbYear)
                plan = "agni_one_year"
            }
            binding.ivBack -> {
               onBackPressed()
            }
            binding.btnContinue -> {
               if (isConnected){
                   lifecycleScope.launch {
                       viewModel.launchBillingFlow(this@PlanSelectionActivity,plan)
                   }
               }
            }
        }
    }

}