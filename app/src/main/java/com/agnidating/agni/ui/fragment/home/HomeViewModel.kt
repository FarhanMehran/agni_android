package com.agnidating.agni.ui.fragment.home

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.agnidating.agni.BuildConfig
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.addFlower.AddFlower
import com.agnidating.agni.model.home.User
import com.agnidating.agni.model.user_details.UserDetails
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.utils.*
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.android.billingclient.api.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 04/12/2022
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val apiService: ApiService,val sharedPrefs: SharedPrefs) : ViewModel() {

    private var homeDataSource: HomeDataSource? = null
    val flowersLiveData=MutableLiveData<Int>()
    var baseLiveData = MutableLiveData<ResultWrapper<BaseResponse>>()
    var addFlowerLiveData = MutableLiveData<ResultWrapper<AddFlower>>()
    var userInfo = MutableLiveData<ResultWrapper<UserDetails>>()
    private val modifier = MutableStateFlow<List<User>>(emptyList())
    var profiles:Flow<PagingData<User>>? =null
    val homeList=profiles?.asLiveData()
    val connectStatus=MutableLiveData<Boolean>()
    private lateinit var billingClient: BillingClient
    val buyStatus=MutableLiveData<Boolean>()
    var socketClient:WebSocketClient?=null
    val sentLiveData=MutableLiveData<String>()
    val unreadNotifications=MutableLiveData<Int>()




    fun getProfiles() {
        profiles=Pager(
            PagingConfig(pageSize = 10)
        ) {
            HomeDataSource(apiService,flowersLiveData, unreadNotifications).also {
                homeDataSource =it
            }
        }.flow.cachedIn(viewModelScope)
    }

    private fun applyEvents(m: PagingData<User>, mod: User):PagingData<User> {
        return m.filter { mod.id!=it.id }
    }

    fun sendRequest(map: HashMap<String, String>) {
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response = safeApiCall { apiService.sendRequest(map) }
            baseLiveData.postValue(response)
        }
    }

    fun getUserDetails(id:String){
        userInfo.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response = safeApiCall { apiService.getUserInfo(id) }
            userInfo.postValue(response)
        }
    }

    /**
     * Buy Flower Billing code
     */

    /**
     * initialize google billing client
     */
    fun initBillingClient(mContext: Context){
        billingClient = BillingClient.newBuilder(mContext)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        viewModelScope.launch {
                            handlePurchase(purchase)
                        }
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                } else {
                }
            }
            .build()
        connectToBilling()
    }

    /**
     * connect to billing when user click on purchase button
     */
    private fun connectToBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                viewModelScope.launch {
                    connectStatus.postValue(true)
                }
            }
        })
    }

    suspend fun launchBillingFlow(activity: DashboardActivity, pid: String) {
        try {
            val product= QueryProductDetailsParams.Product.newBuilder().setProductId(pid).setProductType(
                BillingClient.ProductType.INAPP).build()
            val productList = ArrayList<QueryProductDetailsParams.Product>()
            productList.add(product)
            val params = QueryProductDetailsParams.newBuilder()
            params.setProductList(productList)
            val productDetailsResult = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(params.build())
            }


            val productDetails= BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(
                productDetailsResult.productDetailsList?.get(0)!!
            ).build()
            val flowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productDetails).toMutableList())
                .build()
            billingClient.launchBillingFlow(activity, flowParams).responseCode
        } catch (e: Exception) {
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState== Purchase.PurchaseState.PURCHASED){

            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build()
            val consumeResult = withContext(Dispatchers.IO) {
                billingClient.consumePurchase(consumeParams)
            }
            if (consumeResult.billingResult.responseCode==BillingClient.BillingResponseCode.OK){
                buyStatus.postValue(true)
            }
        }
    }

    fun addFlowers(map:HashMap<String,String>){
        addFlowerLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.addFlowers(map) }
            addFlowerLiveData.postValue(response)
        }
    }

    /**
     * Socket implementation
     */
    fun connectSocket(msg:String,id:Int,userId:Int,count:Int=0){

        val uri= URI.create("${BuildConfig.SOCKET_URL}token=${sharedPrefs.getMessageId()}&room=${getRoomId(id,userId)}&userID=$userId")

        uri.toString().logs()

        socketClient=object : WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                "connected".logs()
                val json=  if(count==0){
                    getMessageJson(msg,userId,id)
               }else{
                    getFlowerMessageJson(msg,userId,id,count)
               }
                socketClient?.send(json)
                val statusMsg=if (count==0) "Message sent" else "Flower sent"
                sentLiveData.postValue(statusMsg)
            }

            override fun onMessage(message: String?) {

            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                reason?.logs()
            }

            override fun onError(ex: Exception?) {
                ex?.message?.logs()
            }

        }
        if (socketClient!!.isOpen){
            socketClient?.close()
        }
        socketClient?.connect()
    }

    fun reload(){
        homeDataSource?.invalidate()
    }


}