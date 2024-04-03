package com.capcorp.ui.driver.homescreen.home.makeoffer.request_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.home.grocery_request_detail.RequestContract
import com.capcorp.ui.driver.homescreen.home.grocery_request_detail.RequestPresenter
import com.capcorp.ui.driver.homescreen.home.makeoffer.MakeAnOfferActivity
import com.capcorp.ui.payment.card_list.CardListActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.chat.chatmessage.ChatActivity
import com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity.OrderSuccessActivity
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestImageAdapter
import com.capcorp.utils.*
import com.capcorp.utils.location.LocationProvider
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_detail_driver.*
import kotlinx.android.synthetic.main.activity_request_detail_driver.cvImage
import kotlinx.android.synthetic.main.activity_request_detail_driver.ivImage
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvBack
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvDestinationValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvItemDesc
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvItemName
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvItemPrice
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvItemPriceValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvOriginalPackValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvQuantityValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvRequiredByDate
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvSizeValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvSourceValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvTax
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvTaxValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvTravelRewards
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvTravelRewardsValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvWhereToGetValue
import kotlinx.android.synthetic.main.item_requests.view.*
import java.net.URL
import java.util.*

class RequestDetailDriver : BaseActivity(), RequestImageAdapter.ProfileImagesAdapterListener,
    View.OnClickListener, RequestContract.View {

    private var orderListingDetail: OrderListing? = null
    private var presenter = RequestPresenter()
    private var isAccepted = false

    //private lateinit var locationProvide: LocationProvider
    private var sourceLatLong: LatLng? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var cardId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_detail_driver)
        presenter.attachView(this)
        orderListingDetail =
            Gson().fromJson(intent.getStringExtra(REQUEST_DETAIL), OrderListing::class.java)


        if (intent.getBooleanExtra(TYPE_OFFER_MADE, false)) {
            btnMakeAnOffer.visibility = View.GONE
        } else {

            val isDriverCancel = intent.getStringExtra(ISDRIVERCANCEL)?.toBoolean()
            if (isDriverCancel != null && isDriverCancel)
                btnMakeAnOffer.visibility = View.GONE
            else
                btnMakeAnOffer.visibility = View.VISIBLE

        }
//        setRecyclerAdapter()
        setView(orderListingDetail)
        clickListener()
        /* locationProvide = LocationProvider.CurrentLocationBuilder(this).build()
         locationProvide.getLastKnownLocation(OnSuccessListener {
             if (it != null) {

                 sourceLatLong = LatLng(it.latitude, it.longitude)
                 latitude = sourceLatLong!!.latitude
                 longitude = sourceLatLong!!.longitude
             }


         })*/
    }

