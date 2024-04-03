package com.capcorp.ui.driver.homescreen.mydeliveries.deliveries

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.detail_offer_accepeted.home_delivery.HomeDeliveryDetailDriverActivity
import com.capcorp.ui.driver.homescreen.detail_offer_completed.detail_delivery.HomeCompleteDriverActivity
import com.capcorp.ui.driver.homescreen.home.requests.RequestsAdapter
import com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_made.OfferMadeActivity
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfileActivity
import com.capcorp.ui.settings.profile.support.SupportOrderActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_my_deliveries.view.*
import kotlinx.android.synthetic.main.item_my_deliveries.view.ivImage
import kotlinx.android.synthetic.main.item_my_deliveries.view.tvDate
import kotlinx.android.synthetic.main.item_my_deliveries.view.tvDestination
import kotlinx.android.synthetic.main.item_my_deliveries.view.tvItemName
import kotlinx.android.synthetic.main.item_my_deliveries.view.tvOrderCode
import kotlinx.android.synthetic.main.item_my_deliveries.view.tvPrice
import kotlinx.android.synthetic.main.item_my_deliveries.view.tvRequiredBy
import kotlinx.android.synthetic.main.item_my_deliveries.view.tvSource
import kotlinx.android.synthetic.main.item_requests.view.*
import java.util.*


