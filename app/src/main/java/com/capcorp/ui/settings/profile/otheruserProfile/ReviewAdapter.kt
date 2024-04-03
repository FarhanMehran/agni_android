package com.capcorp.ui.settings.profile.otheruserProfile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.utils.USERTYPE
import com.capcorp.utils.USER_ID
import com.capcorp.utils.UserType
import com.capcorp.utils.getOnlyDate
import com.capcorp.webservice.models.ReviewDetails
import kotlinx.android.synthetic.main.item_reviews.view.*

class ReviewAdapter(
    private val mContext: Context,
    private val reviewList: ArrayList<ReviewDetails>,
    private val reviewListner: ReviewListner
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_reviews, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    interface ReviewListner {

        fun onReviewClicked(userId: String?, type: String?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(orderList: ReviewDetails) = with(itemView) {
            tvNotificationText.text = reviewList[adapterPosition].fullName

            tvUserName.text = orderList.fullName
            tvMemberSince.text = orderList.createdDate?.let { getOnlyDate(it, "MMM yyyy") }

            reviewList[adapterPosition].description?.let {

                if (it.isNotBlank()) {
                    tvNotificationText.visibility = View.VISIBLE
                    tvNotificationText.text = it
                } else
                    tvNotificationText.text = ""


            }

            reviewList[adapterPosition].from?.let {

                if (it.isNotBlank()) {
                    tvShopper.visibility = View.VISIBLE
                    if (it == UserType.USER)
                        tvShopper.text = context.getString(R.string.shopper)
                    else
                        tvShopper.text = context.getString(R.string.traveler)


                } else {
                    tvShopper.visibility = View.GONE
                }
            }

            ratingBars.rating = reviewList[adapterPosition].totalRating


//            if (reviewList[adapterPosition].from == UserType.DRIVER) {
//
//                reviewList[adapterPosition].isApproved?.let {
//                    if (it == 1) {
//                        tvMemberSince.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bg_verify_grey, 0, 0, 0);
//                        tvMemberSince.text = "Verified with mobile,email"
//                    } else {
//                        tvMemberSince.text = "Not Verified"
//                    }
//
//                }
//
//            } else
//                tvMemberSince.visibility = View.GONE


            /*if (reviewList[adapterPosition].from == UserType.USER) {

                reviewList[adapterPosition].isApproved?.let {

                    if (reviewList[adapterPosition].badge != null) {

                        val mBadge = reviewList[adapterPosition].badge

                        if (it == 1) {

                            tvVerify?.text = mContext.getString(R.string.verified)
                            tvVerify?.visibility = View.VISIBLE
                            when (mBadge) {
                                BadgeName.NEW_SHOPPER -> {
                                    iv_badge_icon?.setImageResource(R.drawable.new_user)
                                    tvBadgeName.text = mContext.getString(R.string.new_shopper)
                                }
                                BadgeName.SILVER_SHOPPER -> {
                                    iv_badge_icon?.setImageResource(R.drawable.badge_silver_new)
                                    tvBadgeName.text = mContext.getString(R.string.silver_shopper)
                                }
                                BadgeName.BRONZE_SHOPPER -> {
                                    iv_badge_icon?.setImageResource(R.drawable.ic_bronz)
                                    tvBadgeName.text = mContext.getString(R.string.bronze_shopper)
                                }
                                BadgeName.GOLD_SHOPPER -> {
                                    iv_badge_icon?.setImageResource(R.drawable.ic_gold)
                                    tvBadgeName.text = mContext.getString(R.string.gold_shopper)
                                }
                                BadgeName.PLATINUM_SHOPPER -> {
                                    iv_badge_icon?.setImageResource(R.drawable.ic_platinum)
                                    tvBadgeName.text = mContext.getString(R.string.platinum_shopper)
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

            parentPanel?.setOnClickListener {

                reviewListner.onReviewClicked(
                    userId = reviewList[adapterPosition]._id,
                    type = reviewList[adapterPosition].type
                )


            }
            itemView.ivProfilePic.setOnClickListener {
                mContext.startActivity(
                    Intent(mContext, OtherUserProfileActivity::class.java)
                        .putExtra(USER_ID, reviewList[adapterPosition].Id)
                        .putExtra(USERTYPE, UserType.USER)
                )
            }

            reviewList[adapterPosition].profilePicURL?.let {

                Glide.with(mContext).load(it.original)
                    .apply(RequestOptions().circleCrop())
                    .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                    .into(ivProfilePic)

            }


        }
    }

}