package com.capcorp.ui.user.homescreen.orders.requested

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
import com.capcorp.ui.settings.profile.profile.ProfileActivity
import com.capcorp.ui.user.homescreen.orders.RequestBeforeDetailActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_detail_new.*
import kotlinx.android.synthetic.main.item_order_completed_new.view.*
import kotlinx.android.synthetic.main.item_order_recieved.view.*
import kotlinx.android.synthetic.main.item_order_recieved.view.ivImage
import kotlinx.android.synthetic.main.item_order_recieved.view.tvDate
import kotlinx.android.synthetic.main.item_order_recieved.view.tvDestination
import kotlinx.android.synthetic.main.item_order_recieved.view.tvItemName
import kotlinx.android.synthetic.main.item_order_recieved.view.tvOfferCount
import kotlinx.android.synthetic.main.item_order_recieved.view.tvOrderCode
import kotlinx.android.synthetic.main.item_order_recieved.view.tvPrice
import kotlinx.android.synthetic.main.item_order_recieved.view.tvRequiredBy
import kotlinx.android.synthetic.main.item_order_recieved.view.tvSource
import kotlinx.android.synthetic.main.item_order_recieved.view.tvTypeValue
import kotlinx.android.synthetic.main.item_requests.view.*
import java.util.*
import kotlin.collections.ArrayList


class RequestedAdapter(
    private val mContext: Context,
    private val SelectedOrderId: selectedOrderId,
    private val orderList: ArrayList<OrderListing>,
    private val requestedFragment: RequestedFragment
) : RecyclerView.Adapter<RequestedAdapter.ViewHolder>() {

    private var selectedPosition = -1
    private var userDetail: SignupModel? = null
    private var isItemClicked = 0

    init {
        userDetail = SharedPrefs.with(mContext).getObject(USER_DATA, SignupModel::class.java)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_order_recieved, parent, false)
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

        }

        init {
            itemView.ivImage.setOnClickListener {
                mContext.startActivity(Intent(mContext, ProfileActivity::class.java))
            }

            itemView.setOnClickListener {
                selectedPosition = adapterPosition
                requestedFragment.startActivityForResult(
                    Intent(mContext, RequestBeforeDetailActivity::class.java)
                        .putExtra(REQUEST_DETAIL, Gson().toJson(orderList[adapterPosition]))
                        .putExtra("from_request", "true"), REQUESTED_RESULT_CODE
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
                        requestedFragment.presenter.deleteOrder(getAuthAccessToken(mContext),
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
        }

        @SuppressLint("SetTextI18n")
        fun bind(orderList: OrderListing) = with(itemView) {
            Glide.with(mContext).load(userDetail?.profilePicURL?.original)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.ivImage)

            itemView.tvSource.text = orderList.pickUpAddress
            itemView.tvDestination.text = orderList.dropDownAddress
            itemView.tvOrderCode.text = orderList.orderId

            if ((orderList.offers?.size ?: 0) > 1) {
                itemView.tvOfferCount.text =
                    orderList.offers?.size.toString() + " " + mContext.getString(R.string.offers)
            } else {
                itemView.tvOfferCount.text =
                    orderList.offers?.size.toString() + " " + mContext.getString(R.string.offer)
            }
            itemView.tvRequiredBy.text = context.getString(R.string.by) + " " +
                orderList.pickUpDate?.let { getOnlyDate(it, "EEE · MMM d") }
            itemView.tvDate.text =
                orderList.createdDate?.let { getOnlyDate(it,"MMM dd yyyy") }
            when (orderList.orderType) {
                RequestType.TRANSPORT -> {
                    Glide.with(mContext).load(orderList.itemImages?.get(0)?.original)
                        .into(itemView.ivImage)
                    itemView.tvItemName.text = orderList.type
                }
                RequestType.PARCEL -> {
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
                    itemView.tvItemName.text = orderList.itemName
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


                }

            }
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
                val price = h2dFee + stripefee + tax + itemPrice + payment
                itemView.tvPrice.text = context.getString(R.string.dollar) + " " + String.format("%.2f",price)
            }else {
                val price = h2dFee + stripefee + payment
                itemView.tvPrice.text = context.getString(R.string.dollar) + " " + String.format("%.2f",price)
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
        }
    }

    interface selectedOrderId {
        fun selectedOrderId(orderId: String?, position: Int)
    }
}
