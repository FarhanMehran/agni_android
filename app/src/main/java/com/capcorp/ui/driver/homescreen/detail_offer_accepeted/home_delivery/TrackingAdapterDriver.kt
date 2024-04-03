package com.capcorp.ui.driver.homescreen.detail_offer_accepeted.home_delivery

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.user.homescreen.detail_offer_accepted.shop.Tracking
import com.capcorp.utils.DriverStatus
import com.capcorp.utils.getOnlyDate
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.raw_tracking.view.*


class TrackingAdapterDriver(
    private val mContext: Activity,
    private val orderListing: OrderListing,
    val tracking: Tracking
) : RecyclerView.Adapter<TrackingAdapterDriver.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackingAdapterDriver.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.raw_tracking, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrackingAdapterDriver.ViewHolder, position: Int) {
        holder.bind(orderListing, position)
    }

    override fun getItemCount(): Int {
        return when (orderListing.type) {
            "Shop" -> {
                4
            }
            "Delivery" -> {
                5
            }
            else -> {
                4
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(orderList: OrderListing, position: Int) = with(itemView) {
            viewUp.setBackgroundColor(resources.getColor(R.color.view_accept))
            viewDown.setBackgroundColor(resources.getColor(R.color.view_accept))

            tvStatus.setTextColor(resources.getColor(R.color.grey_error))
            tvTitle.setTextColor(resources.getColor(R.color.grey_error))
            btnStatus.setOnClickListener {
                setUpClickEvents(orderList, position)
            }

            btnStatus.visibility = View.INVISIBLE
            tvStatus.visibility = View.VISIBLE
            tvDate.visibility = View.GONE

            when (orderList.type) {
                "Shop" -> {
                    setUpShopOrder(orderList, position, itemView, context)
                }
                "Delivery" -> {
                    setUpDeliveryOrder(orderList, position, itemView, context)
                }
                else -> {
                    setUpPickupOrder(orderList, position, itemView, context)
                }
            }


        }
    }


    private fun setUpClickEvents(orderList: OrderListing, position: Int) {
        when (orderList.type) {
            "Shop" -> {
                if (position == 1) {
                    tracking.enterCodeDialog()
                } else if (position == 2) {
                    if (orderList.purchaseMadeDate != 0L && orderList.pickDate != 0L) {

                        if (orderList.productImages.isNotEmpty()) {
                            var dialogAlert: AlertDialog? = null
                            val builder = AlertDialog.Builder(mContext)
                            val inflater = mContext.layoutInflater
                            val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                            val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                            Glide.with(mContext).load(orderList.productImages[0].thumbnail)
                                .apply(RequestOptions().placeholder(R.drawable.ic_package))
                                .into(mImage)
                            builder.setView(alertLayout)
                            builder.setCancelable(true)
                            dialogAlert = builder.create()
                            dialogAlert.setCanceledOnTouchOutside(true)
                            dialogAlert.window?.setGravity(Gravity.CENTER)
                            dialogAlert.show()
                        }
                    } else {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_upload_evidence, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text = mContext.getString(R.string.upload_product_evidence)
                        val dialogButton: TextView = view.findViewById(R.id.btnUploadPhoto) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.openCameraOrGallery(DriverStatus.PICKUP)
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else if (position == 3) {
                    if (orderList.purchaseMadeDate != 0L) {
                        if (orderList.receiptImages.isNotEmpty()) {
                            var dialogAlert: AlertDialog? = null
                            val builder = AlertDialog.Builder(mContext)
                            val inflater = mContext.layoutInflater
                            val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                            val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                            Glide.with(mContext).load(orderList.receiptImages[0].thumbnail)
                                .apply(RequestOptions().placeholder(R.drawable.ic_package))
                                .into(mImage)
                            builder.setView(alertLayout)
                            builder.setCancelable(true)
                            dialogAlert = builder.create()
                            dialogAlert.setCanceledOnTouchOutside(true)
                            dialogAlert.window?.setGravity(Gravity.CENTER)
                            dialogAlert.show()
                        }
                    } else {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_upload_evidence, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text = mContext.getString(R.string.upload_receipt_title)
                        val dialogButton: TextView = view.findViewById(R.id.btnUploadPhoto) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.openCameraOrGallery(DriverStatus.PURCHASE_MADE)
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }

                }
            }
            "Delivery" -> {
                if (position == 1) {
                    tracking.enterCodeDialog()
                } else if (position == 2) {
                    if (orderList.addReceiptDate != 0L && orderList.pickDate != 0L) {
                        if (orderList.productImages.isNotEmpty()) {
                            var dialogAlert: AlertDialog? = null
                            val builder = AlertDialog.Builder(mContext)
                            val inflater = mContext.layoutInflater
                            val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                            val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                            Glide.with(mContext).load(orderList.productImages[0].thumbnail)
                                .apply(RequestOptions().placeholder(R.drawable.ic_package))
                                .into(mImage)
                            builder.setView(alertLayout)
                            builder.setCancelable(true)
                            dialogAlert = builder.create()
                            dialogAlert.setCanceledOnTouchOutside(true)
                            dialogAlert.window?.setGravity(Gravity.CENTER)
                            dialogAlert.show()
                        }
                    } else {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_upload_evidence, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text = mContext.getString(R.string.upload_product_evidence)
                        val dialogButton: TextView = view.findViewById(R.id.btnUploadPhoto) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.openCameraOrGallery(DriverStatus.PICKUP)
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else if (position == 3) {
                    if (!orderList.receiptImages.isEmpty()) {
                        var dialogAlert: AlertDialog? = null
                        val builder = AlertDialog.Builder(mContext)
                        val inflater = mContext.layoutInflater
                        val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                        val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                        Glide.with(mContext).load(orderList.receiptImages[0].thumbnail)
                            .apply(RequestOptions().placeholder(R.drawable.ic_package))
                            .into(mImage)
                        builder.setView(alertLayout)
                        builder.setCancelable(true)
                        dialogAlert = builder.create()
                        dialogAlert.setCanceledOnTouchOutside(true)
                        dialogAlert.window?.setGravity(Gravity.CENTER)
                        dialogAlert.show()
                    }
                }
            }
            else -> {
                if (position == 1) {
                    tracking.enterCodeDialog()
                } else if (position == 2) {
                    if (orderList.committedDate != 0L && orderList.pickDate != 0L) {
                        if (orderList.productImages.isNotEmpty()) {
                            var dialogAlert: AlertDialog? = null
                            val builder = AlertDialog.Builder(mContext)
                            val inflater = mContext.layoutInflater
                            val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                            val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                            Glide.with(mContext).load(orderList.productImages[0].thumbnail)
                                .apply(RequestOptions().placeholder(R.drawable.ic_package))
                                .into(mImage)
                            builder.setView(alertLayout)
                            builder.setCancelable(true)
                            dialogAlert = builder.create()
                            dialogAlert.setCanceledOnTouchOutside(true)
                            dialogAlert.window?.setGravity(Gravity.CENTER)
                            dialogAlert.show()
                        }
                    } else {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_upload_evidence, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text = mContext.getString(R.string.upload_product_picture)
                        val dialogButton: TextView = view.findViewById(R.id.btnUploadPhoto) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.openCameraOrGallery(DriverStatus.PICKUP)
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                }
            }
        }

    }


    private fun setUpPickupOrder(
        orderList: OrderListing,
        position: Int,
        itemView: View,
        context: Context
    ) {
        if (position == 0) {
            if (orderList.completeDate != 0L) {
                itemView.btnStatus.visibility = View.GONE

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.btnStatus.text = mContext.getString(R.string.completed)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)

            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.waiting_on_you)
            }
        } else if (position == 1) {
            if (orderList.pickDate != 0L && orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = mContext.getString(R.string.product_delivered)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvStatus.text = mContext.getString(R.string.completed_order,orderList.orderCode)
                itemView.btnStatus.visibility = View.GONE
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                } else {
                    itemView.tvDate.visibility = View.GONE
                }
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

            } else if (orderList.pickDate != 0L) {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_delivered)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.btnStatus.text = mContext.getString(R.string.mark_as_delivered)
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_delivered)
                itemView.tvStatus.text = mContext.getString(R.string.security_code)
                itemView.tvStatus.visibility = View.VISIBLE

            }
        } else if (position == 2) {
            if (orderList.pickDate != 0L && orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.committedDate != 0L && orderList.pickDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.text =
                    orderList.pickDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.tvTitle.text = mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.btnStatus.text = mContext.getString(R.string.view_photo)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else if (orderList.committedDate != 0L) {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_received)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.btnStatus.text = mContext.getString(R.string.upload_photo)
                itemView.tvStatus.visibility = View.GONE
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.waiting_on_you)
            }
        } else if (position == 3) {
            if (orderList.committedDate != 0L && orderList.pickDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.committedDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.receiver_details)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.btnStatus.visibility = View.GONE
                itemView.tvStatus.text = context.getString(R.string.completed)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                var date = orderList.committedDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.text =
                    orderList.committedDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE

            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)
                itemView.tvTitle.text = context.getString(R.string.receiver_details)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.waiting_for_your_shopper)
            }
        }
    }

    private fun setUpShopOrder(
        orderList: OrderListing,
        position: Int,
        itemView: View,
        context: Context
    ) {
        if (position == 0) {
            if (orderList.completeDate != 0L) {
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.btnStatus.visibility = View.GONE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)
                itemView.tvStatus.text =
                    context.getString(R.string.waiting_for_shopper_confirmation)
                itemView.tvTitle.text = context.getString(R.string.delivery_confirmation)
                itemView.tvDate.text =
                    orderList.completeDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            } else {
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)
                itemView.tvTitle.text = context.getString(R.string.delivery_confirmation)
                itemView.tvStatus.text = context.getString(R.string.waiting_for_your_shopper)
            }
        } else if (position == 1) {
            if (orderList.pickDate != 0L && orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.product_delivered)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.completed)
                itemView.btnStatus.visibility = View.GONE
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                }
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else if (orderList.pickDate != 0L) {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_delivered)
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.btnStatus.text = context.getString(R.string.enter_code)
                itemView.tvDate.visibility = View.GONE
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_delivered)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.security_code_from_shipper)
            }
        } else if (position == 2) {
            if (orderList.pickDate != 0L && orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }

            if (orderList.purchaseMadeDate != 0L && orderList.pickDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.text =
                    orderList.pickDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = context.getString(R.string.view_photo)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else if (orderList.purchaseMadeDate != 0L) {
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.btnStatus.text = context.getString(R.string.upload_picture)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.visibility = View.VISIBLE

            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.waiting_on_you)
            }
        } else if (position == 3) {
            if (orderList.purchaseMadeDate != 0L && orderList.pickDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.purchaseMadeDate != 0L) {
                itemView.tvDate.text =
                    orderList.purchaseMadeDate?.let { getOnlyDate(it, "MM/dd/yyyy") }

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.visibility = View.VISIBLE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvStatus.visibility = View.GONE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvTitle.text = context.getString(R.string.product_purchased)
                itemView.btnStatus.text = context.getString(R.string.view_photo)
            } else {
                itemView.tvTitle.text = context.getString(R.string.product_purchased)
                itemView.btnStatus.text = context.getString(R.string.upload_receipt)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpDeliveryOrder(
        orderList: OrderListing,
        position: Int,
        itemView: View,
        context: Context
    ) {
        if (position == 0) {
            if (orderList.completeDate != 0L) {
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.waiting_for_your_shopper)
                //itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                //itemView.btnStatus.setText("Confirm Delivery")
                itemView.tvTitle.text = context.getString(R.string.delivery_confirmation)
                itemView.tvDate.text =
                    orderList.completeDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            } else {
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.grey_error))
                itemView.tvTitle.text = context.getString(R.string.delivery_confirmation)
                itemView.tvStatus.text = context.getString(R.string.waiting_for_your_shopper)
            }
        } else if (position == 1) {
            if (orderList.pickDate != 0L && orderList.deliveredDate != 0L) {
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.product_delivered)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.completed)
                itemView.tvDate.text =
                    orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else if (orderList.pickDate != 0L) {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_delivered)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = context.getString(R.string.enter_code)
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_delivered)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.security_code_from_shipper)
            }
        } else if (position == 2) {
            if (orderList.pickDate != 0L && orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }

            if (orderList.addReceiptDate != 0L && orderList.pickDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.text =
                    orderList.pickDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = context.getString(R.string.view_photo)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else if (orderList.addReceiptDate != 0L) {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.btnStatus.text = context.getString(R.string.complete)
            } else if (orderList.addReceiptDate != 0L) {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.waiting_on_you)
            }
        } else if (position == 3) {
            if (orderList.addReceiptDate != 0L && orderList.pickDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.addReceiptDate != 0L) {
                itemView.tvDate.text =
                    orderList.addReceiptDate?.let { getOnlyDate(it, "MM/dd/yyyy") }

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.visibility = View.VISIBLE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvStatus.visibility = View.GONE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvTitle.text = context.getString(R.string.product_purchased)
                itemView.btnStatus.text = context.getString(R.string.view_purchase)
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_receipt_uploaded_inactive)
                itemView.tvTitle.text = context.getString(R.string.product_purchased)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.waiting_for_shopper)

            }
        } else if (position == 4) {
            if (orderList.addressGivenDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.addressGivenDate != 0L) {

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.address_given)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.btnStatus.text = context.getString(R.string.already_have_the_address)
                itemView.tvStatus.visibility = View.GONE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.addressGivenDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            } else {
                itemView.tvTitle.text = context.getString(R.string.address_given)
                itemView.btnStatus.visibility = View.GONE
                itemView.tvStatus.text = context.getString(R.string.waiting_for_shopper)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)
            }
        }
    }
}