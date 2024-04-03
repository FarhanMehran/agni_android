package com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capcorp.R
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.SharedPrefs
import com.capcorp.utils.USER_DATA
import com.capcorp.utils.UserType
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.activity_order_success.*


class OrderSuccessActivity : AppCompatActivity() {

    lateinit var context: OrderSuccessActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        context = this@OrderSuccessActivity

        if (intent.getStringExtra("from").equals("offer")) {
            tvTitle.text = getString(R.string.offer_submited_successfully)
            tvTitle2.text = getString(R.string.find_all_of_your_trip)
        }
        btnDone.setOnClickListener {
            val userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)
            if (!intent.getStringExtra("from").equals("offer")) {
                startActivity(
                    Intent(
                        this,
                        HomeActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                overridePendingTransition(0, 0)
                finish()
            } else {
                startActivity(
                    Intent(
                        this,
                        com.capcorp.ui.driver.homescreen.HomeActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                overridePendingTransition(0, 0)
                finish()
            }
        }

    }

    override fun onBackPressed() {
        val userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)
        if (!intent.getStringExtra("from").equals("offer")) {
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            overridePendingTransition(0, 0)
            finish()
        } else {
            startActivity(
                Intent(
                    this,
                    com.capcorp.ui.driver.homescreen.HomeActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            overridePendingTransition(0, 0)
            finish()
        }
    }

}
