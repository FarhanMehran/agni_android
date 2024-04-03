package com.capcorp.ui.user.homescreen.detail_offer_accepted.shop

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.capcorp.ui.user.homescreen.orders.add_receiver.AddReceiverDetail
import com.capcorp.utils.DriverStatus
import com.capcorp.utils.ORDER_ID
import com.capcorp.utils.REQUEST_ADD_DETAIL
import com.capcorp.utils.getOnlyDate
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.raw_tracking.view.*


class TrackingAdapter(
    private val mContext: Activity,
    private val orderListing: OrderListing,
    val tracking: Tracking
) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.raw_tracking, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrackingAdapter.ViewHolder, position: Int) {
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

            btnStatus.visibility = View.GONE
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

            btnStatus.setOnClickListener {
                btnClickEvent(orderList, position)
            }
        }
    }

    private fun btnClickEvent(orderList: OrderListing, position: Int) {
        when (orderList.type) {
            "Shop" -> {
                if (position == 0) {
                    if (orderList.deliveredDate != 0L) {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_mark_as_delivered, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text = mContext.getString(R.string.shop_details_position0)
                        val dialogButton: TextView = view.findViewById(R.id.btnMarkAsDelivered) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.callDriveStatusAPI(DriverStatus.COMPLETED)
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else if (position == 1) {
                    if (orderList.pickDate != 0L) {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_security_code, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text =mContext.getString(R.string.shop_details1) + orderList.orderCode
                        val dialogButton: TextView = view.findViewById(R.id.btnOk) as TextView
                        dialogButton.text = mContext.resources.getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.callDriveStatusAPI(DriverStatus.COMPLETED)
                        }
                        dialog.show()
                    }
                } else if (position == 2) {
                    if (orderList.productImages.isNotEmpty()) {
                        var dialogAlert: AlertDialog? = null
                        val builder = AlertDialog.Builder(mContext)
                        val inflater = mContext.layoutInflater
                        val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                        val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                        Glide.with(mContext).load(orderList.productImages[0].thumbnail)
                            .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(mImage)
                        builder.setView(alertLayout)
                        builder.setCancelable(true)
                        dialogAlert = builder.create()
                        dialogAlert.setCanceledOnTouchOutside(true)
                        dialogAlert.window?.setGravity(Gravity.CENTER)
                        dialogAlert.show()
                    }
                } else if (position == 3) {
                    if (orderList.receiptImages.isNotEmpty()) {
                        var dialogAlert: AlertDialog? = null
                        val builder = AlertDialog.Builder(mContext)
                        val inflater = mContext.layoutInflater
                        val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                        val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                        Glide.with(mContext).load(orderList.receiptImages[0].thumbnail)
                            .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(mImage)
                        builder.setView(alertLayout)
                        builder.setCancelable(true)
                        dialogAlert = builder.create()
                        dialogAlert.setCanceledOnTouchOutside(true)
                        dialogAlert.window?.setGravity(Gravity.CENTER)
                        dialogAlert.show()
                    }
                }
            }
            "Delivery" -> {
                if (position == 0) {
                    if (orderList.deliveredDate != 0L) {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_mark_as_delivered, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text = mContext.getString(R.string.delivery_position0)
                        val dialogButton: TextView = view.findViewById(R.id.btnMarkAsDelivered) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.callDriveStatusAPI(DriverStatus.COMPLETED)
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else if (position == 1) {
                    if (orderList.pickDate != 0L) {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_security_code, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text =mContext.getString(R.string.delivery_position1)  + " : " + orderList.orderCode
                        val dialogButton: TextView = view.findViewById(R.id.btnOk) as TextView
                        dialogButton.text = mContext.resources.getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else if (position == 2) {
                    if (orderList.productImages.isNotEmpty()) {
                        var dialogAlert: AlertDialog? = null
                        val builder = AlertDialog.Builder(mContext)
                        val inflater = mContext.layoutInflater
                        val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                        val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                        Glide.with(mContext).load(orderList.productImages[0].thumbnail)
                            .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(mImage)
                        builder.setView(alertLayout)
                        builder.setCancelable(true)
                        dialogAlert = builder.create()
                        dialogAlert.setCanceledOnTouchOutside(true)
                        dialogAlert.window?.setGravity(Gravity.CENTER)
                        dialogAlert.show()
                    }
                } else if (position == 3) {
                    if (orderList.addressGivenDate != 0L && orderList.addReceiptDate != 0L) {
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
                    } else {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_upload_evidence, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text =mContext.getString(R.string.please_upload_photo_of_recepit)
                        val dialogButton: TextView = view.findViewById(R.id.btnUploadPhoto) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.openCameraOrGallery("UploadReceipt")
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else if (position == 4) {
                    val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                    dialog.setCanceledOnTouchOutside(true)

                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    val view = mContext.layoutInflater.inflate(R.layout.dialog_mark_as_delivered, null)
                    dialog.setContentView(view)
                    val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                    val tvTitle = view.findViewById(R.id.tvTitle1) as TextView
                    tvDesc.text = mContext.getString(R.string.please_confirm_given_information)
                    tvTitle.text =  mContext.getString(R.string.delivery_confirmation)
                    val dialogButton: TextView = view.findViewById(R.id.btnMarkAsDelivered) as TextView
                    dialogButton.text =  mContext.resources.getString(R.string.already_had_information)
                    val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                    dialogButton.setOnClickListener {
                        dialog.dismiss()
                        tracking.callDriveStatusAPI(DriverStatus.ADDRESS_GIVEN)
                    }
                    dialogButtonCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }
            else -> {
                if (position == 0) {
                    if (orderList.deliveredDate != 0L) {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_mark_as_delivered, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text = mContext.getString(R.string.pickup_details0)
                        val dialogButton: TextView = view.findViewById(R.id.btnMarkAsDelivered) as TextView
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            tracking.callDriveStatusAPI(DriverStatus.COMPLETED)
                        }
                        dialogButtonCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else if (position == 1) {
                    if (orderList.pickDate != 0L) {

                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_security_code, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        tvDesc.text =mContext.getString(R.string.secutiry_code_details) + orderList.orderCode
                        val dialogButton: TextView = view.findViewById(R.id.btnOk) as TextView
                        dialogButton.text = mContext.resources.getString(R.string.ok)
                        dialogButton.setOnClickListener {
                            dialog.dismiss()

                        }
                        dialog.show()
                    }
                } else if (position == 2) {
                    if (orderList.productImages.isNotEmpty()) {
                        var dialogAlert: AlertDialog? = null
                        val builder = AlertDialog.Builder(mContext)
                        val inflater = mContext.layoutInflater
                        val alertLayout = inflater.inflate(R.layout.layout_image_show, null)

                        val mImage = alertLayout.findViewById(R.id.iv_Image) as ImageView
                        Glide.with(mContext).load(orderList.productImages[0].thumbnail)
                            .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(mImage)
                        builder.setView(alertLayout)
                        builder.setCancelable(true)
                        dialogAlert = builder.create()
                        dialogAlert.setCanceledOnTouchOutside(true)
                        dialogAlert.window?.setGravity(Gravity.CENTER)
                        dialogAlert.show()
                    }
                } else if (position == 3) {
                    if (orderList.receiverInfo != null && !orderList.receiverInfo?.employeeName.isNullOrEmpty()) {
                    } else {
                        val dialog = BottomSheetDialog(mContext, R.style.TransparentDialog)
                        dialog.setCanceledOnTouchOutside(true)

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        val view = mContext.layoutInflater.inflate(R.layout.dialog_mark_as_delivered, null)
                        dialog.setContentView(view)
                        val tvDesc = view.findViewById(R.id.tvDescription) as TextView
                        val tvTitle = view.findViewById(R.id.tvTitle1) as TextView
                        tvDesc.text = mContext.getString(R.string.please_add_receivers_info)
                        tvTitle.text =  mContext.getString(R.string.add_receivers_info)
                        val dialogButton: TextView = view.findViewById(R.id.btnMarkAsDelivered) as TextView
                        dialogButton.text =  mContext.resources.getString(R.string.ok)
                        val dialogButtonCancel: TextView = view.findViewById(R.id.btnCancel) as TextView
                        dialogButton.setOnClickListener {
                            dialog.dismiss()
                            mContext.startActivityForResult(
                                Intent(mContext, AddReceiverDetail::class.java)
                                    .putExtra(ORDER_ID, orderList._id), REQUEST_ADD_DETAIL
                            )
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
            if (orderList.committedDate != 0L && orderList.deliveredDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = mContext.getString(R.string.confirm_delivery)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                } else {
                    itemView.tvDate.visibility = View.GONE
                }
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.waiting_on_you)
            }
        } else if (position == 1) {
            if (orderList.deliveredDate != 0L) {
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

            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_delivered)
                itemView.tvStatus.text = mContext.getString(R.string.security_code_for_nomad)
                itemView.tvStatus.visibility = View.VISIBLE
                if (orderList.pickDate != 0L) {
                    itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                    itemView.tvStatus.visibility = View.GONE
                    itemView.btnStatus.visibility = View.VISIBLE
                    itemView.btnStatus.text = mContext.getString(R.string.security_code)
                    itemView.tvDate.visibility = View.GONE
                }

            }
        } else if (position == 2) {
            if (orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.pickDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.text =
                    orderList.pickDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.tvTitle.text = mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_active)
                itemView.btnStatus.text =mContext.getString(R.string.view_photo)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.waiting_for_nomad)
            }
        } else if (position == 3) {
            if (orderList.pickDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.committedDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = mContext.getString(R.string.receiver_details)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.btnStatus.visibility = View.GONE
                itemView.tvStatus.text = mContext.getString(R.string.completed)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                var date = orderList.committedDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.text =
                    orderList.committedDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE

            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.receiver_details)
                if (orderList.receiverInfo?.employeeName != null) {
                    itemView.tvStatus.visibility = View.GONE
                    itemView.btnStatus.visibility = View.VISIBLE
                    itemView.btnStatus.text = mContext.getString(R.string.edit_information)
                } else {
                    itemView.tvStatus.visibility = View.GONE
                    itemView.btnStatus.visibility = View.VISIBLE
                    itemView.btnStatus.text = mContext.getString(R.string.add_information)
                }
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
            if (orderList.purchaseMadeDate != 0L && orderList.deliveredDate != 0L) {
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.btnStatus.text = mContext.getString(R.string.confirm_delivery)
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                }
            } else {
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.tvStatus.text = mContext.getString(R.string.waiting_on_you)
            }
        } else if (position == 1) {
            if (orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = mContext.getString(R.string.product_delivered)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.completed_order,orderList.orderCode)
                itemView.btnStatus.visibility = View.GONE
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                }
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_delivered)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.security_code_for_nomad)
                if (orderList.pickDate != 0L) {
                    itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                    itemView.tvStatus.visibility = View.GONE
                    itemView.btnStatus.visibility = View.VISIBLE
                    itemView.btnStatus.text = mContext.getString(R.string.security_code)
                    itemView.tvDate.visibility = View.GONE
                }
            }
        } else if (position == 2) {
            if (orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }

            if (orderList.pickDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.text =
                    orderList.pickDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvTitle.text = mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = mContext.getString(R.string.view_picture)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.waiting_for_nomand)
            }
        } else if (position == 3) {
            if (orderList.pickDate != 0L) {
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
                itemView.tvTitle.text = mContext.getString(R.string.product_purchased)
                itemView.btnStatus.text = mContext.getString(R.string.view_recepit)
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_receipt_uploaded_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.product_purchased)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.waiting_for_nomand)
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
            if (orderList.addressGivenDate != 0L && orderList.deliveredDate != 0L) {
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.btnStatus.text = mContext.getString(R.string.confirm_delivery)
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                }
            } else {
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.delivery_confirmation)
                itemView.tvStatus.text = mContext.getString(R.string.waiting_on_you)
            }
        } else if (position == 1) {
            if (orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.pickDate != 0L) {
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = mContext.getString(R.string.security_code)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_active)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = mContext.getString(R.string.view_code)
                itemView.btnStatus.visibility = View.VISIBLE
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                }
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_delivered_inactive)
                itemView.tvTitle.text =  mContext.getString(R.string.security_code)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text =  mContext.getString(R.string.security_code_for_nomad)
            }
        } else if (position == 2) {
            if (orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }

            if (orderList.pickDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.text =
                    orderList.pickDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_active)
                itemView.tvTitle.text =  mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text =  mContext.getString(R.string.view_product)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_product_received_inactive)
                itemView.tvTitle.text =  mContext.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.waiting_for_nomand)
            }
        } else if (position == 3) {
            if (orderList.pickDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.addressGivenDate != 0L && orderList.addReceiptDate != 0L) {
                itemView.tvDate.text =
                    orderList.addReceiptDate?.let { getOnlyDate(it, "MM/dd/yyyy") }

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvDate.visibility = View.VISIBLE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvStatus.visibility = View.GONE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_receipt_uploaded_active)
                itemView.tvTitle.text =  mContext.getString(R.string.receipt_upload)
                itemView.btnStatus.text =  mContext.getString(R.string.view_photo)
            } else if (orderList.addressGivenDate != 0L) {
                itemView.tvTitle.text =  mContext.getString(R.string.receipt_upload)

                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_receipt_uploaded_inactive)
                itemView.tvTitle.text =  mContext.getString(R.string.receipt_upload)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.btnStatus.text = mContext.getString(R.string.upload)
            } else {
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_receipt_uploaded_inactive)
                itemView.tvTitle.text = mContext.getString(R.string.receipt_upload)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text =  mContext.getString(R.string.waiting_on_you)

            }
        } else if (position == 4) {
            if (orderList.addressGivenDate != 0L && orderList.addReceiptDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.addressGivenDate != 0L) {

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text =  mContext.getString(R.string.delivery_information)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = mContext.getString(R.string.completed)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_house_active)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.addressGivenDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            } else {
                itemView.tvTitle.text =  mContext.getString(R.string.delivery_information)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.btnStatus.text =  mContext.getString(R.string.upload)
                itemView.tvStatus.visibility = View.GONE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tracking_home_inactive)
            }
        }
    }
}