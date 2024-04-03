package com.capcorp.ui.driver.homescreen.mydeliveries

import android.os.Bundle
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity


class MyDeliveriesActivity : BaseActivity() {

    companion object {
        var pickCountry: String = ""
        var dropDownCountry: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_deliveries)

        pickCountry = intent.getStringExtra("pickCountry").toString()
        dropDownCountry = intent.getStringExtra("dropDownCountry").toString()

        val bundle = Bundle()
        bundle.putString("pickCountry", pickCountry)
        bundle.putString("dropDownCountry", dropDownCountry)
        val fragInfo = MyDeliveriesFragment()
        fragInfo.arguments = bundle
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragInfo, MyDeliveriesFragment.TAG).commit()
    }

}
