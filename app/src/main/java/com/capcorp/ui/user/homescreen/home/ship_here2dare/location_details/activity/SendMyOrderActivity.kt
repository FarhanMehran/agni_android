package com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.fragment.review.ReviewContract
import com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.fragment.review.ReviewPresenter
import com.capcorp.utils.*
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.orders.OrderListing
import com.capcorp.webservice.models.request_model.ShipDataRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_send_my_order.*
import kotlinx.android.synthetic.main.activity_send_my_order.ivImage
import kotlinx.android.synthetic.main.activity_send_my_order.tvBack
import kotlinx.android.synthetic.main.activity_send_my_order.tvDestinationValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvH2DFeeValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvItemDesc
import kotlinx.android.synthetic.main.activity_send_my_order.tvItemName
import kotlinx.android.synthetic.main.activity_send_my_order.tvItemPrice
import kotlinx.android.synthetic.main.activity_send_my_order.tvItemPriceValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvOriginalPackValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvProcessingFeeValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvQuantityValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvRequiredByDate
import kotlinx.android.synthetic.main.activity_send_my_order.tvSizeValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvSourceValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvTax
import kotlinx.android.synthetic.main.activity_send_my_order.tvTaxValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvTotalValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvTravelRewardsValue
import kotlinx.android.synthetic.main.activity_send_my_order.tvWhereToGetValue
import java.net.URL
import java.util.*


