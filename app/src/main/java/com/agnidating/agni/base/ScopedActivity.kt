package com.agnidating.agni.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agnidating.agni.R
import com.agnidating.agni.model.MessageEvent
import com.agnidating.agni.ui.activities.auth.phone.YourPhoneActivity
import com.agnidating.agni.utils.custom_view.ProgressDialog
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
abstract class ScopedActivity : AppCompatActivity() {
    private var progress: ProgressDialog? = null

    @Inject
    lateinit var mySharedPrefs: SharedPrefs
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        progress = ProgressDialog.getInstant(this)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: MessageEvent?) {
        if (event?.logout == true) {
            logOut()
        }
    }

    open fun logOut() {
        mySharedPrefs.clearPreference()
        startActivity(Intent(this, YourPhoneActivity::class.java))
        finishAffinity()
    }

    /* fun hideKeyboard(v: View) {
         val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         imm.hideSoftInputFromWindow(v.windowToken, 0)
     }*/

    fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 60)
        toast.show()
    }


    fun showProgress() {
        progress?.showProgres()
    }

    fun hideProgress() {
        progress?.hideProgress()
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        overridePendingTransitionEnter()
    }

    private fun overridePendingTransitionEnter() {
        overridePendingTransition(
            R.anim.slide_from_right, R.anim.slide_to_left
        )
    }

    private fun slideBottomToSlideUp() {
        overridePendingTransition(
            R.anim.slide_up, R.anim.slide_bottom
        )
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        slideBottomToSlideUp()
    }

    private fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionExit()
    }
}