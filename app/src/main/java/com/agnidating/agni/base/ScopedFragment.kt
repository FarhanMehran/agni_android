package com.agnidating.agni.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.agnidating.agni.R
import com.agnidating.agni.ui.activities.getStarted.GetStarted
import com.agnidating.agni.utils.errorDialog
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.successDialog

abstract class ScopedFragment : Fragment() {

    private fun hideKeyboard(v: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }


    fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            hideKeyboard(it)
        }
    }

    fun handleBack(ivBack:ImageView){
       ivBack.setOnClickListener {
           errorDialog("You are being redirected to the Login screen.Do you want to continue?",true){
               startActivity(Intent(requireContext(), GetStarted::class.java))
               (requireActivity() as Activity).finishAffinity()
           }
       }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        intent?.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        overridePendingTransitionEnter()
    }

    private fun overridePendingTransitionEnter() {
        requireActivity().overridePendingTransition(
            R.anim.slide_from_right, R.anim.slide_to_left
        )
    }

/*    fun showProgress() {
        MyProgress.show(requireActivity() as AppCompatActivity)
    }

    fun hideProgress() {
        MyProgress.hide(requireActivity() as AppCompatActivity)
    }*/

    private fun slideBottomToSlideUp() {
        requireActivity().overridePendingTransition(
            R.anim.slide_up, R.anim.slide_bottom
        )
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        slideBottomToSlideUp()
    }
}