//    private fun setRecyclerAdapter() {
//        val layoutManager = LinearLayoutManager(this)
//        rvOffers.layoutManager = layoutManager
//        requestOfferDriverAdapter = RequestOfferDriverAdapter(rvOffers.context, this, offerList)
//        rvOffers.adapter = requestOfferDriverAdapter
//    }

    private fun clickListener() {
        tvBack.setOnClickListener(this)
        btnMakeAnOffer.setOnClickListener(this)
        //tvAcceptOffer.setOnClickListener(this)
        cvImage.setOnClickListener(this)

        tvItemPrice.setOnClickListener(this)
        tvTax.setOnClickListener(this)
        tvTotalInvestment.setOnClickListener(this)
        tvTravelRewards.setOnClickListener(this)
        tvReimbursement.setOnClickListener(this)
        btnChat.setOnClickListener(this)
        tvH2DFee.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBack -> onBackPressed()
            R.id.btnMakeAnOffer -> {
               /* if (orderListingDetail?.isDriverApproved == 1) {*/
                    if (!orderListingDetail?.stripeConnectId.isNullOrEmpty()) {
                        val intent = Intent(this, MakeAnOfferActivity::class.java)
                        intent.putExtra(ORDER_ID, orderListingDetail?._id)
                        intent.putExtra("itemPrice", orderListingDetail?.recommendedReward)
                        intent.putExtra(POSITION, getIntent().getStringExtra(POSITION))
                        intent.putExtra(ORDER_DETAIL, true)
                        intent.putExtra("date", orderListingDetail?.pickUpDate)
                        intent.putExtra("cardId", "")
                        startActivity(intent)
                    } else {
                        val dialog = Dialog(this, R.style.DialogStyle)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(true)
                        dialog.setContentView(R.layout.alert_dialog)
                        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                        val dialogButton: TextView =
                            dialog.findViewById(R.id.btnContinue) as TextView
                        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                        tvTitle.text = getString(R.string.oops)
                        tvDescription.text = getString(R.string.stripe_error)
                        dialogButton.text = getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                /*} else {
                    val dialog = Dialog(this, R.style.DialogStyle)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.alert_dialog)
                    val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                    val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                    val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                    tvTitle.text = getString(R.string.oops)
                    tvDescription.text = getString(R.string.verify_error)
                    dialogButton.text = getString(R.string.ok)
                    dialogButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }*/
            }
            R.id.cvImage -> {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(USER_ID, orderListingDetail?.userId?._id)
                intent.putExtra(USER_NAME, orderListingDetail?.userId?.fullName)
                intent.putExtra(
                    PROFILE_PIC_URL,
                    orderListingDetail?.userId?.profilePicURL?.original
                )
                startActivity(intent)
            }
            R.id.tvItemPrice -> openBottomSheetDialog("ItemPrice")
            R.id.tvTax -> openBottomSheetDialog("Tax")
            R.id.tvTotalInvestment -> openBottomSheetDialog("Investment")
            R.id.tvTravelRewards -> openBottomSheetDialog("Reward")
            R.id.tvReimbursement -> openBottomSheetDialog("Reimbursement")
            R.id.tvH2DFee -> openBottomSheetDialog("H2DFee")
            R.id.btnChat -> {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(USER_ID, orderListingDetail?.userId?._id)
                intent.putExtra(USER_NAME, orderListingDetail?.userId?.fullName)
                intent.putExtra(
                    PROFILE_PIC_URL,
                    orderListingDetail?.userId?.profilePicURL?.original
                )
                startActivity(intent)
            }
        }
    }

    private fun openBottomSheetDialog(type: String) {
        val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = layoutInflater.inflate(R.layout.dialog_info, null)
        val clItemPrice = view.findViewById<ConstraintLayout>(R.id.clPrice)
        val clItemPriceTraveler = view.findViewById<ConstraintLayout>(R.id.clPriceTraveler)
        val clTax = view.findViewById<ConstraintLayout>(R.id.clTax)
        val clH2dFee = view.findViewById<ConstraintLayout>(R.id.clH2dFee)
        val clProcessingFee = view.findViewById<ConstraintLayout>(R.id.clProcessingFee)
        val clTravelRewards = view.findViewById<ConstraintLayout>(R.id.clTravelRewards)
        val clYourReward = view.findViewById<ConstraintLayout>(R.id.clYourReward)
        val clTotalInvestment = view.findViewById<ConstraintLayout>(R.id.clTotalInvestment)
        val clReimbursement = view.findViewById<ConstraintLayout>(R.id.clReimbursement)
        if (type.equals("ItemPrice")) {
            clItemPriceTraveler.visibility = View.VISIBLE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.GONE
        } else if (type.equals("Tax")) {
            clItemPriceTraveler.visibility = View.GONE
            clTax.visibility = View.VISIBLE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.GONE
        } else if (type.equals("Reward")) {
            clItemPriceTraveler.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.VISIBLE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.GONE
        } else if (type.equals("Reimbursement")) {
            clItemPriceTraveler.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.VISIBLE
        } else if (type.equals("Investment")) {
            clItemPriceTraveler.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.VISIBLE
            clReimbursement.visibility = View.GONE
        }else if (type.equals("H2DFee")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.VISIBLE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.GONE
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun acceptConfirmation() {
        AlertDialogUtil.getInstance()
            .createOkCancelDialog(this, R.string.confirm, R.string.accept_offer_confirmation,
                R.string.yes, R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                    override fun onOkButtonClicked() {

                    }

                    override fun onCancelButtonClicked() {
                        // dismiss
                    }

                }).show()
    }

    private fun acceptOfferConfirm() {


        val alertLayout = layoutInflater.inflate(R.layout.layout_accept_offer, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(alertLayout)
        val mEdtDeliveryCharges = alertLayout.findViewById(R.id.etDescription) as TextInputEditText
        val tvCancel = alertLayout.findViewById(R.id.tv_cancel) as TextView
        val tvSubmit = alertLayout.findViewById(R.id.tv_submit) as TextView

        mEdtDeliveryCharges.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                val str = s.toString()
                if (str.length == 1 && str.startsWith(".")) {
                    mEdtDeliveryCharges.setText("")
                    mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                } else if (str.length == 1 && str.startsWith("0")) {
                    mEdtDeliveryCharges.setText("")
                    mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                }
            }
        })

        tvSubmit.setOnClickListener {
            if (mEdtDeliveryCharges.text.toString().isEmpty()) {
                presenter.acceptApi(
                    getAuthAccessToken(this@RequestDetailDriver),
                    orderListingDetail?._id.toString(),
                    orderListingDetail?.payment.toString(),
                    DriverAction.ACCEPT,
                    latitude,
                    longitude,
                    orderListingDetail?.pickUpDate.toString(),
                    "",
                    0.0
                )
            } else {
                var shipping: Double = 0.0
                shipping = mEdtDeliveryCharges.text.toString().toDouble()
                presenter.acceptApi(
                    getAuthAccessToken(this@RequestDetailDriver),
                    orderListingDetail?._id.toString(),
                    orderListingDetail?.payment.toString(),
                    DriverAction.ACCEPT,
                    latitude,
                    longitude,
                    orderListingDetail?.pickUpDate.toString(),
                    "",
                    shipping
                )
            }
            dialog.dismiss()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun acceptConfirm(cardId: String) {
        val alertLayout = layoutInflater.inflate(R.layout.layout_accept_offer, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(alertLayout)
        val mEdtDeliveryCharges = alertLayout.findViewById(R.id.etDescription) as TextInputEditText
        val tvCancel = alertLayout.findViewById(R.id.tv_cancel) as TextView
        val tvSubmit = alertLayout.findViewById(R.id.tv_submit) as TextView

        mEdtDeliveryCharges.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                val str = s.toString()
                if (str.length == 1 && str.startsWith(".")) {
                    mEdtDeliveryCharges.setText("")
                    mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                } else if (str.length == 1 && str.startsWith("0")) {
                    mEdtDeliveryCharges.setText("")
                    mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                }
            }
        })

        tvSubmit.setOnClickListener {
            if (mEdtDeliveryCharges.text.toString().isEmpty()) {
                presenter.acceptApi(
                    getAuthAccessToken(this@RequestDetailDriver),
                    orderListingDetail?._id.toString(),
                    orderListingDetail?.payment.toString(),
                    DriverAction.ACCEPT,
                    latitude,
                    longitude,
                    orderListingDetail?.pickUpDate.toString(),
                    cardId,
                    0.0
                )
            } else {
                var shipping: Double = 0.0
                shipping = mEdtDeliveryCharges.text.toString().toDouble()
                presenter.acceptApi(
                    getAuthAccessToken(this@RequestDetailDriver),
                    orderListingDetail?._id.toString(),
                    orderListingDetail?.payment.toString(),
                    DriverAction.ACCEPT,
                    latitude,
                    longitude,
                    orderListingDetail?.pickUpDate.toString(),
                    cardId,
                    shipping
                )
            }
            dialog.dismiss()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(orderList: OrderListing?) {
        tvOffersCount.visibility = View.GONE
        rvOffers.visibility = View.GONE

        tvSourceValue.text = orderList?.pickUpAddress
        tvDestinationValue.text = orderList?.dropDownAddress
        // tvItemPrice.text = """${getString(R.string.currency_sign)} ${orderList?.payment}"""
        tvRequiredByDate.text = orderList?.pickUpDate?.let { getOnlyDate(it, "EEE · MMM d") }
        tvItemName.text = orderList?.itemName
        tvItemDesc.text = orderList?.description
        when (orderList?.itemSize) {
            ItemSize.POCKET -> {
                tvSizeValue.text = getString(R.string.mini)
            }

            ItemSize.SMALL -> {
                tvSizeValue.text = getString(R.string.small)
            }

            ItemSize.MEDIUM -> {
                tvSizeValue.text = getString(R.string.medium)
            }

            ItemSize.LARGE -> {
                tvSizeValue.text = getString(R.string.large)
            }
        }
        if (orderList?.originalPacking.equals("0")) {
            tvOriginalPackValue.text = getString(R.string.no)
        } else {
            tvOriginalPackValue.text = getString(R.string.yes)
        }

        when (orderList?.orderType) {

            RequestType.TRANSPORT -> {
                Glide.with(this).load(orderList.itemImages?.get(0)?.original).into(ivImage)
                tvItemName.text = orderList.type
            }
            RequestType.PARCEL -> {
                if ((orderList.outerParcelImagesURL?.size ?: 0) > 0) {
                    Glide.with(this).load(orderList.outerParcelImagesURL?.get(0)?.original)
                        .into(ivImage)
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
                    Glide.with(this).load(orderList.shipItemImages?.get(0)?.original).into(ivImage)
                }
                tvItemName.text = orderList.itemName
                val price = orderList.itemPrice.toDouble() * orderList.itemQuantity.toInt()

                tvItemPriceValue.text =
                    getString(R.string.currency_sign) + " " + String.format("%.2f", price) + " USD"
                if (orderList.tax == null) {
                    tvTaxValue.text = getString(R.string.currency_sign) + " " + "0" + " USD"
                    tvTaxValue.setTextColor(resources.getColor(R.color.grey_error))
                    tvTax.setTextColor(resources.getColor(R.color.grey_error))
                } else {
                    tvTaxValue.text = getString(R.string.currency_sign) + " " + String.format(
                        "%.2f",
                        orderList.tax.toDouble()
                    ) + " USD"
                }
                tvQuantityValue.text = orderList.itemQuantity

                if (orderList.itemUrl.isNullOrBlank()) {
                    tvWhereToGetValue.text = "N/A"
                } else {
                    orderList.itemUrl?.let {
                        if (it.startsWith("http")) {
                            val url = URL(it)
                            tvWhereToGetValue.text = url.host
                        } else {
                            tvWhereToGetValue.text = it
                        }
                    }

                }

                tvWhereToGetValue.setOnClickListener {
                    if (!orderListingDetail?.itemUrl.isNullOrBlank()) {
                        if (!orderListingDetail?.itemUrl?.startsWith("http")!!) {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://" + orderListingDetail?.itemUrl)
                            )
                            startActivity(browserIntent)
                        } else {
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(orderListingDetail?.itemUrl))
                            startActivity(browserIntent)
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
                        ))
                        tvTravelRewardsValue.text =
                            """${getString(R.string.currency_sign)} $p""" + " USD"
                    } else {
                        p = (String.format(Locale.ENGLISH, "%.2f", orderList.payment))
                        tvTravelRewardsValue.text =
                            """${getString(R.string.currency_sign)} $p""" + " USD"
                    }
                } else {
                    tvTravelRewardsValue.text =
                        getString(R.string.currency_sign) + " " + String.format(
                            "%.2f",
                            orderList.accepted?.totalPrice?.toDouble()
                        ) + " USD"
                }
                tvTotalInvestmentValue.text = "$" + String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    price + orderList.tax.toDouble()
                ) + " USD"

                if (orderList.orderStatus == OrderStatus.REQUESTED) {
                    tvTravelRewardsValue.text =
                        getString(R.string.currency_sign) + " " + String.format(
                            "%.2f",
                            orderList.payment
                        ) + " USD"
                    tvH2DFeeValue.text = "$" + String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        ((orderList.payment * 1) / 100)
                    ) + " USD"
                    tvReimbursementValue.text = "$" + String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        ((price + orderList.tax.toDouble() + orderList.payment) - (orderList.payment * 1) / 100)
                    ) + " USD"
                } else {
                    tvTravelRewardsValue.text =
                        getString(R.string.currency_sign) + String.format(
                            "%.2f",
                            orderList.accepted?.totalPrice
                        ) + " USD"
                    tvH2DFeeValue.text = "$" + String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        ((orderList.payment * 1) / 100)
                    ) + " USD"
                    orderList.accepted?.totalPrice?.let {
                        tvReimbursementValue.text = "$" + String.format(
                            Locale.ENGLISH,
                            "%.2f",
                            (price + orderList.tax.toDouble() + it.toDouble() - (orderList.payment * 1) / 100)
                        ) + " USD"
                    }

                }


                if (!orderList.orderStatus.equals(OrderStatus.REQUESTED)) {
                    if (SharedPrefs.with(this).getString(USER_TYPE, "").equals(UserType.DRIVER)) {
                        if (orderList.couponUse == true) {
                            tvReimbursementValue.text = "$" + String.format(
                                Locale.ENGLISH,
                                "%.2f",
                                (price + orderList.tax.toDouble() + orderList.recommendedReward.toDouble()) - orderList.discount
                            ) + " USD"
                        }
                    }
                }
            }
        }
        /* if (orderList?.type.equals(OrderType.DELIVERY)) {
             rlRelativeShip.visibility = View.GONE
         } else {
             rlRelativeShip.visibility = View.VISIBLE
         }*/

        // tvDescription.text = orderList?.description
        //tvAcceptOffer.text = getString(R.string.accept_offer) + " " + getString(R.string.currency_sign) + orderList?.payment
