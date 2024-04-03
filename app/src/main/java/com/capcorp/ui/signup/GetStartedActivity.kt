package com.capcorp.ui.signup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user_signup.signup_users.SignUpActivity
import com.capcorp.utils.PREF_LANG
import com.capcorp.utils.SharedPrefs
import com.capcorp.utils.makeLinks
import kotlinx.android.synthetic.main.activity_getstarted.*


class GetStartedActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getstarted)

        btnGetStarted.setOnClickListener {
            start(SignUpActivity::class.java)
        }

        btnLogin.setOnClickListener {
            start(SignInActivity::class.java)
        }


        val language = SharedPrefs.with(this).getString(PREF_LANG, "en") ?: "en"
        if (language.equals("en")) {
            textView.makeLinks(
                Pair("Terms and Conditions", View.OnClickListener {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://h2d.app/terms_and_conditions")
                    )
                    startActivity(browserIntent)                       /* val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.h2d.app/FAQ"))
                        startActivity(browserIntent)*/
                }),
                Pair("Privacy Policy", View.OnClickListener {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://h2d.app/privacy_policy"))
                    startActivity(browserIntent)                       /* val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.h2d.app/FAQ"))
                        startActivity(browserIntent)*/
                })
            )
        } else {
            textView.makeLinks(
                Pair("Términos y Condiciones", View.OnClickListener {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://h2d.app/terms_and_conditions")
                    )
                    startActivity(browserIntent)                        /*val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.h2d.app/FAQ"))
                        startActivity(browserIntent)*/
                }),
                Pair("Política de Privacidad", View.OnClickListener {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://h2d.app/privacy_policy"))
                    startActivity(browserIntent)                        /*val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.h2d.app/FAQ"))
                        startActivity(browserIntent)*/
                })
            )
        }

    }

}
