package com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_made

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
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfileActivity
import com.capcorp.ui.user.homescreen.ActivityCheckout
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.Offer
import kotlinx.android.synthetic.main.item_offer_driver.view.*


class RequestOfferForDriverAdapter(
    private val mContext: Context,
    private val offerList: ArrayList<Offer>,
    private val selectedPos: SelectedPos,
    private val offerMadeActivity: OfferMadeActivity
) : RecyclerView.Adapter<RequestOfferForDriverAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_offer_driver, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(offerList[position])
    }

    override fun getItemCount(): Int {
        return offerList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.ivProfile.setOnClickListener {
                mContext.startActivity(
                    Intent(mContext, OtherUserProfileActivity::class.java)
                        .putExtra(USER_ID, offerList.get(adapterPosition)._id)
                        .putExtra(USERTYPE, UserType.DRIVER)
                )
            }


        }

        private fun acceptConfirmation(selectedPosition: Int) {
            if (offerMadeActivity.orderListingDetail != null) {
                SharedPrefs.with(mContext)
                    .save("orderToCheckout", offerMadeActivity.orderListingDetail)
                val intent = Intent(mContext, ActivityCheckout::class.java)
                var selectedOffer = offerMadeActivity.offerList[selectedPosition]
                SharedPrefs.with(mContext).save("offerToCheckout", selectedOffer)
                mContext.startActivity(intent)
            }
        }


        @SuppressLint("SetTextI18n")
        fun bind(offerList: Offer) = with(itemView) {
            itemView.tvUserName.text = offerList.fullName
            itemView.tvArrivalDate.text =
                "Arrival Date :" + offerList.createdDate?.let { getOnlyDate(it, "EEE · MMM d") }
            //itemView.tvShippingCharge.text = context.getString(R.string.currency_sign)+" "+offerList.shippingCharge.toInt()
            itemView.tvPrice.text =
                """${mContext.getString(R.string.currency_sign)} ${String.format("%.2f",offerList.totalPrice)}"""
            itemView.tvRewardValue.text =
                """${mContext.getString(R.string.currency_sign)} ${String.format("%.2f",offerList.totalPrice)}"""
            itemView.tvExtraCostValue.text =
                """${mContext.getString(R.string.currency_sign)} ${
                    kotlin.String.format("%.2f",offerList.shippingCharge)}"""
            Glide.with(mContext).load(offerList.profilePicURL?.original)
                .apply(
                    RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.profile_pic_placeholder)
                ).into(ivProfile)
            ratingBars.rating = offerList.averageRating.toFloat()
            itemView.tvCommentValue.text = offerList.detailsNote

            itemView.btnInfo.visibility = View.VISIBLE

            itemView.btnInfo.setOnClickListener {
                val message = "${
                    "Delivery reward + Extra Cost : ${context.getString(R.string.currency_sign)}${String.format("%.2f",offerList.price)}+${
                        context.getString(R.string.currency_sign)
                    }${String.format("%.2f",offerList.shippingCharge)}"
                }"

                ViewTooltip.on(offerMadeActivity, itemView.btnInfo)
                    .autoHide(true, 3000)
                    .corner(30)
                    .text(message)
                    .position(ViewTooltip.Position.BOTTOM)
                    .show()
            }
        }
    }

    fun getPositon(): Int {
        return selectedPosition
    }

    interface SelectedPos {
        fun selectedPos(position: Int)
    }
}
