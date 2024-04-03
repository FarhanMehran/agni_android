package com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.home.transport.addpictures.AddPictureAdapter
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.webservice.models.ItemImageModel
import com.capcorp.webservice.models.product_information.ProductInformation
import com.capcorp.webservice.models.request_model.ShipDataRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_shipment.*
import java.io.File
import java.util.regex.Pattern

class ShipmentActivity : BaseActivity(), View.OnClickListener,
    AddPictureAdapter.DeletedImagePosition, Utility.PassValues {

    val shipData = ShipDataRequest()
    private lateinit var imageAdapter: AddPictureAdapter
    private var mImageList = ArrayList<ItemImageModel>()
    private lateinit var utility: Utility
    private var mediaType: String = ""
    private var managePermissions: ManagePermissions? = null
    private var itemCount = 0

    override fun onStart() {
        if(mImageList.isEmpty()) {
            mImageList.add(ItemImageModel("", ""))     // For initial add image card
        }else{
            if(mImageList.isNotEmpty() && !mImageList[0].thumbnail.isNullOrEmpty()){
                mImageList.add(0,ItemImageModel("", ""))     // For initial add image card
            }
        }
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment)
        managePermissions = ManagePermissions(this)
        itemCount = edtQuantity.text.toString().toInt()
        shipData.type = OrderType.SHOP
        clDeliveryOnly.background = null
        clPickup.background = null
        clShop.setBackgroundResource(R.drawable.drawable_border_green_button)
        clTax.visibility = View.VISIBLE
        image1Right.visibility = View.GONE
        image2Right.visibility = View.VISIBLE
        image3Right.visibility = View.GONE

        if (intent.hasExtra("content")) {
            val productInformation: ProductInformation =
                intent.getSerializableExtra("content") as ProductInformation
            if (!productInformation.price.isNullOrEmpty())
                edtPrice.setText(productInformation.price)
            if (!productInformation.description.isNullOrEmpty())
                edtDescription.setText(productInformation.description)
            if (!productInformation.title.isNullOrEmpty())
                edtName.setText(productInformation.title)
            if (!productInformation.imgUrl.isNullOrEmpty())
                productInformation.imgUrl?.let {
                    if (it.contains("?")) {
                        mImageList.add(
                            ItemImageModel(
                                it.split("?")[0],
                                it.split("?")[0],
                                it.split("?")[0]
                            )
                        )
                    } else {
                        mImageList.add(ItemImageModel(it, it, it))
                    }
                }
            if (intent.hasExtra("url") && !intent.getStringExtra("url").isNullOrEmpty())
                edtURL.setText(intent.getStringExtra("url"))

        }

        tvBack.setOnClickListener(this)

        cvDeliveryOnly.setOnClickListener(this)
        cvShop.setOnClickListener(this)
        cvPickup.setOnClickListener(this)

        clMini.setOnClickListener(this)
        clMedium.setOnClickListener(this)
        clLarge.setOnClickListener(this)
        clSmall.setOnClickListener(this)
        btnMinus.setOnClickListener(this)
        btnAdd.setOnClickListener(this)

        tvPrice.setOnClickListener(this)
        tvTax.setOnClickListener(this)

        next.setOnClickListener(this)

        tvLearnMorePickup.setOnClickListener(this)
        tvLearnMoreDeliveryOnly.setOnClickListener(this)
        tvLearnMoreShop.setOnClickListener(this)

        tvSelectProductSize.setOnClickListener(this)

        shipData.itemSize = ItemSize.POCKET

        imageAdapter()

        edtPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val price = s.toString()
                if (price.isEmpty()) {
                    edtPrice.setText("$")
                    edtPrice.setSelection(edtPrice.length())
                }
            }
        })

    }

    private fun imageAdapter() {
        imageAdapter =
            AddPictureAdapter(adapterCallback, this, rvItemsImages.context, mImageList, 3)
        rvItemsImages.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvItemsImages.isNestedScrollingEnabled = false
        rvItemsImages.adapter = imageAdapter
    }

    private val adapterCallback = object : AddPictureAdapter.AdapterCallback {
        override fun onAddImageClicked() {
            if (mImageList.size < 4) {
                permissionSafetyMethod()
            }
        }
    }

    private fun permissionSafetyMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (managePermissions!!.checkPermissions()) mediaOption()
        } else mediaOption()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        utility.onActivityResult(requestCode, resultCode, data)
    }

    private fun mediaOption() {
        val bottomSheetMedia = BottomSheetMedia()
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
        bottomSheetMedia.show((this as FragmentActivity).supportFragmentManager, "camera")
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
        mImageList.add(ItemImageModel(preGeneratedUrl, preGeneratedUrl, file.absolutePath))
        imageAdapter.notifyDataSetChanged()
    }


    override fun deletedImagePos(pos: Int) {
        mImageList.removeAt(pos)
        imageAdapter.notifyDataSetChanged()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBack -> {
                finish()
            }
            R.id.tvLearnMoreDeliveryOnly->{
                openBottomSheetDialogForLearnMore(getString(R.string.delivery_only))
            }
            R.id.tvSelectProductSize->{
                openBottomSheetDialog("ProductSize")
            }
            R.id.tvLearnMorePickup->{
                openBottomSheetDialogForLearnMore(getString(R.string.pickup_delivery))
            }
            R.id.tvLearnMoreShop->{
                openBottomSheetDialogForLearnMore(getString(R.string.shop_delivery))
            }
            R.id.cvDeliveryOnly -> {
                shipData.type = OrderType.DELIVERY
                clDeliveryOnly.setBackgroundResource(R.drawable.drawable_border_green_button)
                clPickup.background = null
                clShop.background = null
                clTax.visibility = View.VISIBLE
                image1Right.visibility = View.VISIBLE
                image2Right.visibility = View.GONE
                image3Right.visibility = View.GONE
            }
            R.id.cvShop -> {
                shipData.type = OrderType.SHOP
                clDeliveryOnly.background = null
                clPickup.background = null
                clShop.setBackgroundResource(R.drawable.drawable_border_green_button)
                clTax.visibility = View.VISIBLE
                image1Right.visibility = View.GONE
                image2Right.visibility = View.VISIBLE
                image3Right.visibility = View.GONE
            }
            R.id.cvPickup -> {
                shipData.type = OrderType.PICKUP
                clDeliveryOnly.background = null
                clPickup.setBackgroundResource(R.drawable.drawable_border_green_button)
                clShop.background = null
                clTax.visibility = View.VISIBLE

                image1Right.visibility = View.GONE
                image2Right.visibility = View.GONE
                image3Right.visibility = View.VISIBLE
            }
            R.id.clMini -> {
                shipData.itemSize = ItemSize.POCKET
                clMini.setBackgroundResource(R.drawable.drawable_border_green_button)
                clSmall.background = null
                clMedium.background = null
                clLarge.background = null

            }
            R.id.clSmall -> {
                shipData.itemSize = ItemSize.SMALL
                clMini.background = null
                clSmall.setBackgroundResource(R.drawable.drawable_border_green_button)
                clMedium.background = null
                clLarge.background = null
            }
            R.id.clMedium -> {
                shipData.itemSize = ItemSize.MEDIUM
                clMini.background = null
                clSmall.background = null
                clMedium.setBackgroundResource(R.drawable.drawable_border_green_button)
                clLarge.background = null
            }
            R.id.clLarge -> {
                shipData.itemSize = ItemSize.LARGE
                clMini.background = null
                clSmall.background = null
                clMedium.background = null
                clLarge.setBackgroundResource(R.drawable.drawable_border_green_button)
            }
            R.id.btnMinus -> {
                if (itemCount >= 1) {
                    itemCount--
                    edtQuantity.setText(itemCount.toString())
                }
            }
            R.id.btnAdd -> {
                if (itemCount >= 0) {
                    itemCount++
                    edtQuantity.setText(itemCount.toString())
                }
            }
            R.id.tvPrice -> openBottomSheetDialog("ItemPrice")
            R.id.tvTax -> openBottomSheetDialog("Tax")
            R.id.next -> checkValidationAndPushNext()
        }
    }

    private fun checkValidationAndPushNext() {
        val itemName = edtName.text.toString().trim()
        val itemDescription = edtDescription.text.toString().trim()
        val itemWebURl = edtURL.text.toString().trim()
        val itemPrice = edtPrice.text.toString().trim().replace("$", "")
        val itemQuantity = edtQuantity.text.toString().trim()
        val tax = edtTax.text.toString().trim()
        if(shipData.type.isEmpty()){
            root_ship_details.showSnack(R.string.please_select_delivery_type)
        }else {
            if (shipData.type.equals(getString(R.string.delivery))) {
                if (isValidationsOk(itemName, itemPrice, itemQuantity, tax)) {
                    var amount: Double = 0.0
                    var finalItemPrice: Double = 0.0
                    var res: Double = 0.0
                    var recommendedReward: Double = 0.0
                    amount = itemPrice.toDouble() * itemQuantity.toDouble()
                    shipData.itemName = itemName
                    shipData.description = itemDescription
                    shipData.itemUrl = itemWebURl
                    shipData.itemPrice = itemPrice
                    shipData.itemQuantity = itemQuantity
                    shipData.originalPacking =
                        if (toggleSwitch.checkedTogglePosition == 0) "true" else "false"
                    shipData.insurance = "true"
                    Log.d(":::::::Data",mImageList.toString())
                    mImageList.removeAt(0)
                    shipData.shipItemImages.clear()
                    Log.d(":::::::Data::::After:::",mImageList.toString())
                    shipData.shipItemImages.addAll(mImageList)
                    Log.d(":::::::Data::::After:::",shipData.shipItemImages.toString())
                    when (shipData.itemSize) {
                        ItemSize.POCKET -> {
                            res = (amount * 0.10)
                        }
                        ItemSize.SMALL -> {
                            res = (amount * 0.15)
                        }
                        ItemSize.MEDIUM -> {
                            res = (amount * 0.18)
                        }
                        ItemSize.LARGE -> {
                            res = (amount * 0.20)
                        }
                        else -> {
                            res = (amount * 0.10)
                        }
                    }
                    recommendedReward = res
                    if (tax.trim().isNotEmpty()) {
                        shipData.tax = tax
                        shipData.taxInclude = "true"
                        shipData.recommendedReward = recommendedReward.toString() + tax
                    } else {
                        shipData.taxInclude = "false"
                        shipData.recommendedReward = recommendedReward.toString()
                    }
                    startActivity(
                        Intent(this, ShipmentActivityNextStep::class.java)
                            .putExtra("data", shipData)
                    )

                }
            } else {
                if (isValidationsUrl(itemName, itemWebURl, itemPrice, itemQuantity, tax)) {
                    var amount: Double = 0.0
                    var res: Double = 0.0
                    var recommendedReward: Double = 0.0
                    amount = itemPrice.toDouble() * itemQuantity.toDouble()
                    shipData.itemName = itemName
                    shipData.description = itemDescription
                    shipData.itemUrl = itemWebURl
                    shipData.itemPrice = itemPrice
                    shipData.itemQuantity = itemQuantity
                    shipData.originalPacking =
                        if (toggleSwitch.checkedTogglePosition == 0) "true" else "false"
                    shipData.insurance = "true"

                    Log.d(":::::::Data",mImageList.toString())
                    mImageList.removeAt(0)
                    shipData.shipItemImages.clear()
                    Log.d(":::::::Data::::After:::",mImageList.toString())
                    shipData.shipItemImages.addAll(mImageList)
                    Log.d(":::::::Data::::After:::",shipData.shipItemImages.toString())

                    if (tax.trim().isNotEmpty()) {
                        shipData.tax = tax
                        shipData.taxInclude = "true"
                    }else{
                        shipData.taxInclude = "true"
                    }
                    when (shipData.itemSize) {
                        ItemSize.POCKET -> {
                            res = (amount * 0.10)
                        }
                        ItemSize.SMALL -> {
                            res = (amount * 0.15)
                        }
                        ItemSize.MEDIUM -> {
                            res = (amount * 0.18)
                        }
                        ItemSize.LARGE -> {
                            res = (amount * 0.20)
                        }
                        else -> {
                            res = (amount * 0.10)
                        }
                    }
                    recommendedReward = res
                    if (edtTax.text.trim().isNotEmpty()) {
                        shipData.recommendedReward = String.format("%.2f",recommendedReward + tax.toDouble())
                    } else {
                        shipData.recommendedReward = String.format("%.2f",recommendedReward)
                    }
                    startActivity(
                        Intent(this, ShipmentActivityNextStep::class.java)
                            .putExtra("data", shipData)
                    )
                }
            }
        }
    }

    val WEB_REGEX: Pattern =
        Pattern.compile("(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-zA-Z0-9].?([a-z]+)?")

    private fun isValidationsUrl(
        itemName: String,
        itemWebURl: String,
        itemPrice: String,
        itemQuantity: String,
        tax: String
    ): Boolean {
        return if (mImageList.isEmpty() || mImageList.size == 1) {
            root_ship_details.showSnack(R.string.please_add_atleast_one_image)
            return false
        } else if (shipData.itemSize.isEmpty()) {
            root_ship_details?.showSnack(getString(R.string.please_select_item_size))
            return false
        } else if (itemName.isEmpty()) {
            root_ship_details?.showSnack(getString(R.string.please_enter_item_name))
            return false
        } else if (itemWebURl.isEmpty()) {
            root_ship_details?.showSnack(getString(R.string.please_enter_item_web_url))
            return false
        } /*else if ((!WEB_REGEX.matcher(itemWebURl.trim { it <= ' ' }).matches())) {
            root_ship_details?.showSnack(getString(R.string.please_enter_valid_web_url))
            return false
        }*/ else if (itemPrice.isEmpty()) {
            root_ship_details?.showSnack(getString(R.string.please_enter_item_price))
            return false
        } else if (itemPrice.startsWith("0")) {
            root_ship_details?.showSnack(getString(R.string.item_price_cannot_zero))
            return false
        } else if (itemQuantity.toInt() == 0) {
            root_ship_details?.showSnack(getString(R.string.please__enter_valid_item_quantity))
            return false
        } else {
            true
        }
    }

    private fun isValidationsOk(
        itemName: String,
        itemPrice: String,
        itemQuantity: String,
        tax: String
    ): Boolean {
        return if (mImageList.isEmpty() || mImageList.size == 1) {
            root_ship_details.showSnack(R.string.please_add_atleast_one_image)
            return false
        } else if (shipData.itemSize.isEmpty()) {
            root_ship_details?.showSnack(getString(R.string.please_select_item_size))
            return false
        } else if (itemName.isEmpty()) {
            root_ship_details?.showSnack(getString(R.string.please_enter_item_name))
            return false
        } else if (itemPrice.isEmpty()) {
            root_ship_details?.showSnack(getString(R.string.please_enter_item_price))
            return false
        } else if (itemPrice.startsWith("0")) {
            root_ship_details?.showSnack(getString(R.string.item_price_cannot_zero))
            return false
        } else if (itemQuantity.toInt() == 0) {
            root_ship_details?.showSnack(getString(R.string.please__enter_valid_item_quantity))
            return false
        } else {
            true
        }
    }

    private fun openBottomSheetDialog(type: String) {
        val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val view = layoutInflater.inflate(R.layout.dialog_info, null)
        val clItemPrice = view.findViewById<ConstraintLayout>(R.id.clPrice)
        val clTax = view.findViewById<ConstraintLayout>(R.id.clTax)
        val clH2dFee = view.findViewById<ConstraintLayout>(R.id.clH2dFee)
        val clProcessingFee = view.findViewById<ConstraintLayout>(R.id.clProcessingFee)
        val clTravelRewards = view.findViewById<ConstraintLayout>(R.id.clTravelRewards)
        val clProductSize = view.findViewById<ConstraintLayout>(R.id.clProductSize)
        if (type.equals("ItemPrice")) {
            clItemPrice.visibility = View.VISIBLE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clProductSize.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
        } else if (type.equals("Tax")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.VISIBLE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clProductSize.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
        } else if (type.equals("ProductSize")) {
            clItemPrice.visibility = View.GONE
            clTax.visibility = View.GONE
            clH2dFee.visibility = View.GONE
            clProcessingFee.visibility = View.GONE
            clTravelRewards.visibility = View.GONE
            clProductSize.visibility = View.VISIBLE
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun openBottomSheetDialogForLearnMore(type: String) {
        val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = layoutInflater.inflate(R.layout.dialog_learn_more, null)
        val tvType = view.findViewById<TextView>(R.id.tvType)
        tvType.text = type
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }
}
