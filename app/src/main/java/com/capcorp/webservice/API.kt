package com.capcorp.webservice

import com.capcorp.ui.driver.stripe.ConnectStripeOnBoarding
import com.capcorp.ui.driver.stripe.StripeAccountData
import com.capcorp.ui.payment.model.Card
import com.capcorp.ui.payment.model.CardData
import com.capcorp.utils.*
import com.capcorp.webservice.models.*
import com.capcorp.webservice.models.chats.ChatListing
import com.capcorp.webservice.models.chats.ChatMessageList
import com.capcorp.webservice.models.driver.driver_request.DriverStatusRequestNoPic
import com.capcorp.webservice.models.driver.driver_request_pic.DriverStatusRequestPic
import com.capcorp.webservice.models.getcountries.GetCountry
import com.capcorp.webservice.models.home.HomeDataResponse
import com.capcorp.webservice.models.image_upload.ImageUploadModel
import com.capcorp.webservice.models.images.MainData
import com.capcorp.webservice.models.notifications.Notifications
import com.capcorp.webservice.models.orders.Order
import com.capcorp.webservice.models.orders.OrderListing
import com.capcorp.webservice.models.parceloffices.ParcelApiResponse
import com.capcorp.webservice.models.product_information.ProductInformation
import com.capcorp.webservice.models.request_model.*
import com.capcorp.webservice.models.select_trips.AllTrips
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

const val authorization = "authorization"

interface API {

    /*----------------------    User's Apis    ------------------------*/

    @FormUrlEncoded
    @POST("/api/user/checkPhoneNumberExists")
    fun checkPhoneNumberExist(
        @Field("fullNumber") phoneNumber: String,
        @Field("type") type: String,
        @Field("asCheck") asCheck: String
    ): Call<ApiResponse<SignupModel>>

    @FormUrlEncoded
    @POST("/api/user/sendOTP")
    fun sendOtp(
        @Field("fullNumber") phoneNumber: String,
        @Field("oldfullNumber") oldfullNumber: String?,
        @Field("asSend") asSend: String
    ): Call<ApiResponse<SendOtpResponse>>


    @GET("/api/user/checkEmailExists")
    fun checkEmailExists(@Query("emailId") type: String?): Call<ApiResponse<SignupModel>>

    @FormUrlEncoded
    @POST("/api/user/appSignUp")
    fun signUp(@FieldMap map: Map<String, String>): Call<ApiResponse<SignupModel>>


    @POST("/api/user/appSignUp")
    fun travellerSignup(@Body body: SignUpModelRequest): Call<ApiResponse<SignupModel>>

    @Multipart
    @POST("/api/user/appSignUp")
    fun shopperSignup(
        @Part("countryCode") countryCode: RequestBody,
        @Part("fullNumber") fullNumber: RequestBody,
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("emailId") emailId: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part("defaultLat") defaultLat: RequestBody,
        @Part("defaultLong") defaultLong: RequestBody,
        @Part("password") password: RequestBody,
        @Part("deviceType") deviceType: RequestBody,
        @Part("type") type: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("deviceToken") deviceToken: RequestBody,
        @Part("country") country: RequestBody,
        @Part("languageId") languageId: RequestBody,
        @Part("countryISO") countryISO: RequestBody
    ): Call<ApiResponse<SignupModel>>

    @FormUrlEncoded
    @POST("/api/company/registrationForm")
    fun companySignup(@FieldMap map: Map<String, String>): Call<ApiResponse<SignupModel>>

