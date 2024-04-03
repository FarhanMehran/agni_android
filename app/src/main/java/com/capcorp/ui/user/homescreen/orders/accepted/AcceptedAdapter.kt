package com.capcorp.ui.user.homescreen.orders.accepted

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfileActivity
import com.capcorp.ui.settings.profile.support.SupportOrderActivity
import com.capcorp.ui.user.homescreen.detail_offer_accepted.shop.ShopDetailsUserActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_shop_details_user.*
import kotlinx.android.synthetic.main.item_order_completed_new.view.*
import java.util.*
import kotlin.collections.ArrayList


class AcceptedAdapter(
    private val mContext: Context,
    private val SelectedOrderId: AcceptedAdapter.selectedOrderId,
    private val orderList: ArrayList<OrderListing>,
    private val acceptedFragment: AcceptedFragment
) : RecyclerView.Adapter<AcceptedAdapter.ViewHolder>() {

    var selectedPostion = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_order_completed_new, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                if (orderList.size > 0) {
                    selectedPostion = adapterPosition
                    if (orderList.get(adapterPosition).type.equals(OrderType.SHOP)) {
                        acceptedFragment.startActivityForResult(
                            Intent(mContext, ShopDetailsUserActivity::class.java)
                                .putExtra(
                                    ACCEPT_DETAIL, Gson().toJson(orderList[adapterPosition])
                                ), ACCEPT_LIST_DETAIL
                        )

                    } else if (orderList.get(adapterPosition).type.equals(OrderType.DELIVERY)) {
                        acceptedFragment.startActivityForResult(
                            Intent(mContext, ShopDetailsUserActivity::class.java)
                                .putExtra(ACCEPT_DETAIL, Gson().toJson(orderList[adapterPosition])),
                            ACCEPT_LIST_DETAIL
                        )
                    } else if (orderList.get(adapterPosition).type.equals(OrderType.PICKUP)) {
                        acceptedFragment.startActivityForResult(
                            Intent(mContext, ShopDetailsUserActivity::class.java)
                                .putExtra(ACCEPT_DETAIL, Gson().toJson(orderList[adapterPosition]))
                                .putExtra(POSITION, adapterPosition), ACCEPT_LIST_DETAIL
                        )
                    }
                }
            }
            itemView.ivUserImage.setOnClickListener {
                mContext.startActivity(
                    Intent(mContext, OtherUserProfileActivity::class.java)
                        .putExtra(USER_ID, orderList.get(adapterPosition).accepted?._id)
                        .putExtra(USERTYPE, UserType.USER)
                )
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(orderList: OrderListing) = with(itemView) {
            itemView.tvSource.text = orderList.pickUpAddress
            itemView.tvOrderCode.text = orderList.orderId
            itemView.tvDestination.text = orderList.dropDownAddress
            itemView.tvRequiredBy.text =
                context.getString(R.string.by) + " " + orderList.accepted?.driverArrivalDate?.let {
                    getOnlyDate(
                        it,
                        "EEE · MMM d"
                    )
                }
            itemView.tvDate.text =
                orderList.createdDate?.let { DateUtils.getRelativeTimeSpanString(it) }
            var itemPrice = 0.0
            if (orderList.itemPrice.isNotEmpty() && orderList.itemQuantity.isNotEmpty()) {
                itemPrice = orderList.itemPrice.toDouble() * orderList.itemQuantity.toDouble()
            }
            var h2dFee = 0.0
            if (orderList.h2dFee.isNotEmpty()) {
                h2dFee = orderList.h2dFee.toDouble()
            }
            var stripefee = 0.0
            if (!orderList.stripeFee.isNullOrEmpty()) {
                stripefee = orderList.stripeFee?.toDouble()!!
            }
            var tax = 0.0
            if (orderList.tax.isNotEmpty()) {
                tax = orderList.tax.toDouble()
            }
            var payment = 0.0
            if (orderList.tax.isNotEmpty()) {
                payment = orderList.payment
            }
            var acceptedUserTotalPrice = 0.0
            if (orderList.accepted?.totalPrice?.isNotEmpty() == true) {
                acceptedUserTotalPrice = orderList.accepted?.totalPrice!!.toDouble()
            }
            var discount = 0.0
            if (orderList.tax.isNotEmpty()) {
                discount = orderList.discount
            }

            if (orderList.type == OrderType.SHOP) {
                if (orderList.couponUse == true) {
                    itemView.tvDeliveryReward.text =
                        "Traveler Reward " + "${mContext.getString(R.string.currency_sign)} " + String.format(
                            "%.2f",
                            itemPrice
                        )
                    itemView.tvPrice.text =
                        mContext.getString(R.string.currency_sign) + " " + String.format(
                            "%.2f",
                            (itemPrice + tax + h2dFee + stripefee + acceptedUserTotalPrice) - discount
                        )
                } else {
                    itemView.tvPrice.text =
                        mContext.getString(R.string.currency_sign) + " " + String.format(
                            "%.2f",
                            itemPrice + tax + h2dFee + stripefee + acceptedUserTotalPrice
                        )
                    itemView.tvDeliveryReward.text =
                        "Traveler Reward " + "${mContext.getString(R.string.currency_sign)} " + String.format(
                            "%.2f",
                            itemPrice
                        )
                }
            } else {
                if (orderList.couponUse == true) {
                    itemView.tvDeliveryReward.text =
                        "Traveler Reward " + "${mContext.getString(R.string.currency_sign)} " + String.format(
                            "%.2f",
                            acceptedUserTotalPrice
                        )
                    itemView.tvPrice.text =
                        mContext.getString(R.string.currency_sign) + " " + String.format(
                            "%.2f",
                            (h2dFee + stripefee + acceptedUserTotalPrice) - discount
                        )
                } else {
                    itemView.tvPrice.text =
                        mContext.getString(R.string.currency_sign) + " " + String.format(
                            "%.2f",
                            h2dFee + stripefee + acceptedUserTotalPrice
                        )
                    itemView.tvDeliveryReward.text =
                        "Traveler Reward " + "${mContext.getString(R.string.currency_sign)} " + String.format(
                            "%.2f",
                            acceptedUserTotalPrice
                        )
                }
            }

            if(orderList.couponUse == true){
                tvPrice?.text = context.getString(R.string.currency_sign) + " " + (String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        (orderList.totalCheckout?.minus(orderList.discount))
                )) + " USD"
            } else {
                tvPrice?.text = context.getString(R.string.currency_sign) + " " + (String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        orderList?.totalCheckout
                )) + " USD"
            }

            Glide.with(mContext).load(orderList.userId?.profilePicURL?.original)
                .apply(RequestOptions().circleCrop())
                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                .into(itemView.ivUserImage)

            when (orderList.orderType) {
                RequestType.TRANSPORT -> {
                    //itemView.tvOderType.text = mContext.getString(R.string.transport)
                    Glide.with(mContext).load(orderList.itemImages?.get(0)?.original)
                        .into(itemView.ivImage)
                    itemView.tvItemName.text = orderList.itemName
                }
                RequestType.PARCEL -> {
                    //itemView.tvOderType.text = mContext.getString(R.string.parcel)
                    if ((orderList.outerParcelImagesURL?.size ?: 0) > 0) {
                        Glide.with(mContext).load(orderList.outerParcelImagesURL?.get(0)?.original)
                            .into(itemView.ivImage)
                    }
                    itemView.tvItemName.text =
                        """${orderList.dimensionArray?.get(0)?.weight} kg · (${
                            orderList.dimensionArray?.get(0)?.length
                        } * ${orderList.dimensionArray?.get(0)?.width} * ${
                            orderList.dimensionArray?.get(
                                0
                            )?.height
                        }) cm"""

                }

                RequestType.GROCERIES -> {
                    //itemView.tvOderType.text = context?.getString(R.string.groceries)
                    itemView.tvItemName.text =
                        "${orderList.groceryItems.size} ${context?.getString(R.string.items)} ${
                            mContext.getString(R.string.from)
                        } ${orderList.storeDetails.size} ${mContext.getString(R.string.store)}"
                }
                RequestType.SHIPMENT -> {
                    if ((orderList.shipItemImages?.size ?: 0) > 0) {
                        Glide.with(mContext).load(orderList.shipItemImages?.get(0)?.original)
                            .into(itemView.ivImage)
                    }


                    when (orderList.type) {

                        "Delivery" -> {
                            itemView.tvTypeValue.text = context.getString(R.string.delivery)
                        }
                        "Pickup" -> {
                            itemView.tvTypeValue.text = context.getString(R.string.pickup)
                        }
                        "Shop" -> {
                            itemView.tvTypeValue.text = context.getString(R.string.shop)
                        }
                    }

                    //itemView.tvOderType.text = context?.getString(R.string.ship)
                    itemView.tvItemName.text = orderList.itemName
                    /*ratingBarz.rating= orderList.offers?.get(adapterPosition)?.averageRating?.toFloat()!!
                    tvRatingCountz.text = "("+orderList.offers?.get(adapterPosition)?.totalRating+")"*/
                }
            }
            if (orderList.offers?.isNotEmpty() == true) {
                itemView.tvUserName.text = orderList.accepted?.fullName
                /* Glide.with(mContext).load(orderList.accepted?.profilePicURL?.original)
                     .apply(RequestOptions.circleCropTransform())

                     .into(itemView.ivProfile).toString()*/
                Glide.with(mContext).load(orderList.accepted?.profilePicURL?.original)
                    .apply(
                        RequestOptions.circleCropTransform()
                            .placeholder(R.drawable.profile_pic_placeholder)
                    )
                    .into(itemView.ivUserImage)
            }

        }
    }

    private fun popupRequested(cntxt: Context, ivOptions: ImageView, adapterPosition: Int) {
        val listGen = ArrayList<String>()
        listGen.add(mContext.getString(R.string.cancel))
        listGen.add(mContext.getString(R.string.help))
        val popup = ListPopupWindow(cntxt)
        popup.setAdapter(
            ArrayAdapter(
                cntxt,
                android.R.layout.simple_spinner_dropdown_item,
                listGen
            )
        )
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.isModal = true
        popup.anchorView = ivOptions
        popup.width = 250
        popup.horizontalOffset = 4
        popup.setDropDownGravity(Gravity.END)

        popup.setOnItemClickListener { adapterView, view, position, id ->
            if (listGen.get(position).equals(mContext.getString(R.string.cancel))) {
                /* AlertDialogUtil.getInstance().createOkCancelDialog(mContext, R.string.confirm, R.string.cancel_order_confirmation,
                         R.string.yes, R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                     override fun onOkButtonClicked() {
                         showToast(mContext,mContext.getString(R.string.contact_message))
                         popup.dismiss()
                     }

                     override fun onCancelButtonClicked() {
                         // dismiss
                     }

                 }).show()*/
                showToast(mContext, mContext.getString(R.string.contact_message))
                popup.dismiss()

            } else {
                mContext.startActivity(
                    Intent(mContext, SupportOrderActivity::class.java).putExtra(
                        "name",
                        orderList.get(adapterPosition).itemName
                    )
                        .putExtra("showId", orderList.get(adapterPosition).orderId)
                        .putExtra("desc", orderList.get(adapterPosition).description)
                        .putExtra("orderId", orderList.get(adapterPosition)._id)
                )
                popup.dismiss()
            }

        }
        popup.show()
    }

    interface selectedOrderId {
        fun selectedOrderId(orderId: String?, position: Int)
    }

    fun getSelectedPosition(): Int {
        return selectedPostion
    }

}