class SendMyOrderActivity : BaseActivity(), ReviewContract.View,
        View.OnClickListener {
    var orderListingDetail: OrderListing? = null
    private lateinit var shipData: ShipDataRequest
    private val presenter = ReviewPresenter()

    private var ourFeePercent: Double = 0.0
    private var stripeFee: Double = 0.0
    private var OurFeeFinal: Double = 0.0
    private var total: Double = 0.0
    private var grossTotal: Double = 0.0

    private lateinit var dialogIndeterminate: DialogIndeterminate


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_my_order)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)

        shipData = intent.getSerializableExtra("data") as ShipDataRequest
        presenter.getH2dFee(getAuthAccessToken(this))

        clickListener()

    }

    private fun setView(shipData: ShipDataRequest) {
        tvItemName.text = shipData.itemName
        tvItemDesc.text = shipData.description
        tvSizeValue.text = shipData.itemSize
        tvTypeValue.text = shipData.type
        tvSourceValue.text = shipData.pickUpCountry
        tvDestinationValue.text = shipData.dropDownCountry
        tvRequiredByDate.text = getOnlyDate(shipData.pickUpDate.toLong(), "EEE · MMM d")
        tvQuantityValue.text = shipData.itemQuantity
        if (shipData.originalPacking.equals("true")) {
            tvOriginalPackValue.text = getString(R.string.yes)
        } else {
            tvOriginalPackValue.text = getString(R.string.no)
        }
        val itemPrice = shipData.itemPrice.toDouble() * shipData.itemQuantity.toInt()
        if (shipData.shipItemImages.size > 0)
            Glide.with(this).load(shipData.shipItemImages[0].localUri)
                    .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                    .into(ivImage)

        tvItemPriceValue.text = getString(R.string.currency_sign) + " " + String.format(
                Locale.ENGLISH,
                "%.2f",
                shipData.itemPrice.toDouble() * shipData.itemQuantity.toDouble()
        ) + " USD"

        if (shipData.itemUrl.isBlank()) {
            tvWhereToGetValue.text = "N/A"
        } else {
            shipData.itemUrl.let {
                if (it.startsWith("http")) {
                    val url = URL(it)
                    tvWhereToGetValue.text = url.host
                } else {
                    tvWhereToGetValue.text = it
                }
            }
        }

        val taxAmount: Double = if (shipData.tax.isEmpty()) {
            0.0
        } else {
            shipData.tax.toDouble()
        }
        var reward = shipData.payment.toDouble()
        if (shipData.itemSize == ItemSize.POCKET) {
            if (reward > 50) {
                reward = 50.0
            }
        } else if (shipData.itemSize == ItemSize.LARGE) {
            if (reward > 150) {
                reward = 150.0
            }
        } else if (shipData.itemSize == ItemSize.MEDIUM) {
            if (reward > 120) {
                reward = 120.0
            }
        } else if (shipData.itemSize == ItemSize.SMALL) {
            if (reward > 85) {
                reward = 85.0
            }
        }

        var adminFee: Double = 0.0
        val itemPriceValue = itemPrice
        var ourFee = (itemPriceValue * ourFeePercent) / 100
        if (ourFee < 3.0) {
            ourFee = 3.0
        } else if (ourFee > 50.0) {
            ourFee = 50.0
        }

        var stripeFee = 0.0
        if (shipData.type == OrderType.SHOP) {
            stripeFee = ((((ourFee + itemPriceValue + reward + taxAmount) * 2.9) / 100) + 0.3)
        } else {
            stripeFee = ((((ourFee + reward) * 2.9) / 100) + 0.3)
        }

        adminFee = ourFee + stripeFee

        tvProcessingFeeValue.text = getString(R.string.currency_sign) + " " + String.format(
                Locale.ENGLISH,
                "%.2f",
                stripeFee
        ) + " USD"

        tvTravelRewardsValue.text =
                getString(R.string.currency_sign) + " " + String.format("%.2f", reward) + " USD"
        shipData.stripeFee = stripeFee
        shipData.h2dFee = ourFeePercent
        shipData.adminFee = adminFee

        tvH2DFeeValue.text = String.format("%.2f", ourFee) + " USD"

        total =
                ((shipData.itemPrice.toDouble()) * (shipData.itemQuantity.toDouble()))
        if (shipData.type.equals(OrderType.SHOP)) {
            grossTotal = adminFee + taxAmount + total + reward
        } else {
            grossTotal = adminFee + total + reward

        }
        shipData.itemGrossTotal = grossTotal
        if (shipData.type.equals(OrderType.SHOP)) {
            tvTotalValue.text = """${getString(R.string.currency_sign)} ${
                String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        grossTotal
                ) + " USD"
            }"""
        } else {
            tvTotalValue.text = """${getString(R.string.currency_sign)} ${
                String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        (grossTotal - shipData.itemPrice.toDouble())
                ) + " USD"
            }"""
        }
        if (shipData.tax.isEmpty()) {
            tvTaxValue.text = getString(R.string.currency_sign) + " " + "0.00 USD"
            shipData.tax = "0";
            shipData.taxInclude = "false"
            tvTax.setTextColor(resources.getColor(R.color.grey_error))
            tvTaxValue.setTextColor(resources.getColor(R.color.grey_error))
        } else {
            tvTaxValue.text = getString(R.string.currency_sign) + " " + String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    shipData.tax.toDouble()
            ) + " USD"
        }

        if (shipData.type == OrderType.DELIVERY || shipData.type == OrderType.PICKUP) {
            tvTax.setTextColor(resources.getColor(R.color.grey_error))
            tvTaxValue.setTextColor(resources.getColor(R.color.grey_error))
            tvItemPrice.setTextColor(resources.getColor(R.color.grey_error))
            tvItemPriceValue.setTextColor(resources.getColor(R.color.grey_error))
        }

    }


    private fun clickListener() {
        tvBack.setOnClickListener(this)
        btnSendMyOrder.setOnClickListener(this)
        tvWhereToGetValue.setOnClickListener(this)
        tvItemPrice.setOnClickListener(this)
        tvTax.setOnClickListener(this)
        tvProcessingFee.setOnClickListener(this)
        tvTravelRewards.setOnClickListener(this)
        tvH2DFee.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBack -> onBackPressed()
            R.id.tvWhereToGetValue -> {
                if (shipData.itemUrl.isNotBlank()) {
                    if(!shipData.itemUrl.startsWith("http")){
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"+shipData.itemUrl))
                        startActivity(browserIntent)
                    }else{
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(shipData.itemUrl))
                        startActivity(browserIntent)
                    }
                }
            }

            R.id.btnSendMyOrder -> sendOrder()
            R.id.tvItemPrice -> openBottomSheetDialog("ItemPrice")
            R.id.tvTax -> openBottomSheetDialog("Tax")
            R.id.tvProcessingFee -> openBottomSheetDialog("ProcessingFee")
            R.id.tvTravelRewards -> openBottomSheetDialog("TravelReward")
            R.id.tvH2DFee -> openBottomSheetDialog("H2DFee")
        }
    }

    private fun openBottomSheetDialog(type: String) {
        val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = layoutInflater.inflate(R.layout.dialog_info, null)
        val clItemPrice = view.findViewById<ConstraintLayout>(R.id.clPrice)
        val clTax = view.findViewById<ConstraintLayout>(R.id.clTax)
        val clH2dFee = view.findViewById<ConstraintLayout>(R.id.clH2dFee)
        val clProcessingFee = view.findViewById<ConstraintLayout>(R.id.clProcessingFee)
        val clTravelRewards = view.findViewById<ConstraintLayout>(R.id.clTravelRewards)
        if (type.equals("ItemPrice")) {
            clItemPrice.visibility = View.VISIBLE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
        } else if (type.equals("Tax")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.VISIBLE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
        } else if (type.equals("ProcessingFee")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.VISIBLE
            clTravelRewards.visibility = View.GONE
        } else if (type.equals("TravelReward")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.VISIBLE
        } else if (type.equals("H2DFee")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.VISIBLE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun sendOrder() {
        if (CheckNetworkConnection.isOnline(this)) {
            shipData.h2dFee = OurFeeFinal
            shipData.stripeFee = stripeFee
            presenter.shipRequestCall(getAuthAccessToken(this), shipData)
        }
    }


    override fun onBackPressed() {
        finish()
    }

    override fun apiSuccess() {
        startActivity(Intent(this, OrderSuccessActivity::class.java).putExtra("from", "order"))
    }

    override fun getH2DFeeSucess(h2dFee: H2dFeeResponse?) {
        when (shipData.itemSize) {

            ItemSize.LARGE -> ourFeePercent = h2dFee?.largeItemOurFee ?: 0.0
            ItemSize.MEDIUM -> ourFeePercent = h2dFee?.mediumItemOurFee ?: 0.0
            ItemSize.POCKET -> ourFeePercent = h2dFee?.pocketItemOurFee ?: 0.0
            ItemSize.SMALL -> ourFeePercent = h2dFee?.smallItemOurFee ?: 0.0

        }
        setView(shipData)
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        btnSendMyOrder.showSWWerror()

    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        if (code == 401) {
            val dialog = Dialog(this, R.style.DialogStyle)
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
            btnSendMyOrder.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {

    }
}