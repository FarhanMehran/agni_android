package com.capcorp.ui.driver.homescreen.home.requests

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.ListPopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.home.makeoffer.MakeAnOfferActivity
import com.capcorp.ui.driver.homescreen.home.makeoffer.request_detail.RequestDetailDriver
import com.capcorp.ui.payment.card_list.CardListActivity
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfileActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.chat.chatmessage.ChatActivity
import com.capcorp.utils.*
import com.capcorp.utils.location.LocationProvider
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_requests.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RequestsAdapter(
    private val context: Activity,
    private val deletedRequestPos: DeletedRequestPosition,
    private val insuranceOfferCallback: InsuranceOfferCallback,
    private val orderListing: ArrayList<OrderListing>, private val fragment: Fragment,
    private val selectedRequestPosition: SelectedRequestPosition,
    private val selectedReportPosition: RequestsAdapter.SelectedReportPosition
) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    //lateinit var locationProvide: LocationProvider
    var sourceLatLong: LatLng? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_requests, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderListing.size
    }

    override fun onBindViewHolder(holder: RequestsAdapter.ViewHolder, position: Int) {
        holder.bind(orderListing[position])

    }

    private fun popupRequested(cntxt: Context, ivOptions: ImageView, adapterPosition: Int) {
        val listGen = ArrayList<String>()
        listGen.add(cntxt.getString(R.string.report))
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

        popup.setOnItemClickListener { adapterView, view, i, l ->
            selectedReportPosition.selectedReportPos(
                orderListing.get(adapterPosition).userId?._id, orderListing.get(adapterPosition)._id
                    ?: "", adapterPosition
            )
            popup.dismiss()
        }
        popup.show()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.btnMakeAnOffer?.setOnClickListener(this)
            //itemView.tvAcceptOffer?.setOnClickListener(this)
            itemView.cvItem?.setOnClickListener(this)
            itemView.cvProfile?.setOnClickListener(this)
            //itemView.btnInfo?.setOnClickListener(this)

          /*  locationProvide = LocationProvider.CurrentLocationBuilder(context).build()
            locationProvide.getLastKnownLocation(OnSuccessListener {

                if (it != null) {
                    sourceLatLong = LatLng(it.latitude, it.longitude)
                    latitude = sourceLatLong!!.latitude
                    longitude = sourceLatLong!!.longitude
                }


            })*/
        }

        override fun onClick(v: View?) {
            when (v?.id) {

                R.id.cvProfile -> {
                    context.startActivity(
                        Intent(context, OtherUserProfileActivity::class.java)
                            .putExtra(USER_ID, orderListing.get(adapterPosition).userId?._id)
                            .putExtra(USERTYPE, UserType.USER)
                    )
                }
                R.id.btnMakeAnOffer -> {

                    //if (orderListing[adapterPosition].isDriverApproved == 1) {

                        if (orderListing[adapterPosition].stripeConnectId != null && !orderListing[adapterPosition].stripeConnectId.equals(
                                ""
                            )
                        ) {

                            if (orderListing[adapterPosition].insurance) {
                                if (CheckNetworkConnection.isOnline(context)) {
                                    if (fragment is ShipmentRequestsFragment) {
                                        if (orderListing.get(adapterPosition).type == OrderType.SHOP) {
                                            val dialog = BottomSheetDialog(
                                                context,
                                                R.style.TransparentDialog
                                            )
                                            dialog.setCanceledOnTouchOutside(true)

                                            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                            val view = context.layoutInflater.inflate(
                                                R.layout.dialog_info,
                                                null
                                            )
                                            val clItemPrice =
                                                view.findViewById<ConstraintLayout>(R.id.clPrice)
                                            val clTax =
                                                view.findViewById<ConstraintLayout>(R.id.clTax)
                                            val clH2dFee =
                                                view.findViewById<ConstraintLayout>(R.id.clH2dFee)
                                            val clProcessingFee =
                                                view.findViewById<ConstraintLayout>(R.id.clProcessingFee)
                                            val clAcceptRequest =
                                                view.findViewById<ConstraintLayout>(R.id.clAcceptRequest)
                                            val clTravelRewards =
                                                view.findViewById<ConstraintLayout>(R.id.clTravelRewards)
                                            val clAcceptRequestShop =
                                                view.findViewById<ConstraintLayout>(R.id.clAcceptRequestShop)

                                            val btnYes = view.findViewById<TextView>(R.id.tvYes)


                                            clAcceptRequest.visibility = View.GONE
                                            clAcceptRequestShop.visibility = View.VISIBLE
                                            clItemPrice.visibility = View.GONE
                                            clTax.visibility = View.GONE
                                            clH2dFee.visibility = View.GONE
                                            clProcessingFee.visibility = View.GONE
                                            clTravelRewards.visibility = View.GONE
                                            dialog.setCancelable(true)
                                            dialog.setContentView(view)

                                            btnYes.setOnClickListener {
                                                fragment.startActivity(
                                                    Intent(context, MakeAnOfferActivity::class.java)
                                                        .putExtra(
                                                            ORDER_ID,
                                                            orderListing[adapterPosition]._id
                                                        )
                                                        .putExtra(
                                                            "itemPrice",
                                                            orderListing.get(adapterPosition).recommendedReward
                                                        )
                                                        .putExtra("cardId", "")
                                                        .putExtra("date", orderListing.get(adapterPosition).pickUpDate)
                                                        .putExtra(POSITION, adapterPosition)
                                                )
                                            }
                                            dialog.show()
                                        } else {

                                            val dialog = BottomSheetDialog(
                                                context,
                                                R.style.TransparentDialog
                                            )
                                            dialog.setCanceledOnTouchOutside(true)

                                            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                            val view = context.layoutInflater.inflate(
                                                R.layout.dialog_info,
                                                null
                                            )
                                            val clItemPrice =
                                                view.findViewById<ConstraintLayout>(R.id.clPrice)
                                            val clTax =
                                                view.findViewById<ConstraintLayout>(R.id.clTax)
                                            val clH2dFee =
                                                view.findViewById<ConstraintLayout>(R.id.clH2dFee)
                                            val clProcessingFee =
                                                view.findViewById<ConstraintLayout>(R.id.clProcessingFee)
                                            val clAcceptRequest =
                                                view.findViewById<ConstraintLayout>(R.id.clAcceptRequest)
                                            val clTravelRewards =
                                                view.findViewById<ConstraintLayout>(R.id.clTravelRewards)
                                            val tvDescription =
                                                view.findViewById<TextView>(R.id.tvDescription)

                                            val btnCancel =
                                                view.findViewById<TextView>(R.id.tvCancel)
                                            val btnAddCard =
                                                view.findViewById<TextView>(R.id.tvAddCard)

                                            val itemPrice =
                                                orderListing[adapterPosition].itemPrice.toDouble()
                                            val taxAmount =
                                                orderListing[adapterPosition].tax.toDouble()
                                            val quantity =
                                                orderListing[adapterPosition].itemQuantity.toInt()
                                            val price = (itemPrice * quantity) + taxAmount
                                            tvDescription.text = context.getString(
                                                R.string.delivery_info,
                                                "$" + price.toString()
                                            )
                                            clAcceptRequest.visibility = View.VISIBLE
                                            clItemPrice.visibility = View.GONE
                                            clTax.visibility = View.GONE
                                            clH2dFee.visibility = View.GONE
                                            clProcessingFee.visibility = View.GONE
                                            clTravelRewards.visibility = View.GONE
                                            dialog.setCancelable(true)
                                            dialog.setContentView(view)

                                            btnCancel.setOnClickListener {
                                                dialog.dismiss()
                                            }
                                            btnAddCard.setOnClickListener {
                                                fragment.startActivity(
                                                    Intent(
                                                        context,
                                                        CardListActivity::class.java
                                                    )
                                                        .putExtra("from", "driver").putExtra(
                                                            "order",
                                                            Gson().toJson(orderListing[adapterPosition])
                                                        )
                                                )
                                            }
                                            dialog.show()
                                        }

                                    }


                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.network_error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                if (orderListing.get(adapterPosition).type == OrderType.SHOP) {
                                    val dialog = Dialog(context,R.style.DialogStyle)
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    dialog.setCancelable(true)
                                    dialog.setContentView(R.layout.alert_dialog_success)
                                    val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                                    val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                                    val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                                    tvTitle.text = context.getString(R.string.app_name)
                                    tvDescription.text = context.getString(R.string.dialog_text)
                                    dialogButton.text = context.getString(R.string.ok)
                                    dialogButton.setOnClickListener {
                                        dialog.dismiss()
                                        fragment.startActivity(
                                            Intent(context, MakeAnOfferActivity::class.java)
                                                .putExtra(
                                                    ORDER_ID,
                                                    orderListing[adapterPosition]._id
                                                )
                                                .putExtra(
                                                    "itemPrice",
                                                    orderListing.get(adapterPosition).recommendedReward
                                                )
                                                .putExtra("cardId", "")
                                                .putExtra("date", orderListing.get(adapterPosition).pickUpDate)
                                                .putExtra(POSITION, adapterPosition)
                                        )
                                    }
                                    dialog.show()
                                } else {
                                    fragment.startActivity(
                                        Intent(context, MakeAnOfferActivity::class.java)
                                            .putExtra(ORDER_ID, orderListing[adapterPosition]._id)
                                            .putExtra(
                                                "itemPrice",
                                                orderListing.get(adapterPosition).recommendedReward
                                            )
                                            .putExtra("cardId", "")
                                            .putExtra("date", orderListing.get(adapterPosition).pickUpDate)
                                            .putExtra(POSITION, adapterPosition)
                                    )
                                }
                            }

                        } else {
                            val dialog = Dialog(context,R.style.DialogStyle)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(true)
                            dialog.setContentView(R.layout.alert_dialog)
                            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                            tvTitle.text = context.getString(R.string.oops)
                            tvDescription.text = context.getString(R.string.stripe_error)
                            dialogButton.text = context.getString(R.string.ok)
                            dialogButton.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.show()
                        }

                   /* } else {
                        val dialog = Dialog(context,R.style.DialogStyle)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(true)
                        dialog.setContentView(R.layout.alert_dialog)
                        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                        tvTitle.text = context.getString(R.string.oops)
                        tvDescription.text = context.getString(R.string.verify_error)
                        dialogButton.text = context.getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }*/


                }
                /* R.id.tvAcceptOffer -> {

                     if (orderListing[adapterPosition].isDriverApproved == 1) {

                         if (orderListing[adapterPosition].stripeConnectId != null) {

                             if (CheckNetworkConnection.isOnline(context)) {
                                 //acceptConfirmation()
                                 if (orderListing.get(adapterPosition).type == OrderType.SHOP) {
                                     var messageOrder = context.getString(R.string.order_is_insured_shop)
                                     var messageOrder1 = context.getString(R.string.order_is_not_insured_shop)
                                     val builder = AlertDialog.Builder(context)
                                     if (orderListing.get(adapterPosition).insurance.equals("true")) {
                                         builder.setTitle(context.getString(R.string.insured_order))
                                     } else {
                                         builder.setTitle(context.getString(R.string.shop_order))
                                     }

                                     builder.setCancelable(true)
                                     if (orderListing.get(adapterPosition).insurance.equals("true")) {
                                         builder.setMessage(messageOrder)
                                     } else {
                                         builder.setMessage(messageOrder1)
                                     }
                                     builder.setPositiveButton(context.getString(R.string.yes)) { dialog, which ->
                                         *//* insuranceOfferCallback.insuranceOfferCallback(orderListing[adapterPosition]._id, orderListing.get(adapterPosition).recommendedReward, adapterPosition, orderListing[adapterPosition].payment.toString(), orderListing.get(adapterPosition).pickUpDate.toString(), "false")
                                         fragment.startActivityForResult(Intent(context, CardListActivity::class.java)
                                                 .putExtra("from", "no"), 402)*//*
                                        acceptOfferConfirm(adapterPosition)
                                    }
                                    builder.show()
                                } else {
                                    if (orderListing.get(adapterPosition).insurance.equals("true")) {
                                        var amount: Double = (orderListing.get(adapterPosition).itemPrice
                                                ?: "0.0").toDouble() *
                                                (orderListing.get(adapterPosition).itemQuantity
                                                        ?: "0.0").toDouble()
                                        val builder = android.app.AlertDialog.Builder(context)
                                        builder.setTitle(context.getString(R.string.payment_required))
                                        builder.setCancelable(true)
                                        builder.setMessage(context.getString(R.string.order_has_insurance_policy) + " $ " + String.format(Locale.ENGLISH, "%.1f", amount) + " " + context.getString(R.string.order_has_insurance))
                                        builder.setPositiveButton(context.getString(R.string.add_card_policy)) { dialog, which ->
                                            insuranceOfferCallback.insuranceOfferCallback(orderListing[adapterPosition]._id, orderListing.get(adapterPosition).recommendedReward, adapterPosition, orderListing[adapterPosition].payment.toString(), orderListing.get(adapterPosition).pickUpDate.toString(), "false")
                                            fragment.startActivityForResult(Intent(context, CardListActivity::class.java)
                                                    .putExtra("from", "no"), 402)
                                        }
                                        builder.show()
                                    } else {
                                        acceptOfferConfirm(adapterPosition)
                                    }
                                }
                            } else {
                                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(context, R.string.stripe_error, Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(context, R.string.verify_error, Toast.LENGTH_SHORT).show()

                    }


                }*/
                R.id.cvItem -> {
                    if (orderListing.size > 0) {
                        if (fragment is ShipmentRequestsFragment) {

                            val detailorder = orderListing[adapterPosition]
                            if (detailorder.isDriverCancel.toBoolean())
                                detailorder.payment = detailorder.prevTotalPrice.toDouble()
                            fragment.startActivityForResult(
                                Intent(context, RequestDetailDriver::class.java)
                                    .putExtra(REQUEST_DETAIL, Gson().toJson(detailorder))
                                    .putExtra(POSITION, adapterPosition)
                                    .putExtra("IS_FROM", "REQUEST")
                                    .putExtra(
                                        ISDRIVERCANCEL,
                                        orderListing.get(adapterPosition).isDriverCancel
                                    )
                                    .putExtra(TYPE_OFFER_MADE, false), REQUESTED_RESULT_CODE
                            )
                        }
                    }

                }
                /* R.id.ivInfo -> {
                     var price = ""
                     if (orderListing[adapterPosition].isDriverCancel.toBoolean()) {
                         price = """${context.getString(R.string.currency_sign)}${orderListing[adapterPosition].prevTotalPrice}"""

                     } else {
                         price = """${context.getString(R.string.currency_sign)}${orderListing[adapterPosition].payment}"""
                     }
                     val message = "${"Shopper+Traveller Delivery Reward"} : ${context.getString(R.string.currency_sign)}${price}}"

                     ViewTooltip.on(context, itemView.ivInfo)
                             .autoHide(true, 3000)
                             .corner(30)
                             .text(message)
                             .position(ViewTooltip.Position.RIGHT)
                             .show()
                 }
 */
            }
        }


        private fun acceptOfferConfirm(adapterPosition: Int) {
            if (orderListing[adapterPosition].isDriverCancel.toBoolean() && orderListing.get(
                    adapterPosition
                ).type == OrderType.SHOP
            ) {
                var totalPrice = orderListing[adapterPosition].prevTotalPrice
                var totalPriceDouble: Double = 0.0
                if (totalPrice != null)
                    totalPriceDouble = totalPrice.toDouble()

                makeOffersAndAcceptOrderApiCall(
                    getAuthAccessToken(context),
                    orderListing[adapterPosition]._id,
                    orderListing[adapterPosition].prevTotalPrice,
                    DriverAction.ACCEPT,
                    adapterPosition,
                    latitude,
                    longitude,
                    orderListing.get(adapterPosition).pickUpDate.toString(),
                    0.0
                )
            } else {
                val alertLayout = context.layoutInflater.inflate(R.layout.layout_accept_offer, null)
                val dialog = BottomSheetDialog(context)
                dialog.setContentView(alertLayout)
                val mEdtDeliveryCharges =
                    alertLayout.findViewById(R.id.etDescription) as TextInputEditText
                val tvCancel = alertLayout.findViewById(R.id.tv_cancel) as TextView
                val tvSubmit = alertLayout.findViewById(R.id.tv_submit) as TextView
                mEdtDeliveryCharges.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {}

                    override fun beforeTextChanged(
                        s: CharSequence, start: Int,
                        count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence, start: Int,
                        before: Int, count: Int
                    ) {
                        val str = s.toString()
                        if (str.length == 1 && str.startsWith(".")) {
                            mEdtDeliveryCharges.setText("")
                            mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                        } else if (str.length == 1 && str.startsWith("0")) {
                            mEdtDeliveryCharges.setText("")
                            mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                        }
                    }
                })
                tvSubmit.setOnClickListener {
                    if (mEdtDeliveryCharges.text.toString().isEmpty()) {
                        makeOffersAndAcceptOrderApiCall(
                            getAuthAccessToken(context),
                            orderListing[adapterPosition]._id,
                            orderListing[adapterPosition].payment.toString(),
                            DriverAction.ACCEPT,
                            adapterPosition,
                            latitude,
                            longitude,
                            orderListing.get(adapterPosition).pickUpDate.toString(),
                            0.0
                        )
                    } else {
                        var shipping: Double = 0.0
                        shipping = mEdtDeliveryCharges.text.toString().toDouble()
                        makeOffersAndAcceptOrderApiCall(
                            getAuthAccessToken(context),
                            orderListing[adapterPosition]._id,
                            orderListing[adapterPosition].payment.toString(),
                            DriverAction.ACCEPT,
                            adapterPosition,
                            latitude,
                            longitude,
                            orderListing.get(adapterPosition).pickUpDate.toString(),
                            shipping
                        )
                    }

                    dialog.dismiss()
                }
                tvCancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

        }


        fun makeOffersAndAcceptOrderApiCall(
            accessToken: String?,
            orderId: String?,
            price: String?,
            driverAction: String?,
            position: Int?,
            latitude: Double,
            longitude: Double,
            deliverDate: String,
            shippingCharge: Double
        ) {

            RestClient.get().makeOffersAndAcceptOrder(
                accessToken,
                orderId,
                price,
                driverAction,
                latitude,
                longitude,
                deliverDate,
                "",
                shippingCharge
            )
                .enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                        showToast(context, t?.message)
                    }

                    override fun onResponse(
                        call: Call<ApiResponse<Any>>?,
                        response: Response<ApiResponse<Any>>?
                    ) {
                        if (response?.isSuccessful == true) {
                            if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {

                                val dialog = Dialog(context,R.style.DialogStyle)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setCancelable(true)
                                dialog.setContentView(R.layout.alert_dialog_success)
                                val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                                val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                                val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                                tvTitle.text = context.getString(R.string.success)
                                tvDescription.text = context.getString(R.string.offer_accepted)
                                dialogButton.text = context.getString(R.string.yes)
                                dialogButton.setOnClickListener {
                                    dialog.dismiss()
                                    deletedRequestPos.deletedRequestPos(adapterPosition)
                                }
                                dialog.show()
                            } else {
                                // context?.handleApiError(response.body()?.statusCode, response.body()?.message)
                                if (response.body()?.statusCode == 401) {
                                    val dialog = Dialog(context,R.style.DialogStyle)
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    dialog.setCancelable(true)
                                    dialog.setContentView(R.layout.alert_dialog)
                                    val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                                    val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                                    val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                                    tvTitle.text = context.getString(R.string.error)
                                    tvDescription.text = context.getString(R.string.sorry_account_have_been_logged)
                                    dialogButton.text = context.getString(R.string.ok)
                                    dialogButton.setOnClickListener {
                                        dialog.dismiss()
                                        SharedPrefs.with(context).remove(ACCESS_TOKEN)
                                        context.finishAffinity()
                                        context.startActivity(
                                            Intent(
                                                context,
                                                SplashActivity::class.java
                                            )
                                        )
                                    }
                                    dialog.show()
                                } else {
                                    showToast(context, response.body()?.message)
                                }

                            }
                        } else {
                            val errorModel = getApiError(response?.errorBody()?.string())
                            if (errorModel.statusCode == 401) {
                                val dialog = Dialog(context,R.style.DialogStyle)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setCancelable(true)
                                dialog.setContentView(R.layout.alert_dialog)
                                val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                                val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                                val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                                tvTitle.text = context.getString(R.string.error)
                                tvDescription.text = context.getString(R.string.sorry_account_have_been_logged)
                                dialogButton.text = context.getString(R.string.ok)
                                dialogButton.setOnClickListener {
                                    dialog.dismiss()
                                    SharedPrefs.with(context).remove(ACCESS_TOKEN)
                                    context.finishAffinity()
                                    context.startActivity(
                                        Intent(
                                            context,
                                            SplashActivity::class.java
                                        )
                                    )
                                }
                                dialog.show()
                            } else {
                                showToast(context, errorModel.message)

                            }
                            //getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                        }
                    }

                })
        }

        fun bind(order: OrderListing) {
            /*val spannableStrBuilder = SpannableStringBuilder()
            val spannableString = SpannableString(order.userId?.firstName)
            spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStrBuilder.append(spannableString).append(" ")
                    .append(context.getString(R.string.requested))*/
            itemView.tvRequestedUserName.text = order.userId?.firstName

            itemView.btnChat.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(USER_ID, order.userId?._id)
                intent.putExtra(USER_NAME, order.userId?.fullName)
                intent.putExtra(PROFILE_PIC_URL, order.userId?.profilePicURL?.original)
                context.startActivity(intent)
            }

            if (order.orderType == RequestType.PARCEL) {
                itemView.tvItemName.text =
                    "${order.dimensionArray?.get(0)?.weight} kg · (${order.dimensionArray?.get(0)?.length} x ${
                        order.dimensionArray?.get(0)?.width
                    } x ${order.dimensionArray?.get(0)?.height} ) cm"
            } else {
                itemView.tvItemName.text = order.itemName
            }
            itemView.btnInfo.setOnClickListener {
                popupRequested(context, itemView.btnInfo, adapterPosition)
            }
            val price =
                (order.itemPrice.toDouble() * order.itemQuantity.toInt()) + order.tax.toDouble()

            itemView.tvInversionTotal.text =
                "Inversion total : $" + String.format(Locale.ENGLISH, "%.2f", price)
            if (order.isDriverCancel.toBoolean())
                itemView.btnMakeAnOffer.visibility = View.INVISIBLE
            else
                itemView.btnMakeAnOffer.visibility = View.VISIBLE


            /*if(order.type.equals("Delivery")){
                itemView.tvDeliveryAddress.text = order.deliveryAddress
                itemView.tv_delivery_address.visibility = View.VISIBLE
                itemView.tvDeliveryAddress.visibility = View.VISIBLE
            }else{
                itemView.tv_delivery_address.visibility = View.GONE
                itemView.tvDeliveryAddress.visibility = View.GONE
            }*/
            /* if(order.type.equals("Delivery")){
                 itemView.tvFromAddress.text = order.deliveryAddress
             }else{
                 itemView.tvFromAddress.text = order.pickUpAddress
             }*/
            itemView.tvSource.text = order.pickUpAddress


            //Glide.with(context).load(order.userId?.profilePicURL?.original).apply(RequestOptions.circleCropTransform()).into(itemView.ivProfileHome)
            Glide.with(context).load(order.userId?.profilePicURL?.original)
                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                .into(itemView.ivProfile)
            if (orderListing[adapterPosition].orderStatus ==OrderStatus.REQUESTED) {
                itemView.tvPrice.text =
                    context.getString(R.string.currency_sign) + " "+ String.format("%.2f",orderListing[adapterPosition].payment - (orderListing[adapterPosition].payment * 1) / 100)

            } else {
                itemView.tvPrice.text =
                    context.getString(R.string.currency_sign) + String.format("%.2f",(order.accepted?.totalPrice?.toDouble()
                        ?.minus((orderListing[adapterPosition].payment * 1) / 100)))


            }
            //itemView.tvFromAddress.text = order.pickUpAddress
            itemView.tvDestination.text = order.dropDownAddress
            when (order.type) {

                "Delivery" -> {
                    itemView.tvType.text = context.getString(R.string.delivery) + " Only"
                }
                "Pickup" -> {
                    itemView.tvType.text =
                        context.getString(R.string.pickup) + " + " + context.getString(R.string.delivery)
                }
                "Shop" -> {
                    itemView.tvType.text =
                        context.getString(R.string.shop) + " + " + context.getString(R.string.delivery)
                }

            }
            itemView.tvOrderCode.text = order.orderId
            itemView.tvRequiredBy.text = context.getString(R.string.by) + " " +
                order.pickUpDate?.let { Date(it) }?.let { getFormatFromDate(it, "EEE · MMM d") }
            itemView.tvDate.text =
                order.createdDate?.let { Date(it) }?.let { getFormatFromDate(it, "MMM dd yyyy") }
            val shipImageSize: Int? = order.shipItemImages?.size
            if (order.orderType == RequestType.TRANSPORT) {
                if (shipImageSize != 0) {
                    Glide.with(context).load(order.shipItemImages?.get(0)?.original)
                        .into(itemView.ivImage)
                }
            } else {
                if (order.itemImages?.size != 0) {
                    Glide.with(context).load(order.itemImages?.get(0)?.original)
                        .into(itemView.ivImage)
                }
            }

            /*if (order.offers?.size == 0) {
                itemView.tvOffersCount.visibility = View.INVISIBLE
            } else {
                if (order.offers?.size == 1) {
                    itemView.tvOffersCount.text = """${order.offers?.size} ${context.getString(R.string.offer)}"""
                } else {
                    itemView.tvOffersCount.text = """${order.offers?.size} ${context.getString(R.string.offers)}"""
                }
                itemView.tvOffersCount.visibility = View.VISIBLE
            }*/

        }
    }

    interface DeletedRequestPosition {
        fun deletedRequestPos(pos: Int)
    }

    interface SelectedRequestPosition {
        fun selectedRequestPos(pos: Int)
    }

    interface SelectedReportPosition {
        fun selectedReportPos(orderId: String?, opposition_id: String, pos: Int)
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
