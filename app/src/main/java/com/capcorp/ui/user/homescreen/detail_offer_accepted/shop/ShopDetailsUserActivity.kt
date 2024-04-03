package com.capcorp.ui.user.homescreen.detail_offer_accepted.shop

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.settings.profile.support.SupportOrderActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user.homescreen.chat.chatmessage.ChatActivity
import com.capcorp.ui.user.homescreen.orders.accept_detail.AcceptContract
import com.capcorp.ui.user.homescreen.orders.accept_detail.AcceptPresenter
import com.capcorp.ui.user.homescreen.orders.add_receiver.AddReceiverDetail
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailActivity
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetDriver
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.utils.location.LocationProvider
import com.capcorp.webservice.models.driver.driver_request.DriverStatusRequestNoPic
import com.capcorp.webservice.models.driver.driver_request_pic.DriverStatusRequestPic
import com.capcorp.webservice.models.driver.driver_request_pic.ReceiptImages
import com.capcorp.webservice.models.orders.OrderListing
import com.capcorp.webservice.models.orders.ReceiverInfo
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_detail_new.*
import kotlinx.android.synthetic.main.activity_shop_details_user.*
import kotlinx.android.synthetic.main.activity_shop_details_user.tvBack
import kotlinx.android.synthetic.main.activity_shop_details_user.tvItemName
import kotlinx.android.synthetic.main.activity_shop_details_user.tvRequiredByDate
import java.io.File
import java.text.MessageFormat
import java.util.*
import kotlin.collections.ArrayList

