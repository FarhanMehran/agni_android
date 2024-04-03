package com.capcorp.ui.user.homescreen.orders.expired

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfileActivity
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_detail_new.*
import kotlinx.android.synthetic.main.item_order_completed_new.view.*
import kotlinx.android.synthetic.main.item_order_completed_new.view.ivImage
import kotlinx.android.synthetic.main.item_order_completed_new.view.tvDate
import kotlinx.android.synthetic.main.item_order_completed_new.view.tvDestination
import kotlinx.android.synthetic.main.item_order_completed_new.view.tvItemName
import kotlinx.android.synthetic.main.item_order_completed_new.view.tvOrderCode
import kotlinx.android.synthetic.main.item_order_completed_new.view.tvPrice
import kotlinx.android.synthetic.main.item_order_completed_new.view.tvSource
import kotlinx.android.synthetic.main.item_order_completed_new.view.tvTypeValue
import java.util.*
import kotlin.collections.ArrayList


class ExpiredAdapter(
    private val mContext: Context,
    private val SelectedOrderId: ExpiredAdapter.selectedOrderId,
    private val orderList: ArrayList<OrderListing>,
    private val expiredFragment: ExpiredFragment
) : RecyclerView.Adapter<ExpiredAdapter.ViewHolder>() {

    private var selectedPosition = -1
    private var userDetail: SignupModel? = null

    init {
        userDetail = SharedPrefs.with(mContext).getObject(USER_DATA, SignupModel::class.java)

    }

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        override fun onClick(v: View?) {
            repubishConfirmation(adapterPosition)
        }

        init {
            itemView.btnRepublish.setOnClickListener(this)
            itemView.ivUserImage.setOnClickListener {
                mContext.startActivity(
                    Intent(mContext, OtherUserProfileActivity::class.java)
                        .putExtra(USER_ID, orderList.get(adapterPosition)._id)
                        .putExtra(USERTYPE, UserType.DRIVER)
                )
            }
            itemView.setOnLongClickListener {
                val alert: AlertDialog.Builder = AlertDialog.Builder(mContext)
                alert.setTitle("Delete entry")
                alert.setMessage("Are you sure you want to delete?")
                alert.setPositiveButton(
                    R.string.yes
                ) { _, _ ->
                    orderList[adapterPosition]._id?.let { it1 ->
                        expiredFragment.presenter.deleteOrder(getAuthAccessToken(mContext),
                            it1,"DELETE")
                    }
                }
                alert.setNegativeButton(
                    R.string.no
                ) { dialog, _ ->
                    dialog.cancel()

                }
                alert.show()
                return@setOnLongClickListener true
            }
//            itemView.ivInfo.setOnClickListener {
//                var price = ""
//                price = """${mContext.getString(R.string.currency_sign)} ${orderList[adapterPosition].payment}"""
//                val message = "${"Shopper+Traveller Delivery Reward"} :${price}}"
//
//                ViewTooltip.on(expiredFragment.requireActivity(), itemView.ivInfo)
//                        .autoHide(true, 3000)
//                        .corner(30)
//                        .text(message)
//                        .position(ViewTooltip.Position.RIGHT)
//                        .show()
//            }
//            itemView.ivProfile.setOnClickListener {
//                mContext.startActivity(Intent(mContext, ProfileActivity::class.java))
//            }
            itemView.setOnClickListener {
                mContext.startActivity(
                    Intent(mContext, RequestDetailActivity::class.java)
                        .putExtra(REQUEST_DETAIL, Gson().toJson(orderList[adapterPosition]))
                        .putExtra("fromAcceptDetail", true)
                        .putExtra("from_request", "false")
                )
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(orderList: OrderListing) = with(itemView) {
            itemView.tvSource.text = orderList.pickUpAddress
            itemView.tvDestination.text = orderList.dropDownAddress
            itemView.tvOrderCode.text = orderList.orderId
            itemView.tvRequiredBy.text = context.getString(R.string.by) + " " +
                orderList.deliveredDate?.let { getOnlyDate(it, "EEE · MMM d") }
            itemView.tvDate.text =
                orderList.createdDate?.let { getOnlyDate(it,"MMM dd yyyy") }
            var itemPrice = 0.0
            if(orderList.itemPrice.isNotEmpty() && orderList.itemQuantity.isNotEmpty()){
                itemPrice = orderList.itemPrice.toDouble() * orderList.itemQuantity.toDouble()
            }
            var h2dFee = 0.0
            if(orderList.h2dFee.isNotEmpty()){
                h2dFee =  orderList.h2dFee.toDouble()
            }
            var stripefee = 0.0
            if(!orderList.stripeFee.isNullOrEmpty()){
                stripefee = orderList.stripeFee?.toDouble()!!
            }
            var tax = 0.0
            if(orderList.tax.isNotEmpty()){
                tax = orderList.tax.toDouble()
            }
            var payment = 0.0
            if(orderList.tax.isNotEmpty()){
                payment = orderList.payment
            }
            var acceptedUserTotalPrice = 0.0
            if(orderList.accepted?.totalPrice?.isNotEmpty() == true){
                acceptedUserTotalPrice = orderList.accepted?.totalPrice!!.toDouble()
            }
            var discount = 0.0
            if(orderList.tax.isNotEmpty()){
                discount = orderList.discount
            }

            if(orderList.type == OrderType.SHOP){
                if(orderList.couponUse == true){
                    itemView.tvPrice.text =  context.getString(R.string.dollar) + " "+ String.format("%.2f",itemPrice + h2dFee + stripefee + tax + acceptedUserTotalPrice - discount)
                }else {
                    itemView.tvPrice.text =  context.getString(R.string.dollar) + " "+ String.format("%.2f",itemPrice + h2dFee + stripefee + tax + acceptedUserTotalPrice)
                }
            }else{
                itemView.tvPrice.text =  context.getString(R.string.dollar) + " "+ String.format("%.2f",h2dFee + stripefee + acceptedUserTotalPrice)
            }


            if(orderList.couponUse == true){
                itemView.tvPrice?.text = context.getString(R.string.currency_sign) + " " + (String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        (orderList.totalCheckout?.minus(orderList.discount))
                )) + " USD"
            }else{
                itemView.tvPrice?.text = context.getString(R.string.currency_sign) + " " + (String.format(
                        Locale.ENGLISH,
                        "%.2f",
                        orderList.totalCheckout
                )) + " USD"
            }

            Glide.with(mContext).load(orderList.userId?.profilePicURL?.original)
                .apply(
                    RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.profile_pic_placeholder)
                )
                .into(itemView.ivUserImage)
            if (orderList.offers?.size == null) {
                itemView.clProfile.visibility = View.GONE
            } else {
                itemView.clProfile.visibility = View.VISIBLE
            }
            when (orderList.orderType) {
                RequestType.TRANSPORT -> {
                    //itemView.tvOderType.text = mContext.getString(R.string.transport)
                    Glide.with(mContext).load(orderList.itemImages?.get(0)?.original)
                        .into(itemView.ivImage)
                    itemView.tvItemName.text = orderList.type
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
                    itemView.btnRepublish.visibility = View.VISIBLE
                    itemView.tvDate.visibility = View.GONE
                    //itemView.tvRequiredByDates.text = orderList.pickUpDate?.let { getOnlyDate(it, "EEE · MMM d") }
                }
            }
            if (orderList.offers?.isNotEmpty() == true) {
                itemView.tvUserName.text = orderList.offers?.get(0)?.fullName
                Glide.with(mContext).load(orderList.offers?.get(0)?.profilePicURL?.original)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.ivUserImage).toString()
            }
        }
    }


    private fun repubishConfirmation(position: Int) {
        AlertDialogUtil.getInstance()
            .createOkCancelDialog(mContext, R.string.confirm, R.string.republish_order_confirmation,
                R.string.yes, R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                    override fun onOkButtonClicked() {
                        SelectedOrderId.selectedOrderId(orderList.get(position)._id, position)
                    }

                    override fun onCancelButtonClicked() {
                        //  dismiss
                    }

                }).show()
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    interface selectedOrderId {
        fun selectedOrderId(orderId: String?, position: Int)
    }


}