package com.capcorp.ui.driver.homescreen.home.makeoffer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity.OrderSuccessActivity
import com.capcorp.utils.*
import com.capcorp.utils.location.LocationProvider
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_make_an_offer.*
import kotlinx.android.synthetic.main.activity_make_an_offer.tvBack
import kotlinx.android.synthetic.main.activity_shipment_next.*
import java.math.BigDecimal
import java.util.*

class MakeAnOfferActivity : BaseActivity(), MakeAnOfferContract.View {

    private val presenter = MakeAnOfferPresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var price = 0.0
    private var result = false
    //private lateinit var locationProvide: LocationProvider
    private var sourceLatLong: LatLng? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var myCalendar = Calendar.getInstance()
    var itemPrice: String = ""
    var shipping: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogIndeterminate = DialogIndeterminate(this)
        dialogIndeterminate.setProgressColor(ContextCompat.getColor(this, R.color.colorPrimary))
        setContentView(R.layout.activity_make_an_offer)
        presenter.attachView(this)
        //tvMinPrice.text = """${getString(R.string.minimum_price_for_your_transport_is)} ${getString(R.string.currency_sign)}${"5"}"""
        tvMinPrice.text =
            getString(R.string.recommended_delivery_reward) + getString(R.string.dollar) + intent.getStringExtra(
                "itemPrice"
            ) + " " + getString(R.string.be_aware_that_changes)
        setListeners()

        val date = intent.getLongExtra("date",0)
        /*locationProvide = LocationProvider.CurrentLocationBuilder(this).build()
        locationProvide.getLastKnownLocation(OnSuccessListener {

            if (it != null) {

                sourceLatLong = LatLng(it.latitude, it.longitude)
                latitude = sourceLatLong!!.latitude
                longitude = sourceLatLong!!.longitude
            }


        })*/

        etDate.setText(getOnlyDate(date, "MMM dd, yyyy"))

        etDate.setOnClickListener { openDateTimePicker() }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun setListeners() {
        etPrice.addTextChangedListener(textWatcher)
        llFab.setOnClickListener(fabClickListener)
        tvBack.setOnClickListener { onBackPressed() }

    }

    private val fabClickListener = View.OnClickListener {
        val price = try {
            etPrice.text.toString().toBigDecimal()
        } catch (e: Exception) {
            BigDecimal(0)
        }
        this.price = price.toDouble()
        if (etPrice.text.toString().length == 0) {
            llFab.showSnack(getString(R.string.please_enter_reward_amount))
        } else {
            if (isValidationsOk(price)) {
                if (etDate.text.toString().trim().length == 0) {
                    llFab.showSnack(getString(R.string.select_delivery_date))
                } else {
                    if (etShippingCharge.text.toString().isEmpty()) {
                        intent.getStringExtra("cardId")?.let { it1 ->
                            presenter.makeOffersAndAcceptOrderApiCall(
                                getAuthAccessToken(this),
                                intent.getStringExtra(ORDER_ID),
                                price.toString(),
                                DriverAction.OFFER,
                                latitude,
                                longitude,
                                myCalendar.timeInMillis.toString(),
                                it1,
                                0.0
                            )
                        }
                    } else {
                        shipping = etShippingCharge.text.toString().toDouble()
                        intent.getStringExtra("cardId")?.let { it1 ->
                            presenter.makeOffersAndAcceptOrderApiCall(
                                getAuthAccessToken(this),
                                intent.getStringExtra(ORDER_ID),
                                price.toString(),
                                DriverAction.OFFER,
                                latitude,
                                longitude,
                                myCalendar.timeInMillis.toString(),
                                it1,
                                shipping
                            )
                        }
                    }
                }

            }
        }

    }

    private fun isValidationsOk(price: BigDecimal): Boolean {
        return if (price.compareTo(BigDecimal(3)) == -1) {
            llFab.showSnack(
                """${getString(R.string.you_have_to_pay_atleast)} ${getString(R.string.currency_sign)}$TRANSPORT_MIN_PRICE ${
                    getString(
                        R.string.for_the_transport
                    )
                }""".trimMargin()
            )
            false
        } else {
            true
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val str = s.toString()
            if (str.startsWith("0") && str.length == 2 && !str.elementAt(1).equals(".")) {
                etPrice.setText(str.replaceFirst("0", ""))
                etPrice.setSelection(etPrice.text.toString().length)
            } else if (str.startsWith(".") && str.length == 2) {
                etPrice.setText("0" + str)
                etPrice.setSelection(etPrice.text.toString().length)
            } else if (str.contains(".") && str.length > 1 && str.substring(
                    str.indexOf("."),
                    str.length
                ).length > 3
            ) {
                etPrice.setText(str.substring(0, str.length - 1))
                etPrice.setSelection(etPrice.text.toString().length)
            } else if (!str.contains(".") && str.length > 10) {
                etPrice.setText(str.substring(0, str.length - 1))
                etPrice.setSelection(etPrice.text.toString().length)
            }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
        }
    }


    override fun makeOfferApiSuccess() {
        startActivity(Intent(this, OrderSuccessActivity::class.java).putExtra("from", "offer"))
        finish()
    }

    override fun onBackPressed() {
        //val intent = Intent()
//        if (result) {
//            intent.putExtra(POSITION, getIntent().getIntExtra(POSITION, 0))
//            intent.putExtra(PRICE, price)
//            intent.putExtra("additional", shipping)
//            setResult(Activity.RESULT_OK, intent)
//        } else {
//            setResult(Activity.RESULT_CANCELED, intent)
//
//        }
        finish()
    }

    private fun openDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            etDate.setText(getFormatFromDate(myCalendar.time, "MMM dd, yyyy"))
            //openTimePicker()
        }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        showToast(this, R.string.sww_error)
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        if (code == 401) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(this).remove(ACCESS_TOKEN)
                finishAffinity()
                startActivity(Intent(this, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            showToast(this, errorBody)
        }
    }

    override fun validationsFailure(type: String?) {

    }
}
