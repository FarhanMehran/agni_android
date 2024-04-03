package com.capcorp.utils

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*


fun Context.getDate(timestamp: Long): String? {
    try {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy")
        val currenTimeZone = calendar.time as Date
        return sdf.format(currenTimeZone)
    } catch (e: Exception) {
    }
    return ""
}


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showDialog(
    cancelable: Boolean = false,
    cancelableTouchOutside: Boolean = false,
    builderFunction: AlertDialog.Builder.() -> Any
) {
    val builder = AlertDialog.Builder(this)
    builder.builderFunction()
    val dialog = builder.create()

    dialog.setCancelable(cancelable)
    dialog.setCanceledOnTouchOutside(cancelableTouchOutside)
    dialog.show()
}

fun AlertDialog.Builder.positiveButton(text: String = "OK", handleClick: (i: Int) -> Unit = {}) {
    this.setPositiveButton(text) { _, i -> handleClick(i) }
}

fun AlertDialog.Builder.negativeButton(
    text: String = "CANCEL",
    handleClick: (i: Int, dialog: DialogInterface) -> Unit = { _: Int, _: DialogInterface ->


    }
) {
    this.setNegativeButton(text) { dialogInterface, i -> handleClick(i, dialogInterface) }
}