class DeliveriesAdapter(
    private var context: Context?,
    private var orderListing: ArrayList<OrderListing>,
    val orderStatus: String?,
    val deliveriesFragment: DeliveriesFragment,
    private val insuranceOfferCallback: RequestsAdapter.InsuranceOfferCallback,
    private val deliveryInterface: DeliveriesInterface
) : RecyclerView.Adapter<DeliveriesAdapter.ViewHolder>() {

    private var userType: String

    init {
        userType = SharedPrefs.with(context).getString(USER_TYPE, "")

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveriesAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_my_deliveries, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DeliveriesAdapter.ViewHolder, position: Int) {
        holder.bind(orderListing[position])
    }

    override fun getItemCount(): Int {
        return orderListing.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

            itemView.ivUserImage.setOnClickListener {
                context?.startActivity(
                    Intent(context, OtherUserProfileActivity::class.java)
                        .putExtra(USER_ID, orderListing.get(adapterPosition).userId?._id)
                        .putExtra(USERTYPE, UserType.USER)
                )
            }

            /*itemView.btnChangeMyOffer.setOnClickListener {
                if (CheckNetworkConnection.isOnline(context)) {
                    if (orderListing.get(adapterPosition).type == OrderType.SHOP) {
                        *//*var messageOrder = context?.getString(R.string.order_is_insured_shop)
                        var messageOrder1 = context?.getString(R.string.order_is_not_insured_shop)
                        val builder = AlertDialog.Builder(context)
                        if (orderListing.get(adapterPosition).insurance.equals("true")) {
                            builder.setTitle(context?.getString(R.string.insured_order))
                        } else {
                            builder.setTitle(context?.getString(R.string.shop_order))
                        }
                        builder.setCancelable(true)
                        if (orderListing.get(adapterPosition).insurance.equals("true")) {
                            builder.setMessage(messageOrder)
                        } else {
                            builder.setMessage(messageOrder1)
                        }
                        builder.setPositiveButton(context?.getString(R.string.ok)) { dialog, which ->
                            *//**//* insuranceOfferCallback.insuranceOfferCallback(orderListing[adapterPosition]._id, orderListing.get(adapterPosition).recommendedReward, adapterPosition, orderListing[adapterPosition].payment.toString(), orderListing.get(adapterPosition).pickUpDate.toString(), "true")
                             deliveriesFragment.startActivityForResult(Intent(context, CardListActivity::class.java)
                                     .putExtra("from", "no"), 406)*//**//*

                        }*//*

                        deliveriesFragment.startActivity(
                            Intent(context, MakeAnOfferActivity::class.java)
                                .putExtra(ORDER_ID, orderListing[adapterPosition]._id)
                                .putExtra(
                                    "itemPrice",
                                    orderListing.get(adapterPosition).recommendedReward
                                )
                                .putExtra("cardId", "")
                                .putExtra(POSITION, adapterPosition)
                        )

*//*
                        builder.show()
*//*
                    } else {
                        if (orderListing.get(adapterPosition).insurance.equals("true")) {
                            *//* var amount: Double = (orderListing.get(adapterPosition).itemPrice
                                     ?: "0.0").toDouble() *
                                     (orderListing.get(adapterPosition).itemQuantity
                                             ?: "0.0").toDouble()
                             var messageOrder = context?.getString(R.string.order_has_insurance_policy) + " $ " + String.format("%.1f", amount) + " " + context?.getString(R.string.order_has_insurance)
                             val builder = AlertDialog.Builder(context)
                             builder.setTitle(context?.getString(R.string.payment_required))
                             builder.setCancelable(true)
                             builder.setMessage(messageOrder)
                             builder.setPositiveButton(context?.getString(R.string.add_card_policy)) { dialog, which ->

                             }
                             builder.show()*//*

                            insuranceOfferCallback.insuranceOfferCallback(
                                orderListing[adapterPosition]._id,
                                orderListing.get(adapterPosition).recommendedReward,
                                adapterPosition,
                                orderListing[adapterPosition].payment.toString(),
                                orderListing.get(adapterPosition).pickUpDate.toString(),
                                "true"
                            )
                            deliveriesFragment.startActivityForResult(
                                Intent(context, CardListActivity::class.java)
                                    .putExtra("from", "driver"), 406
                            )

                        } else {
                            deliveriesFragment.startActivity(
                                Intent(context, MakeAnOfferActivity::class.java)
                                    .putExtra(ORDER_ID, orderListing[adapterPosition]._id)
                                    .putExtra(
                                        "itemPrice",
                                        orderListing.get(adapterPosition).recommendedReward
                                    )
                                    .putExtra("cardId", "")
                                    .putExtra(POSITION, adapterPosition)
                            )
                        }
                    }
                } else {
                    CheckNetworkConnection.showNetworkError(itemView)
                }
            }*/
            itemView.cvMain.setOnClickListener {
                when (orderStatus) {
                    OrderStatus.REQUESTED -> {
                        deliveriesFragment.startActivityForResult(
                            Intent(context, OfferMadeActivity::class.java)
                                .putExtra(
                                    REQUEST_DETAIL,
                                    Gson().toJson(orderListing.get(adapterPosition))
                                )
                                .putExtra(POSITION, adapterPosition)
                                .putExtra(TYPE_OFFER_MADE, false),
                            deliveriesFragment.RQ_CODE_MAKE_OFFER
                        )
                    }
                    OrderStatus.ACCEPTED -> {
                        if (orderListing.size > 0) {
                            if (orderListing.get(adapterPosition).type.equals("Delivery")) {
                                deliveriesFragment.startActivityForResult(
                                    Intent(context, HomeDeliveryDetailDriverActivity::class.java)
                                        .putExtra(POSITION, adapterPosition)
                                        .putExtra(
                                            ACCEPT_DETAIL,
                                            Gson().toJson(orderListing.get(adapterPosition))
                                        )
                                        .putExtra(POSITION, adapterPosition),
                                    145
                                )
                            } else if (orderListing.get(adapterPosition).type.equals("Pickup")) {
                                deliveriesFragment.startActivityForResult(
                                    Intent(context, HomeDeliveryDetailDriverActivity::class.java)
                                        .putExtra(POSITION, adapterPosition)
                                        .putExtra(
                                            ACCEPT_DETAIL,
                                            Gson().toJson(orderListing.get(adapterPosition))
                                        )
                                        .putExtra(POSITION, adapterPosition),
                                    145
                                )
                            } else if (orderListing.get(adapterPosition).type.equals("Shop")) {
                                deliveriesFragment.startActivityForResult(
                                    Intent(context, HomeDeliveryDetailDriverActivity::class.java)
                                        .putExtra(POSITION, adapterPosition)
                                        .putExtra(
                                            ACCEPT_DETAIL,
                                            Gson().toJson(orderListing.get(adapterPosition))
                                        )
                                        .putExtra(POSITION, adapterPosition),
                                    145
                                )
                            }
                        }

                    }
                    OrderStatus.COMPLETED -> {
                        if (orderListing.size > 0) {
                            if (orderListing.get(adapterPosition).type.equals("Delivery")) {
                                deliveriesFragment.startActivityForResult(
                                    Intent(context, HomeCompleteDriverActivity::class.java)
                                        .putExtra(
                                            COMPLETED_DETAIL,
                                            Gson().toJson(orderListing.get(adapterPosition))
                                        )
                                        .putExtra(POSITION, adapterPosition)
                                        .putExtra("fromCompletedDetail", true), 400
                                )
                            } else if (orderListing.get(adapterPosition).type.equals("Pickup")) {
                                deliveriesFragment.startActivityForResult(
                                    Intent(context, HomeCompleteDriverActivity::class.java)
                                        .putExtra(
                                            COMPLETED_DETAIL,
                                            Gson().toJson(orderListing.get(adapterPosition))
                                        )
                                        .putExtra(POSITION, adapterPosition)
                                        .putExtra("fromCompletedDetail", true), 400
                                )
                            } else if (orderListing.get(adapterPosition).type.equals("Shop")) {
                                deliveriesFragment.startActivityForResult(
                                    Intent(context, HomeCompleteDriverActivity::class.java)
                                        .putExtra(
                                            COMPLETED_DETAIL,
                                            Gson().toJson(orderListing.get(adapterPosition))
                                        )
                                        .putExtra(POSITION, adapterPosition)
                                        .putExtra("fromCompletedDetail", true), 400
                                )
                            }
                        }
                    }
                }


            }
        }

        fun bind(order: OrderListing) {
            itemView.tvOrderCode.text = order?.orderId

            val spannableStrBuilder = SpannableStringBuilder()
            when (orderStatus) {
                OrderStatus.REQUESTED -> {

                    itemView.tvRequiredBy.text = context?.getString(R.string.by) + " " +
                        orderListing.get(adapterPosition).pickUpDate?.let {
                            getOnlyDate(
                                it,
                                "EEE · MMM d"
                            )
                        }
                    itemView.clProfile.visibility = View.GONE
                    //  itemView.ivUserImage.visibility = View.GONE
                    //  itemView.rlBottom.visibility = View.VISIBLE
                    /* order?.offers?.forEach {
                         if (it._id.equals(SharedPrefs.with(context).getString(USER_ID, ""))) {
                             itemView.tvAmount.text = """${context?.getString(R.string.currency_sign)}${it.price}"""
                         }
                     }*/
                    for (i in order?.offers!!.indices) {
                        itemView.tvPrice.text =
                            """${context?.getString(R.string.currency_sign)}${order.offers!!.get(i).totalPrice}"""
                    }

//                    val spannableString = SpannableString(context?.getString(R.string.you))
//                    spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                    spannableStrBuilder.append(spannableString).append(" ")
//                            .append(context?.getString(R.string.made_an_offer))
                    itemView.tvUserName.text = order.userId?.firstName
                }
                OrderStatus.ACCEPTED -> {
                    itemView.clProfile.visibility = View.VISIBLE
                    itemView.tvRequiredBy.text = context?.getString(R.string.by) + " " +
                        orderListing.get(adapterPosition).deliveredDate?.let {
                            getOnlyDate(
                                it,
                                "EEE · MMM d"
                            )
                        }
                    // itemView.ivProfileHome.visibility = View.VISIBLE
                    context?.let {
                        Glide.with(it).load(order?.userId?.profilePicURL?.original)
                            .apply(
                                RequestOptions.circleCropTransform()
                                    .placeholder(R.drawable.profile_pic_placeholder)
                            ).into(itemView.ivUserImage)
                    }
                    // itemView.rlBottom.visibility = View.GONE
                    val spannableString = SpannableString(order?.userId?.firstName)
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableStrBuilder.append(spannableString).append(" ")
                        .append(context?.getString(R.string.accepted_your_offer))
                    itemView.tvUserName.text = order?.userId?.firstName
                    var itemPrice = 0.0
                    if(order.itemPrice.isNotEmpty() && order.itemQuantity.isNotEmpty()){
                        itemPrice = order.itemPrice.toDouble() * order.itemQuantity.toDouble()
                    }
                    var h2dFee = 0.0
                    if(order.h2dFee != null && order.h2dFee.isNotEmpty()){
                        h2dFee =  order.h2dFee.toDouble()
                    }
                    var stripefee = 0.0
                    if(!order.stripeFee.isNullOrEmpty()){
                        stripefee = order.stripeFee?.toDouble()!!
                    }
                    var tax = 0.0
                    if(order.tax.isNotEmpty()){
                        tax = order.tax.toDouble()
                    }
                    var payment = 0.0
                    if(order.tax.isNotEmpty()){
                        payment = order.payment
                    }
                    var acceptedUserTotalPrice = 0.0
                    if(order.accepted?.totalPrice?.isNotEmpty() == true){
                        acceptedUserTotalPrice = order.accepted?.totalPrice!!.toDouble()
                    }
                    var discount = 0.0
                    if(order.tax.isNotEmpty()){
                        discount = order.discount
                    }

                    if(order.type == OrderType.SHOP) {
                        itemView.tvPrice.text = context?.getString(R.string.currency_sign) + " " + String.format("%.2f",acceptedUserTotalPrice)
                    }else{
                        itemView.tvPrice.text = context?.getString(R.string.currency_sign) + " " + String.format("%.2f",itemPrice + tax)

                    }
                    if(order.type == OrderType.SHOP) {
                        itemView.tvDeliveryReward.text =
                            "Total Investment " + context?.getString(R.string.currency_sign) + " " + String.format("%.2f",itemPrice + tax +h2dFee + stripefee)
                    }else{
                     if(order.couponUse == true){
                         itemView.tvDeliveryReward.text =
                             "Total Investment " + context?.getString(R.string.currency_sign) + " " + String.format("%.2f",h2dFee + stripefee + acceptedUserTotalPrice - discount)
                     }else{
                         itemView.tvDeliveryReward.text =
                             "Total Investment " + context?.getString(R.string.currency_sign) + " " + String.format("%.2f",h2dFee + stripefee + acceptedUserTotalPrice)
                     }
                    }

                }
                OrderStatus.COMPLETED -> {

                    itemView.clProfile.visibility = View.VISIBLE
                    itemView.tvRequiredBy.text = context?.getString(R.string.by) + " " +
                        orderListing.get(adapterPosition).deliveredDate?.let {
                            getOnlyDate(
                                it,
                                "EEE · MMM d"
                            )
                        }
                    //itemView.ivProfileHome.visibility = View.VISIBLE
                    context?.let {
                        Glide.with(it).load(order?.userId?.profilePicURL?.original)
                            .apply(
                                RequestOptions.circleCropTransform()
                                    .placeholder(R.drawable.profile_pic_placeholder)
                            ).into(itemView.ivUserImage)
                    }
                    // itemView.rlBottom.visibility = View.GONE
                    val spannableString = SpannableString(order?.userId?.firstName)
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableStrBuilder.append(spannableString).append(" ")
                        .append(context?.getString(R.string.accepted_your_offer))
                    itemView.tvUserName.text = order?.userId?.firstName
                    var itemPrice = 0.0
                    if(order.itemPrice.isNotEmpty() && order.itemQuantity.isNotEmpty()){
                        itemPrice = order.itemPrice.toDouble() * order.itemQuantity.toDouble()
                    }
                    var h2dFee = 0.0
                    if(order.h2dFee != null && order.h2dFee.isNotEmpty()){
                        h2dFee =  order.h2dFee.toDouble()
                    }
                    var stripefee = 0.0
                    if(!order.stripeFee.isNullOrEmpty()){
                        stripefee = order.stripeFee?.toDouble()!!
                    }
                    var tax = 0.0
                    if(order.tax.isNotEmpty()){
                        tax = order.tax.toDouble()
                    }
                    var payment = 0.0
                    if(order.tax.isNotEmpty()){
                        payment = order.payment
                    }
                    var acceptedUserTotalPrice = 0.0
                    if(order.accepted?.totalPrice?.isNotEmpty() == true){
                        acceptedUserTotalPrice = order.accepted?.totalPrice!!.toDouble()
                    }
                    var discount = 0.0
                    if(order.tax.isNotEmpty()){
                        discount = order.discount
                    }


                   if(order.type == OrderType.SHOP) {
                       itemView.tvPrice.text = context?.getString(R.string.currency_sign) + " " + String.format("%.2f",acceptedUserTotalPrice)
                   }else{
                       itemView.tvPrice.text = context?.getString(R.string.currency_sign) + " " + String.format("%.2f",itemPrice + tax)
                   }

                    if(order.type == OrderType.SHOP) {
                        itemView.tvDeliveryReward.text =
                            context?.getString(R.string.currency_sign) + " " + String.format("%.2f",itemPrice + tax + h2dFee + stripefee)
                    }else{
                            if(order.couponUse == true){
                                itemView.tvDeliveryReward.text =  "Total Investment " +
                                context?.getString(R.string.currency_sign) + " " + String.format("%.2f",h2dFee + stripefee + acceptedUserTotalPrice - discount)
                            }else{
                                itemView.tvDeliveryReward.text =  "Total Investment " +
                                        context?.getString(R.string.currency_sign) + " " + String.format("%.2f",h2dFee + stripefee + acceptedUserTotalPrice)
                            }
                    }
                }
            }



            itemView.tvDate.text =
                order?.createdDate?.let { getOnlyDate(it, "MMM dd yyyy") }
            itemView.tvSource.text = order?.pickUpAddress
            itemView.tvDestination.text = order?.dropDownAddress
//            if (order?.orderType.equals(RequestType.GROCERIES)) {
//                itemView.cardView.visibility = View.GONE
//            } else {
//                itemView.cardView.visibility = View.VISIBLE
//            }
            when (order?.orderType) {
                RequestType.TRANSPORT -> {
                   // itemView.tvTypeValue.text = context?.getString(R.string.transport)
                    itemView.tvItemName.text = order.type
                    context?.let {
                        Glide.with(it).load(order.itemImages?.get(0)?.original)
                            .into(itemView.ivImage)
                    }
                }
                RequestType.PARCEL -> {
                    if (order.outerParcelImagesURL?.size ?: 0 > 0) {
                        context?.let {
                            Glide.with(it).load(order.outerParcelImagesURL?.get(0)?.original)
                                .into(itemView.ivImage)
                        }
                    }
                   // itemView.tvTypeValue.text = context?.getString(R.string.parcel)
                    itemView.tvItemName.text = """${order.dimensionArray?.get(0)?.weight} kg · (${
                        order.dimensionArray?.get(0)?.length
                    } x ${order.dimensionArray?.get(0)?.width} x ${order.dimensionArray?.get(0)?.height} ) cm"""
                }
                RequestType.GROCERIES -> {
                   // itemView.tvTypeValue.text = context?.getString(R.string.groceries)
                    itemView.tvItemName.text =
                        "${order.groceryItems.size} ${context?.getString(R.string.items)}"
                    itemView.tvItemName.text =
                        "${order.groceryItems.size} ${context?.getString(R.string.items)} ${
                            context?.getString(R.string.from)
                        } ${order.storeDetails.size} ${context?.getString(R.string.store)}"

                }
                RequestType.SHIPMENT -> {
                    if (order.shipItemImages?.size ?: 0 > 0) {
                        context?.let {
                            Glide.with(it).load(order.shipItemImages?.get(0)?.original)
                                .into(itemView.ivImage)
                        }
                    }
                   // itemView.tvTypeValue.text = context?.getString(R.string.ship)

                    when (orderListing[adapterPosition].type) {

                        "Delivery" -> {
                            itemView.tvTypeValue.text = context?.getString(R.string.delivery)
                        }
                        "Pickup" -> {
                            itemView.tvTypeValue.text = context?.getString(R.string.pickup)
                        }
                        "Shop" -> {
                            itemView.tvTypeValue.text = context?.getString(R.string.shop)
                        }
                    }


                    //   =
                    itemView.tvItemName.text = orderListing.get(adapterPosition).itemName

                }
            }


//            itemView.iv_order_operation.setOnClickListener {
//                popupRequested(context!!, itemView.iv_order_operation, adapterPosition)
//            }


        }
    }

    private fun popupRequested(mContext: Context, ivOptions: ImageView, adapterPosition: Int) {
        val listGen = ArrayList<String>()
        listGen.add(mContext.getString(R.string.cancel))
        listGen.add(mContext.getString(R.string.help))
        val popup = ListPopupWindow(mContext)
        popup.setAdapter(
            ArrayAdapter(
                mContext,
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

                //if(orderListing.get(adapterPosition).insurance.toBoolean() && orderListing.get(adapterPosition).type.equals("Shop") && userType.equals(UserType.DRIVER))
                if (orderStatus == "ACCEPTED") {
                    if (orderListing[adapterPosition].insurance && orderListing[adapterPosition].type.equals(
                            "Shop"
                        ) && userType == UserType.DRIVER
                    ) {
                        openCancelOrderApi(context!!, adapterPosition)
                    } else {
                        showToast(context, context?.getString(R.string.cancel_msg))

                        popup.dismiss()
                    }

                } else {
                    if (orderListing[adapterPosition].type.equals("Shop") && userType == UserType.DRIVER)
                        openCancelOrderApi(context!!, adapterPosition)
                    else
                        showToast(context, context?.getString(R.string.cancel_msg))

                    popup.dismiss()
                }


            } else {
                mContext.startActivity(
                    Intent(mContext, SupportOrderActivity::class.java).putExtra(
                        "name",
                        orderListing[adapterPosition].itemName
                    )
                        .putExtra("showId", orderListing[adapterPosition].orderId)
                        .putExtra("desc", orderListing[adapterPosition].description)
                        .putExtra("orderId", orderListing[adapterPosition]._id)
                )
                popup.dismiss()
            }

        }
        popup.show()
    }

    fun openCancelOrderApi(context: Context, adapterPosition: Int) {
        var dialogAlert: androidx.appcompat.app.AlertDialog? = null

        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        val alertLayout = LayoutInflater.from(context).inflate(R.layout.layout_cancel_order, null)

        val radioGroup = alertLayout.findViewById(R.id.radio_group) as RadioGroup
        val tvCancelOrder = alertLayout.findViewById(R.id.tv_cancel_orders) as TextView

        var mReason: String = ""

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radio_not_find) {
                mReason = context.getString(R.string.i_could_not_find)
            } else if (checkedId == R.id.radio_price_not_cotrrect) {
                mReason = context.getString(R.string.product_price_not_correct)
            } else if (checkedId == R.id.radio_shopper_not_responding) {
                mReason = context.getString(R.string.shopper_not_responding)
            } else {
                mReason = context.getString(R.string.delivery_place_issue)
            }
        }

        tvCancelOrder.setOnClickListener {
            if (mReason.isEmpty()) {
                showToast(context, context.getString(R.string.please_select_reason))
            } else {

                /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                      if (managePermissions!!.checkPermissions()) {
                          presenter.makeOffersAndAcceptOrderApiCall(getAuthAccessToken(this), orderListingDetail?._id, orderListingDetail?.payment.toString(), "REJECT", currentLocation.get(1).toDouble(), currentLocation.get(0).toDouble(), orderListingDetail?.accepted?.driverArrivalDate.toString(), mReason)
                          dialogAlert?.dismiss()
                      }
                  } else {
                      presenter.makeOffersAndAcceptOrderApiCall(getAuthAccessToken(this), orderListingDetail?._id, orderListingDetail?.payment.toString(), "REJECT", currentLocation.get(1).toDouble(), currentLocation.get(0).toDouble(), orderListingDetail?.accepted?.driverArrivalDate.toString(), mReason)
                      dialogAlert?.dismiss()
                  }*/

                val orderItem = orderListing.get(adapterPosition)
                deliveryInterface.onCanceled(
                    mReason,
                    orderItem._id.toString(),
                    orderItem.accepted?.totalPrice.toString(),
                    orderItem.arrivedLatLong?.get(1)!!.toDouble(),
                    orderItem.arrivedLatLong?.get(0)!!.toDouble(),
                    orderItem.accepted?.driverArrivalDate.toString()
                )
                dialogAlert?.dismiss()

            }
        }

        builder.setView(alertLayout)
        builder.setCancelable(true)
        dialogAlert = builder.create()
        dialogAlert.setCanceledOnTouchOutside(true)
        dialogAlert.window?.setGravity(Gravity.BOTTOM)
        dialogAlert.show()
    }


    interface InsuranceOfferCallback {
        fun insuranceOfferCallback(
            orderId: String?,
            recommendedReward: String,
            pos: Int,
            price: String?,
            deliverDate: String,
            offerType: String
        )
    }
}