package com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_made

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.capcorp.webservice.models.orders.Offer
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_detail_driver.*
import kotlinx.android.synthetic.main.activity_request_detail_driver.cvImage
import kotlinx.android.synthetic.main.activity_request_detail_driver.ivImage
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvBack
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvDestinationValue
import kotlinx.android.synthetic.main.activity_request_detail_driver.tvH2DFee
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
import java.net.URL
import java.util.*

class OfferMadeActivity : BaseActivity(), RequestImageAdapter.ProfileImagesAdapterListener,
    View.OnClickListener, RequestContract.View, RequestOfferForDriverAdapter.SelectedPos {

    var orderListingDetail: OrderListing? = null
    private lateinit var mImagesAdapter: RequestImageAdapter
    private lateinit var requestOfferAdapter: RequestOfferForDriverAdapter
    var offerList: ArrayList<Offer> = ArrayList()
    private var presenter = RequestPresenter()
    private var isAccepted = false
    private var price = 0.0
    private var additional = 0.0
    private var cardId: String = ""
    private var selectedPos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_detail_driver)
        presenter.attachView(this)
        orderListingDetail =
            Gson().fromJson(intent.getStringExtra(REQUEST_DETAIL), OrderListing::class.java)
        //tvMakeOffer.visibility = View.GONE
        //tvAcceptOffer.text = getString(R.string.change_my_offer)
        tvMakeAnOffer.text = getString(R.string.change_my_offer, String.format(
            Locale.ENGLISH,
            "%.2f",
            orderListingDetail?.myOffers?.price
        ))

        setRecyclerAdapter()
        setView(orderListingDetail)
        clickListener()
    }

    private fun setRecyclerAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvOffers.layoutManager = layoutManager
        requestOfferAdapter =
            RequestOfferForDriverAdapter(this, offerList, this, this@OfferMadeActivity)
        rvOffers.adapter = requestOfferAdapter
    }

    private fun clickListener() {
        tvBack.setOnClickListener(this)
        btnMakeAnOffer.setOnClickListener(this)
        cvImage.setOnClickListener(this)

        //tvAcceptOffer.setOnClickListener(this)
        //imageView.setOnClickListener(this)
        tvItemPrice.setOnClickListener(this)
        tvTax.setOnClickListener(this)
        tvTotalInvestment.setOnClickListener(this)
        tvTravelRewards.setOnClickListener(this)
        tvReimbursement.setOnClickListener(this)
        tvH2DFee.setOnClickListener(this)
        btnChat.setOnClickListener(this)
        tvWhereToGetValue.setOnClickListener(this)
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
            clItemPrice.visibility = View.VISIBLE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.GONE
        } else if (type.equals("Tax")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.VISIBLE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.GONE
        } else if (type.equals("Reward")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.VISIBLE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.GONE
        } else if (type.equals("Reimbursement")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.GONE
            clReimbursement.visibility = View.VISIBLE
        } else if (type.equals("Investment")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clYourReward.visibility = View.GONE
            clTotalInvestment.visibility = View.VISIBLE
            clReimbursement.visibility = View.GONE
        } else if (type.equals("H2DFee")) {
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
            R.id.btnMakeAnOffer -> {

                if (orderListingDetail?.insurance == true) {
                    if (CheckNetworkConnection.isOnline(this)) {

                        if (orderListingDetail?.type == OrderType.SHOP) {
                            val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
                            dialog.setCanceledOnTouchOutside(true)

                            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                            val view = this.layoutInflater.inflate(R.layout.dialog_info, null)
                            val clItemPrice = view.findViewById<ConstraintLayout>(R.id.clPrice)
                            val clTax = view.findViewById<ConstraintLayout>(R.id.clTax)
                            val clH2dFee = view.findViewById<ConstraintLayout>(R.id.clH2dFee)
                            val clProcessingFee =
                                view.findViewById<ConstraintLayout>(R.id.clProcessingFee)
                            val clAcceptRequest =
                                view.findViewById<ConstraintLayout>(R.id.clAcceptRequest)
                            val clTravelRewards =
                                view.findViewById<ConstraintLayout>(R.id.clTravelRewards)
                            val clAcceptRequestShop =
                                view.findViewById<ConstraintLayout>(R.id.clAcceptRequestShop)

                            val btnYes = view.findViewById<TextView>(R.id.tvYes)


                            clAcceptRequest.visibility = View.GONE
                            clAcceptRequestShop.visibility = View.VISIBLE
                            clItemPrice.visibility = View.GONE
                            clTax.visibility = View.GONE
                            clH2dFee.visibility = View.GONE
                            clProcessingFee.visibility = View.GONE
                            clTravelRewards.visibility = View.GONE
                            dialog.setCancelable(true)
                            dialog.setContentView(view)

                            btnYes.setOnClickListener {
                                startActivity(
                                    Intent(this, MakeAnOfferActivity::class.java)
                                        .putExtra(ORDER_ID, orderListingDetail?._id)
                                        .putExtra(
                                            "itemPrice",
                                            orderListingDetail?.recommendedReward
                                        )
                                        .putExtra("cardId", "")
                                        .putExtra("date", orderListingDetail?.pickUpDate)
                                        .putExtra(POSITION, 0)
                                )
                            }
                            dialog.show()
                        } else {

                            val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
                            dialog.setCanceledOnTouchOutside(true)

                            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                            val view = this.layoutInflater.inflate(R.layout.dialog_info, null)
                            val clItemPrice = view.findViewById<ConstraintLayout>(R.id.clPrice)
                            val clTax = view.findViewById<ConstraintLayout>(R.id.clTax)
                            val clH2dFee = view.findViewById<ConstraintLayout>(R.id.clH2dFee)
                            val clProcessingFee =
                                view.findViewById<ConstraintLayout>(R.id.clProcessingFee)
                            val clAcceptRequest =
                                view.findViewById<ConstraintLayout>(R.id.clAcceptRequest)
                            val clTravelRewards =
                                view.findViewById<ConstraintLayout>(R.id.clTravelRewards)
                            val tvDescription = view.findViewById<TextView>(R.id.tvDescription)

                            val btnCancel = view.findViewById<TextView>(R.id.tvCancel)
                            val btnAddCard = view.findViewById<TextView>(R.id.tvAddCard)

                            val itemPrice = orderListingDetail?.itemPrice?.toDouble()!!
                            val taxAmount = orderListingDetail?.tax?.toDouble()!!
                            val quantity = orderListingDetail?.itemQuantity?.toInt()!!
                            val price = (itemPrice * quantity) + taxAmount
                            tvDescription.text =
                                this.getString(R.string.delivery_info, "$" + price.toString())
                            clAcceptRequest.visibility = View.VISIBLE
                            clItemPrice.visibility = View.GONE
                            clTax.visibility = View.GONE
                            clH2dFee.visibility = View.GONE
                            clProcessingFee.visibility = View.GONE
                            clTravelRewards.visibility = View.GONE
                            dialog.setCancelable(true)
                            dialog.setContentView(view)

                            btnCancel.setOnClickListener {
                                dialog.dismiss()
                            }
                            btnAddCard.setOnClickListener {
                                startActivity(
                                    Intent(
                                        this,
                                        CardListActivity::class.java
                                    )
                                        .putExtra("from", "driver")
                                        .putExtra("order", Gson().toJson(orderListingDetail))
                                )
                            }
                            dialog.show()
                        }


                    } else {
                        Toast.makeText(
                            this,
                            R.string.network_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (orderListingDetail?.type == OrderType.SHOP) {
                        val dialog = Dialog(this,R.style.DialogStyle)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(true)
                        dialog.setContentView(R.layout.alert_dialog_success)
                        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                        tvTitle.text = getString(R.string.app_name)
                        tvDescription.text = "You have to buy the product by yourself and we will reimburse you the full amount plus your delivery reward once you deliver the product."
                        dialogButton.text = getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            startActivity(
                                Intent(this, MakeAnOfferActivity::class.java)
                                    .putExtra(ORDER_ID, orderListingDetail?._id)
                                    .putExtra("itemPrice", orderListingDetail?.recommendedReward)
                                    .putExtra("date", orderListingDetail?.pickUpDate)
                                    .putExtra("cardId", "")
                                    .putExtra(POSITION, 0)
                            )
                        }
                        dialog.show()
                    } else {
                        startActivity(
                            Intent(this, MakeAnOfferActivity::class.java)
                                .putExtra(ORDER_ID, orderListingDetail?._id)
                                .putExtra("itemPrice", orderListingDetail?.recommendedReward)
                                .putExtra("date", orderListingDetail?.pickUpDate)
                                .putExtra("cardId", "")
                                .putExtra(POSITION, 0)
                        )
                    }
                }
            }
            R.id.imageView -> {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(USER_ID, orderListingDetail?.userId?._id)
                intent.putExtra(USER_NAME, orderListingDetail?.userId?.fullName)
                intent.putExtra(
                    PROFILE_PIC_URL,
                    orderListingDetail?.userId?.profilePicURL?.original
                )
                startActivity(intent)
            }
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

    @SuppressLint("SetTextI18n")
    private fun setView(orderList: OrderListing?) {

        if ((orderList?.offers?.size ?: 0) > 0) {
            rvOffers.visibility = View.VISIBLE
            if (offerList.size > 0)
                offerList.clear()
            orderList?.offers?.let {
                for (i in it.indices) {
                    val ofrs = it[i]
                    ofrs.orderStatus = orderList.orderStatus
                    offerList.add(ofrs)
                }
            }
            tvOffersCount.text = "OFFERS " + offerList.size
            tvOffersCount.visibility = View.VISIBLE
            if (::requestOfferAdapter.isInitialized)
                requestOfferAdapter.notifyDataSetChanged()
        } else {
            rvOffers.visibility = View.GONE
            tvOffersCount.visibility = View.GONE
        }

        if (orderList?.orderStatus != OrderStatus.REQUESTED) {
            rvOffers.visibility = View.GONE
            tvOffersCount.visibility = View.GONE
            btnMakeAnOffer.visibility = View.GONE
        }

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
        if (orderList?.originalPacking == null || orderList.originalPacking.equals("0")) {
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

                tvItemPriceValue.text = getString(R.string.currency_sign) + String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    price
                ) + " USD"
                if (orderList.tax.equals("0")) {
                    tvTaxValue.text = getString(R.string.currency_sign) + " " + "0.00 USD"
                    tvTaxValue.setTextColor(resources.getColor(R.color.grey_error))
                    tvTax.setTextColor(resources.getColor(R.color.grey_error))
                } else {
                    tvTaxValue.text = getString(R.string.currency_sign) + " " + String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        orderList.tax.toDouble()
                    ) + " USD"
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
                        ))
                        tvTravelRewardsValue.text = """${getString(R.string.currency_sign)} $p"""
                    } else {
                        p = (String.format(Locale.ENGLISH, "%.2f", orderList.payment))
                        tvTravelRewardsValue.text = """${getString(R.string.currency_sign)} $p"""
                    }
                } else {
                    tvTravelRewardsValue.text =
                        """${getString(R.string.currency_sign)} ${orderList.accepted?.totalPrice ?: ""}"""
                }
                tvTotalInvestmentValue.text = "$" + String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    price + orderList.tax.toDouble()
                ) + " USD"
                if(orderList.recommendedReward.isNullOrEmpty()){
                    orderList.recommendedReward  = "0.0";
                }
                tvTravelRewardsValue.text =
                    "$" + String.format(Locale.ENGLISH, "%.2f", orderList.payment) + " USD"
                tvH2DFeeValue.text = "$" + String.format(Locale.ENGLISH, "%.2f", ((orderList.payment*1)/100)) + " USD"
                tvReimbursementValue.text = "$" + String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    ((price + orderList.tax.toDouble() + orderList.payment)-(orderList.payment*1)/100)
                ) + " USD"
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
            intent.putExtra(PRICE, price)
            intent.putExtra("additional", additional)
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
        //tvDescription.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        if (code == 401) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog_success)
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
            //tvDescription.showSnack(errorBody!!)
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
            price = data?.getDoubleExtra(PRICE, 0.0) ?: 0.0
            additional = data?.getDoubleExtra("additional", 0.0) ?: 0.0
            onBackPressed()
        }
        if (requestCode == 402 && resultCode == Activity.RESULT_OK && data != null) {
            if (CheckNetworkConnection.isOnline(this)) {
                cardId = data.getStringExtra("cardId").toString()
                startActivity(
                    Intent(this, MakeAnOfferActivity::class.java)
                        .putExtra(ORDER_ID, orderListingDetail?._id)
                        .putExtra("itemPrice", orderListingDetail?.recommendedReward)
                        .putExtra("cardId", cardId)
                        .putExtra("date", orderListingDetail?.pickUpDate)
                        .putExtra(POSITION, intent.getStringExtra(POSITION))
                )

            }
        }
    }

    override fun selectedPos(position: Int) {
        selectedPos = position
    }


}