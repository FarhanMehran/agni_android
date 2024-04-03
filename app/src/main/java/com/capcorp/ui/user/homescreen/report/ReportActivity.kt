package com.capcorp.ui.user.homescreen.report

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.utils.*
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : BaseActivity(), View.OnClickListener, ReportContract.View {
    private var langPickerPopup: androidx.appcompat.widget.ListPopupWindow? = null
    private var mReasonNameList = java.util.ArrayList<String>()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var reasonName: String = ""
    private val presenter = ReportPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        setListener()
        addReasons()
    }

    private fun addReasons() {
        mReasonNameList.add(getString(R.string.nudity))
        mReasonNameList.add(getString(R.string.violenece))
        mReasonNameList.add(getString(R.string.harassment))
        mReasonNameList.add(getString(R.string.suicide_or_self))
        mReasonNameList.add(getString(R.string.spam))
        mReasonNameList.add(getString(R.string.unauthorised_sales))
        mReasonNameList.add(getString(R.string.terorrism))
        tv_reasons.text = mReasonNameList[0]
        initLanguagePicker(tv_reasons)
    }

    fun setListener() {
        tvBackSupport.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        tv_reasons.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvBackSupport -> {
                finish()
            }
            R.id.btnSubmit -> {
                if (reasonName.isEmpty()) {
                    rootView_support?.showSnack(getString(R.string.select_reason))
                } else {
                    dialogIndeterminate.show()
                    intent.extras?.getString(OPPOSITION_ID)?.let {
                        intent.extras?.let { it1 ->
                            it1.getString(ORDER_ID)?.let { it2 ->
                                presenter.reportOrder(
                                    getAuthAccessToken(this),
                                    it, reasonName, it2
                                )
                            }
                        }
                    }
                }
            }
            R.id.tv_reasons -> {
                openReasonPicker()
            }
        }
    }

    private fun openReasonPicker() {
        langPickerPopup?.show()
    }

    private fun initLanguagePicker(anchorView: View) {
        langPickerPopup = androidx.appcompat.widget.ListPopupWindow(anchorView.context)
        langPickerPopup?.setAdapter(
            ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item, mReasonNameList
            )
        )
        langPickerPopup?.isModal = true
        langPickerPopup?.anchorView = anchorView
        langPickerPopup?.width = androidx.appcompat.widget.ListPopupWindow.WRAP_CONTENT
        langPickerPopup?.height = androidx.appcompat.widget.ListPopupWindow.WRAP_CONTENT

        langPickerPopup?.setOnItemClickListener { _, _, position, _ ->
            reasonName = mReasonNameList.get(position)
            tv_reasons.text = reasonName
            langPickerPopup?.dismiss()
        }
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        dialogIndeterminate.dismiss()
        rootView_support?.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        dialogIndeterminate.dismiss()
        rootView_support?.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReportSuccess() {
        dialogIndeterminate.dismiss()
        val dialog = Dialog(this,R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog_success)
        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        tvTitle.text = getString(R.string.success)
        tvDescription.text = getString(R.string.thank_you_submitting_report)
        dialogButton.text = getString(R.string.ok)
        dialogButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()

    }

}