//        if (orderList?.offers?.size ?: 0 > 0) {
//            rvOffers.visibility = View.VISIBLE
//            //tvNoOffer.visibility = View.GONE
//            ///tvNoOfferLabel.visibility = View.GONE
//            tvOfferCount.text = getString(R.string.offers) + " · " + orderList?.offers?.size.toString()
//        } else {
//            tvOfferCount.text = getString(R.string.offers) + " · " + "0"
//            //tvNoOffer.visibility = View.VISIBLE
//            //tvNoOfferLabel.visibility = View.VISIBLE
//            rvOffers.visibility = View.GONE
//        }


//        val spannableStrBuilder = SpannableStringBuilder()
//        val spannableString = SpannableString(orderList?.userId?.firstName)
//        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannableStrBuilder.append(spannableString).append(" ")
//                .append(getString(R.string.requestd_by_two))
//        tvUserName.setText(spannableStrBuilder, TextView.BufferType.SPANNABLE)
//        tvTime.text = orderList?.createdDate?.let { DateUtils.getRelativeTimeSpanString(it) }
//        ivUserImage.setOnClickListener {
//            startActivity(Intent(this, OtherUserProfileActivity::class.java)
//                    .putExtra(USER_ID, orderListingDetail?.userId?._id)
//                    .putExtra(USERTYPE, UserType.USER))
//        }
//        Glide.with(this).load(orderList?.userId?.profilePicURL?.original)
//                .apply(RequestOptions().circleCrop())
//                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder)).into(ivUserImage)

        /* Glide.with(this).load(R.drawable.user_image)
                 .apply(RequestOptions.circleCropTransform()).into(ivUserImage)*/

        // mImagesAdapter = RequestImageAdapter(this, this)
        /* val imageListSize:Int
         imageListSize= orderList?.shipItemImages?.size!!
         if (imageListSize==1){
             circleIndicator.visibility=View.GONE
         }else if (imageListSize>1){
             circleIndicator.visibility=View.VISIBLE
         }else{
             circleIndicator.visibility=View.GONE
         }*/
