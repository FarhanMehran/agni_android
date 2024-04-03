package com.capcorp.ui.driver.homescreen.mydeliveries.filter

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.home.requests.RequestContract
import com.capcorp.ui.driver.homescreen.home.requests.RequestsPresenter
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.FilterDataRequest
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.OrderListing
import kotlinx.android.synthetic.main.activity_filter.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class FilterActivity : BaseActivity(), View.OnClickListener, RequestContract.View {
    private val presenter = RequestsPresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var filterTypeData: String = "None"
    private var defCalendar = Calendar.getInstance()
    private var myCalendar = Calendar.getInstance()
    private var myCalendar2 = Calendar.getInstance()
    lateinit var filterDetail: FilterDataRequest
    private var isFirstTime = true
    private var mStartDate: Long = 0
    private var mEndDate: Long = 0
    private var mStartDateSelected: String = ""
    private var mEndDateSelected: String = ""
    private var filterType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        defCalendar.set(Calendar.DAY_OF_MONTH, defCalendar.get(Calendar.DAY_OF_MONTH) - 3)
        setListener()
        checkedListener()
        if (intent.hasExtra(ISFIRST_TIME)) {
            isFirstTime = intent.getBooleanExtra(ISFIRST_TIME, false)
        }
        setDataViews()

    }

    fun setDataViews() {
        if (isFirstTime == false) {
            filterDetail =
                SharedPrefs.with(this).getObject(FILTER_DATA, FilterDataRequest::class.java)
            filterTypeData = filterDetail.filterType
            mStartDateSelected = filterDetail.StartDate
            mEndDateSelected = filterDetail.EndDate
            mStartDate = filterDetail.mStartDateTimeStamp
            mEndDate = filterDetail.mEndDateTimeStamp
            seekbar.progress = filterDetail.priceRange.roundToInt()
            seekbar_reward.progress = filterDetail.rewardRange.roundToInt()
            if (mEndDate != 0.toLong()) {
                myCalendar2.timeInMillis = mEndDate - 86400000
            }
            if (mStartDate != 0.toLong()) {
                myCalendar.timeInMillis = mStartDate
            }
            if (filterTypeData.equals(OrderType.SHOP)) {
                radio_shop_only.isChecked = true
            } else if (filterTypeData.equals(OrderType.DELIVERY)) {
                radio_delivery_only.isChecked = true
            } else if (filterTypeData.equals(OrderType.PICKUP)) {
                radio_pick_up_only.isChecked = true
            } else if (filterTypeData.equals(OrderType.NONE)) {
                radio_none.isChecked = true
            }
            if (!mStartDateSelected.isEmpty()) {
                edtStartDate.text = mStartDateSelected
            }
            if (!mEndDateSelected.isEmpty()) {
                edtEndDate.text = mEndDateSelected
            }
        }
    }

    fun setListener() {
        tvReset.setOnClickListener(this)
        btn_apply.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        tvBackFilter.setOnClickListener(this)
        edtStartDate.setOnClickListener(this)
        edtEndDate.setOnClickListener(this)
    }

    fun checkedListener() {
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tv_price.text = getString(R.string.currency_sign) + seekbar.progress
                //priceRange = seekBar.progress.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        seekbar_reward.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tv_reward.text = getString(R.string.currency_sign) + seekbar_reward.progress
                //priceRange = seekBar.progress.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        radio_group_filter.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radio_delivery_only) {
                filterTypeData = OrderType.DELIVERY
            } else if (checkedId == R.id.radio_pick_up_only) {
                filterTypeData = OrderType.PICKUP
            } else if (checkedId == R.id.radio_shop_only) {
                filterTypeData = OrderType.SHOP
            } else {
                filterTypeData = OrderType.NONE
            }
        }
    }


    private fun openStartDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = defCalendar.timeInMillis
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun openEndDateTimePicker() {
        //myCalendar2.set(Calendar.DAY_OF_MONTH,myCalendar.get(Calendar.DAY_OF_MONTH)-3)
        val datePickerDialog = DatePickerDialog(
            this, date2, myCalendar2
                .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
            myCalendar2.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = myCalendar.timeInMillis
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    var date2: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar2.set(Calendar.YEAR, year)
            myCalendar2.set(Calendar.MONTH, monthOfYear)
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("MMM dd, yyyy")
            val formatedDate = sdf.format(myCalendar2.time)
            edtEndDate.text = formatedDate
            mEndDate = sdf.parse(formatedDate).time + 86400000
            mEndDateSelected = formatedDate
        }

    var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("MMM dd, yyyy")
            val formatedDate = sdf.format(myCalendar.time)
            edtStartDate.text = formatedDate
            mStartDate = sdf.parse(formatedDate).time
            mStartDateSelected = formatedDate
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_apply -> {
                if (edtStartDate.text.toString().length > 0 && edtEndDate.text.toString()
                        .isEmpty()
                ) {
                    Toast.makeText(this, getString(R.string.select_end_date), Toast.LENGTH_SHORT)
                        .show()
                } else if (edtStartDate.text.toString()
                        .isEmpty() && edtEndDate.text.toString().length > 0
                ) {
                    Toast.makeText(this, getString(R.string.select_start_date), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (CheckNetworkConnection.isOnline(this)) {
                        var filterDataRequest = FilterDataRequest()
                        filterDataRequest.priceRange = seekbar.progress.toDouble()
                        filterDataRequest.rewardRange = seekbar_reward.progress.toDouble()
                        filterDataRequest.filterType = filterTypeData
                        filterDataRequest.mEndDateTimeStamp = mEndDate
                        filterDataRequest.mStartDateTimeStamp = mStartDate
                        filterDataRequest.StartDate = mStartDateSelected
                        filterDataRequest.EndDate = mEndDateSelected
                        SharedPrefs.with(this).save(FILTER_DATA, filterDataRequest)

                        var hashMap = HashMap<String, Any>()
                        hashMap["priceRange"] = seekbar.progress.toDouble()
                        hashMap["filterType"] = filterTypeData
                        hashMap["startDate"] = mStartDate
                        hashMap["endDate"] = mEndDate
                        hashMap["rewardFilter"] = seekbar_reward.progress.toDouble()

                        presenter.filterApi(getAuthAccessToken(this), hashMap)

                    }
                }
            }
            R.id.edtStartDate -> {
                openStartDateTimePicker()
            }
            R.id.edtEndDate -> {
                if (mStartDateSelected.isEmpty()) {
                    rootView_filter.showSnack(getString(R.string.select_start_date_first))
                } else {
                    openEndDateTimePicker()
                }
            }

            R.id.btn_cancel -> {
                onBackPressed()
            }
            R.id.tvBackFilter -> {
                onBackPressed()
            }
            R.id.tvReset -> {
                var filterDataRequest = FilterDataRequest()
                filterDataRequest.priceRange = 20000.0
                filterDataRequest.rewardRange = 5000.0
                filterDataRequest.filterType = ""
                filterDataRequest.mEndDateTimeStamp = 0
                filterDataRequest.mStartDateTimeStamp = 0
                filterDataRequest.StartDate = ""
                filterDataRequest.EndDate = ""
                SharedPrefs.with(this).save(FILTER_DATA, filterDataRequest)

                seekbar.progress = 20000
                seekbar_reward.progress = 5000
                radio_delivery_only.isChecked = false
                radio_pick_up_only.isChecked = false
                radio_shop_only.isChecked = false
                radio_none.isChecked = true
                filterType = "None"
                mStartDateSelected = ""
                mEndDateSelected = ""
                edtStartDate.text = ""
                edtEndDate.text = ""
                mStartDate = 0
                mEndDate = 0
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onApiSuccess(orderList: List<OrderListing>) {
    }

    override fun acceptOrderApiSuccess(position: Int) {
    }

    override fun onFilterSuccess(signupModel: SignupModel) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        rootView_filter.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        if (code == 401) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog_success)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(this).remove(ACCESS_TOKEN)
                finishAffinity()
                startActivity(Intent(this, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            rootView_filter.showSnack(errorBody ?: getString(R.string.sww_error))
        }
    }

    override fun validationsFailure(type: String?) {
    }

}
