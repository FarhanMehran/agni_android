package com.capcorp.ui.signup

import android.os.Bundle
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.signup.enter_phoneno.EnterPhoneNumberFragment
import com.capcorp.ui.signup.login.LoginFragment
import com.capcorp.utils.USERTYPE
import kotlinx.android.synthetic.main.activity_signup.*

class SignInActivity : BaseActivity() {

    private var isSignup = false
    private var titleArray: Array<String>? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        titleArray = resources.getStringArray(R.array.signup_titles)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        type = intent.getStringExtra(USERTYPE)

        var bundle = Bundle()
        bundle.putString("type", type)

        var fragment = LoginFragment()
        fragment.arguments = bundle

        //fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        fragmentManager.beginTransaction().add(R.id.container, fragment).commit()
        tvTitleLogin.visibility = View.VISIBLE
        //tvTitle.visibility = View.INVISIBLE
        isSignup = false
        setListeners()
    }

    fun showPhoneNumberFragment(data: HashMap<String, String>) {

        var bundle = Bundle()
        bundle.putSerializable("mapData", data)

        var fragment = EnterPhoneNumberFragment()
        fragment.arguments = bundle

        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        tvTitleLogin.text = titleArray!![0]

        isSignup = true
    }

    private fun setListeners() {
        ivBack.setOnClickListener { onBackPressed() }
        /*fragmentManager.addOnBackStackChangedListener {
            if (fragmentManager.backStackEntryCount > 0) {
                when (fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name) {

                    EnterPhoneNumberFragment::class.java.simpleName -> {
                        tvTitle.animateText(titleArray!![1])
                    }

                    PhoneVerificationFragment::class.java.simpleName -> {
                        tvTitle.animateText(titleArray!![2])
                    }

                    ProfileSetupFragment::class.java.simpleName -> {
                        tvTitle.animateText(titleArray!![3])
                    }
                    LoginFragment::class.java.simpleName -> {
                        tvTitle.animateText(getString(R.string.login))
                    }
                    EnterPasswordFragment::class.java.simpleName -> {
                        tvTitle.animateText(getString(R.string.login))
                    }
                    else -> {
                        tvTitle.animateText(titleArray!![0])
                    }
                }
            } else {
                tvTitle.animateText(if (isSignup) titleArray!![0] else getString(R.string.login))
            }
        }*/
    }


    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }
}
