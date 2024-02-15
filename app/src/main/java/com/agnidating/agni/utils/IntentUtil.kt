package com.agnidating.agni.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent


fun <T> Activity.startNewActivity(destinationActivity: Class<T>) {
    val intent = Intent(this, destinationActivity)
    this.startActivity(intent)
    //this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun <T> Activity.startNewActivityWithBundle(destinationActivity: Class<T>, bundle: Bundle?) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null)
        intent.putExtras(bundle)
    this.startActivity(intent)
    //this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}


fun <T> Activity.startNewActivityForResult(
    destinationActivity: Class<T>,
    bundle: Bundle?,
    requestCode: Int
) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null)
        intent.putExtras(bundle)
    this.startActivityForResult(intent, requestCode)
    //this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun <T> Activity.startNewActivityWithFinish(
    destinationActivity: Class<T>,
    bundle: Bundle?
) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null)
        intent.putExtras(bundle)
    this.startActivity(intent)
    this.finish()
    //this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}
fun Activity.openChromeTab(url:String){
    val builder = CustomTabsIntent.Builder()
    val colorInt: Int = Color.parseColor("#000000")
    val defaultColors = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(colorInt)
        .build()
    builder.setDefaultColorSchemeParams(defaultColors)
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

fun <T> Activity.startNewActivityWithAllFinish(
    destinationActivity: Class<T>,
    bundle: Bundle?
) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null)
        intent.putExtras(bundle)
    this.startActivity(intent)
    this.finishAffinity()
    //this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}


fun <T> Activity.startNewActivityWithAllFinishWithOutAnimation(
    destinationActivity: Class<T>,
    bundle: Bundle?
) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null)
        intent.putExtras(bundle)
    this.startActivity(intent)
    this.finishAffinity()
}

fun <T> Activity.startNewActivityWithAllFinishWithBackAnimation(destinationActivity: Class<T>) {
    val intent = Intent(this, destinationActivity)
    this.startActivity(intent)
    this.finishAffinity()
    //this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}

fun <T> Activity.startNewActivityWithClearTop(destinationActivity: Class<T>, bundle: Bundle?) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null)
        intent.putExtras(bundle)
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    finish()
    //this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun Activity.finishActivity() {
    this.finish()
    //this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}

