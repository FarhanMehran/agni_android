package com.agnidating.agni.network

import com.agnidating.agni.model.*
import com.agnidating.agni.model.addFlower.AddFlower
import com.agnidating.agni.model.blockUser.BlockedResponse
import com.agnidating.agni.model.home.HomeResponse
import com.agnidating.agni.model.notification.NotificationsResponse
import com.agnidating.agni.model.profile.ProfileResponse
import com.agnidating.agni.model.user_details.UserDetails
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("Home/firstRegister")
    suspend fun register(
        @FieldMap map: HashMap<String, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("Home/loginuser")
    suspend fun register_(
        @FieldMap map: HashMap<String, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("Home/firstTimeOtp")
    suspend fun newRegister(
        @FieldMap map: HashMap<String, String>
    ): Response<LoginResponse>
    @FormUrlEncoded
    @POST("Home/registeruser")
    suspend fun newRegister_(
        @FieldMap map: HashMap<String, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("Home/registeruser")
    suspend fun registerResend(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/verificationmobileOtp")
    suspend fun verifyOtp(
        @FieldMap map: HashMap<String, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("Home/resendOtp")
    suspend fun resendOtp(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateName")
    suspend fun setName(
        @Field("name") name: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateLocation")
    suspend fun setLocation(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateDob")
    suspend fun setDateOfBirth(
        @FieldMap name: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateGender")
    suspend fun setGender(
        @Field("gender") gender: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateInterest")
    suspend fun setInterest(
        @Field("gender") gender: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("Home/updateInterest")
    suspend fun updateInterest(
        @Field("gender") gender: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateEducation")
    suspend fun updateEducation(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateReligion")
    suspend fun updateReligion(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateCommunity")
    suspend fun updateCommunity(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @Multipart
    @POST("Home/updateImages")
    suspend fun updateImages(
        @Part list: ArrayList<MultipartBody.Part>
    ): Response<LoginResponse>

    @Multipart
    @POST("Home/editProfile")
    suspend fun editProfile(
        @Part list: ArrayList<MultipartBody.Part>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateBio")
    suspend fun updateBio(
        @Field("bio") bio: String
    ): Response<BaseResponse>

    @POST("Home/getProfile")
    suspend fun getProfile(
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("Home/updatePhone")
    suspend fun updatePhone(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/otpVerificationOnmobileUpdate")
    suspend fun verifyPhoneUpdate(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateLocation")
    suspend fun updateInterestedLocation(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>


    @POST("Home/logout")
    suspend fun logout(
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateinterestageRange")
    suspend fun updateInterestedAgeRange(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/updateinterestdistanceRange")
    suspend fun updateDistanceRange(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/contactUs")
    suspend fun contactUs(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/userHome")
    suspend fun getProfilesList(
        @Field("offset") offset: Int,
        @Field("limit") limit: Int,
    ): Response<HomeResponse>

    @FormUrlEncoded
    @POST("Home/otherUserInfo")
    suspend fun getUserInfo(
        @Field("userId") userId: String,
    ): Response<UserDetails>

    @FormUrlEncoded
    @POST("Home/sendRequest")
    suspend fun sendRequest(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/acceptRequest")
    suspend fun acceptReject(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/getlistRequest")
    suspend fun getListRequests(
        @Field("offset") offset: Int,
        @Field("limit") limit: Int,
    ): Response<HomeResponse>

    @FormUrlEncoded
    @POST("Home/matchProfile")
    suspend fun getMatchedRequest(
        @Field("offset") offset: Int,
        @Field("limit") limit: Int,
    ): Response<HomeResponse>

    @FormUrlEncoded
    @POST("Home/blockRequest")
    suspend fun blockUser(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/requestUnmatch")
    suspend fun unMatch(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @Multipart
    @POST("Home/reportUser")
    suspend fun reportUser(
        @PartMap map: HashMap<String, RequestBody>,
        @Part img: List<MultipartBody.Part>
    ): Response<BaseResponse>

    @POST("Home/gettopProfile")
    suspend fun getTopProfile(
    ): Response<HomeResponse>

    @POST("Home/notificationList")
    suspend fun getNotifications(
    ): Response<NotificationsResponse>

    @FormUrlEncoded
    @POST("Home/deleteImg")
    suspend fun deleteImg(
        @Field("imageId") imageId: String
    ): Response<DeleteResponse>


    @FormUrlEncoded
    @POST("Home/addFlower")
    suspend fun addFlowers(
        @FieldMap map: HashMap<String, String>
    ): Response<AddFlower>

    @FormUrlEncoded
    @POST("Home/recentChat")
    suspend fun getRecentChats(
        @Field("receiverId") id: String
    ): Response<UserMessage>

    @FormUrlEncoded
    @POST("Home/hideProfile")
    suspend fun hideProfile(
        @Field("status") status: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/subscriptionTransaction")
    suspend fun upgradeSubscription(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("Home/clearChat")
    suspend fun clearChat(
        @FieldMap map: HashMap<String, String>
    ): Response<BaseResponse>

    @POST("Home/deleteUser")
    suspend fun deleteAccount(

    ): Response<BaseResponse>

    @POST("Home/blockuserList")
    suspend fun getBlockedUsers(

    ): Response<BlockedResponse>


    @FormUrlEncoded
    @POST(" Home/unblockUser")
    suspend fun unblockUser(
        @Field("userId") id: String
    ): Response<UnblockResponse>
}