class ShopDetailsUserActivity : BaseActivity(), OnMapReadyCallback, View.OnClickListener,
    AcceptContract.View, Tracking, Utility.PassValues {
    override fun changeDriverStatusPic() {

        val dialog = Dialog(this,R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog_success)
        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        tvTitle.text = getString(R.string.success)
        tvDescription.text = getString(R.string.receipt_uploaded_successfully)
        dialogButton.text = getString(R.string.ok)
        dialogButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        val currentTimeStamp: Long = System.currentTimeMillis()
        Log.e("currentTime", currentTimeStamp.toString())


        orderListingDetail?.addressGivenDate = currentTimeStamp
        orderListingDetail?.driverStatus = DriverStatus.ADD_RECIEPT
        flagChange = true
        setOrderStatus()
    }

    private var orderListingDetail: OrderListing? = null
    private var mapFragment: MapFragment? = null
    private var presenter = AcceptPresenter()
    private var flagChange: Boolean = false
    private var isOrderCancel: Boolean = false
    private var currentLocation = ArrayList<Float>()
    //private lateinit var locationProvide: LocationProvider
    private var sourceLatLong: LatLng? = null
    private var arrivedAddress: String = ""
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var isCompleted = false
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var trackingAdapter: TrackingAdapter
    private var mediaType: String = ""
    private lateinit var utility: Utility
    lateinit var managePermissions: ManagePermissions
    private var receiptClicked: Boolean = false
    private var orderId : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_details_user)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        managePermissions = ManagePermissions(this)
        currentLocation.clear()
        currentLocation.add(0.0f)
        currentLocation.add(0.0f)
        isCompleted = intent.getBooleanExtra(IS_COMPLETED, false)

        if (mapFragment == null) {

            mapFragment = MapFragment.newInstance()
            mapFragment?.getMapAsync(this)
        }

        if (intent.hasExtra(NOTIFICATION)) {
            dialogIndeterminate.show()
            view_parent_view.visibility = View.INVISIBLE
            orderId = intent.getStringExtra(ORDER_ID)
        } else {
            view_parent_view.visibility = View.VISIBLE
            orderListingDetail =
                Gson().fromJson(intent.getStringExtra(ACCEPT_DETAIL), OrderListing::class.java)
            orderId = orderListingDetail?._id.toString()

        }
        orderId?.let { presenter.getOrderDetail(getAuthAccessToken(this), it) }
        clickListener()
    }

    private fun setRecyclerAdapter(orderList: OrderListing?) {
        layoutManager = LinearLayoutManager(this)
        rvItemsTracking.layoutManager = layoutManager
        trackingAdapter = orderList?.let { TrackingAdapter(this, it, this) }!!
        rvItemsTracking.adapter = trackingAdapter
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

    private fun clickListener() {
        tvBack.setOnClickListener(this)
        textView12.setOnClickListener(this)
        tvHelp.setOnClickListener(this)
        tvCallDriver.setOnClickListener(this)
        btnAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCallDriver -> {
                bottomSheetView()
            }
            R.id.tvHelp -> {
                startActivity(
                    Intent(this, SupportOrderActivity::class.java).putExtra(
                        "name",
                        orderListingDetail?.itemName
                    )
                        .putExtra("desc", orderListingDetail?.description)
                        .putExtra("orderId", orderListingDetail?._id)
                        .putExtra("showId", orderListingDetail?.orderId)
                        .putExtra(IS_COMPLETED, isCompleted)
                )
            }
            R.id.btnAdd -> {
                if (btnAdd.text == "ADD") {
                    startActivityForResult(
                        Intent(this, AddReceiverDetail::class.java)
                            .putExtra(ORDER_ID, orderListingDetail?._id), REQUEST_ADD_DETAIL
                    )
                } else {
                    startActivityForResult(
                        Intent(this, AddReceiverDetail::class.java)
                            .putExtra(ORDER_ID, orderListingDetail?._id)
                            .putExtra(
                                RECEIVER_INFO,
                                Gson().toJson(orderListingDetail?.receiverInfo)
                            ), REQUEST_ADD_DETAIL
                    )
                }
            }
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://18.236.46.218:8001/help")))

            R.id.tvBack -> {
                onBackPressed()
            }
            R.id.textView12 -> {
                startActivity(
                    Intent(this, RequestDetailActivity::class.java)
                        .putExtra(REQUEST_DETAIL, Gson().toJson(orderListingDetail))
                        .putExtra("fromAcceptDetail", true)
                        .putExtra("from_request", "false")
                )
            }

        }
    }

    private fun bottomSheetView() {
        val bottomSheetMedia = BottomSheetDriver()
        bottomSheetMedia.setOnChatListener(View.OnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(USER_ID, orderListingDetail?.accepted?._id)
            intent.putExtra(USER_NAME, orderListingDetail?.accepted?.fullName)
            intent.putExtra(PROFILE_PIC_URL, orderListingDetail?.accepted?.profilePicURL?.original)
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
                                    orderListingDetail?.accepted?.phoneNo
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


    override fun onMapReady(p0: GoogleMap) {

    }

    @SuppressLint("SetTextI18n")
    private fun setView(orderList: OrderListing?) {

        if (orderList?.type.equals("Pickup")) {
            if (orderList?.driverStatus.equals(DriverStatus.NO_ACTION)) {
                btnAdd.visibility = View.GONE
                btnAdd.text = getString(R.string.add)
                if (orderList?.receiverInfo?.employeeName != null) {
                    btnAdd.text = getString(R.string.edit)
                }
            } else {
                btnAdd.visibility = View.GONE
                btnAdd.text = getString(R.string.edit)
            }
        } else {
            btnAdd.visibility = View.GONE
        }

        if (orderList?.type != " Shop" && orderList?.type != "Delivery") {
            tvReciverDetail.text = getString(R.string.receiver_details)
            tvUserName.text = orderList?.receiverInfo?.employeeName
            tvPhoneNumber.text = orderList?.receiverInfo?.phoneNumber
        } else {
            tvReciverDetail.text = getString(R.string.traveler_details)
            tvUserName.text = orderList.userId?.fullName
            tvPhoneNumber.text = orderList.userId?.phoneNo
        }

        tvPrice.text = """${getString(R.string.currency_sign)} ${orderList?.accepted?.totalPrice}"""

        if(orderList?.couponUse == true){
            tvPrice?.text = getString(R.string.currency_sign) + " " + (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    (orderList.totalCheckout?.minus(orderList.discount))
            )) + " USD"
        } else {
            tvPrice?.text = getString(R.string.currency_sign) + " " + (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    orderList?.totalCheckout
            )) + " USD"
        }

        tvRequiredByDate.text =
            orderList?.accepted?.driverArrivalDate?.let { getOnlyDate(it, "EEE · MMM d") }
        setRecyclerAdapter(orderList)

        tvUserName.text = orderList?.accepted?.fullName
        tvPhoneNumber.text = orderList?.accepted?.phoneNo

        if ((orderList?.orderCode != null) && (!orderList.orderCode.equals(""))) {
            tvOrderCode.text = getString(R.string.order_code) + " : " + orderList.orderCode
        }

        Glide.with(this)
            .load(
                MAP_SCREENSHOT_BASE_URL + orderList?.accepted?.location?.get(1) + "," + orderList?.accepted?.location?.get(
                    0
                ) + MAP_SCREENSHOT_END_URL + getString(R.string.google_places_api_key)
            )
            .into(ivMap)

        Glide.with(this)
            .load(orderList?.accepted?.profilePicURL?.original)
            .apply(
                RequestOptions.circleCropTransform().placeholder(R.drawable.profile_pic_placeholder)
            )
            .into(ivMapUser)

        tvMapName.text = orderList?.accepted?.fullName


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
        // setOrderStatus()
    }

    private fun setOrderStatus() {
        presenter.getOrderDetail(getAuthAccessToken(this), orderListingDetail?._id.toString())
    }

    override fun onBackPressed() {
        if (intent.hasExtra(NOTIFICATION)) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            flagChange = true
            val resultIntent = Intent()
            if (flagChange) {
                resultIntent.putExtra(ORDER_CANCEL, isOrderCancel)
                resultIntent.putExtra(ACCEPT_DETAIL, Gson().toJson(orderListingDetail))
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                setResult(Activity.RESULT_CANCELED, resultIntent)
            }
            finish()
        }

    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        tvMapName.showSWWerror()
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
                tvMapName.showSnack(errorBody!!)
            }
        }

    }

    override fun validationsFailure(type: String?) {
    }

    override fun cancelSucess() {
        isOrderCancel = true
        tvMapName.showSnack(R.string.order_cancelled_successfully)
        onBackPressed()
    }

    override fun orderMarkCompleteSucess() {
        orderListingDetail?.driverStatus = DriverStatus.DELIVERED
        setOrderStatus()
    }

    override fun changeDriverStatus() {
        val dialog = Dialog(this,R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog_success)
        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        tvTitle.text = getString(R.string.success)
        tvDescription.text = getString(R.string.order_confirmed_successfully)
        dialogButton.text = getString(R.string.ok)
        dialogButton.setOnClickListener {
            dialog.dismiss()
            flagChange = true
            orderId?.let { presenter.getOrderDetail(getAuthAccessToken(this), it) }
        }
        dialog.show()

        setOrderStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun orderDetailSuccess(data: OrderListing) {
        dialogIndeterminate.dismiss()
        view_parent_view.visibility = View.VISIBLE
        orderListingDetail = data
        setView(orderListingDetail)
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

    override fun openCameraOrGallery(isFrom: String) {
        permissionSafetyMethod()
    }

    override fun enterCodeDialog() {

    }

    private fun mediaOption() {
        val bottomSheetMedia = BottomSheetMedia()
        bottomSheetMedia.isCancelable = false
        bottomSheetMedia.setOnCameraListener(View.OnClickListener {
            openCamera()
            bottomSheetMedia.dismiss()
        })

        bottomSheetMedia.setOnGalleryListener(View.OnClickListener {
            openGallery()
            bottomSheetMedia.dismiss()

        })

        bottomSheetMedia.setOnCancelListener(View.OnClickListener {
            bottomSheetMedia.dismiss()
        })
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
            if (managePermissions.checkPermissions()) mediaOption()
        } else mediaOption()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this::utility.isInitialized)
            utility.onActivityResult(requestCode, resultCode, data)
        else
            when (requestCode) {
                REQUEST_ADD_DETAIL -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val receiverInformation = ReceiverInfo(
                            employeeName = data?.getStringExtra("EmployeeName").toString(),
                            emailId = data?.getStringExtra("EmployeeEmail").toString(),
                            phoneNumber = data?.getStringExtra("EmployeePhone").toString()
                        )

                        orderListingDetail?.receiverInfo = receiverInformation
                        orderListingDetail?.isOTPSend = true
                        setView(orderListingDetail)
                        flagChange = true
                        callDriveStatusAPI(DriverStatus.COMMITED)
                    } else if (resultCode == Activity.RESULT_CANCELED) {

                    }
                }
            }
    }

    override fun passImageURI(file: File?, photoURI: Uri?) {
        //val atIndex = mImageList.size - 1
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
                receiptClicked = true
                val receiptImages = mutableListOf<ReceiptImages>()
                receiptImages.add(ReceiptImages(preGeneratedUrl, preGeneratedUrl))
                presenter.driverStatusPic(
                    getAuthAccessToken(this), DriverStatusRequestPic(
                        receiptImages,
                        null,
                        orderListingDetail?._id.toString(),
                        DriverStatus.ADD_RECIEPT,
                        currentLocation,
                        currentLocation.get(1).toDouble(),
                        currentLocation.get(0).toDouble(),
                        arrivedAddress,
                        orderListingDetail?.accepted?._id
                            ?: ""
                    )
                )
        } else {
            receiptClicked = true
            val receiptImages = mutableListOf<ReceiptImages>()
            receiptImages.add(ReceiptImages(preGeneratedUrl, preGeneratedUrl))
            presenter.driverStatusPic(
                getAuthAccessToken(this), DriverStatusRequestPic(
                    receiptImages,
                    null,
                    orderListingDetail?._id.toString(),
                    DriverStatus.ADD_RECIEPT,
                    currentLocation,
                    currentLocation.get(1).toDouble(),
                    currentLocation.get(0).toDouble(),
                    arrivedAddress,
                    orderListingDetail?.accepted?._id
                        ?: ""
                )
            )
        }
    }
}
