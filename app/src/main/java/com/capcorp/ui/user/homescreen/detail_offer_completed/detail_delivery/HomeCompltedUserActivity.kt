package com.capcorp.ui.user.homescreen.detail_offer_completed.detail_delivery

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.capcorp.ui.settings.profile.support.SupportOrderActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user.homescreen.orders.accept_detail.AcceptContract
import com.capcorp.ui.user.homescreen.orders.accept_detail.AcceptPresenter
import com.capcorp.ui.user.homescreen.orders.completed_job_details.CompletedDetailContract
import com.capcorp.ui.user.homescreen.orders.completed_job_details.CompletedDetailPresenter
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home_completed_user.*
import kotlinx.android.synthetic.main.activity_home_completed_user.root_delivered
import kotlinx.android.synthetic.main.activity_home_completed_user.tvBack
import kotlinx.android.synthetic.main.activity_home_completed_user.tvHelp
import kotlinx.android.synthetic.main.activity_home_completed_user.tvItemName
import kotlinx.android.synthetic.main.activity_home_completed_user.tvPhoneNumber
import kotlinx.android.synthetic.main.activity_home_completed_user.tvPrice
import kotlinx.android.synthetic.main.activity_home_completed_user.tvReceiverDetail
import kotlinx.android.synthetic.main.activity_home_completed_user.tvRequiredByDate
import kotlinx.android.synthetic.main.activity_home_completed_user.tvUserName
import kotlinx.android.synthetic.main.activity_home_completed_user.viewOrderDetails
import java.util.*

