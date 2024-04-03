package com.capcorp.ui.signup

import android.os.Bundle
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.signup.enter_password.EnterPasswordFragment
import com.capcorp.ui.signup.enter_phoneno.EnterPhoneNumberFragment
import com.capcorp.ui.signup.login.LoginFragment
import com.capcorp.ui.signup.phone_verification.PhoneVerificationFragment
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity() {

    private var titleArray: Array<String>? = null

    private var isSignup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        titleArray = resources.getStringArray(R.array.signup_titles)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (intent.getBooleanExtra("signup", false)) {
            fragmentManager.beginTransaction().add(R.id.container, EnterPhoneNumberFragment())
                .commit()
            tvTitle.text = titleArray!![0]
            tvTitle.visibility = View.VISIBLE
            tvTitleLogin.visibility = View.INVISIBLE
            isSignup = true
        } else {
            fragmentManager.beginTransaction().add(R.id.container, LoginFragment()).commit()
            tvTitleLogin.visibility = View.VISIBLE
            tvTitle.visibility = View.INVISIBLE
            isSignup = false
        }

        setListeners()
    }

    fun showPhoneNumberFragment(data: HashMap<String, String>) {

        val bundle = Bundle()
        bundle.putSerializable("mapData", data)

        val fragment = EnterPhoneNumberFragment()
        fragment.arguments = bundle

        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        tvTitle.text = titleArray!![0]
        tvTitle.visibility = View.VISIBLE
        tvTitleLogin.visibility = View.INVISIBLE
        isSignup = true
    }


    private fun setListeners() {
        ivBack.setOnClickListener { onBackPressed() }
        fragmentManager.addOnBackStackChangedListener {
            if (fragmentManager.backStackEntryCount > 0) {
                when (fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name) {

                    EnterPhoneNumberFragment::class.java.simpleName -> {
                        tvTitle.animateText(titleArray!![1])
                    }

                    PhoneVerificationFragment::class.java.simpleName -> {
                        tvTitle.animateText(titleArray!![2])
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
        }
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }
}
