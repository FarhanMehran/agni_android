package com.capcorp.ui.user_signup.thanks

import android.content.Intent
import android.os.Bundle
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import kotlinx.android.synthetic.main.activity_thanks_shopper.*

class ThanksShopperActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thanks_shopper)
        fab_next_click.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}
