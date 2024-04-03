package com.capcorp.ui.user.homescreen.orders.request_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfileActivity
import com.capcorp.ui.user.homescreen.ActivityCheckout
import com.capcorp.ui.user.homescreen.chat.chatmessage.ChatActivity
import com.capcorp.ui.user.homescreen.orders.RequestBeforeDetailActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.Offer
import kotlinx.android.synthetic.main.item_offer.view.*


class RequestOfferAdapter(
    private val mContext: Context,
    private val offerList: ArrayList<Offer>,
    private val selectedPos: SelectedPos,
    private val requestDetailActivity: RequestBeforeDetailActivity
) : RecyclerView.Adapter<RequestOfferAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_offer, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(offerList[position])
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.btnAccept.setOnClickListener {
                selectedPosition = adapterPosition
                if (CheckNetworkConnection.isOnline(mContext)) {
                    acceptConfirmation(selectedPosition)
                } else {
                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show()
                }
            }
            itemView.btnChat.setOnClickListener {
                selectedPosition = adapterPosition
                val intent = Intent(requestDetailActivity, ChatActivity::class.java)
                intent.putExtra(USER_ID, offerList[adapterPosition]._id)
                intent.putExtra(USER_NAME, offerList[adapterPosition].fullName)
                intent.putExtra(PROFILE_PIC_URL, offerList[adapterPosition].profilePicURL?.original)
                requestDetailActivity.startActivity(intent)
            }
            itemView.ivProfile.setOnClickListener {
                mContext.startActivity(
                    Intent(mContext, OtherUserProfileActivity::class.java)
                        .putExtra(USER_ID, offerList.get(adapterPosition)._id)
                        .putExtra(USERTYPE, UserType.DRIVER)
                )
            }

            itemView.ivExpand.setOnClickListener {
                itemView.clExpand.visibility = View.VISIBLE
                itemView.ivExpand.visibility = View.GONE
                itemView.ivCollapse.visibility = View.VISIBLE
            }

            itemView.ivCollapse.setOnClickListener {

                itemView.clExpand.visibility = View.GONE
                itemView.ivExpand.visibility = View.VISIBLE
                itemView.ivCollapse.visibility = View.GONE
            }

        }

        private fun acceptConfirmation(selectedPosition: Int) {
            if (requestDetailActivity.orderListingDetail != null) {
                SharedPrefs.with(mContext)
                    .save("orderToCheckout", requestDetailActivity.orderListingDetail)
                val intent = Intent(mContext, ActivityCheckout::class.java)
                var selectedOffer = requestDetailActivity.offerList[selectedPosition]
                SharedPrefs.with(mContext).save("offerToCheckout", selectedOffer)
                mContext.startActivity(intent)
            }
            /*  AlertDialogUtil.getInstance().createOkCancelDialog(mContext, R.string.confirm, R.string.accept_offer_confirmation1,
                      R.string.yes, R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                  override fun onOkButtonClicked() {
                      selectedPos.selectedPos(selectedPosition)
                      try {
                          requestDetailActivity.apiAcceptRequest(selectedPosition)

                      } catch (e: Exception) {

                      }
                  }

                  override fun onCancelButtonClicked() {
                      // dismiss
                  }

              }).show()*/
        }


        @SuppressLint("SetTextI18n")
        fun bind(offerList: Offer) = with(itemView) {
            itemView.tvUserName.text = offerList.fullName

            if (offerList.orderStatus != null && offerList.orderStatus.equals(NotificationType.ACCEPTED)) {
                itemView.btnAccept.text = NotificationType.ACCEPTED
                itemView.btnAccept.isClickable = false
            }
            itemView.tvArrivalDate.text =
                offerList.driverArrivalDate.let { getOnlyDate(it, "EEE · MMM d") }
            //itemView.tvShippingCharge.text = context.getString(R.string.currency_sign)+" "+offerList.shippingCharge.toInt()
            itemView.tvPrice.text =
                """${mContext.getString(R.string.currency_sign)} ${String.format("%.2f",offerList.totalPrice)}"""
            itemView.tvRewardValue.text =
                """${mContext.getString(R.string.currency_sign)} ${String.format("%.2f",offerList.totalPrice)}"""
            itemView.tvExtraCostValue.text =
                """${mContext.getString(R.string.currency_sign)} ${String.format("%.2f",offerList.shippingCharge)}"""
            Glide.with(mContext).load(offerList.profilePicURL?.original)
                .apply(
                    RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.profile_pic_placeholder)
                ).into(ivProfile)
            ratingBars.rating = offerList.driverAverageRating.toFloat()
            itemView.tvCommentValue.text = offerList.detailsNote

        }
    }

    fun getPositon(): Int {
        return selectedPosition
    }

    interface SelectedPos {
        fun selectedPos(position: Int)
    }
}
