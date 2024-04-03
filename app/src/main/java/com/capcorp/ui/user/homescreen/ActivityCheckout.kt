package com.capcorp.ui.user.homescreen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.payment.add_card.AddCardContract
import com.capcorp.ui.payment.add_card.AddCardPresenter
import com.capcorp.ui.payment.add_card.CardActivity
import com.capcorp.ui.payment.model.CardData
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailContract
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailPresenter
import com.capcorp.utils.*
import com.capcorp.webservice.models.DataApplyCoupon
import com.capcorp.webservice.models.DataRemoveCoupon
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.Offer
import com.capcorp.webservice.models.orders.OrderListing
import com.capcorp.webservice.models.request_model.AcceptOrderRequest
import com.codebrew.clikat.adapters.CardListAdapter
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.StripeIntent
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_checkout.tvBack
import org.json.JSONObject
import java.net.URL
import java.util.*


class ActivityCheckout : BaseActivity(), AddCardContract.View, CardListAdapter.DeleteCard,
    RequestDetailContract.View {

    private var presenterRequestDetailPresenter = RequestDetailPresenter()
    private lateinit var adapter: CardListAdapter
    private val presenter = AddCardPresenter()
    private val cardList = ArrayList<CardData>()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    var listData: CardData? = null
    var selectedPos = 0
    private var userData: SignupModel? = null
    private lateinit var layoutManager: LinearLayoutManagerWrapper
    lateinit var orderListingDetail: OrderListing
    lateinit var selectedOffer: Offer
    var volleyRequestQueue: RequestQueue? = null
    var updatePaymentMethod = BASE_URL + "/api/user/updatePaymentMethod"
    var rollbackURL = BASE_URL + "/api/user/orderRollback"
    var acceptOfferUrl = BASE_URL + "/api/user/acceptOffer"
    var pi_client_secret = ""
    lateinit var stripe: Stripe
    private var ourFeePercent: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        presenter.attachView(this)
        PaymentConfiguration.init(this, STRIPE_KEY)
        stripe = Stripe(this, STRIPE_KEY)

        presenterRequestDetailPresenter.attachView(this)
        userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)
        orderListingDetail =
            SharedPrefs.with(this).getObject("orderToCheckout", OrderListing::class.java)
        selectedOffer = SharedPrefs.with(this).getObject("offerToCheckout", Offer::class.java)

        Log.println(Log.VERBOSE, "CHECKOUT", " ORDER NUMBER - " + orderListingDetail.orderId)
        Log.println(Log.VERBOSE, "CHECKOUT", " ORDER PRICE - " + orderListingDetail.totalCheckout)
        Log.println(Log.VERBOSE, "CHECKOUT", " OFFER PRICE - " + selectedOffer.totalPrice)
        dialogIndeterminate = DialogIndeterminate(this)
        setRecyclerAdapter()
        presenterRequestDetailPresenter.getH2dFee(getAuthAccessToken(this))

        tvAddNewCard.setOnClickListener {
            startActivity(Intent(this, CardActivity::class.java))
        }
        tvBack.setOnClickListener {
            onBackPressed()
        }

        cvNext.setOnClickListener {
            if (ivNext.tag.equals("next")) {
                if (etCouponCode.text.toString().isNotEmpty()) {
                    orderListingDetail._id?.let { it1 ->
                        presenterRequestDetailPresenter.applyCoupon(
                            getAuthAccessToken(this),
                            it1, etCouponCode.text.toString().trim()
                        )
                    }
                } else {
                    val dialog = Dialog(this@ActivityCheckout,R.style.DialogStyle)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.alert_dialog)
                    val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                    val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                    val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                    tvTitle.text = getString(R.string.error)
                    tvDescription.text = getString(R.string.please_enter_coupen_code)
                    dialogButton.text = getString(R.string.ok)
                    dialogButton.setOnClickListener {
                        dialog.dismiss()
                        makeOrderRollback()
                    }
                    dialog.show()
                }
            } else {
                orderListingDetail._id?.let { it1 ->
                    presenterRequestDetailPresenter.removeCoupon(
                        getAuthAccessToken(this),
                        it1, etCouponCode.text.toString().trim()
                    )
                }
            }
        }
        //btnPayOrder.visibility = View.INVISIBLE
        btnPayOrder.setOnClickListener {
            this.onTouchUpPayOrder()
        }
    }

    private fun setView(orderListingDetail: OrderListing) {
        val itemPrice = orderListingDetail.itemPrice.toDouble()
        val taxAmount = orderListingDetail.tax.toDouble()
        val quantity = orderListingDetail.itemQuantity.toInt()

        val itemPriceValue = itemPrice.times(quantity)
        var ourFee = (itemPriceValue * ourFeePercent) / 100
        if (ourFee < 3) {
            ourFee = 3.0
        } else {
            if (ourFee > 50) {
                ourFee = 50.0
            }
        }

        var stripeFee = 0.0
        var offerPrice = 0.0;
        if(selectedOffer.totalPrice != null && selectedOffer.totalPrice.toString().isNotEmpty()){
            offerPrice = selectedOffer.totalPrice!!
        }

        if (this.orderListingDetail.type.equals("Shop")) {
            stripeFee =
                (((ourFee + itemPriceValue + offerPrice + taxAmount)*2.9)/100) + 0.3
        } else {
            stripeFee =
                (((ourFee + offerPrice)*2.9)/100) + 0.3
        }
        if (this.orderListingDetail.type.equals("Shop")) {
            textView6.setTextColor(resources.getColor(R.color.grey_error))
            txtProductPriceAmount.setTextColor(resources.getColor(R.color.grey_error))
        }

        val adminFee = ourFee + stripeFee + offerPrice;
        var newOrderTotal = 0.0
        if (this.orderListingDetail.type.equals("Shop")) {
            newOrderTotal = adminFee + itemPriceValue + taxAmount
        }else{
            newOrderTotal = adminFee
        }
        if(orderListingDetail.couponUse == true){
                txtCouponAmount.visibility = View.VISIBLE
                textView20.visibility = View.VISIBLE
                txtCouponAmount.text = "-" + getString(R.string.currency_sign) + (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    orderListingDetail.discount
                ))+ " USD"
                txtTotalAmount.text = getString(R.string.currency_sign) + (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    orderListingDetail.totalCheckout
                ))+ " USD"
                textView20.text = "Coupon " + orderListingDetail.couponKey
                ivNext.tag = "close"
                etCouponCode.setText(orderListingDetail.couponKey)
                ivNext.setImageResource(R.drawable.ic_close)
        }else{
            ivNext.tag = "next"
            ivNext.setImageResource(R.drawable.ic_down_arrow)
            etCouponCode.setText("")
            txtCouponAmount.visibility = View.GONE
            textView20.visibility = View.GONE
            txtTotalAmount.text = getString(R.string.currency_sign) + String.format(
                Locale.ENGLISH,
                "%.2f",
                orderListingDetail.totalCheckout
            )
        }

        txtTotalAmount.text = getString(R.string.currency_sign) + String.format(
            Locale.ENGLISH,
            "%.2f",
            newOrderTotal
        )+ " USD"

        txtProductPriceAmount.text = getString(R.string.currency_sign) + String.format(
            Locale.ENGLISH,
            "%.2f",
            (itemPriceValue + taxAmount)
        ) + " USD"
        txtH2DAmount.text = getString(R.string.currency_sign) + String.format(
            Locale.ENGLISH,
            "%.2f",
            ourFee
        ) + " USD"

        txtFeesAmount.text = getString(R.string.currency_sign) + String.format(
            Locale.ENGLISH,
            "%.2f",
            stripeFee
        ) + " USD"

        txtTravelerRewardAmount.text = getString(R.string.currency_sign) + String.format(
            Locale.ENGLISH,
            "%.2f",
            offerPrice
        ) + " USD"

        }

    @SuppressLint("SetTextI18n")
    private fun setupCheckoutCard() {
        var stripeFee = 0.0
        var itemPrice = orderListingDetail.itemPrice.toDouble()
        var taxAmount = orderListingDetail.tax.toDouble()
        var quantity = orderListingDetail.itemQuantity.toInt()

        var itemPriceValue = itemPrice.times(quantity)
        var ourFee = itemPriceValue.times(10).div(100)

        if (ourFee < 3) {
            ourFee = 3.0
        } else {
            if (ourFee > 50) {
                ourFee = 50.0
            }
        }
        if (this.orderListingDetail.type.equals("Shop")) {
            stripeFee =
                (((ourFee + itemPriceValue + selectedOffer.totalPrice!! + taxAmount).times(2.9)).div(
                    100
                )) + 0.3
        } else {
            stripeFee =
                (((ourFee + selectedOffer.totalPrice!! + taxAmount).times(2.9)).div(100)) + 0.3
        }

        var adminFee = ourFee + stripeFee + selectedOffer.totalPrice!!

        var newOrderTotal = 0.0

        if (this.orderListingDetail.type.equals("Shop")) {
            newOrderTotal = adminFee + itemPriceValue
        } else {
            newOrderTotal = adminFee
        }
        var feesAmount = stripeFee + ourFee
        var productPrice = itemPriceValue.plus(taxAmount)
        txtTravelerRewardAmount.text = getString(R.string.currency_sign) + (String.format(
            Locale.ENGLISH,
            "%.2f",
            selectedOffer.totalPrice
        ))+ " USD"
        txtFeesAmount.text =
            getString(R.string.currency_sign) + (String.format(Locale.ENGLISH, "%.2f", feesAmount)) + " USD"
        txtProductPriceAmount.text = getString(R.string.currency_sign) + (String.format(
            Locale.ENGLISH,
            "%.2f",
            productPrice
        )) + " USD"
        txtTotalAmount.text = getString(R.string.currency_sign) + (String.format(
            Locale.ENGLISH,
            "%.2f",
            newOrderTotal
        ))+ " USD"

        txtH2DAmount.text = "0.00 USD"

        if (orderListingDetail.couponUse == true) {
            txtCouponAmount.visibility = View.VISIBLE
            textView20.visibility = View.VISIBLE
            txtCouponAmount.text = "-" + getString(R.string.currency_sign) + (String.format(
                Locale.ENGLISH,
                "%.2f",
                orderListingDetail.discount
            ))+ " USD"
            txtTotalAmount.text = getString(R.string.currency_sign) + (String.format(
                Locale.ENGLISH,
                "%.2f",
                orderListingDetail.totalCheckout
            ))+ " USD"
            textView20.text = "Coupon " + orderListingDetail.couponKey
            ivNext.tag = "close"
            etCouponCode.setText(orderListingDetail.couponKey)
            ivNext.setImageResource(R.drawable.ic_close)
        }
    }

    private fun setRecyclerAdapter() {
        layoutManager = LinearLayoutManagerWrapper(this)
        rvCardsCheckout.layoutManager = layoutManager
        adapter = CardListAdapter(this, cardList, this)
        rvCardsCheckout.adapter = adapter
    }

    fun onTouchUpPayOrder() {
        if (adapter.getSelectedItemPosition() != null) {
            dialogIndeterminate.show()
            listData = adapter.getSelectedItemPosition()
            var orderId = orderListingDetail.orderId
            var cardId = listData!!.cardId

            if (orderId != null && cardId != null) {
                volleyRequestQueue = Volley.newRequestQueue(this)
                //dialog = ProgressDialog.show(activity, "", "Please wait...", true);
                val parameters: MutableMap<String, String> = HashMap()
                parameters.put("cardId", cardId)
                parameters.put("orderId", orderId)

                val strReq: StringRequest = object : StringRequest(
                    Method.PUT, updatePaymentMethod,
                    com.android.volley.Response.Listener { response ->
                        Log.e("CHECKOUT - ", "response: " + response)
                        //dialog?.dismiss()

                        // Handle Server response here
                        try {
                            val responseObj = JSONObject(response)
                            val statusCode = responseObj.getInt("statusCode")
                            val message = responseObj.getString("message")
                            if (responseObj.has("data")) {
                                val data = responseObj.getJSONObject("data")
                                // Handle your server response data here
                                this.generatePaymentIntent()
                            } else {
                                dialogIndeterminate.dismiss()
                                this.showPaymentErrorAlert()
                            }
                        } catch (e: Exception) {
                            dialogIndeterminate.dismiss()
                            this.showPaymentErrorAlert()
                            Log.e("CHECKOUT - ", "problem occurred")
                            e.printStackTrace()

                        }
                    },
                    com.android.volley.Response.ErrorListener { volleyError -> // error occurred
                        dialogIndeterminate.dismiss()
                        Log.e(
                            "CHECKOUT - ",
                            "problem occurred, volley error: " + volleyError.message
                        )
                        this.showPaymentErrorAlert()

                    }) {

                    override fun getParams(): MutableMap<String, String> {
                        return parameters
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {

                        val headers: MutableMap<String, String> = HashMap()
                        // Add your Header paramters here
                        headers.put("Authorization", getAuthAccessToken(applicationContext))
                        return headers
                    }
                }
                // Adding request to request queue
                volleyRequestQueue?.add(strReq)
            }
        } else {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.select_cards)
            tvDescription.text = getString(R.string.please_select_card)
            dialogButton.text = getString(R.string.yes)
            dialogButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    fun showPaymentErrorAlert() {
        val dialog = Dialog(this,R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog)
        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        tvTitle.text = getString(R.string.error)
        tvDescription.text = getString(R.string.checkout_payment_error)
        dialogButton.text = getString(R.string.ok)
        dialogButton.setOnClickListener {
            dialog.dismiss()
            makeOrderRollback()
        }
        dialog.show()
    }

    fun makeOrderRollback() {
        if (adapter.getSelectedItemPosition() != null) {
            listData = adapter.getSelectedItemPosition()
            var orderId = orderListingDetail._id
            if (orderId != null) {
                //  volleyRequestQueue = Volley.newRequestQueue(this)
                //dialog = ProgressDialog.show(activity, "", "Please wait...", true);
                val parameters: MutableMap<String, String> = HashMap()
                parameters.put("orderId", orderId)

                val strReq: StringRequest = object : StringRequest(
                    Method.PUT, rollbackURL,
                    com.android.volley.Response.Listener { response ->
                        Log.e("ROLLBACK - ", "response: " + response)
                        //dialog?.dismiss()

                        // Handle Server response here
                        try {
                            val responseObj = JSONObject(response)
                            val statusCode = responseObj.getInt("statusCode")
                            val message = responseObj.getString("message")
                            if (responseObj.has("data")) {
                                val data = responseObj.getJSONObject("data")
                                // Handle your server response data here
                            }
                        } catch (e: Exception) { // caught while parsing the response
                            Log.e("CHECKOUT - ", "problem occurred")
                            e.printStackTrace()
                        }
                    },
                    com.android.volley.Response.ErrorListener { volleyError -> // error occurred
                        Log.e(
                            "CHECKOUT - ",
                            "problem occurred, volley error: " + volleyError.message
                        )
                    }) {

                    override fun getParams(): MutableMap<String, String> {
                        return parameters
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {

                        val headers: MutableMap<String, String> = HashMap()
                        // Add your Header paramters here
                        headers.put("Authorization", getAuthAccessToken(applicationContext))
                        return headers
                    }
                }
                // Adding request to request queue
                volleyRequestQueue?.add(strReq)
            }
        } else {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.select_cards)
            tvDescription.text = getString(R.string.please_select_card)
            dialogButton.text = getString(R.string.yes)
            dialogButton.setOnClickListener {
                dialog.dismiss()

            }
            dialog.show()
        }
    }

    fun generatePaymentIntent() {
        if (CheckNetworkConnection.isOnline(this)) {

            val acceptOrderRequest = AcceptOrderRequest()

            val reward = selectedOffer.totalPrice

            val tax: Double = if (orderListingDetail.tax == null)
                0.0
            else
                orderListingDetail.tax.toDouble()

            //    val adminFee =   (calculateAdminFee(reward!!, orderListingDetail!!, tax) * 10).roundToInt() / 10.0
            val adminFee = String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAdminFee(reward!!, orderListingDetail, tax)
            )

            acceptOrderRequest.price = selectedOffer.totalPrice.toString()
            acceptOrderRequest.driverId = selectedOffer._id.toString()
            acceptOrderRequest.adminFee = adminFee.toDouble()
            if (orderListingDetail.insurance) {
                selectedOffer.driverCardId?.let {
                    acceptOrderRequest.driverCardId = it
                }
            }

            if (intent.hasExtra(NOTIFICATION)) {
                acceptOrderRequest.orderId = orderListingDetail._id.toString()
            } else {
                acceptOrderRequest.orderId = orderListingDetail._id.toString()
            }
            acceptOrderRequest.type = selectedOffer.type.toString()

            this.makeAcceptOfferRequest(acceptOrderRequest)

        } else {
            dialogIndeterminate.dismiss()
            CheckNetworkConnection.showNetworkError(txtCouponAmount)
        }
    }

    fun makeAcceptOfferRequest(acceptOrder: AcceptOrderRequest) {
        if (adapter.getSelectedItemPosition() != null) {
            listData = adapter.getSelectedItemPosition()
            var orderId = acceptOrder.orderId
            var price = acceptOrder.price
            var driverId = acceptOrder.driverId
            var type = acceptOrder.type
            var driverCardId = acceptOrder.driverCardId
            val reward = selectedOffer.totalPrice
            val tax: Double = if (orderListingDetail.tax == null)
                0.0
            else
                orderListingDetail.tax.toDouble()
            val adminFee = String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAdminFee(reward!!, orderListingDetail, tax)
            )
            if (orderId != null) {
                volleyRequestQueue = Volley.newRequestQueue(this)
                //dialog = ProgressDialog.show(activity, "", "Please wait...", true);
                val parameters: MutableMap<String, String> = HashMap()
                parameters.put("orderId", orderId)
                parameters.put("price", price)
                parameters.put("driverId", driverId)
                parameters.put("type", type)
                parameters.put("adminFee", adminFee)
                if (driverCardId != null) {
                    parameters.put("driverCardId", driverCardId)
                }

                val strReq: StringRequest = object : StringRequest(
                    Method.PUT, acceptOfferUrl,
                    com.android.volley.Response.Listener { response ->
                        Log.e("CHECKOUT - ", "response: " + response)
                        //dialog?.dismiss()

                        // Handle Server response here
                        try {
                            val responseObj = JSONObject(response)
                            val statusCode = responseObj.getInt("statusCode")
                            val message = responseObj.getString("message")
                            if (responseObj.has("data")) {
                                val data = responseObj.getJSONObject("data")
                                // Handle your server response data here
                                pi_client_secret = data.getString("client_secret")
                                orderListingDetail._id?.let {
                                    presenterRequestDetailPresenter.getOrderDetail(
                                        getAuthAccessToken(this),
                                        it
                                    )
                                }

                            } else {
                                dialogIndeterminate.dismiss()
                                this.showPaymentErrorAlert()
                            }
                        } catch (e: Exception) {
                            dialogIndeterminate.dismiss()
                            this.showPaymentErrorAlert()
                            Log.e("CHECKOUT - ", "problem occurred")
                            e.printStackTrace()
                        }
                    },
                    com.android.volley.Response.ErrorListener { volleyError -> // error occurred
                        dialogIndeterminate.dismiss()
                        Log.e(
                            "CHECKOUT - ",
                            "problem occurred, volley error: " + volleyError.message
                        )
                        this.showPaymentErrorAlert()
                    }) {

                    override fun getParams(): MutableMap<String, String> {
                        return parameters
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {

                        val headers: MutableMap<String, String> = HashMap()
                        // Add your Header paramters here
                        headers.put("Authorization", getAuthAccessToken(applicationContext))
                        return headers
                    }
                }
                // Adding request to request queue
                volleyRequestQueue?.add(strReq)
            }
        } else {
            dialogIndeterminate.dismiss()
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.select_cards)
            tvDescription.text = getString(R.string.please_select_card)
            dialogButton.text = getString(R.string.yes)
            dialogButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    fun confirmPaymentIntent() {

        var paymentMethodId = orderListingDetail.userCardDetails?.cardId

        stripe.confirmPayment(
            this,
            ConfirmPaymentIntentParams.createWithPaymentMethodId(
                paymentMethodId.toString(),
                pi_client_secret
            )
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                when (result.intent.status) {

                    StripeIntent.Status.Succeeded -> {
                        dialogIndeterminate.dismiss()
                        val dialog = Dialog(this@ActivityCheckout,R.style.DialogStyle)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(true)
                        dialog.setContentView(R.layout.alert_dialog_success)
                        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                        tvTitle.text = getString(R.string.success)
                        tvDescription.text = getString(R.string.checkout_payment_completed)
                        dialogButton.text = getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            val intent = Intent(this@ActivityCheckout, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            this@ActivityCheckout.startActivity(intent)
                        }
                        dialog.show()
                    }
                    else -> {
                        dialogIndeterminate.dismiss()
                        val dialog = Dialog(this@ActivityCheckout,R.style.DialogStyle)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(true)
                        dialog.setContentView(R.layout.alert_dialog)
                        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                        tvTitle.text = getString(R.string.error)
                        tvDescription.text = getString(R.string.checkout_payment_error)
                        dialogButton.text = getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            makeOrderRollback()
                        }
                        dialog.show()
                    }
                }

            }

            override fun onError(e: java.lang.Exception) {
                dialogIndeterminate.dismiss()
                val dialog = Dialog(this@ActivityCheckout,R.style.DialogStyle)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.alert_dialog)
                val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                tvTitle.text = getString(R.string.error)
                tvDescription.text = getString(R.string.checkout_payment_error)
                dialogButton.text = getString(R.string.ok)
                dialogButton.setOnClickListener {
                    dialog.dismiss()
                    makeOrderRollback()
                }
                dialog.show()
            }
        })
    }


    private fun calculateAdminFee(reward: Double, orderList: OrderListing, tax: Double): Double {

        val itemPrice = orderList.itemPrice.toDouble() * orderList.itemQuantity.toDouble()


        val tempOurFee = (itemPrice * 10.0) / 100

        var ourFee = 0.0

        ourFee = when {
            tempOurFee < 3 -> 3.0
            tempOurFee > 50 -> 50.0
            else -> tempOurFee
        }

        val tempStripeFee = (3.6 * (reward + ourFee + itemPrice + tax)) / 100

        val stripeFee = (tempStripeFee + 0.3) * 2

        val adminFee = stripeFee + reward + ourFee

        return adminFee

    }

    override fun onResume() {
        super.onResume()
        getAllCards()
    }

    fun getAllCards() {
        if (CheckNetworkConnection.isOnline(this)) {
            presenter.onCardList(getAuthAccessToken(this))
        }
    }

    override fun onCardListSuccess(list: List<CardData>?) {
        dialogIndeterminate.dismiss()
        cardList.clear()
        cardList.addAll(list as ArrayList)
        adapter.notifyDataSetChanged()
        if (cardList.size == 0) {
            tvNoCardFound.visibility = View.VISIBLE
            btnPayOrder.visibility = View.GONE
        } else {
            tvNoCardFound.visibility = View.GONE
            btnPayOrder.visibility = View.VISIBLE
        }

    }

    fun goBack(view: View) {
        onBackPressed()
    }


    override fun onAddCarcSuccess() {
        TODO("Not yet implemented")
    }

    override fun onDeleteCardSuccess() {
        cardList.removeAt(selectedPos)
        adapter.notifyDataSetChanged()
        if (cardList.size == 0) {
            tvNoCardFound.visibility = View.VISIBLE
            btnPayOrder.visibility = View.GONE
        } else {
            tvNoCardFound.visibility = View.GONE
            btnPayOrder.visibility = View.VISIBLE
        }

    }

    override fun acceptRequestSuccess() {
    }

    override fun orderDetailSuccess(data: OrderListing) {
        // orderId = data._id.toString()
        orderListingDetail = data
        confirmPaymentIntent()
    }

    override fun applyCouponSuccess(body: DataApplyCoupon) {
        ivNext.tag = "close"
        ivNext.setImageResource(R.drawable.ic_close)
        txtCouponAmount.visibility = View.VISIBLE
        textView20.visibility = View.VISIBLE
        txtCouponAmount.text = "-" + getString(R.string.currency_sign) + (String.format(
            Locale.ENGLISH,
            "%.2f",
            body.discount
        ))
        txtTotalAmount.text = getString(R.string.currency_sign) + (String.format(
            Locale.ENGLISH,
            "%.2f",
            body.totalCheckout
        ))
        textView20.text = "Coupon " + etCouponCode.text.toString().trim()
    }

    override fun removeCouponSuccess(body: DataRemoveCoupon?) {
        ivNext.tag = "next"
        ivNext.setImageResource(R.drawable.ic_down_arrow)
        etCouponCode.setText("")
        txtCouponAmount.visibility = View.GONE
        textView20.visibility = View.GONE
        txtTotalAmount.text = getString(R.string.currency_sign) + String.format(
            Locale.ENGLISH,
            "%.2f",
            body?.totalCheckout
        )
    }

    override fun getH2DFeeSucess(h2dFee: H2dFeeResponse?) {
        when (orderListingDetail.itemSize) {

            ItemSize.LARGE -> ourFeePercent = h2dFee?.largeItemOurFee ?: 0.0
            ItemSize.MEDIUM -> ourFeePercent = h2dFee?.mediumItemOurFee ?: 0.0
            ItemSize.POCKET -> ourFeePercent = h2dFee?.pocketItemOurFee ?: 0.0
            ItemSize.SMALL -> ourFeePercent = h2dFee?.smallItemOurFee ?: 0.0

        }
        setView(orderListingDetail)
    }



    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        dialogIndeterminate.dismiss()
        llContainer_cardCheckout.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        dialogIndeterminate.dismiss()
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
            llContainer_cardCheckout.showSnack(errorBody ?: "")
        }
    }

    override fun validationsFailure(type: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteCard(pos: Int, cardId: String) {
        selectedPos = pos
        delete(cardId)
    }

    fun delete(cardId: String) {
        if (CheckNetworkConnection.isOnline(this)) {
            presenter.deleteCard(getAuthAccessToken(this), cardId)
        }
    }
}