class HomeCompltedUserActivity : BaseActivity(), View.OnClickListener, AcceptContract.View,
    CompletedDetailContract.View {
    override fun changeDriverStatusPic() {
    }

    override fun changeDriverStatus() {
    }

    private var orderListingDetail: OrderListing? = null
    private var presenter = AcceptPresenter()
    private var presenterRating = CompletedDetailPresenter()

    private var dialogAlert: AlertDialog? = null
    private var orderId: String? = ""
    private var isCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_completed_user)
        presenter.attachView(this)
        presenterRating.attachView(this)
        isCompleted = intent.getBooleanExtra(IS_COMPLETED, false)

        if (intent.hasExtra(NOTIFICATION)) {
            orderId = intent.getStringExtra(ORDER_ID)
        } else {
            orderListingDetail =
                Gson().fromJson(intent.getStringExtra(COMPLETED_DETAIL), OrderListing::class.java)
            orderId = orderListingDetail?._id
        }
        orderId?.let { presenter.getOrderDetail(getAuthAccessToken(this), it) }


        clickListener()
    }

    private fun clickListener() {
        tvBack.setOnClickListener(this)
        viewOrderDetails.setOnClickListener(this)
        tvHelp.setOnClickListener(this)
        tvReviewTraveler.setOnClickListener(this)
        //tv_view_product.setOnClickListener(this)
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
                        .putExtra("orderId", orderListingDetail?._id)
                        .putExtra(IS_COMPLETED, isCompleted)
                )
            }
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://18.236.46.218:8001/help")))

            R.id.tvBack -> {
                onBackPressed()
            }
            R.id.viewOrderDetails -> {
                startActivity(
                    Intent(this, RequestDetailActivity::class.java)
                        .putExtra(REQUEST_DETAIL, Gson().toJson(orderListingDetail))
                        .putExtra("fromAcceptDetail", true)
                        .putExtra("from_request", "false")
                )
            }

            R.id.tvReviewTraveler -> {
                if (orderListingDetail?.isDriverRated?.equals("0") == true) {
                    showBottomSheetDialog(orderListingDetail)
                }
            }
        }
    }

    fun openProductImagesDialog(context: Context, url: String) {
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
        val mEdtDescription = alertLayout.findViewById(R.id.etDescription) as TextInputEditText

        var rating: Double = 0.0

        Glide.with(this).load(orderList?.accepted?.profilePicURL?.original)
            .apply(
                RequestOptions.circleCropTransform().placeholder(R.drawable.profile_pic_placeholder)
            ).into(mProfileImage)
        tvName.text = orderList?.accepted?.fullName
        mRating.rating = orderList?.accepted?.driverAverageRating!!.toFloat()

        val userType: String = SharedPrefs.with(this).getString(USER_TYPE, "")
        if (userType == "USER") {
            tvRateExp.text = getString(R.string.rate_traveller)
        } else {
            tvRateExp.text = getString(R.string.rate_shopper)
        }


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
                    if (userType.equals("USER")) {
                        presenterRating.ratingRequest(
                            getAuthAccessToken(this),
                            rating,
                            orderListingDetail?._id!!,
                            mEdtDescription.text.toString().trim()
                        )
                    } else {
                        presenterRating.ratingRequest(
                            getAuthAccessToken(this),
                            rating,
                            orderListingDetail?._id!!,
                            mEdtDescription.text.toString().trim()
                        )
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

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var trackingAdapterDriverCompleted: TrackingAdapterDriverCompleted
    private fun setRecyclerAdapter(orderList: OrderListing?) {
        layoutManager = LinearLayoutManager(this)
        rvItemsTracking.layoutManager = layoutManager
        trackingAdapterDriverCompleted =
            orderList?.let { TrackingAdapterDriverCompleted(this, it) }!!
        rvItemsTracking.adapter = trackingAdapterDriverCompleted
    }

    @SuppressLint("SetTextI18n")
    private fun setView(orderList: OrderListing?) {
        setRecyclerAdapter(orderList)
        /*if (orderList?.type == "Shop") {
            tvReceiverDetail.text = "RECEIVER DETAILS"
            tvUserName.text = orderList.receiverInfo?.employeeName
            tvPhoneNumber.text = orderList.receiverInfo?.phoneNumber
        } else {*/
            tvReceiverDetail.text = "TRAVELLER DETAILS"
            tvUserName.text = orderList?.accepted?.fullName
            tvPhoneNumber.text = orderList?.accepted?.phoneNo
        /*}*/


        tvRequiredByDate.text = orderList?.deliveredDate?.let { getOnlyDate(it,"MMM dd yyyy") }

        if (orderList?.orderStatus == OrderStatus.EXPIRED) {
            tvPrice.text = "${getString(R.string.currency_sign)} ${String.format("%.2f",orderList.payment)}" + " USD"
        } else {
            tvPrice.text = "${getString(R.string.currency_sign)} ${String.format("%.2f",orderList?.accepted?.totalPrice?.toDouble())}" + " USD"
        }

        if(orderList?.couponUse == true){

            tvPrice?.text = getString(R.string.currency_sign) + " " + (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    (orderList.totalCheckout?.minus(orderList.discount))
            )) + " USD"
        }else{

            tvPrice?.text = getString(R.string.currency_sign) + " " + (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    orderList?.totalCheckout
            )) + " USD"
        }




        if (orderListingDetail?.isDriverRated?.equals("1") == true) {
            tvReviewTraveler.text = "You have already reviewed the shopper"
            tvReviewTraveler.setTextColor(resources.getColor(R.color.black))
            tvReviewTraveler.isEnabled = false
        }else{
            tvReviewTraveler.text = getString(R.string.review_the_traveller_question_mark)
            tvReviewTraveler.setTextColor(resources.getColor(R.color.colorPrimary))
            tvReviewTraveler.isEnabled = true
        }

        tvItemName.text = orderList?.itemName
    }

    override fun onBackPressed() {
        if (intent.hasExtra(NOTIFICATION)) {
            startActivity(Intent(this, HomeActivity::class.java))
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
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            val intent = Intent()
            intent.putExtra(IS_DRIVERRATED, "1")
            intent.putExtra(POSITION, intent.getIntExtra(POSITION, 0))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        tvReviewTraveler.text = getString(R.string.you_have_already_received_the_traveler)
        Toast.makeText(this, getString(R.string.successfully_rated_traveler), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenterRating.detachView()
    }

    override fun orderDetailSuccess(data: OrderListing) {
        orderListingDetail = data
        setView(data)
    }
}
