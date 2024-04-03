package com.capcorp.utils.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import android.widget.TextView
import com.capcorp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetCustomer : BottomSheetDialogFragment() {

    private var chatListener: View.OnClickListener? = null
    private var callListener: View.OnClickListener? = null
    private var cancelListener: View.OnClickListener? = null
    private var textView: TextView? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(activity, R.layout.bottom_sheet_customer, null)
        dialog.setContentView(contentView)
        contentView.findViewById<TextView>(R.id.tvChat).setOnClickListener(chatListener)
        contentView.findViewById<TextView>(R.id.tvCall).setOnClickListener(callListener)
        contentView.findViewById<TextView>(R.id.tvCancel).setOnClickListener(cancelListener)

    }


    fun setOnChatListener(onClickListener: View.OnClickListener) {
        this.chatListener = onClickListener
    }

    fun setOnCallListener(onClickListener: View.OnClickListener) {
        this.callListener = onClickListener
    }

    fun setOnCancelListener(onClickListener: View.OnClickListener) {
        this.cancelListener = onClickListener
    }
}