//        val layoutParams = RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//        layoutParams.height = resources.displayMetrics.widthPixels
        //  viewPager.layoutParams = layoutParams

        //  viewPager.adapter = mImagesAdapter
        // viewPager.offscreenPageLimit = 3
//        if (orderList?.shipItemImages?.size != 1) {
//            circleIndicator.setViewPager(viewPager)
//            mImagesAdapter.registerDataSetObserver(circleIndicator.dataSetObserver)
//        }
//        if (orderList?.offers?.size ?: 0 > 0) {
//            if (offerList.size > 0)
//                offerList.clear()
//            orderList?.offers?.let { offerList.addAll(it) }
//            requestOfferDriverAdapter.notifyDataSetChanged()
//        }

//        when (orderList?.orderType) {
//            RequestType.TRANSPORT -> {
//                tvDimen.visibility = View.GONE
//                tvWeight.visibility = View.GONE
//                tvDimenValue.visibility = View.GONE
//                tvWeightvalue.visibility = View.GONE
//                tvOderType.text = getString(R.string.transport)
//
//                tvItemName.text = orderList.itemName
//                orderList.itemImages?.let { mImagesAdapter.addProfileImages(it) }
//            }
//            RequestType.PARCEL -> {
//                tvDimen.visibility = View.VISIBLE
//                tvWeight.visibility = View.VISIBLE
//                tvDimenValue.visibility = View.VISIBLE
//                tvWeightvalue.visibility = View.VISIBLE
//                tvWeightvalue.text = orderList.dimensionArray?.get(0)?.weight + " g"
//                tvDimenValue.text = orderList.dimensionArray?.get(0)?.length + " cm" + " * " +
//                        orderList.dimensionArray?.get(0)?.height + " cm" + " * " +
//                        orderList.dimensionArray?.get(0)?.width + " cm "
//                tvOderType.text = getString(R.string.parcel)
//
//                tvItemName.text = """${orderList.dimensionArray?.get(0)?.weight} kg · (${orderList.dimensionArray?.get(0)?.length} * ${orderList.dimensionArray?.get(0)?.width} * ${orderList.dimensionArray?.get(0)?.height}) cm"""
//                orderList.outerParcelImagesURL?.let { mImagesAdapter.addProfileImages(it) }
//            }
//
//            RequestType.GROCERIES -> {
//                tvDimen.visibility = View.GONE
//                tvWeight.visibility = View.GONE
//                tvDimenValue.visibility = View.GONE
//                tvWeightvalue.visibility = View.GONE
//                tvOderType.text = getString(R.string.groceries)
//                tvItemName.text = "${orderList.groceryItems.size} ${getString(R.string.items)}"
//            }
//            RequestType.SHIPMENT -> {
//                tvDimen.visibility = View.GONE
//                tvWeight.visibility = View.GONE
//                tvDimenValue.visibility = View.GONE
//                tvWeightvalue.visibility = View.GONE
//                if (orderList.type.equals(OrderType.SHOP)) {
//                    textView7.text = getString(R.string.item_price)
//                    tvTaxCharge.visibility = View.VISIBLE
//                    tvTax.visibility = View.VISIBLE
//                } else {
//                    textView7.text = getString(R.string.estimated_value)
//                    tvTaxCharge.visibility = View.GONE
//                    tvTax.visibility = View.GONE
//                }
//                tvItemPrice.text = """${getString(R.string.currency_sign)} ${String.format(Locale.ENGLISH, "%.1f", orderList.itemPrice?.toDouble())}"""
//                tvItemQuantity.text = orderList.itemQuantity
//                if (orderList.itemUrl.isNullOrBlank())
//                    tvItemWebUrl.text = "N/A"
//                else
//                    tvItemWebUrl.text = orderList.itemUrl
//                if (orderList.tax == null) {
//                    tvTaxCharge.text = getString(R.string.currency_sign) + " " + "0"
//                } else {
//                    tvTaxCharge.text = getString(R.string.currency_sign) + " " + String.format(Locale.ENGLISH, "%.1f", orderList.tax?.toDouble())
//                }
//                tvOderType.text = getString(R.string.ship)
//                tvItemName.text = orderList.itemName
//                orderList.shipItemImages?.let { mImagesAdapter.addProfileImages(it) }
//            }
//        }

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

    }


    override fun onBackPressed() {
        val intent = Intent()
        if (isAccepted) {
            intent.putExtra(POSITION, getIntent().getIntExtra(POSITION, 0))
            setResult(Activity.RESULT_OK, intent)
        } else {
            setResult(Activity.RESULT_CANCELED, intent)
        }
        finish()
    }

    override fun acceptSuccess() {
        startActivity(Intent(this, OrderSuccessActivity::class.java).putExtra("from", "offer"))
    }

    override fun onImageClicked(imagePath: String?) {

    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
//        tvDescription.showSWWerror()
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
//            tvDescription.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            isAccepted = true
            onBackPressed()
        }
        if (requestCode == 402 && resultCode == Activity.RESULT_OK && data != null) {
            if (CheckNetworkConnection.isOnline(this)) {
                cardId = data.getStringExtra("cardId").toString()
                startActivity(
                    Intent(this, MakeAnOfferActivity::class.java)
                        .putExtra(ORDER_ID, orderListingDetail?._id)
                        .putExtra("itemPrice", orderListingDetail?.recommendedReward)
                        .putExtra("data", orderListingDetail)
                        .putExtra("date", orderListingDetail?.pickUpDate)
                        .putExtra("cardId", cardId)
                        .putExtra(POSITION, intent.getStringExtra(POSITION))
                )

            }
        }
        if (requestCode == 403 && resultCode == Activity.RESULT_OK && data != null) {
            if (CheckNetworkConnection.isOnline(this)) {
                cardId = data.getStringExtra("cardId").toString()
                if (CheckNetworkConnection.isOnline(this)) {
                    /*presenter.acceptApi(getAuthAccessToken(this@RequestDetailDriver),
                            orderListingDetail?._id.toString(), orderListingDetail?.payment.toString(),
                            DriverAction.ACCEPT, latitude, longitude, orderListingDetail?.pickUpDate.toString(), cardId, 0.0)*/
                    acceptConfirm(cardId)
                } else {
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}