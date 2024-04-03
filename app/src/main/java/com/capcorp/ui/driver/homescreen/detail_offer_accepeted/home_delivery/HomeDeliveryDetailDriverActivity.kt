package com.capcorp.ui.driver.homescreen.detail_offer_accepeted.home_delivery

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_accepted.AcceptedContract
import com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_accepted.AcceptedPresenter
import com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_made.OfferMadeActivity
import com.capcorp.ui.settings.profile.support.SupportOrderActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.chat.chatmessage.ChatActivity
import com.capcorp.ui.user.homescreen.detail_offer_accepted.shop.Tracking
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetCustomer
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.utils.location.LocationProvider
import com.capcorp.webservice.models.driver.driver_request.DriverStatusRequestNoPic
import com.capcorp.webservice.models.driver.driver_request_pic.DriverStatusRequestPic
import com.capcorp.webservice.models.driver.driver_request_pic.ReceiptImages
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_home_delivery_detail_driver.*
import java.io.File
import java.text.MessageFormat

class HomeDeliveryDetailDriverActivity : BaseActivity(),/*LocationUpdatesHelper(),*/ Tracking, View.OnClickListener,
    AcceptedContract.View, Utility.PassValues, ReceiptImageAdapter.ProfileImagesAdapterListener {
    private var orderListingDetail: OrderListing? = null
    private var presenter = AcceptedPresenter()
    private var flagChange: Boolean = false
    private var deliverDialog: Dialog? = null
    private var isOrderCancel: Boolean = false
    private var currentLocation = ArrayList<Float>()
    //private lateinit var locationProvide: LocationProvider
    private var sourceLatLong: LatLng? = null
    private var isDeliveredClicked: Boolean = false
    private var isProductClicked: Boolean = false
    private var dialogAlert: AlertDialog? = null
    private var mediaType: String = ""
    private lateinit var utility: Utility
    private lateinit var mReceiptImagesAdapter: ReceiptImageAdapter
    private var arrivedAddress: String = ""
    private var managePermissions: ManagePermissions? = null
    private lateinit var dialogIndeterminate: DialogIndeterminate

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var trackingAdapterDriver: TrackingAdapterDriver

    /* companion object {
        var notified = MutableLiveData<Int>()
    }*/
    var orderid: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_delivery_detail_driver)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        managePermissions = ManagePermissions(this)
        currentLocation.clear()
        currentLocation.add(0.0f)
        currentLocation.add(0.0f)
        if (intent.hasExtra(NOTIFICATION)) {
            dialogIndeterminate.show()
            view_parent_view.visibility = View.INVISIBLE
            orderid = intent.getStringExtra(ORDER_ID).toString()

        } else {
            view_parent_view.visibility = View.VISIBLE
            orderListingDetail =
                Gson().fromJson(intent.getStringExtra(ACCEPT_DETAIL), OrderListing::class.java)
            orderid = orderListingDetail?._id.toString()
        }

        orderid.let { presenter.getOrderDetail(getAuthAccessToken(this), it) }

        utility = Utility(this, mediaType)
        clickListener()
    }

    private fun clickListener() {
        tvBack.setOnClickListener(this)
        textView12.setOnClickListener(this)
        tvTrackOrder.setOnClickListener(this)
        tvHelp.setOnClickListener(this)
        tvCallDriver.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCallDriver -> {
                bottomSheetView()
            }
            R.id.tvTrackOrder -> {
                showLongToast(this, getString(R.string.contact_message))
            }

            R.id.tvHelp -> {
                startActivity(
                    Intent(this, SupportOrderActivity::class.java).putExtra(
                        "name",
                        orderListingDetail?.itemName
                    )
                        .putExtra("desc", orderListingDetail?.description)
                        .putExtra("showId", orderListingDetail?.orderId)
                        .putExtra("orderId", orderListingDetail?._id)
                )
            }

            R.id.tvBack -> {
                onBackPressed()
            }
            R.id.textView12 -> {
                startActivity(
                    Intent(this, OfferMadeActivity::class.java)
                        .putExtra(
                            REQUEST_DETAIL,
                            Gson().toJson(orderListingDetail)
                        )
                        .putExtra(POSITION, 0)
                        .putExtra(TYPE_OFFER_MADE, false)
                )
            }

            R.id.tvDelivered -> {
                showDeliverDialog()
            }
        }
    }


    private fun mediaOption() {
        val bottomSheetMedia = BottomSheetMedia()
        bottomSheetMedia.isCancelable = false
        bottomSheetMedia.setOnCameraListener {
            openCamera()
            bottomSheetMedia.dismiss()
        }

        bottomSheetMedia.setOnGalleryListener {
            openGallery()
            bottomSheetMedia.dismiss()

        }

        bottomSheetMedia.setOnCancelListener {
            bottomSheetMedia.dismiss()
        }
        bottomSheetMedia.show((this).supportFragmentManager, "camera")
    }

    private fun openGallery() {
        mediaType = UtilityConstants.GALLERY
        utility = Utility(this, UtilityConstants.GALLERY)
        utility.selectImage()
    }

    private fun openCamera() {
        mediaType = UtilityConstants.CAMERA
        utility = Utility(this, UtilityConstants.CAMERA)
        utility.selectImage()
    }

    private fun permissionSafetyMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (managePermissions!!.checkPermissions()) mediaOption()
        } else mediaOption()

    }

    override fun passImageURI(file: File?, photoURI: Uri?) {
        val key = System.currentTimeMillis().toString()
        val preGeneratedUrl = "$S3_BUCKET$key"
        val amazonMobile = AwsMobile(this, key)
        amazonMobile.uploadFile(key, file!!, object : TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    Thread(Runnable {
                        run {
                            amazonMobile.getS3ClientInstance()
                                .setObjectAcl("h2d-dev", key, CannedAccessControlList.PublicRead)
                        }
                    }).start()
                }
            }

            override fun onError(id: Int, ex: Exception?) {

            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isProductClicked = true
                val productImages = mutableListOf<ReceiptImages>()
                productImages.add(ReceiptImages(preGeneratedUrl, preGeneratedUrl))
                presenter.driverStatusPic(
                    getAuthAccessToken(this), DriverStatusRequestPic(
                        null,
                        productImages,
                        orderListingDetail?._id.toString(),
                        isFrom,
                        currentLocation,
                        currentLocation.get(1).toDouble(),
                        currentLocation.get(0).toDouble(),
                        arrivedAddress,
                        orderListingDetail?.userId?._id
                            ?: ""
                    )
                )
        } else {
            isProductClicked = true
            val productImages = mutableListOf<ReceiptImages>()
            productImages.add(ReceiptImages(preGeneratedUrl, preGeneratedUrl))
            presenter.driverStatusPic(
                getAuthAccessToken(this), DriverStatusRequestPic(
                    null,
                    productImages,
                    orderListingDetail?._id.toString(),
                    isFrom,
                    currentLocation,
                    currentLocation.get(1).toDouble(),
                    currentLocation.get(0).toDouble(),
                    arrivedAddress,
                    orderListingDetail?.userId?._id
                        ?: ""
                )
            )
        }

    }

    private fun showDeliverDialog() {
        deliverDialog = BottomSheetDialog(this,R.style.DialogStyle)
        deliverDialog?.setCanceledOnTouchOutside(true)
        deliverDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = layoutInflater.inflate(R.layout.dialog_complete_order, null)
        deliverDialog?.setContentView(view)
        val tvSubmit = view.findViewById<TextView>(R.id.btnSubmit)
        val tvCancel = view.findViewById<TextView>(R.id.btnCancel)
        val etOrderCode = view.findViewById<EditText>(R.id.edtCode)
        tvCancel?.setOnClickListener { deliverDialog?.dismiss() }
        tvSubmit?.setOnClickListener {
            if (!etOrderCode?.text.toString().trim().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (currentLocation.size > 0) {
                            arrivedAddress =
                                setAddress(this, currentLocation.get(1), currentLocation.get(0))
                        }
                        if (CheckNetworkConnection.isOnline(this)) {
                            isDeliveredClicked = true
                            presenter.driverStatus(
                                getAuthAccessToken(this), DriverStatusRequestNoPic(
                                    orderListingDetail?._id.toString(),
                                    DriverStatus.DELIVERED,
                                    currentLocation,
                                    etOrderCode?.text.toString(),
                                    arrivedAddress,
                                    currentLocation.get(1).toDouble(),
                                    currentLocation.get(0).toDouble(),
                                    orderListingDetail?.userId?._id
                                        ?: ""
                                )
                            )
                            deliverDialog!!.dismiss()
                        }
                } else {
                    if (currentLocation.size > 0) {
                        arrivedAddress =
                            setAddress(this, currentLocation.get(1), currentLocation.get(0))
                    }
                    if (CheckNetworkConnection.isOnline(this)) {
                        isDeliveredClicked = true
                        presenter.driverStatus(
                            getAuthAccessToken(this), DriverStatusRequestNoPic(
                                orderListingDetail?._id.toString(),
                                DriverStatus.DELIVERED,
                                currentLocation,
                                etOrderCode?.text.toString(),
                                arrivedAddress,
                                currentLocation.get(1).toDouble(),
                                currentLocation.get(0).toDouble(),
                                orderListingDetail?.userId?._id
                                    ?: ""
                            )
                        )
                        deliverDialog!!.dismiss()
                    }
                }
            } else {
                tvPrice.showSnack(R.string.please_enter_order_code)
            }
        }
        deliverDialog?.show()
        deliverDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun openReceiptImagesDialog(context: Context) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val alertLayout = inflater.inflate(R.layout.layout_view_receipt_delivery, null)

        val viewPager = alertLayout.findViewById(R.id.viewPager) as ViewPager

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.height = resources.displayMetrics.widthPixels
        viewPager.layoutParams = layoutParams

        viewPager.adapter = mReceiptImagesAdapter
        viewPager.offscreenPageLimit = 3

        builder.setView(alertLayout)
        builder.setCancelable(true)
        dialogAlert = builder.create()
        dialogAlert?.setCanceledOnTouchOutside(true)
        dialogAlert?.window?.setGravity(Gravity.CENTER)
        dialogAlert!!.show()
    }

    private fun bottomSheetView() {
        val bottomSheetMedia = BottomSheetCustomer()
        bottomSheetMedia.setOnChatListener(View.OnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(USER_ID, orderListingDetail?.userId?._id)
            intent.putExtra(USER_NAME, orderListingDetail?.userId?.fullName)
            intent.putExtra(PROFILE_PIC_URL, orderListingDetail?.userId?.profilePicURL?.original)
            startActivity(intent)
            bottomSheetMedia.dismiss()
        })

        bottomSheetMedia.setOnCallListener(View.OnClickListener {
            val telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simState = telMgr.simState
            when (simState) {
                TelephonyManager.SIM_STATE_ABSENT,
                TelephonyManager.SIM_STATE_NETWORK_LOCKED,
                TelephonyManager.SIM_STATE_PIN_REQUIRED,
                TelephonyManager.SIM_STATE_PUK_REQUIRED,
                TelephonyManager.SIM_STATE_UNKNOWN, 6 // When sim disabled
                -> Toast.makeText(this, R.string.msg_sim_status, Toast.LENGTH_SHORT).show()
                TelephonyManager.SIM_STATE_READY ->
                    startActivity(
                        Intent(
                            Intent.ACTION_DIAL, Uri.fromParts(
                                "tel",
                                MessageFormat.format(
                                    "{0}-{1}",
                                    +91,
                                    orderListingDetail?.userId?.phoneNo
                                ), null
                            )
                        )
                    )
            }
            bottomSheetMedia.dismiss()

        })

        bottomSheetMedia.setOnCancelListener(View.OnClickListener {
            bottomSheetMedia.dismiss()
        })
        bottomSheetMedia.show(supportFragmentManager, "camera")
    }


    private fun cancelConfirmation() {
        AlertDialogUtil.getInstance()
            .createOkCancelDialog(this, R.string.confirm, R.string.cancel_order_confirmation,
                R.string.yes, R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                    override fun onOkButtonClicked() {

                        val jason = JsonObject()
                        jason.addProperty("orderId", orderListingDetail?._id.toString())

                        presenter.cancelOrder(
                            getAuthAccessToken(this@HomeDeliveryDetailDriverActivity),
                            jason
                        )
                    }

                    override fun onCancelButtonClicked() {
                        //  dismiss
                    }

                }).show()
    }


    @SuppressLint("SetTextI18n")
    private fun setView(orderList: OrderListing?) {

        if (orderListingDetail?.orderStatus == OrderStatus.REQUESTED || (orderListingDetail?.insurance == true && orderListingDetail?.type == OrderType.SHOP && orderListingDetail?.orderStatus == OrderStatus.ACCEPTED)) {
            tvTrackOrder.visibility = View.VISIBLE
        } else {
            if (orderListingDetail?.driverStatus == DriverStatus.NO_ACTION) {
                tvTrackOrder.visibility = View.VISIBLE
            } else {
                tvTrackOrder.visibility = View.GONE
            }
        }

        tvPrice.text = """${getString(R.string.currency_sign)} ${orderList?.accepted?.totalPrice}"""
        tvRequiredByDate.text =
            orderList?.accepted?.driverArrivalDate?.let { getOnlyDate(it, "EEE · MMM d") }
        setRecyclerAdapter(orderList)

        when (orderList?.orderType) {

            RequestType.TRANSPORT -> {
                tvItemName.text = orderList.type
            }
            RequestType.PARCEL -> {
                tvItemName.text = """${orderList.dimensionArray?.get(0)?.weight} kg · (${
                    orderList.dimensionArray?.get(0)?.length
                } * ${orderList.dimensionArray?.get(0)?.width} * ${orderList.dimensionArray?.get(0)?.height}) cm"""
            }
            RequestType.GROCERIES -> {
                tvItemName.text =
                    "${orderList.groceryItems.size} ${getString(R.string.items)} ${getString(R.string.from)} ${orderList.storeDetails.size} ${
                        getString(R.string.store)
                    }"
            }
            RequestType.SHIPMENT -> {
                tvItemName.text = orderList.itemName
            }
        }

    }

    private fun setRecyclerAdapter(orderList: OrderListing?) {
        layoutManager = LinearLayoutManager(this)
        rvItemsTracking.layoutManager = layoutManager
        trackingAdapterDriver = orderList?.let { TrackingAdapterDriver(this, it, this) }!!
        rvItemsTracking.adapter = trackingAdapterDriver
    }


    override fun onBackPressed() {
        if (intent.hasExtra(NOTIFICATION)) {
            startActivity(Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java))
        } else {
            flagChange = true
            if (flagChange) {
                setResult(Activity.RESULT_OK)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        tvItemName.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        if (code == 401) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(this).remove(ACCESS_TOKEN)
                finishAffinity()
                startActivity(Intent(this, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            if (errorBody.equals(getString(R.string.order_deleted))) {
                val dialog = Dialog(this,R.style.DialogStyle)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.alert_dialog)
                val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                tvTitle.text = getString(R.string.error)
                tvDescription.text = getString(R.string.order_dlted_by_shopper)
                dialogButton.text = getString(R.string.ok)
                dialogButton.setOnClickListener {
                    onBackPressed()
                }
                dialog.show()
            } else {
                tvItemName.showSnack(errorBody!!)
            }
        }
    }

    override fun validationsFailure(type: String?) {
    }

    override fun onImageClicked(imagePath: String?) {
    }

   /* override fun onLocationReceived(location: Location?) {

    }*/

    override fun changeDriverStatus() {
        when (orderListingDetail?.driverStatus) {
            DriverStatus.ADD_RECIEPT -> {
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.success))
                builder.setCancelable(false)
                builder.setMessage(getString(R.string.goods_received_successfully))
                builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    permissionSafetyMethod()
                }
                builder.show()
            }

            DriverStatus.PICKUP -> {
                orderListingDetail?.driverStatus = DriverStatus.DELIVERED
                flagChange = true
            }
            DriverStatus.DELIVERED -> {
                orderListingDetail?.driverStatus = DriverStatus.CONFIRMATION
                flagChange = true
            }
        }
        val currentTimeStamp: Long = System.currentTimeMillis()
        Log.e("currentTime", currentTimeStamp.toString())
        orderListingDetail?.pickDate = currentTimeStamp
        orderListingDetail?.deliveredDate = currentTimeStamp

        setOrderStatus()
    }

    override fun changeDriverStatusPic() {
        when (orderListingDetail?.driverStatus) {
            DriverStatus.ADD_RECIEPT -> {
                orderListingDetail?.driverStatus = DriverStatus.PICKUP
                flagChange = true

                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.success))
                builder.setCancelable(false)
                builder.setMessage(getString(R.string.product_upload_successfully))
                builder.setPositiveButton(android.R.string.ok) { dialog, which ->

                }
                builder.show()
            }
            DriverStatus.PURCHASE_MADE -> {

            }
            DriverStatus.PICKUP -> {

            }
            DriverStatus.DELIVERED -> {

            }
        }
        val currentTimeStamp: Long = System.currentTimeMillis()
        Log.e("currentTime", currentTimeStamp.toString())
        setOrderStatus()
    }

    private fun setOrderStatus() {
        presenter.getOrderDetail(getAuthAccessToken(this), orderListingDetail?._id.toString())

        if (orderListingDetail?.orderStatus == OrderStatus.REQUESTED || (orderListingDetail?.insurance == true && orderListingDetail?.type == OrderType.SHOP && orderListingDetail?.orderStatus == OrderStatus.ACCEPTED)) {
            tvTrackOrder.visibility = View.VISIBLE
        } else {
            if (orderListingDetail?.driverStatus == DriverStatus.NO_ACTION) {
                tvTrackOrder.visibility = View.VISIBLE
            } else {
                tvTrackOrder.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun cancelOrder() {
        flagChange = true
        isOrderCancel = true
        onBackPressed()
    }

    override fun orderDetailSuccess(data: OrderListing) {
        dialogIndeterminate.dismiss()
        view_parent_view.visibility = View.VISIBLE
        orderListingDetail = data
        setView(orderListingDetail)
    }

    fun openCancelOrderApi(context: Context) {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val alertLayout = inflater.inflate(R.layout.layout_cancel_order, null)

        val radioGroup = alertLayout.findViewById(R.id.radio_group) as RadioGroup
        val tvCancelOrder = alertLayout.findViewById(R.id.tv_cancel_orders) as TextView

        var mReason: String = ""

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radio_not_find) {
                mReason = getString(R.string.i_could_not_find)
            } else if (checkedId == R.id.radio_price_not_cotrrect) {
                mReason = getString(R.string.product_price_not_correct)
            } else if (checkedId == R.id.radio_shopper_not_responding) {
                mReason = getString(R.string.shopper_not_responding)
            } else {
                mReason = getString(R.string.delivery_place_issue)
            }
        }

        tvCancelOrder.setOnClickListener {
            if (mReason.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_reason), Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        presenter.makeOffersAndAcceptOrderApiCall(
                            getAuthAccessToken(this),
                            orderListingDetail?._id,
                            orderListingDetail?.accepted?.totalPrice,
                            "REJECT",
                            currentLocation.get(1).toDouble(),
                            currentLocation.get(0).toDouble(),
                            orderListingDetail?.accepted?.driverArrivalDate.toString(),
                            mReason
                        )
                        dialogAlert?.dismiss()
                } else {
                    presenter.makeOffersAndAcceptOrderApiCall(
                        getAuthAccessToken(this),
                        orderListingDetail?._id,
                        orderListingDetail?.accepted?.totalPrice,
                        "REJECT",
                        currentLocation.get(1).toDouble(),
                        currentLocation.get(0).toDouble(),
                        orderListingDetail?.accepted?.driverArrivalDate.toString(),
                        mReason
                    )
                    dialogAlert?.dismiss()
                }

            }
        }

        builder.setView(alertLayout)
        builder.setCancelable(true)
        dialogAlert = builder.create()
        dialogAlert?.setCanceledOnTouchOutside(true)
        dialogAlert?.window?.setGravity(Gravity.BOTTOM)
        dialogAlert!!.show()
    }

    override fun onResume() {
        super.onResume()
        /*locationProvide = LocationProvider.CurrentLocationBuilder(this).build()
        locationProvide.getLastKnownLocation(OnSuccessListener {
            if (it != null) {

                sourceLatLong = LatLng(it.latitude, it.longitude)
                currentLocation.clear()
                currentLocation.add(sourceLatLong!!.longitude.toFloat())
                currentLocation.add(sourceLatLong!!.latitude.toFloat())
            }

        })*/
    }

    override fun rejectOrder() {
        flagChange = true
        isOrderCancel = true
        onBackPressed()
        Toast.makeText(this, getString(R.string.Successfully_cancelled_order), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        utility.onActivityResult(requestCode, resultCode, data)
    }

    override fun callDriveStatusAPI(status: String) {
        if (currentLocation.size > 0) {
            arrivedAddress = setAddress(this, currentLocation[1], currentLocation[0])
        }
        if (CheckNetworkConnection.isOnline(this)) {
            presenter.driverStatus(
                getAuthAccessToken(this), DriverStatusRequestNoPic(
                    orderListingDetail?._id.toString(),
                    status,
                    currentLocation,
                    "",
                    arrivedAddress,
                    currentLocation.get(1).toDouble(),
                    currentLocation.get(0).toDouble(),
                    orderListingDetail?.accepted?._id
                        ?: ""
                )
            )
        }
    }

    var isFrom: String = ""
    override fun openCameraOrGallery(isFrom: String) {
        this.isFrom = isFrom
        permissionSafetyMethod()
    }

    override fun enterCodeDialog() {
        showDeliverDialog()
    }
}
