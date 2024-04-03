package com.capcorp.ui.user.homescreen.orders.request_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.DataApplyCoupon
import com.capcorp.webservice.models.DataRemoveCoupon
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.Order
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_detail_new.*
import java.net.URL
import java.util.*


class RequestDetailActivity : BaseActivity(), RequestImageAdapter.ProfileImagesAdapterListener,
    View.OnClickListener, RequestDetailContract.View {
    var orderListingDetail: OrderListing? = null
    private var presenter = RequestDetailPresenter()
    private var orderId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_detail_new)
        presenter.attachView(this)


        if (intent.hasExtra(NOTIFICATION)) {
            orderId = intent.getStringExtra(ORDER_ID)
        } else {
            intent.getStringExtra(REQUEST_DETAIL)?.let { Log.d("requestDetail", it) }
            val data =
                Gson().fromJson(intent.getStringExtra(REQUEST_DETAIL), OrderListing::class.java)
            orderId = data?._id
        }
        orderId?.let { presenter.getOrderDetail(getAuthAccessToken(this), it) }
        clickListener()
    }


    private fun clickListener() {
        tvBack.setOnClickListener(this)
        tvItemPrice.setOnClickListener(this)
        tvTax.setOnClickListener(this)
        tvH2DFee.setOnClickListener(this)
        tvProcessingFee.setOnClickListener(this)
        tvTravelRewards.setOnClickListener(this)
        tvWhereToGetValue.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBack -> onBackPressed()
                R.id.tvWhereToGetValue -> {
                    if (!orderListingDetail?.itemUrl.isNullOrBlank()) {
                        if(!orderListingDetail?.itemUrl?.startsWith("http")!!){
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"+orderListingDetail?.itemUrl))
                            startActivity(browserIntent)
                        }else{
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(orderListingDetail?.itemUrl))
                            startActivity(browserIntent)
                        }
                    }
                }
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

    @SuppressLint("SetTextI18n")
    private fun setView(orderList: OrderListing?) {

        var orderLIst = Gson().toJson(orderList)

        tvSourceValue.text = orderList?.pickUpAddress
        tvDestinationValue.text = orderList?.dropDownAddress
        tvItemDesc.text = orderList?.description

        if (intent?.getStringExtra("from_request") == "true") {
            if (orderList?.isDriverCancel == "true") {
                tvItemPriceValue.text =
                    getString(R.string.currency_sign) + " " + String.format("%.2f",orderList.prevTotalPrice) + " USD"
            } else {
                tvItemPriceValue.text =
                    getString(R.string.currency_sign) + " " + String.format("%.2f",orderList?.payment) + " USD"
            }

        } else {
            tvItemPriceValue.text =
                getString(R.string.currency_sign) + " " + String.format("%.2f",orderList?.accepted?.totalPrice?.toDouble()) + " USD"
        }

        val userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)
        var userDataString = Gson().toJson(userData)

//        if (userData.type == UserType.USER) {
//        Log.d("requestDetailActivity", "${orderList?.totalCheckout}")
        tvTotal.text = getString(R.string.total)
