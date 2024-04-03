package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class OrderDetails(

    @field:SerializedName("orderType")
    val orderType: String? = null,

    @field:SerializedName("groceryItems")
    val groceryItems: List<Grocessary?>? = null,

    @field:SerializedName("pickAddress")
    val pickAddress: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("purchaseMadeLatlong")
    val purchaseMadeLatlong: List<Double?>? = null,

    @field:SerializedName("completeDate")
    val completeDate: Any? = null,

    @field:SerializedName("arrivedLatLong")
    val arrivedLatLong: List<Double?>? = null,

    @field:SerializedName("itemName")
    val itemName: String? = null,

    @field:SerializedName("rejectReason")
    val rejectReason: List<Any?>? = null,

    @field:SerializedName("userCaptureTxnId")
    val userCaptureTxnId: String? = null,

    @field:SerializedName("itemGrossTotal")
    val itemGrossTotal: Double? = null,

    @field:SerializedName("isBanned")
    val isBanned: Boolean? = null,

    @field:SerializedName("payment")
    val payment: Double? = null,

    @field:SerializedName("userUnCaptureAmount")
    val userUnCaptureAmount: Double? = null,

    @field:SerializedName("companyOffers")
    val companyOffers: List<Any?>? = null,

    @field:SerializedName("purchaseMadeDate")
    val purchaseMadeDate: Double? = null,

    @field:SerializedName("h2dFee")
    val h2dFee: String? = null,

    @field:SerializedName("prevTotalPrice")
    val prevTotalPrice: Double? = null,

    @field:SerializedName("arrivedAddress")
    val arrivedAddress: String? = null,

    @field:SerializedName("orderRefundIssueStatus")
    val orderRefundIssueStatus: String? = null,

    @field:SerializedName("driverRefund")
    val driverRefund: Boolean? = null,

    @field:SerializedName("userLastReAuthPaymentDate")
    val userLastReAuthPaymentDate: Any? = null,

    @field:SerializedName("republish")
    val republish: Boolean? = null,

    @field:SerializedName("cardId")
    val cardId: String? = null,

    @field:SerializedName("isDismantleNeeded")
    val isDismantleNeeded: Boolean? = null,

    @field:SerializedName("purchaseAddress")
    val purchaseAddress: String? = null,

    @field:SerializedName("dropDownLocation")
    val dropDownLocation: List<Double?>? = null,

    @field:SerializedName("userUnCapturerReceiptUrl")
    val userUnCapturerReceiptUrl: String? = null,

    @field:SerializedName("refund")
    val refund: Boolean? = null,

    @field:SerializedName("isElevated")
    val isElevated: Boolean? = null,

    @field:SerializedName("driverCapturerReceiptUrl")
    val driverCapturerReceiptUrl: String? = null,

    @field:SerializedName("isDispatched")
    val isDispatched: Boolean? = null,

    @field:SerializedName("driverAuthPaymentId")
    val driverAuthPaymentId: Any? = null,

    @field:SerializedName("driverStatus")
    val driverStatus: String? = null,

    @field:SerializedName("issendDeliveryReward")
    val issendDeliveryReward: Boolean? = null,

    @field:SerializedName("userAuthpaymentId")
    val userAuthpaymentId: String? = null,

    @field:SerializedName("receiptImages")
    val receiptImages: List<Any?>? = null,

    @field:SerializedName("isDeleted")
    val isDeleted: Boolean? = null,

    @field:SerializedName("wrongItemDesc")
    val wrongItemDesc: String? = null,

    @field:SerializedName("isDriverRated")
    val isDriverRated: Double? = null,

    @field:SerializedName("dropDownCountry")
    val dropDownCountry: String? = null,

    @field:SerializedName("driverUnCaptureAmount")
    val driverUnCaptureAmount: Double? = null,

    @field:SerializedName("itemQuantity")
    val itemQuantity: Double? = null,

    @field:SerializedName("pickUpAdditionalNotes")
    val pickUpAdditionalNotes: String? = null,

    @field:SerializedName("forwardRequest")
    val forwardRequest: List<Any?>? = null,

    @field:SerializedName("userId")
    val userId: UserId? = null,

    @field:SerializedName("createdDate")
    val createdDate: Long? = null,

    @field:SerializedName("confirmationLatLong")
    val confirmationLatLong: List<Double?>? = null,

    @field:SerializedName("paymentID")
    val paymentID: String? = null,

    @field:SerializedName("dropDownAddress")
    val dropDownAddress: String? = null,

    @field:SerializedName("itemPrice")
    val itemPrice: Double? = null,

    @field:SerializedName("driverToUserTotalRating")
    val driverToUserTotalRating: Double? = null,

    @field:SerializedName("committedLatLong")
    val committedLatLong: List<Double?>? = null,

    @field:SerializedName("itemUrl")
    val itemUrl: String? = null,

    @field:SerializedName("insurance")
    val insurance: Boolean? = null,

    @field:SerializedName("confirmationDate")
    val confirmationDate: Double? = null,

    @field:SerializedName("driverLastReAuthPaymentDate")
    val driverLastReAuthPaymentDate: Any? = null,

    @field:SerializedName("wrongitemPicUrl")
    val wrongitemPicUrl: WrongitemPicUrl? = null,

    @field:SerializedName("driverRefundAmt")
    val driverRefundAmt: String? = null,

    @field:SerializedName("reports")
    val reports: List<Any?>? = null,

    @field:SerializedName("totalCheckout")
    val totalCheckout: Double? = null,

    @field:SerializedName("pickUpCountry")
    val pickUpCountry: String? = null,

    @field:SerializedName("pickDate")
    val pickDate: Double? = null,

    @field:SerializedName("adminFee")
    val adminFee: Double? = null,

    @field:SerializedName("userToDriverTotalRating")
    val userToDriverTotalRating: Double? = null,

    @field:SerializedName("mode")
    val mode: String? = null,

    @field:SerializedName("deliveredLatLong")
    val deliveredLatLong: List<Double?>? = null,

    @field:SerializedName("deliveredDate")
    val deliveredDate: Double? = null,

    @field:SerializedName("addReceiptDate")
    val addReceiptDate: Double? = null,

    @field:SerializedName("committedAddress")
    val committedAddress: String? = null,

    @field:SerializedName("__v")
    val V: Double? = null,

    @field:SerializedName("itemSize")
    val itemSize: String? = null,

    @field:SerializedName("driverCaptureTxnId")
    val driverCaptureTxnId: String? = null,

    @field:SerializedName("originalPacking")
    val originalPacking: Boolean? = null,

    @field:SerializedName("transferDeliveryRewardId")
    val transferDeliveryRewardId: String? = null,

    @field:SerializedName("shipItemImages")
    val shipItemImages: List<ShipItemImagesItem?>? = null,

    @field:SerializedName("offers")
    val offers: List<OffersItem?>? = null,

    @field:SerializedName("deliveredAddress")
    val deliveredAddress: String? = null,

    @field:SerializedName("dropDownAdditionalNotes")
    val dropDownAdditionalNotes: String? = null,

    @field:SerializedName("accepted")
    val accepted: Accepted? = null,

    @field:SerializedName("tax")
    val tax: Double? = null,

    @field:SerializedName("dimensionArray")
    val dimensionArray: List<Any?>? = null,

    @field:SerializedName("orderZone")
    val orderZone: OrderZone? = null,

    @field:SerializedName("jobId")
    val jobId: String? = null,

    @field:SerializedName("pickUpDate")
    val pickUpDate: Long? = null,

    @field:SerializedName("taxInclude")
    val taxInclude: Boolean? = null,

    @field:SerializedName("userCapturerReceiptUrl")
    val userCapturerReceiptUrl: String? = null,

    @field:SerializedName("dispatchedStores")
    val dispatchedStores: List<Any?>? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("send")
    val send: Boolean? = null,

    @field:SerializedName("driverUnCapturerReceiptUrl")
    val driverUnCapturerReceiptUrl: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("orderStatus")
    val orderStatus: String? = null,

    @field:SerializedName("recommendedReward")
    val recommendedReward: Double? = null,

    @field:SerializedName("isDriverCancel")
    val isDriverCancel: Boolean? = null,

    @field:SerializedName("createdChargeDate")
    val createdChargeDate: Long? = null,

    @field:SerializedName("itemImages")
    val itemImages: List<ItemImagesItem?>? = null,

    @field:SerializedName("isUserRated")
    val isUserRated: Double? = null,

    @field:SerializedName("isFragile")
    val isFragile: Boolean? = null,

    @field:SerializedName("innerParcelImagesURL")
    val innerParcelImagesURL: List<InnerParcelImagesURLItem?>? = null,

    @field:SerializedName("isCapture")
    val isCapture: Boolean? = null,

    @field:SerializedName("paymentStatus")
    val paymentStatus: String? = null,

    @field:SerializedName("refundAmt")
    val refundAmt: String? = null,

    @field:SerializedName("isOTPSend")
    val isOTPSend: Boolean? = null,

    @field:SerializedName("confirmationAddress")
    val confirmationAddress: String? = null,

    @field:SerializedName("pickUpAddress")
    val pickUpAddress: String? = null,

    @field:SerializedName("liveTracking")
    val liveTracking: List<Double?>? = null,

    @field:SerializedName("outerParcelImagesURL")
    val outerParcelImagesURL: List<OuterParcelImagesURLItem?>? = null,

    @field:SerializedName("arrivedDate")
    val arrivedDate: Double? = null,

    @field:SerializedName("productImages")
    val productImages: List<Any?>? = null,

    @field:SerializedName("rejectedRequest")
    val rejectedRequest: List<Any?>? = null,

    @field:SerializedName("addressGivenDate")
    val addressGivenDate: Double? = null,

    @field:SerializedName("committedDate")
    val committedDate: Double? = null,

    @field:SerializedName("pickDateLatLong")
    val pickDateLatLong: List<Double?>? = null,

    @field:SerializedName("reportCount")
    val reportCount: Double? = null,

    @field:SerializedName("orderCode")
    val orderCode: String? = null,

    @field:SerializedName("companyForwardRequest")
    val companyForwardRequest: List<Any?>? = null,

    @field:SerializedName("pickUpLocation")
    val pickUpLocation: List<Double?>? = null
)