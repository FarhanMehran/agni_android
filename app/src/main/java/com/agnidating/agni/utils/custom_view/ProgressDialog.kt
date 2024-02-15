package com.agnidating.agni.utils.custom_view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.agnidating.agni.R


class ProgressDialog : Dialog {

    private var pd: Dialog? = null
    internal var context: Context

    constructor(context: Context) : super(context) {
        this.context=context
        pd = Dialog(context, R.style.progressDialog)
        pd!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.setContentView(R.layout.progressbar_loader)
        pd!!.setCanceledOnTouchOutside(false)
    }

    constructor(context: Activity, msg: String): super(context) {
        this.context = context
        pd = Dialog(context, R.style.progressDialog)
        pd!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.setContentView(R.layout.progressbar_loader)
        pd!!.setCanceledOnTouchOutside(false)
    }



    fun showProgres() {
        pd!!.show()
    }

    fun hideProgress(){
        pd!!.dismiss()
    }

    companion object  {
        fun getInstant(context: Activity): ProgressDialog {
            return ProgressDialog(context)
        }

        fun getInstant(context: Activity, msg: String): ProgressDialog {
            return ProgressDialog(context, msg)
        }
    }
}

