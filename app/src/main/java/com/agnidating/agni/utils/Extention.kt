package com.agnidating.agni.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.agnidating.agni.R
import com.agnidating.agni.model.BirthDate
import com.agnidating.agni.model.profile.Images
import com.agnidating.agni.utils.custom_view.ErrorDialog
import com.agnidating.agni.utils.custom_view.SuccessDialog
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


fun log(msg: String?) {
   // Log.d("agni_log", msg.toString())
}

fun String.logs() {
  //  Log.d("agni_log", this)
}

fun String.isValidEmail(): Boolean {
    return !(TextUtils.isEmpty(this) || !Patterns.EMAIL_ADDRESS.matcher(this).matches())
}

fun View.gone() {
    this.visibility = View.GONE
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun TextView.setSpannedString(vararg spans:String,onTap:(String)->Unit){
    val completeText=text
    val spannableString= SpannableString(completeText)
    for (i in spans.indices){
        val span=spans[i]
        val clickableSpan=object: ClickableSpan(){
            override fun onClick(widget: View) {
                onTap.invoke(span)
            }
        }
        val spanStart=completeText.indexOf(span)
        val spanEnd=spanStart+span.length
        spannableString.setSpan(clickableSpan,spanStart,spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text=spannableString
    movementMethod= LinkMovementMethod.getInstance()
}

fun String.isValidPersonName(): Boolean {
    return this.matches(Regex(REGEX_PATTERN.PERSON_NAME))
}

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.toAge(): String {
    val dob: Calendar = Calendar.getInstance()
    val today: Calendar = Calendar.getInstance()

    SimpleDateFormat("yyyy-MM-dd").parse(this).also {
        dob.time = it
    }

    var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    val ageInt = age

    return ageInt.toString()
}

fun ArrayList<File>.toMultipartList(key: String, type: String): ArrayList<MultipartBody.Part> {
    val list = ArrayList<MultipartBody.Part>()
    this.forEach {
        val requestBody = it.asRequestBody(type.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(key, it.name, requestBody)
        list.add(part)
    }
    return list
}

fun AppCompatActivity.successDialog(msg: String?, onDismiss: () -> Unit) {
    val dialog = SuccessDialog(msg, onDismiss)
    dialog.show(supportFragmentManager, "")
}

fun Fragment.successDialog(msg: String?, onDismiss: () -> Unit) {
    val dialog = SuccessDialog(msg, onDismiss)
    dialog.show(parentFragmentManager, "")
}

fun AppCompatActivity.errorDialog(msg: String?,visibility:Boolean=false, onDismiss: () -> Unit) {
    val dialog = ErrorDialog(msg, onDismiss)
    dialog.visible=visibility
    dialog.show(supportFragmentManager, "")
}

fun Fragment.errorDialog(msg: String?,visibility:Boolean=false, onDismiss: () -> Unit) {
    val dialog = ErrorDialog(msg, onDismiss)
    dialog.visible=visibility
    dialog.show(parentFragmentManager, "")
}


@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.changeStatusBarColorToTransParent() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

fun Activity.loadJSONFromAsset(asset: String): String? {
    val json: String? = try {
        val `is`: InputStream = this.assets.open(asset)
        val size: Int = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        String(buffer, charset("UTF-8"))
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
    return json
}


@BindingAdapter("selectedCountry", "countryPicker")
fun ImageView.setFlag(selectedCountry: String?, countryPicker: CountryCodePicker) {
    countryPicker.setCountryForNameCode(selectedCountry)
    this.setImageResource(countryPicker.selectedCountryFlagResourceId)
}

@BindingAdapter("edit_text")
fun CountryCodePicker.setEditText(editText: EditText) {
    registerCarrierNumberEditText(editText)
}

fun Fragment.checkNetwork(): Boolean {
    if (requireActivity().isOnline().not()) {
        requireActivity().showToast(getString(R.string.network_error))
        return false
    }
    return true
}

fun ImageView.load(file: File, placeHolder: Int?) {
    if (placeHolder == null) {
        Glide.with(context).load(file).into(this)
    } else {
        Glide.with(context).load(file).placeholder(placeHolder).into(this)
    }
}

fun ImageView.load(
    profileUrl: String?,
    baseUrl: String? = null,
    placeHolder: Int? = null
) {
    val url = if (baseUrl == null) profileUrl + "" else baseUrl + profileUrl
    url.logs()
    if (placeHolder == null) {
        Glide.with(context).load(url).into(this)
    } else {
        Glide.with(context).load(url).placeholder(placeHolder).into(this)
    }
}

@BindingAdapter("photo", "baseUrl")
fun ImageView.setPhoto(photo: String, baseUrl: String? = null) {
    load(photo, baseUrl)
}

@BindingAdapter("photos", "baseUrl")
fun ImageView.setPhotos(photos: List<Images>?, baseUrl: String? = null) {
    if (photos.isNullOrEmpty().not()) {
        load(photos!![0].profile, baseUrl)
    }
}

fun Activity.checkNetwork(): Boolean {
    if (isOnline().not()) {
        showToast(getString(R.string.network_error))
        return false
    }
    return true
}

fun String.isDateValid(dateFormat: String): Boolean {
    val format = SimpleDateFormat(dateFormat, Locale.getDefault())
    val date: Date = try {
        format.parse(this)
    } catch (e: Exception) {
        Date()
    }
    return format.format(date).equals(this)
}


fun getDeviceToken(onReceive: (String) -> Unit) {
    try {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onReceive.invoke("")
                }
                else{
                    onReceive.invoke(task.result)
                }
            }
    } catch (e: Exception) {
        e.message?.logs()
    }
}

fun Context.openDatePicker(selectedTime:Long,onResult:(result:BirthDate)->Unit){
    val c = Calendar.getInstance()
    c.time= Date(selectedTime)
    val mYear = c[Calendar.YEAR]
    val mMonth = c[Calendar.MONTH]
    val mDay = c[Calendar.DAY_OF_MONTH]

    val datePickerDialog = DatePickerDialog(
        this,
        { pickerView, year, monthOfYear, dayOfMonth ->

            val day: String = if (dayOfMonth.toString().length == 1) {
                "0$dayOfMonth"
            } else {
                dayOfMonth.toString()
            }

            val month = if (monthOfYear<9) {
                "0${monthOfYear + 1}"
            } else {
                "${monthOfYear + 1}"
            }
            val date=SimpleDateFormat("yyyy-M-d").parse("$year-$month-$day")
            onResult(BirthDate(day,month,year.toString(),date.time))
        }, mYear, mMonth, mDay
    )

    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()-568080000000 - 1000
    datePickerDialog.show()
}

@BindingAdapter("date")
fun TextView.setDate(date: String) {
    text = date.toDate()
}

@BindingAdapter("time")
fun TextView.setTime(time: String) {
    text = time.to12Time()
}

@SuppressLint("SimpleDateFormat")
fun String.toTime(): String {
    return try {
        var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(this)
        format = SimpleDateFormat("hh:mm a")
        format.format(date)
    } catch (e: Exception) {
        ""
    }
}
@SuppressLint("SimpleDateFormat")
fun String.to12Time(): String {
    var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(this)
    format = SimpleDateFormat("hh:mm a")
    return format.format(date)
}
@SuppressLint("SimpleDateFormat")
fun String.toDate(): String {
    var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(this)
    format = SimpleDateFormat("dd-MMM-yyyy")
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun String.toMessageDate(): String {
    var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(this)
    val result = DateUtils.getRelativeTimeSpanString(
        date.time,
        System.currentTimeMillis(),
        DateUtils.DAY_IN_MILLIS
    ).toString()
    if (result.lowercase() != "today" && result.lowercase() != "yesterday") {
        format = SimpleDateFormat("EEE, dd MMM")
        return format.format(date)
    }
    return result
}

fun String.toast(mContext: Context) {
    Toast.makeText(mContext, this, Toast.LENGTH_SHORT).show()
}


@SuppressLint("SimpleDateFormat")
fun getCreatedOn(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date())
}
@SuppressLint("HardwareIds")
fun Context.getDeviceIds(): String {
    return Settings.Secure.getString(
        this.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

fun EditText.hideKeyboard(){
    val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


@SuppressLint("SimpleDateFormat")
fun isDateEquals(firstDateString: String, secondDateString: String): Boolean {
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val firstDate = simpleDateFormat.parse(firstDateString)
    val secondDate = simpleDateFormat.parse(secondDateString)
    simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
    return simpleDateFormat.format(firstDate).equals(simpleDateFormat.format(secondDate))
}

fun religions(): ArrayList<String> {
    return arrayListOf(
        "Hindu ",
        "Muslim ",
        "Sikh ",
        "Christian ",
        "Jain ",
        "Parsi ",
        "Buddhist ",
        "Spiritual ",
        "Not religious ",
        "Other ",
        "Prefer not to say"
    )
}

/**
 * get heights in ft. and in symbols
 */
fun getHeights(): Array<String> {
    var heights=ArrayList<String>()
    for (i in 3..10){
        for (j in 0..11){
            heights.add("${i}' $j\"")
            if (i==10)
                break
        }
    }
    heights.toString().logs()
    return heights.toTypedArray()
}


fun communities(): ArrayList<String> {
    return arrayListOf(
        "Punjabi ",
        "Gujrati ",
        "Hindi ",
        "Telugu ",
        "Sunni ",
        "Shia ",
        "Tamil ",
        "Malayali ",
        "Bengali ",
        "Marathi",
        "Urdu ",
        "Kannada",
        "Marwari",
        "Odia ",
        "Sindhi",
        "Kashmiri",
        "Rajashtani ",
        "Jatt ",
        "Other ",
        "No community ",
        "Prefer not to say"
    )
}



//Blur effect