//        } else {
//            tvTotal.text = getString(R.string.total_driver_header)
//            val reward = orderList?.accepted?.totalPrice?.toDouble()
//            tvTotalValue?.text = getString(R.string.currency_sign) + " " + String.format(Locale.ENGLISH, "%.1f", reward)
//        }

        if(orderList?.orderStatus == OrderStatus.ACCEPTED){
            tvRequiredByDate.text = orderList.accepted?.driverArrivalDate?.let { getOnlyDate(it, "EEE · MMM d") }
        }else if(orderList?.orderStatus == OrderStatus.COMPLETED){
            tvRequiredByDate.text = orderList.deliveredDate?.let { getOnlyDate(it, "EEE · MMM d") }
        }else if(orderList?.orderStatus == OrderStatus.REQUESTED){
            tvRequiredByDate.text = orderList.pickUpDate?.let { getOnlyDate(it, "EEE · MMM d") }
        }else if(orderList?.orderStatus == OrderStatus.EXPIRED){
            tvRequiredByDate.text = orderList.deliveredDate?.let { getOnlyDate(it, "EEE · MMM d") }
        }

        if(orderList?.couponUse == true){
            tvCouponValue.text =  "-" + getString(R.string.currency_sign) + String.format("%.2f",
                orderList.discount
            ) + " USD"
            tvCouponValue.visibility = View.VISIBLE
            tvCoupon.visibility = View.VISIBLE
            tvTotalValue?.text = getString(R.string.currency_sign) + " " + (String.format(
                Locale.ENGLISH,
                "%.2f",
                (orderList.totalCheckout?.minus(orderList.discount))
            )) + " USD"
        }else{
            tvCouponValue.visibility = View.GONE
            tvCoupon.visibility = View.GONE
            tvTotalValue?.text = getString(R.string.currency_sign) + " " + (String.format(
                Locale.ENGLISH,
                "%.2f",
                orderList?.totalCheckout
            )) + " USD"
        }

        when (orderList?.itemSize) {
            ItemSize.POCKET -> {
                tvSizeValue.text = "Mini"
            }

            ItemSize.SMALL -> {
                tvSizeValue.text = "Small"
            }

            ItemSize.MEDIUM -> {
                tvSizeValue.text = "Medium"
            }

            ItemSize.LARGE -> {
                tvSizeValue.text = "Large"
            }
        }

        if (orderList?.originalPacking == null || orderList.originalPacking == "0") {
            tvOriginalPackValue.text = "NO"
        } else {
            tvOriginalPackValue.text = "YES"
        }

        if (orderList?.type != OrderType.SHOP) {
            tvItemPrice.setTextColor(resources.getColor(R.color.grey_error))
            tvItemPriceValue.setTextColor(resources.getColor(R.color.grey_error))
            tvTax.setTextColor(resources.getColor(R.color.grey_error))
            tvTaxValue.setTextColor(resources.getColor(R.color.grey_error))
        }
        if (orderList?.taxInclude.equals("0")) {
            tvTax.setTextColor(resources.getColor(R.color.grey_error))
            tvTaxValue.setTextColor(resources.getColor(R.color.grey_error))
        }
        when (orderList?.orderType) {

            RequestType.TRANSPORT -> {
                Log.d(":::::Data",orderList.itemImages?.get(0)?.original+ "")
                Glide.with(this).load(orderList.itemImages?.get(0)?.original).into(ivImage)
                tvItemName.text = orderList.type
            }
            RequestType.PARCEL -> {
                if ((orderList.outerParcelImagesURL?.size ?: 0) > 0) {
                    Log.d(":::::Data",orderList.itemImages?.get(0)?.original+ "")
                    Glide.with(this).load(orderList.outerParcelImagesURL?.get(0)?.original)
                        .into(ivImage)
                }else{
                    Log.d(":::::Data",orderList.itemImages?.get(0)?.original+ "")
                }
                tvItemName.text = """${orderList.dimensionArray?.get(0)?.weight} kg · (${
                    orderList.dimensionArray?.get(0)?.length
                } * ${orderList.dimensionArray?.get(0)?.width} * ${orderList.dimensionArray?.get(0)?.height}) cm"""
            }

            RequestType.GROCERIES -> {
                tvItemName.text =
                    "${orderList.groceryItems.size} ${getString(R.string.items)} ${getString(R.string.from)} ${orderList.storeDetails.size} ${
                        getString(R.string.store)
                    }"
            }
            RequestType.SHIPMENT -> {
                if ((orderList.shipItemImages?.size ?: 0) > 0) {
                    Log.d(":::::Data",orderList.itemImages?.get(0)?.original+ "")
                    Glide.with(this).load(orderList.shipItemImages?.get(0)?.original).into(ivImage)
                }else{
                    Log.d(":::::Data",orderList.itemImages.toString()+ "")
                }
                tvItemName.text = orderList.itemName
                val price = orderList.itemPrice.toDouble() * orderList.itemQuantity.toInt()

                tvItemPriceValue.text = getString(R.string.currency_sign) + String.format("%.2f",price) + " USD"
                if (orderList.tax == null) {
                    tvTaxValue.text = getString(R.string.currency_sign) + " " + "0.00" + " USD"
                } else {
                    tvTaxValue.text = getString(R.string.currency_sign) + " " + String.format("%.2f",orderList.tax.toDouble()) + " USD"
                }
                tvQuantityValue.text = orderList.itemQuantity

                if (orderList.itemUrl.isNullOrBlank()) {
                    tvWhereToGetValue.text = "N/A"
                } else {
                    orderList.itemUrl?.let {
                        if(it.startsWith("http")){
                            val url = URL(it)
                            tvWhereToGetValue.text = url.host
                        } else{
                            tvWhereToGetValue.text = it
                        }
                    }

                }

                if (intent?.getStringExtra("from_request") == "true") {
                    var p = ""
                    if (orderList.isDriverCancel == "true") {
                        p = (String.format(
                            Locale.ENGLISH,
                            "%.2f",
                            orderList.prevTotalPrice.toDouble()
                        )) + " USD"
                        tvTravelRewardsValue.text = getString(R.string.currency_sign) + p
                    } else {
                        p = (String.format(Locale.ENGLISH, "%.2f", orderList.payment))+ " USD"
                        tvTravelRewardsValue.text = """${getString(R.string.currency_sign)} $p"""
                    }
                } else {
                    tvTravelRewardsValue.text =
                        getString(R.string.currency_sign) + " "+String.format(Locale.ENGLISH, "%.2f",orderList.accepted?.totalPrice?.toDouble()) + " USD"
                }
                val fee = (String.format(Locale.ENGLISH, "%.2f", orderList.h2dFee.toDouble()))
                tvH2DFeeValue.text = getString(R.string.currency_sign) + fee + " USD"
                tvProcessingFeeValue.text = getString(R.string.currency_sign) + (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    orderList.stripeFee?.toDouble()
                ))+ " USD"
            }
        }

    }

    override fun onBackPressed() {

        if (intent.getStringExtra("from_request") == "true") {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            if (intent.hasExtra(NOTIFICATION)) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                finish()
            }
        }
    }


    override fun onImageClicked(imagePath: String?) {

    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        root_details_request.showSWWerror()
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
            root_details_request.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun acceptRequestSuccess() {

    }

    override fun orderDetailSuccess(data: OrderListing) {
        orderId = data._id.toString()
        orderListingDetail = data
        setView(data)
    }

    override fun applyCouponSuccess(body: DataApplyCoupon) {

    }

    override fun removeCouponSuccess(body: DataRemoveCoupon?) {

    }

    override fun getH2DFeeSucess(h2dFee: H2dFeeResponse?) {

    }

}