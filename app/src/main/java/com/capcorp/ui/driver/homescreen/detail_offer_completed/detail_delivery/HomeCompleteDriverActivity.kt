package com.capcorp.ui.driver.homescreen.detail_offer_completed.detail_delivery

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.detail_offer_accepeted.home_delivery.TrackingAdapterDriverCompleted
import com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_made.OfferMadeActivity
import com.capcorp.ui.settings.profile.support.SupportOrderActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.orders.accept_detail.AcceptContract
import com.capcorp.ui.user.homescreen.orders.accept_detail.AcceptPresenter
import com.capcorp.ui.user.homescreen.orders.completed_job_details.CompletedDetailContract
import com.capcorp.ui.user.homescreen.orders.completed_job_details.CompletedDetailPresenter
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home_complete_driver.*
import java.io.IOException
import java.util.*

class HomeCompleteDriverActivity : BaseActivity(), View.OnClickListener, AcceptContract.View,
    CompletedDetailContract.View {
    override fun changeDriverStatusPic() {
    }

    override fun changeDriverStatus() {
    }

    private var orderListingDetail: OrderListing? = null
    private var presenter = AcceptPresenter()
    private var dialogAlert: AlertDialog? = null
    private var presenterRating = CompletedDetailPresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate


    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var trackingAdapterDriverCompleted: TrackingAdapterDriverCompleted
    private var orderid :String? = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_complete_driver)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        presenterRating.attachView(this)

        if (intent.hasExtra(NOTIFICATION)) {
            dialogIndeterminate.show()
            view_parent_view.visibility = View.INVISIBLE
            orderid = intent.getStringExtra(ORDER_ID).toString()

        } else {
            view_parent_view.visibility = View.VISIBLE
            orderListingDetail =
                Gson().fromJson(intent.getStringExtra(COMPLETED_DETAIL), OrderListing::class.java)
            orderid = orderListingDetail?._id
        }
        orderid?.let { presenter.getOrderDetail(getAuthAccessToken(this), it) }
        clickListener()
    }

    private fun clickListener() {
        tvBack.setOnClickListener(this)
        tvHelp.setOnClickListener(this)
        viewOrderDetails.setOnClickListener(this)
        tvReviewUser.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvHelp -> {
                startActivity(
                    Intent(this, SupportOrderActivity::class.java).putExtra(
                        "name",
                        orderListingDetail?.itemName
                    )
                        .putExtra("showId", orderListingDetail?.orderId)
                        .putExtra("desc", orderListingDetail?.description)
                        .putExtra("orderId", orderListingDetail?._id).putExtra("isCompleted", true)
                )
            }
            R.id.tvBack -> {
                onBackPressed()
            }
            R.id.viewOrderDetails -> {
                startActivity(
                    Intent(this, OfferMadeActivity::class.java)
                        .putExtra(
                            REQUEST_DETAIL,
                            Gson().toJson(orderListingDetail)
                        )
                        .putExtra(POSITION, 0)
                        .putExtra(TYPE_OFFER_MADE, false)
                )
            }
            R.id.textView12 -> {
                startActivity(
                    Intent(this, RequestDetailActivity::class.java)
                        .putExtra(REQUEST_DETAIL, Gson().toJson(orderListingDetail))
                        .putExtra("fromAcceptDetail", true)
                        .putExtra("from_request", "false")
                )
            }

            R.id.tvReviewUser -> {
                showBottomSheetDialog(orderListingDetail)
            }
        }
    }

    fun openReceiptImagesDialog(context: Context, url: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val alertLayout = inflater.inflate(R.layout.layout_image_show, null)
        val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
        Glide.with(this).load(url).apply(RequestOptions().placeholder(R.drawable.ic_package))
            .into(mImage)
        builder.setView(alertLayout)
        builder.setCancelable(true)
        dialogAlert = builder.create()
        dialogAlert?.setCanceledOnTouchOutside(true)
        dialogAlert?.window?.setGravity(Gravity.CENTER)
        dialogAlert!!.show()
    }

    private fun showBottomSheetDialog(orderList: OrderListing?) {
        val alertLayout = layoutInflater.inflate(R.layout.bottom_sheet_rating, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(alertLayout)
        val lnrRateFirst = alertLayout.findViewById(R.id.rate_first) as LinearLayout
        val lnrRateSecond = alertLayout.findViewById(R.id.rate_second) as LinearLayout
        val lnrRateThird = alertLayout.findViewById(R.id.rate_third) as LinearLayout
        val lnrRateFourth = alertLayout.findViewById(R.id.rate_fourth) as LinearLayout
        val lnrRateFifth = alertLayout.findViewById(R.id.rate_fifth) as LinearLayout

        val ivRateFirst = alertLayout.findViewById(R.id.iv_one_star) as ImageView
        val ivRateSecond = alertLayout.findViewById(R.id.iv_two_star) as ImageView
        val ivRateThird = alertLayout.findViewById(R.id.iv_third_star) as ImageView
        val ivRateFourth = alertLayout.findViewById(R.id.iv_fourth_star) as ImageView
        val ivRateFifth = alertLayout.findViewById(R.id.iv_fifth_star) as ImageView

        val tvRateFirst = alertLayout.findViewById(R.id.tv_rating_count_first) as TextView
        val tvRateSecond = alertLayout.findViewById(R.id.tv_rating_count_second) as TextView
        val tvRateThird = alertLayout.findViewById(R.id.tv_rating_count_third) as TextView
        val tvRateFourth = alertLayout.findViewById(R.id.tv_rating_count_fourth) as TextView
        val tvRateFifth = alertLayout.findViewById(R.id.tv_rating_count_fifth) as TextView

        val tvRateExp = alertLayout.findViewById(R.id.tv_rate_experience) as TextView
        val tvSubmit = alertLayout.findViewById(R.id.tv_submit) as TextView
        val tvCancel = alertLayout.findViewById(R.id.tv_cancel) as TextView

        val tvName = alertLayout.findViewById(R.id.tvName) as TextView
        val mProfileImage = alertLayout.findViewById(R.id.ivProfile) as ImageView
        val mRating = alertLayout.findViewById(R.id.ratingBarz) as RatingBar
        val tvDesc = alertLayout.findViewById(R.id.tv_review_transporter) as TextView
        val mEdtDescription = alertLayout.findViewById(R.id.etDescription) as TextInputEditText
        tvDesc.text = getString(R.string.review_the_shopper)
        var rating: Double = 0.0

        val userType: String = SharedPrefs.with(this).getString(USER_TYPE, "")
        if (userType == "USER") {
            tvRateExp.text = getString(R.string.rate_traveller)
        } else {
            tvRateExp.text = getString(R.string.rate_shopper)
        }
        Glide.with(this).load(orderList?.userId?.profilePicURL?.original)
            .apply(RequestOptions().circleCrop())
            .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
            .into(mProfileImage)
        tvName.text = orderList?.userId?.fullName
        //mRating.rating = orderList?.userId?.averageRating!!
        mRating.rating = orderList?.userId?.averageRating!!.toFloat()
        lnrRateFirst.setOnClickListener {
            rating = 1.0
            lnrRateFirst.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateSecond.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            lnrRateThird.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            lnrRateFourth.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            lnrRateFifth.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            ivRateFirst.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateSecond.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            ivRateThird.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            ivRateFourth.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            ivRateFifth.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            tvRateFirst.setTextColor(Color.WHITE)
            tvRateSecond.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
            tvRateThird.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
            tvRateFourth.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
            tvRateFifth.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
        }
        lnrRateSecond.setOnClickListener {
            rating = 2.0
            lnrRateFirst.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateSecond.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateThird.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            lnrRateFourth.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            lnrRateFifth.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            ivRateFirst.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateSecond.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateThird.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            ivRateFourth.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            ivRateFifth.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            tvRateFirst.setTextColor(Color.WHITE)
            tvRateSecond.setTextColor(Color.WHITE)
            tvRateThird.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
            tvRateFourth.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
            tvRateFifth.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
        }
        lnrRateThird.setOnClickListener {
            rating = 3.0
            lnrRateFirst.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateSecond.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateThird.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateFourth.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            lnrRateFifth.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            ivRateFirst.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateSecond.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateThird.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateFourth.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            ivRateFifth.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            tvRateFirst.setTextColor(Color.WHITE)
            tvRateSecond.setTextColor(Color.WHITE)
            tvRateThird.setTextColor(Color.WHITE)
            tvRateFourth.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
            tvRateFifth.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
        }
        lnrRateFourth.setOnClickListener {
            rating = 4.0
            lnrRateFirst.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateSecond.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateThird.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateFourth.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateFifth.setBackgroundResource(R.drawable.bg_drawable_white_rating)
            ivRateFirst.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateSecond.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateThird.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateFourth.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateFifth.setImageResource(R.drawable.ic_star_rating_unactive_grey)
            tvRateFirst.setTextColor(Color.WHITE)
            tvRateSecond.setTextColor(Color.WHITE)
            tvRateThird.setTextColor(Color.WHITE)
            tvRateFourth.setTextColor(Color.WHITE)
            tvRateFifth.setTextColor(ContextCompat.getColorStateList(this, R.color.black50))
        }
        lnrRateFifth.setOnClickListener {
            rating = 5.0
            lnrRateFirst.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateSecond.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateThird.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateFourth.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            lnrRateFifth.setBackgroundResource(R.drawable.bg_drawable_green_rating)
            ivRateFirst.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateSecond.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateThird.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateFourth.setImageResource(R.drawable.bg_star_unactive_white)
            ivRateFifth.setImageResource(R.drawable.bg_star_unactive_white)
            tvRateFirst.setTextColor(Color.WHITE)
            tvRateSecond.setTextColor(Color.WHITE)
            tvRateThird.setTextColor(Color.WHITE)
            tvRateFourth.setTextColor(Color.WHITE)
            tvRateFifth.setTextColor(Color.WHITE)
        }
        tvSubmit.setOnClickListener {
            if (rating == 0.0) {
                Toast.makeText(this, getString(R.string.select_rating), Toast.LENGTH_SHORT).show()
            } else {
                if (CheckNetworkConnection.isOnline(this)) {
                    var userType: String = SharedPrefs.with(this).getString(USER_TYPE, "")
                    if (userType.equals(UserType.USER)) {
                        orderid?.let { it1 ->
                            presenterRating.ratingRequest(
                                getAuthAccessToken(this),
                                rating,
                                it1,
                                mEdtDescription.text.toString().trim()
                            )
                        }
                    } else {
                        orderid?.let { it1 ->
                            presenterRating.ratingRequest(
                                getAuthAccessToken(this),
                                rating,
                                it1,
                                mEdtDescription.text.toString().trim()
                            )
                        }
                    }
                }
                dialog.dismiss()
            }
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(orderList: OrderListing?) {

        if (orderList?.isUserRated == "0") {
            tvReviewUser.text = getString(R.string.review_the_shopper_question_mark)
            tvReviewUser.setTextColor(resources.getColor(R.color.colorPrimary))
        }


        orderid = orderList?._id.toString()
        tvPrice.text = """${getString(R.string.currency_sign)} ${orderList?.accepted?.totalPrice}"""
        tvRequiredByDate.text = orderList?.deliveredDate?.let { getOnlyDate(it, "EEE · MMM d") }

        tvUserName.text = orderList?.userId?.firstName + " " + orderList?.userId?.lastName
        tvPhoneNumber.text = orderList?.userId?.phoneNo


        when (orderList?.orderType) {

            RequestType.TRANSPORT -> {
                tvItemName.text = orderList.type
            }
            RequestType.PARCEL -> {
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
                tvItemName.text = orderList.itemName
            }
        }

        setRecyclerAdapter(orderList)

    }

    private fun setRecyclerAdapter(orderList: OrderListing?) {
        layoutManager = LinearLayoutManager(this)
        rvItemsTracking.layoutManager = layoutManager
        trackingAdapterDriverCompleted =
            orderList?.let { TrackingAdapterDriverCompleted(this, it) }!!
        rvItemsTracking.adapter = trackingAdapterDriverCompleted
    }


    private fun setLocation(mLastLocation: LatLng, textView: TextView) {
        var addresses: List<Address>?
        try {
            Thread {
                addresses = Geocoder(this, Locale.getDefault()).getFromLocation(
                    mLastLocation.latitude,
                    mLastLocation.longitude,
                    1
                )
                val finalAddresses = addresses
                runOnUiThread {
                    kotlin.run {
                        if (finalAddresses != null && finalAddresses.isNotEmpty()) {
                            textView.text = ""
                            if (finalAddresses[0].adminArea != null)
                                textView.text = finalAddresses[0].adminArea
                        }

                    }
                }
            }.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    override fun onBackPressed() {
        if (intent.hasExtra(NOTIFICATION)) {
            startActivity(Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java))
        } else {
            finish()
        }
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        root_delivered.showSWWerror()
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
            root_delivered.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {
    }

    override fun cancelSucess() {
        onBackPressed()
    }

    override fun orderMarkCompleteSucess() {
        orderListingDetail?.driverStatus = DriverStatus.DELIVERED
        //setOrderStatus()
    }

    override fun ratingSuccess() {
        if (intent.hasExtra(NOTIFICATION)) {
            startActivity(Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java))
        } else {
            val intent = Intent()
            intent.putExtra(IS_USERRATED, "1")
            intent.putExtra(POSITION, intent.getIntExtra(POSITION, 0))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        Toast.makeText(this, getString(R.string.successfully_rate_user), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenterRating.detachView()
    }

    override fun orderDetailSuccess(data: OrderListing) {
        dialogIndeterminate.dismiss()
        view_parent_view.visibility = View.VISIBLE
        orderListingDetail = data
        setView(orderListingDetail)
    }

}
