package com.capcorp.ui.user.homescreen.account.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.payment.ConnectStripeActivity
import com.capcorp.ui.driver.stripe.ConnectStripeOnBoarding
import com.capcorp.utils.getAuthAccessToken
import kotlinx.android.synthetic.main.activity_payouts.*
import stfalcon.universalpickerdialog.UniversalPickerDialog


class PayoutsActivity : AppCompatActivity(), PayoutContract.View,
    UniversalPickerDialog.OnPickListener {

    lateinit var context: PayoutsActivity
    private val presenter = PayoutsPresenter()
    var array: Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payouts)
        presenter.attachView(this)

        context = this@PayoutsActivity

        presenter.getCountryAPICall()

        tvBack.setOnClickListener {
            onBackPressed()
        }
        tvCountry.setOnClickListener {
            if (array.isNotEmpty()) {
                UniversalPickerDialog.Builder(this)
                    .setTitle(context.getString(R.string.select_country))
                    .setListener(this)
                    .setInputs(
                        UniversalPickerDialog.Input(0, array),
                    )
                    .show()
            }
        }

        btnConnectToStripe.setOnClickListener {
            if (!tvCountry.text.toString().trim().isEmpty())
                presenter.onConnectStripeOnBoardingApi(
                    getAuthAccessToken(this),
                    tvCountry.text.toString().trim()
                )
            else
                Toast.makeText(this@PayoutsActivity,"Please select Country",Toast.LENGTH_LONG).show()
        }


    }

    override fun countryAPISuccess(data: Array<String>?) {
        data?.let {
            array = it
        }

    }

    override fun onConnectStripeOnBoardingSuccess(stripeData: ConnectStripeOnBoarding.Data) {
        if(!stripeData.url.isNullOrEmpty()) {
            val intent = Intent(this, ConnectStripeActivity::class.java)
            intent.putExtra("url", stripeData.url)
            intent.putExtra("stripeID", stripeData.stripeId)
            startActivity(intent)
            finish()
        }else if(stripeData.alreadyRegistegitred){
            Toast.makeText(this@PayoutsActivity,"Already connected with Stripe",Toast.LENGTH_LONG).show()
            finish()
        }else{
            Toast.makeText(this@PayoutsActivity,"Something went wrong. Try again after sometime",Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {

    }

    override fun handleApiError(code: Int?, errorBody: String?) {

    }

    override fun validationsFailure(type: String?) {

    }

    override fun onPick(selectedValues: IntArray?, key: Int) {
        selectedValues?.let {
            val str: String = array[it[0]]
            tvCountry.text = str
            tvCountryHint.visibility = View.VISIBLE
            btnConnectToStripe.setBackgroundResource(R.drawable.bg_button)
            btnConnectToStripe.isEnabled = true

        }
    }

}
