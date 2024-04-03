package com.capcorp.ui.driver.homescreen.detail_offer_accepeted.home_delivery

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.utils.getOnlyDate
import com.capcorp.webservice.models.orders.OrderListing
import kotlinx.android.synthetic.main.raw_tracking.view.*


class TrackingAdapterDriverCompleted(
    private val mContext: Activity,
    private val orderListing: OrderListing
) : RecyclerView.Adapter<TrackingAdapterDriverCompleted.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackingAdapterDriverCompleted.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.raw_tracking, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: TrackingAdapterDriverCompleted.ViewHolder,
        position: Int
    ) {
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

                } else if (position == 1) {

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
                if (position == 2) {
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
                if (position == 2) {
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
                itemView.tvTitle.text = context.getString(R.string.delivery_confirmation)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.completed)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            }
        } else if (position == 1) {
            if (orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text =context.getString(R.string.product_delivered)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvStatus.text = context.getString(R.string.completed_order,orderList.orderCode)
                itemView.btnStatus.visibility = View.GONE
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                } else {
                    itemView.tvDate.visibility = View.GONE
                }
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

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
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.btnStatus.text = context.getString(R.string.view_photo)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
        } else if (position == 3) {
            if (orderList.pickDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.committedDate != 0L) {
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.receiver_details)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.btnStatus.visibility = View.GONE
                itemView.tvStatus.text = context.getString(R.string.completed)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.committedDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE

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
                itemView.btnStatus.visibility = View.GONE

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.delivery_confirmation)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.completed)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.completeDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            }
        } else if (position == 1) {
            if (orderList.deliveredDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.product_delivered)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvStatus.text = context.getString(R.string.completed_order,orderList.orderCode)
                itemView.btnStatus.visibility = View.GONE
                if (orderList.deliveredDate != 0L) {
                    itemView.tvDate.text =
                        orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                    itemView.tvDate.visibility = View.VISIBLE
                } else {
                    itemView.tvDate.visibility = View.GONE
                }
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))

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
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.btnStatus.text = context.getString(R.string.view_picture)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
        } else if (position == 3) {
            if (orderList.pickDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.purchaseMadeDate != 0L) {
                itemView.btnStatus.visibility = View.VISIBLE

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.product_purchased)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = context.getString(R.string.view_recepit)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.purchaseMadeDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE

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
                itemView.btnStatus.visibility = View.GONE
                itemView.tvStatus.text = context.getString(R.string.completed)
                //itemView.btnStatus.visibility = View.VISIBLE
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                //itemView.btnStatus.setText("Confirm Delivery")
                itemView.tvTitle.text =context.getString(R.string.delivery_confirmation)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.completeDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            }
        } else if (position == 1) {
            if (orderList.pickDate != 0L) {
                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.security_code)
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.completed) +" : "+ orderList.orderCode
                itemView.tvDate.text =
                    orderList.deliveredDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
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
                itemView.tvTitle.text = context.getString(R.string.product_received)
                itemView.tvStatus.visibility = View.GONE
                itemView.btnStatus.text = context.getString(R.string.view_product)
                itemView.btnStatus.visibility = View.VISIBLE
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
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
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.tvTitle.text = context.getString(R.string.receipt_upload)
                itemView.btnStatus.text = context.getString(R.string.view_photo)
            }
        } else if (position == 4) {
            if (orderList.addressGivenDate != 0L && orderList.addReceiptDate != 0L) {
                itemView.viewUp.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            }
            if (orderList.addressGivenDate != 0L) {

                itemView.tvTitle.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvStatus.setTextColor(context.resources.getColor(R.color.black))
                itemView.tvTitle.text = context.getString(R.string.delivery_information)
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = context.getString(R.string.completed)
                itemView.btnStatus.visibility = View.GONE
                itemView.ivStatusImage.setImageResource(R.drawable.ic_tick)
                itemView.viewDown.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                itemView.tvDate.text =
                    orderList.addressGivenDate?.let { getOnlyDate(it, "MM/dd/yyyy") }
                itemView.tvDate.visibility = View.VISIBLE
            }
        }
    }
}