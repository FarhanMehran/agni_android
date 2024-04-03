package com.capcorp.ui.settings.profile.otheruserProfile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.ReviewDetails
import com.capcorp.webservice.models.SignupModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_other_user_profile.*
import kotlinx.android.synthetic.main.activity_other_user_profile.ivVerified
import kotlinx.android.synthetic.main.fragment_account_driver.*
import java.util.*
import kotlin.collections.ArrayList

class OtherUserProfileActivity : BaseActivity(), OtherUserProfileContract.View,
    View.OnClickListener {
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private val otherUserProfilePresenter = OtherUserProfilePresenter()
    private lateinit var reviewAdapter: ReviewAdapter
    private var reviewList = ArrayList<ReviewDetails>()
    private lateinit var layoutManager: LinearLayoutManager
    private var mIsVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)
        otherUserProfilePresenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        getProfile()
        setUserReviewAdapter()
        setListener()
    }

    fun setListener() {
        tvBack.setOnClickListener(this)
    }

    fun getProfile() {
        if (CheckNetworkConnection.isOnline(this)) {
            intent.getStringExtra(USERTYPE)?.let {
                intent.getStringExtra(USER_ID)?.let { it2 ->
                    otherUserProfilePresenter.onOtherUserProfile(
                        getAuthAccessToken(this), it2,
                        it
                    )
                }

            }
        }
    }

    private fun setUserReviewAdapter() {
        layoutManager = LinearLayoutManager(this)
        rv_reviews.layoutManager = layoutManager

    }

    override fun onOtherUserProfile(data: SignupModel?) {

        mIsVerified = data?.isApproved == 1
        if (mIsVerified) {
            ivVerified.setImageResource(R.drawable.verified)
        } else {
            ivVerified.setImageResource(R.drawable.un_verified)
        }
        ivVerified.setOnClickListener {
            if(mIsVerified) {
                ViewTooltip.on(this, ivVerified)
                    .autoHide(true, 3000)
                    .corner(30)
                    .text(getString(R.string.identity_verification_completed))
                    .position(ViewTooltip.Position.BOTTOM)
                    .show()
            }else{
                ViewTooltip.on(this, ivVerified)
                    .autoHide(true, 3000)
                    .corner(30)
                    .text(getString(R.string.identity_verification_pending))
                    .position(ViewTooltip.Position.BOTTOM)
                    .show()
            }
        }
        tvUserName.text = data?.fullName
        data?.createdDate?.let {
            tvMemberSince.text = getString(R.string.member_since, getFormatFromDate(Date(it),"dd MMM yyyy"))
        }
        tvDescription.text = data?.bio
        data?.details?.asShopper?.let { tvAsShopperValue.text = it.toString() }
        data?.details?.asTraveler?.let { tvAsNomadValue.text = it.toString() }
        data?.details?.reviesCounter?.let { tvCancelledValue.text = it.toString() + "%" }

//        tvDescription.text = data?.

        /*if (data?.type == UserType.USER) {
            data.isApproved?.let {

                if (data.badge != null) {

                    val mBadge = data.badge

                    if (it == 1) {
                        when (mBadge) {
                            BadgeName.NEW_SHOPPER -> {
                                iv_badge_icon?.setImageResource(R.drawable.new_user)
                                tvBadgeName.text = getString(R.string.new_shopper)
                            }
                            BadgeName.SILVER_SHOPPER -> {
                                iv_badge_icon?.setImageResource(R.drawable.badge_silver_new)
                                tvBadgeName.text = getString(R.string.silver_shopper)
                            }
                            BadgeName.BRONZE_SHOPPER -> {
                                iv_badge_icon?.setImageResource(R.drawable.ic_bronz)
                                tvBadgeName.text = getString(R.string.bronze_shopper)
                            }
                            BadgeName.GOLD_SHOPPER -> {
                                iv_badge_icon?.setImageResource(R.drawable.ic_gold)
                                tvBadgeName.text = getString(R.string.gold_shopper)
                            }
                            BadgeName.PLATINUM_SHOPPER -> {
                                iv_badge_icon?.setImageResource(R.drawable.ic_platinum)
                                tvBadgeName.text = getString(R.string.platinum_shopper)
                            }
                            else -> {

                            }
                        }
                    } else {
                        iv_badge_icon?.visibility = View.GONE
                    }

                }


            }

        }*/

        Glide.with(this).load(data?.profilePicURL?.original)
            .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder).circleCrop())
            .into(ivProfilePic)



        data?.reviews?.let {

            if (it.isNotEmpty()) {
                reviewList = it

                if (it.size > 1)
                    tvReviews.text = "Reviews(${it.size})"
            }


        }


        if (reviewList.isEmpty()) {
            tvNoReviewFound.visibility = View.VISIBLE
        } else {
            tvNoReviewFound.visibility = View.GONE
        }


        Log.d("reviewsData", Gson().toJson(data?.reviews))
        reviewAdapter =
            ReviewAdapter(rv_reviews.context, reviewList, object : ReviewAdapter.ReviewListner {
                override fun onReviewClicked(userId: String?, type: String?) {

                    if (userId != null && type != null) {

                        reviewList.clear()

                        if (CheckNetworkConnection.isOnline(this@OtherUserProfileActivity)) {
                            otherUserProfilePresenter
                                .onOtherUserProfile(
                                    getAuthAccessToken(this@OtherUserProfileActivity),
                                    userId, type
                                )
                        }

                    }
                }

            })
        rv_reviews.adapter = reviewAdapter
    }

/*
        if (data?.userReviews?.size ?: 0 > 0) {
            if (data?.userReviews?.size ?: 0 > 5) {
                for (i in 0..5) {
                    data?.userReviews?.let { reviewList.addAll(it) }
                    reviewAdapter.notifyDataSetChanged()
                }
            } else {
                data?.userReviews?.let { reviewList.addAll(it) }
                reviewAdapter.notifyDataSetChanged()
            }
        }
        if (data?.driverReviews?.size ?: 0 > 0) {
            if (data?.driverReviews?.size ?: 0 > 5) {
                for (i in 0..5) {
                    data?.driverReviews?.let { reviewList.addAll(it) }
                    reviewAdapter.notifyDataSetChanged()
                }
            } else {
                data?.driverReviews?.let { reviewList.addAll(it) }
                reviewAdapter.notifyDataSetChanged()
            }
        }
        if (reviewList.isEmpty()) {
            tvNoReviewFound.visibility = View.VISIBLE
        } else {
            tvNoReviewFound.visibility = View.GONE
        }*/
    /* if (reviewList.size <= 5) {
         tvViewAll.visibility = View.GONE
     } else {
         tvViewAll.visibility = View.VISIBLE
     }*/


    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        rootview_profile.showSWWerror()
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
            rootview_profile.showSnack(errorBody ?: getString(R.string.sww_error))
        }
    }

    override fun validationsFailure(type: String?) {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        otherUserProfilePresenter.detachView()
    }
}
