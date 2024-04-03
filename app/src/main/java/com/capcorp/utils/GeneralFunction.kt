package com.capcorp.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.capcorp.R
import com.capcorp.webservice.models.ApiErrorModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun replaceFragment(
    fragmentManager: FragmentManager,
    fragment: Fragment,
    tag: String,
    containerId: Int = R.id.container
) {
    fragmentManager.beginTransaction().setCustomAnimations(
        R.animator.slide_in_rght, R.animator.slide_out_left,
        R.animator.slide_in_left, R.animator.slide_out_rght
    ).replace(containerId, fragment, tag)
        .addToBackStack(tag).commit()
}

fun replaceFragmentwithoutBackstck(
    fragmentManager: FragmentManager,
    fragment: Fragment,
    tag: String,
    containerId: Int = R.id.container
) {
    fragmentManager.beginTransaction().setCustomAnimations(
        R.animator.slide_in_rght, R.animator.slide_out_left,
        R.animator.slide_in_left, R.animator.slide_out_rght
    ).replace(containerId, fragment, tag)
        .commit()
}

fun View.hideKeyboard(context: Context) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun hideSoftKeyBoard(context: Context, view: View) {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    } catch (e: Exception) {
        // TODO: handle exception
        e.printStackTrace()
    }
}

fun convertTimeStampToTime(timestamp: Long): String {

    val calendar = Calendar.getInstance()
    val tz = TimeZone.getDefault()
    calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
    val sdf = SimpleDateFormat("hh:mm a")
    sdf.timeZone = tz
    val currenTimeZone = Date(timestamp * 1000)
    return sdf.format(currenTimeZone)
}