    @Multipart
    @POST("/api/commonRoutes/uploadFile")
    fun uploadImage(
        @Part("fileOf") fileOf: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ApiResponse<ImageUploadModel>>

    @FormUrlEncoded
    @POST("/api/user/appLogin")
    fun login(@FieldMap map: Map<String, String>): Call<ApiResponse<SignupModel>>

    @FormUrlEncoded
    @POST("/api/user/socialLogin")
    fun socialLogin(@FieldMap map: Map<String, String>): Call<ApiResponse<SignupModel>>

    @FormUrlEncoded
    @POST("/api/user/changePassword")
    fun apiChangePassword(
        @Field("otp") otp: String, @Field("fullNumber") phoneNo: String,
        @Field("newPassword") newPassword: String
    ): Call<PojoSuccess>

    @PUT("/api/commonRoutes/logout")
    fun logout(@Header(AUTHORISATION) authorisation: String): Call<ApiResponse<Any>>

    @POST("/api/user/support")
    fun support(
        @Header(AUTHORISATION) authorisation: String,
        @Body body: SupportRequest
    ): Call<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("/api/user/blockPush")
    fun blockPush(
        @Header(AUTHORISATION) authorisation: String,
        @Field("updateStatus") updateStatus: String
    ): Call<ApiResponse<SignupModel>>

    @FormUrlEncoded
    @POST("/api/user/submitRating")
    fun ratingrequest(
        @Header(AUTHORISATION) authorisation: String,
        @Field("rating") rating: Double,
        @Field("orderId") orderId: String,
        @Field("description") description: String
    ): Call<ApiResponse<Any>>

    @GET("/api/user/orderListing")
    fun orderListing(
        @Header(AUTHORISATION) authorisation: String,
        @Query(ORDER_STATUS) orderStatus: String,
        @Query(LIMIT) limit: String,
        @Query(SKIP) skip: String
    ): Call<ApiResponse<Order>>

    @FormUrlEncoded
    @POST("/api/user/deleteOrder")
    fun deleteOrder(
        @Header(AUTHORISATION) authorisation: String,
        @Field(ORDER_ID) orderId: String,
        @Field(TRAVELER_ACTION) travelerAction: String
    ): Call<ApiResponse<Any>>


    @GET("/api/user/myTrips")
    fun myTrips(
        @Header(AUTHORISATION) authorisation: String,
        @Query(LIMIT) limit: String,
        @Query(SKIP) skip: String
    ): Call<ApiResponse<AllTrips>>

    @GET("/api/user/getProfile")
    fun getOtherProfile(
        @Header(AUTHORISATION) authorisation: String,
        @Query("oppositionId") user_id: String
    ): Call<ApiResponse<SignupModel>>

    @GET("/api/views/home")
    fun getHome(): Call<HomeDataResponse>

    @GET("")
    fun getTripImages(@Url url: String): Call<MainData>

    @GET("/api/user/getParcelOffices")
    fun parcelOffices(
        @Header(AUTHORISATION) authorisation: String,
        @Query(LIMIT) limit: String,
        @Query(SKIP) skip: String
    ): Call<ParcelApiResponse>

    @FormUrlEncoded
    @POST("/api/couponsRoutes/useCoupon")
    fun useCoupon(
        @Header(AUTHORISATION) authorisation: String,
        @Field(ORDER_ID) limit: String,
        @Field(COUPON_CODE) skip: String
    ): Call<ApiResponse<ArrayList<DataApplyCoupon>>>

    @FormUrlEncoded
    @POST("/api/couponsRoutes/removeCoupon")
    fun removeCoupon(
        @Header(AUTHORISATION) authorisation: String,
        @Field(ORDER_ID) limit: String,
        @Field(COUPON_CODE) skip: String
    ): Call<ApiResponse<ArrayList<DataRemoveCoupon>>>

    /* @POST("")
     fun requestTransport(@Header(AUTHORISATION) authorisation: String, @Body body: TransportDataRequset): Call<ApiResponse<Any>>*/

    @Multipart
    @POST("/api/user/requestType1")
    fun requestTransport(
        @Header(AUTHORISATION) auth: String,
        @Part("type") type: RequestBody,
        @Part("description") description: RequestBody,
        @Part("isDismantleNeeded") isDismantleNeeded: RequestBody,
        @Part("isElevated") isElevated: RequestBody,
        @Part("isFragile") isFragile: RequestBody,
        @Part("pickUpLocation") pickUpLocation: RequestBody,
        @Part("dropDownLocation") dropDownLocation: RequestBody,
        @Part("pickUpAddress") pickUpAddress: RequestBody,
        @Part("dropDownAddress") dropDownAddress: RequestBody,
        @Part("pickUpAdditionalNotes") pickUpAdditionalNotes: RequestBody,
        @Part("dropDownAdditionalNotes") dropDownAdditionalNotes: RequestBody,
        @Part("pickUpDate") pickUpDate: RequestBody,
        @Part("payment") payment: RequestBody,
        @Part image: List<MultipartBody.Part>
    ): Call<ApiResponse<Any>>


    @POST("/api/user/requestType2")
    fun requestParcel(
        @Header(AUTHORISATION) authorisation: String,
        @Body body: ParcelDataRequest
    ): Call<ApiResponse<Any>>

    /*@Multipart
    @POST("/api/user/requestType4")
    fun requestShip(@Header(AUTHORISATION) auth: String, @Part("type") type: RequestBody,
                    @Part("description") description: RequestBody,@Part("pickUpLocation") pickUpLocation: RequestBody,
                    @Part("dropDownLocation") dropDownLocation: RequestBody, @Part("pickUpAddress") pickUpAddress: RequestBody, @Part("dropDownAddress") dropDownAddress: RequestBody,
                    @Part("itemUrl") itemUrl: RequestBody, @Part("itemPrice") itemPrice: RequestBody, @Part("itemQuantity") itemQuantity: RequestBody,
                    @Part("payment") payment: RequestBody,@Part("pickUpDate") pickUpDate: RequestBody, @Part image: List<MultipartBody.Part>): Call<ApiResponse<Any>>
*/

    @POST("/api/user/requestType4")
    fun requestShipment(
        @Header(AUTHORISATION) authorisation: String,
        @Body body: ShipDataRequest
    ): Call<ApiResponse<Any>>

    @GET("/api/user/getH2dFee")
    fun getH2dFee(@Header(AUTHORISATION) authorisation: String): Call<ApiResponse<H2dFeeResponse>>

    @Multipart
    @POST("/api/scrap/retrieveProductData")
    fun getProductDetails(
        @Header(AUTHORISATION) authorisation: String,
        @Part("url") url: RequestBody,
        @Part("bodyContent") bodyContent: RequestBody
    ): Call<ApiResponse<ProductInformation>>

    @POST("/api/user/verifydocument")
    fun verifyDocument(
        @Header(AUTHORISATION) authorisation: String,
        @Body body: VerifyDocumentRequestModel
    ): Call<ApiResponse<SignupModel>>

    @POST("/api/user/requestType3")
    fun requestGrocery(
        @Header(AUTHORISATION) authorisation: String,
        @Body body: GroceryRequest
    ): Call<ApiResponse<Any>>

    @PUT("/api/user/acceptOffer")
    fun acceptOffer(
        @Header(AUTHORISATION) authorisation: String,
        @Body acceptOrderRequest: AcceptOrderRequest
    ): Call<ApiResponse<Any>>


    @GET("/api/user/getStores")
    fun storeList(
        @Header(AUTHORISATION) authorisation: String,
        @Query("limit") limit: String,
        @Query("skip") skip: String
    ): Call<ApiResponse<GroceryStoreResponse>>

    @GET("/api/user/getStoresItems")
    fun storeItem(
        @Header(AUTHORISATION) authorisation:
        String, @Query("storeId") storeId: String,
        @Query("limit") limit: String,
        @Query("skip") skip: String
    ): Call<ApiResponse<StoreItemResponse>>

    @FormUrlEncoded
    @POST("/api/user/republishRequest")
    fun republishOrderApi(
        @Header(AUTHORISATION) auth: String?,
        @Field("orderId") orderId: String?,
        @Field("pickUpDate") date: String?
    ): Call<ApiResponse<Any>>


    /*----------------------    Driver's Apis   ------------------------*/

    @FormUrlEncoded
    @POST("/api/company/addDriver")
    fun apiAddDriver(
        @Header("authorization") authAccessToken: String,
        @Field("firstName") fName: String,
        @Field("lastName") lName: String,
        @Field("countryCode") countryCode: String,
        @Field("phoneNo") phone: String,
        @Field("emailId") email: String,
        @Field("type") type: String,
        @Field("profilePicURL") preGeneratedUrl: String
    ): Call<PojoSuccess>

    @PUT("/api/company/deleteUnApprovedDriver")
    fun apiDeleteDriver(
        @Header("authorization") authAccessToken: String,
        @Body deleteDriver: DeleteDriver
    ): Call<Success>

    @GET("/api/company/getDrivers")
    fun apiGetDriver(
        @Header("authorization") authAccessToken: String,
        @Query("limit") limit: Double,
        @Query("skip") skip: Double
    ): Call<MyDrivers>


    @GET("/api/user/getRequests")
    fun getRequests(
        @Header(AUTHORISATION) authorization: String?,
        @Query(LIMIT) limit: Int?,
        @Query(SKIP) skip: Int?,
        @Query("orderType") orderType: String?,
        @Query("pickUpCountryLongLat") pickUpCountry: ArrayList<Double>?,
        @Query("dropDownCountryLongLat") dropDownCountry: ArrayList<Double>?,
        @Query(LOCATION) location: ArrayList<Double>?
    ): Call<ApiResponse<Order>>

    @GET("/api/user/applyFilter")
    fun applyFilter(
        @Header(AUTHORISATION) authorization: String?,
        @QueryMap map: @JvmSuppressWildcards Map<String, Any>
    ): Call<ApiResponse<SignupModel>>


    @FormUrlEncoded
    @POST("/api/user/makeOffersAndAcceptOrder")
    fun makeOffersAndAcceptOrder(
        @Header(AUTHORISATION) auth: String?,
        @Field("orderId") orderId: String?,
        @Field("price") price: String?,
        @Field("driverAction") driverAction: String?,
        @Field("lat") lat: Double?,
        @Field("long") long: Double?,
        @Field("driverArrivalDate") driverArrivalDate: String?,
        @Field("driverCardId") driverCardId: String?,
        @Field("shippingCharge") shippingCharge: Double?
    ): Call<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("/api/user/makeOffersAndAcceptOrder")
    fun rejectOrder(
        @Header(AUTHORISATION) auth: String?,
        @Field("orderId") orderId: String?,
        @Field("price") price: String?,
        @Field("driverAction") driverAction: String?,
        @Field("lat") lat: Double?,
        @Field("long") long: Double?,
        @Field("driverArrivalDate") driverArrivalDate: String?,
        @Field("reason") reason: String?
    ): Call<ApiResponse<Any>>

    @GET("/api/user/myDeliveries")
    fun getMyDeliveries(
        @Header(AUTHORISATION) authorization: String?,
        @Query(LIMIT) limit: Int?,
        @Query(SKIP) skip: Int?,
        @Query("orderStatus") orderType: String?,
        @Query("pickUpCountry") pickUpCountry: String?,
        @Query("dropDownCountry") dropDownCountry: String
    ): Call<ApiResponse<Order>>

    @GET("/api/user/chatLogs")
    fun getChatLogs(
        @Header(AUTHORISATION) authorization: String?,
        @Query(LIMIT) limit: Int?,
        @Query(SKIP) skip: Int?
    ): Call<ApiResponse<ArrayList<ChatListing>>>

    @GET("/api/user/chatLogs")
    fun searchChat(
        @Header(AUTHORISATION) authorization: String?,
        @Query(LIMIT) limit: Int?,
        @Query(SKIP) skip: Int?, @Query("search") search: String
    ): Call<ApiResponse<ArrayList<ChatListing>>>

    @GET("/api/user/getChat")
    fun getChatMessages(
        @Header(AUTHORISATION) authorization: String?,
        @Query(MESSAGE_ID) messageId: String,
        @Query(RECEIVER_ID) receiverId: String,
        @Query(LIMIT) limit: Int?,
        @Query(SKIP) skip: Int?,
        @Query(MESSAGE_ORDER) messageOrder: String
    ): Call<ApiResponse<ChatMessageList>>

    @GET("/api/user/stripeCountries")
    fun getCountry(): Call<GetCountry>

    @FormUrlEncoded
    @PUT("/api/user/switchToDriver")
    fun switchToDriver(
        @Header(AUTHORISATION) authorisation: String,
        @Field("type") type: String?
    ): Call<ApiResponse<SignupModel>>

    @PUT("/api/user/addReceiverInfo")
    fun addReceiverInfo(
        @Header(AUTHORISATION) authorisation: String,
        @Body addReceiverRequest: AddReciverRequest
    ): Call<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("/api/user/editProfile")
    fun editProfile(
        @Header(AUTHORISATION) authorisation: String,
        @FieldMap map: @JvmSuppressWildcards Map<String, String>
    ): Call<ApiResponse<SignupModel>>

    @PUT("/api/user/cancelOrder")
    fun cancelOrder(
        @Header(AUTHORISATION) authorisation: String,
        @Body orderId: JsonObject
    ): Call<ApiResponse<Any>>

    @PUT("/api/user/cancelOrder")
    fun cancelOrderBooking(
        @Header(AUTHORISATION) authorisation: String,
        @Body cancelOrderRequest: CancelOrderRequest
    ): Call<ApiResponse<Any>>

    @PUT("/api/user/driverStatus")
    fun driverStatus(
        @Header(AUTHORISATION) authorisation: String,
        @Body driverStatusRequest: DriverStatusRequestNoPic
    ): Call<ApiResponse<Any>>

    @PUT("/api/user/driverStatus")
    fun driverStatusWithPic(
        @Header(AUTHORISATION) authorisation: String,
        @Body driverStatusRequest: DriverStatusRequestPic
    ): Call<ApiResponse<Any>>

    @GET("/api/user/getNotification")
    fun getNotifications(
        @Header(AUTHORISATION) authorisation: String,
        @Query(SKIP) skip: Int,
        @Query(LIMIT) limit: Int
    ): Call<Notifications>

    @PUT("/api/user/markCompleteOrder")
    fun markCompleteOrder(
        @Header(AUTHORISATION) authorisation: String,
        @Query(ORDER_ID) orderId: String,
        @Query("orderCode") orderCode: String
    ): Call<ApiResponse<Any>>

    @GET("/api/user/getOrderDetails")
    fun getOrderDetail(
        @Header(AUTHORISATION) authorisation: String,
        @Query(ORDER_ID) orderId: String
    ): Call<ApiResponse<OrderListing>>

    @GET("directions/json?sensor=false&mode=driving&alternatives=true&units=imperial&key=AIzaSyD5NOOpYKObjlZbt_-4CUOMi14mObzbGNo")
    fun getPolYLine(
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ): Call<ResponseBody>

    @GET("/api/user/FAQ")
    fun faq(): Call<ApiResponse<FaqModel>>

    @GET("api/views/home/faqs")
    fun getKnowMore(): Call<KnowMoreResponse>

    @GET("/api/commonRoutes/getReasons")
    fun getReason(
        @Header(AUTHORISATION) authorization: String?,
        @Query("type") type: String?
    ): Call<ApiResponse<ReasonModel>>

    @GET("/api/user/cardList")
    fun getCardList(@Header(AUTHORISATION) authorisation: String): Call<ApiResponse<Card>>

    @FormUrlEncoded
    @PUT("/api/user/updatePaymentMethod")
    fun updatePaymentMethod(
        @Header(AUTHORISATION) authorisation: String,
        @Field("orderId") orderId: String,
        @Field("cardId") cardId: String
    ): Call<ApiResponse<OrderListing>>

    @FormUrlEncoded
    @POST("/api/user/addCard")
    fun addCard(
        @Header(AUTHORISATION) authorisation: String,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<ApiResponse<CardData>>

    @FormUrlEncoded
    @POST("/api/user/deleteCard")
    fun deleteCard(
        @Header(AUTHORISATION) authorisation: String,
        @Field("cardId") cardId: String
    ): Call<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("/api/user/connectWithStripeSignupOnboarding")
    fun connectStripe(
        @Header(AUTHORISATION) authorisation: String,
        @Field("state") state: String,
        @Field("code") code: String
    ): Call<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("/api/user/connectWithStripeOnboarding")
    fun connectStripeOnBoarding(
        @Header(AUTHORISATION) authorisation: String,
        @Field("country") country: String
    ): Call<ConnectStripeOnBoarding>

    @FormUrlEncoded
    @POST("/api/user/selectLanguage")
    fun languageChange(
        @Header(AUTHORISATION) authorisation: String,
        @Field("languageId") cardId: Int
    ): Call<ApiResponse<Any>>


    @FormUrlEncoded
    @POST("/api/user/verifyOtp")
    fun verifyOtp(
        @Field("fullNumber") fullNumber: String,
        @Field("otp") otp: String
    ): Call<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("/api/user/verifyOtp")
    fun verifyOtpInUpdate(
        @Field("fullNumber") fullNumber: String,
        @Field("otp") otp: String,
        @Field("oldfullNumber") oldfullNumber: String,
        @Field("countryISO") countryISO: String,
        @Field("asSend") asSend: String
    ): Call<ApiResponse<Any>>

    @POST("/api/user/connectwithStripeAccDetails")
    fun getStripeDetails(@Header(AUTHORISATION) authorization: String): Call<ApiResponse<StripeAccountData>>

    @FormUrlEncoded
    @POST("/api/user/report")
    fun reportOrder(
        @Header(AUTHORISATION) authorisation: String,
        @Field("oppositionId") oppositionId: String,
        @Field("title") title: String,
        @Field("orderId") otp: String
    ): Call<ApiResponse<Any>>
}
