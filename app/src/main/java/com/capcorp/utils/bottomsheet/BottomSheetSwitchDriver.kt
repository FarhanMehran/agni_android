package com.capcorp.utils.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import android.widget.TextView
import com.capcorp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSwitchDriver : BottomSheetDialogFragment() {

    private var switchCompanyListener: View.OnClickListener? = null
    private var switchFreelancerListener: View.OnClickListener? = null
    private var cancelListener: View.OnClickListener? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(activity, R.layout.bottom_sheet_switch_driver, null)
        dialog.setContentView(contentView)
        contentView.findViewById<TextView>(R.id.tv_switch_compnay)
            .setOnClickListener(switchCompanyListener)
        contentView.findViewById<TextView>(R.id.tv_switch_freelancer)
            .setOnClickListener(switchFreelancerListener)
        contentView.findViewById<TextView>(R.id.tvCancel).setOnClickListener(cancelListener)
    }

    fun setOnCompanyListener(onClickListener: View.OnClickListener) {
        this.switchCompanyListener = onClickListener
    }

    fun setOnFreelancerListener(onClickListener: View.OnClickListener) {
        this.switchFreelancerListener = onClickListener
    }

    fun setOnCancelListener(onClickListener: View.OnClickListener) {
        this.cancelListener = onClickListener
    }
}