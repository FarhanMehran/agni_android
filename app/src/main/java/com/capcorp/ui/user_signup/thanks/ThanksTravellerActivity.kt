package com.capcorp.ui.user_signup.thanks

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user_signup.upload_documents.UploadDocumentsActivity
import com.capcorp.utils.UserType
import kotlinx.android.synthetic.main.activity_thanks_traveller.*

class ThanksTravellerActivity : BaseActivity(), View.OnClickListener {


    private var mUserType: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thanks_traveller)
        setListener()
        mUserType = intent.getStringExtra("type").toString()
        if (mUserType.equals(UserType.USER)) {
            tvInfos.text = getString(R.string.verify_account_messages)
            tvGetStarted.text = getString(R.string.verify_account)
        } else {
            tvInfos.text = getString(R.string.you_can_verify_your_account)
            tvGetStarted.text = getString(R.string.upload_your_documents)
        }
    }

    fun setListener() {
        tvSkip.setOnClickListener(this)
        tvGetStarted.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSkip -> {
                if (mUserType.equals(UserType.USER)) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    val intent =
                        Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }

            }

            R.id.tvGetStarted -> {
                val intent = Intent(this, UploadDocumentsActivity::class.java)
                intent.putExtra("from_screen_type", "signup_process")
                startActivity(intent)
            }
        }
    }
}