fun View.showSnack(resId: Int) {
    try {
        val snackbar = Snackbar.make(this, resId, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.maxLines = 3
        /*  textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12f)*/
        snackbar.setAction(R.string.okay, View.OnClickListener { snackbar.dismiss() })
        snackbarView.setBackgroundColor(Color.parseColor("#27242b"))
        snackbar.setActionTextColor(Color.parseColor("#0078FF"))
        snackbar.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun View.showSnack(msg: String) {

    try {
        val snackbar = Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.maxLines = 3
        snackbar.setAction(R.string.okay) { snackbar.dismiss() }
        snackbarView.setBackgroundColor(Color.parseColor("#27242b"))
        snackbar.setActionTextColor(Color.parseColor("#0078FF"))
        snackbar.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.showSWWerror() {

    try {
        val snackbar = Snackbar.make(this, R.string.sww_error, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.maxLines = 3
        snackbar.setAction(R.string.okay) { snackbar.dismiss() }
        snackbarView.setBackgroundColor(Color.parseColor("#27242b"))
        snackbar.setActionTextColor(Color.parseColor("#0078FF"))
        snackbar.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.showSWWerror(message: String) {

    try {
        val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.maxLines = 3
        snackbar.setAction(R.string.okay) { snackbar.dismiss() }
        snackbarView.setBackgroundColor(Color.parseColor("#27242b"))
        snackbar.setActionTextColor(Color.parseColor("#0078FF"))
        snackbar.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/*----Display API Error-----*/
fun View.displayApiError(statusCode: Int?, errorBody: String?, context: Activity?) {
    if (statusCode == 401) {
        // showPopUp(context!!, context.getString(R.string.bad_token_msg))
//            context?.getString(R.string.bad_token_msg)?.let { this.showSnack(it) }
//            val DeviceToken = PrefsManager.get().getString(AppConstant.FCM_ID, "")
//            PrefsManager.get().removeAll()
//            PrefsManager.get().save(AppConstant.FCM_ID, DeviceToken)
//            context?.finishAffinity()
//            context?.startActivity(Intent(context, AuthenticationActivity::class.java))
    } else {
        if (errorBody != null) {
            val msg = errorBody
            this.showSnack(msg)
        }
    }
}

fun getApiError(error: String?): ApiErrorModel {
    return Gson().fromJson(error, ApiErrorModel::class.java)
}

fun dpToPx(dp: Int, context: Activity): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun pxToDp(px: Float, context: Activity): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}


/*
fun openSettingDialog(context: Activity?) {
    if (context != null) {
        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(context, R.style.AlertDialogWhiteBGTheme)
        builder.setMessage(R.string.deniedpermission)
            .setCancelable(false)
            .setPositiveButton(R.string.gotosettings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivityForResult(intent, 0)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}
*/

fun localToGMT(dateUTC: String, startTime: String): Date {
    val dateSring = dateUTC + " " + startTime
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
    var date: Date? = null
    try {
        date = format.parse(dateSring)
        println(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    //Date date = new Date();
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    return Date(sdf.format(date))
}


fun calender_date_to_timestamp(str_date: String): Long {
    var time_stamp: Long = 0
    try {
        val formatter = SimpleDateFormat("MMM dd, yyyy")
        //SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        val date = formatter.parse(str_date) as Date
        time_stamp = date.time
    } catch (ex: ParseException) {
        ex.printStackTrace()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    time_stamp = time_stamp / 1000
    return time_stamp
}

fun getFormattedDate(
    parseFormat: String, convertFormat: String, orderDate: String,
    timezoneParse: TimeZone, timeZoneConverted: TimeZone
): String? {
    val form = SimpleDateFormat(parseFormat, Locale.getDefault())
    form.timeZone = timezoneParse
    val date: Date
    try {
        date = form.parse(orderDate)
        val form1 = SimpleDateFormat(convertFormat, Locale.getDefault())
        form.timeZone = timeZoneConverted
        return form1.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun getFormatFromDate(date: Date, format: String): String {
    val sdf = SimpleDateFormat(format, Locale.ENGLISH)
    return try {
        sdf.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }

}


fun getDateFromFormat(date: String, format: String): Date {
    val sdf = SimpleDateFormat(format, Locale.ENGLISH)
    return try {
        sdf.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        Calendar.getInstance().time
    }
}

fun isYesterday(calendar: Calendar): Boolean {
    val tempCal = Calendar.getInstance()
    tempCal.add(Calendar.DAY_OF_MONTH, -1)
    return calendar.get(Calendar.DAY_OF_MONTH) == tempCal.get(Calendar.DAY_OF_MONTH)
}

interface OnDateSelectedListener {
    fun dateTimeSelected(dateCal: Calendar)
}

fun openDatePickerForDob(context: Context, onDateSelectedListener: OnDateSelectedListener) {
    val c = Calendar.getInstance()
    val yearCal = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    c.add(Calendar.YEAR, -18)

    val dpd = DatePickerDialog(
        context,
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val mCurrentTimeSelected = Calendar.getInstance()
            mCurrentTimeSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mCurrentTimeSelected.set(Calendar.MONTH, monthOfYear)
            mCurrentTimeSelected.set(Calendar.YEAR, year)
            onDateSelectedListener.dateTimeSelected(mCurrentTimeSelected)
        },
        yearCal,
        month,
        day
    )
    dpd.datePicker.maxDate = c.timeInMillis
    dpd.show()
}

fun existsInWeek(calendar: Calendar): Boolean {
    val tempCal = Calendar.getInstance()
    tempCal.add(Calendar.DAY_OF_MONTH, -7)
    tempCal.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
    tempCal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
    tempCal.set(Calendar.SECOND, calendar.get(Calendar.SECOND))
    return calendar.time.after(tempCal.time)
}

fun getAuthAccessToken(context: Context?): String {
    return "bearer " + getAccessToken(context)
}

fun getAccessToken(context: Context?): String {
    return SharedPrefs.with(context).getString(ACCESS_TOKEN, "")
}

fun getPhoneNo(context: Context?): String {
    return SharedPrefs.with(context).getString(PHONE_NUMBER, "")
}

/*
fun getTimeAgo(time: Long, ctx: Context): String? {
    var time = time
    val SECOND_MILLIS = 1000
    val MINUTE_MILLIS = 60 * SECOND_MILLIS
    val HOUR_MILLIS = 60 * MINUTE_MILLIS
    val DAY_MILLIS = 24 * HOUR_MILLIS
    if (time < 1000000000000L) {
        time *= 1000       // if timestamp given in seconds, convert to millis
    }
    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        // future date coding
        val diff = time - now
        return if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " " + ctx.getString(R.string.ahead)
        } else null
    }
    val diff = now - time
    if (diff < MINUTE_MILLIS) {
        return ctx.getString(R.string.just_now)
    } else if (diff < 2 * MINUTE_MILLIS) {
        return ctx.getString(R.string.min_ago)
    } else if (diff < 50 * MINUTE_MILLIS) {
        return (diff / MINUTE_MILLIS).toString() + " " + ctx.getString(R.string.mins_ago)
    } else if (diff < 90 * MINUTE_MILLIS) {
        return ctx.getString(R.string.hour_ago)
    } else if (diff < 24 * HOUR_MILLIS) {
        return (diff / HOUR_MILLIS).toString() + " " + ctx.getString(R.string.hours_ago)

    } else {
        return getOnlyDate(time, "dd MMM, yyyy")
    }
}
*/

fun getOnlyDate(tinestamp: Long, dateFormatType: String): String {
    val smsTime = Calendar.getInstance()
    smsTime.timeInMillis = tinestamp
    val dateTimeFormatString = dateFormatType
    return DateFormat.format(dateTimeFormatString, smsTime).toString()
}

fun showToast(context: Context?, msg: String?) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun showLongToast(context: Context?, msg: String?) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}


fun showToast(context: Context?, msg: Int?) {
    msg?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
}

fun getUniqueIntId(): Int {
    return Calendar.getInstance().timeInMillis.toInt() + Random().nextInt(900) + 100
}

fun getUniqueId(): String {
    return (Calendar.getInstance().timeInMillis.toInt() + Random().nextInt(900) + 100).toString()
}

fun setAddress(context: Context?, latitude: Float, longitude: Float): String {
    var result = StringBuilder()
    val geocoder: Geocoder
    var addresses: List<Address>? = null
    geocoder = context?.let { Geocoder(it, Locale.getDefault()) }!!
    try {
        addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        if (addresses != null && addresses.size > 0) {
            val address = addresses.get(0)
            result.append(address.locality + ", ")
            result.append(address.countryName)
        } else {
            Toast.makeText(context, R.string.no_result_found, Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result.toString()
}