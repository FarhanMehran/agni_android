package com.agnidating.agni.ui.fragment.favourite

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.BuildConfig
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.addFlower.AddFlower
import com.agnidating.agni.model.home.HomeResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.utils.getFlowerMessageJson
import com.agnidating.agni.utils.getRoomId
import com.agnidating.agni.utils.logs
import com.agnidating.agni.utils.safeApiCall
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.android.billingclient.api.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 04/28/2022
 */
@HiltViewModel
class FavoriteViewModel @Inject constructor(private val apiService: ApiService,private val sharedPrefs: SharedPrefs):BaseViewModel() {
    val topLiveData=MutableLiveData<ResultWrapper<HomeResponse>>()
    val baseViewModel=MutableLiveData<ResultWrapper<AddFlower>>()
    var socketClient:WebSocketClient?=null
    private lateinit var billingClient: BillingClient
    val connectStatus=MutableLiveData<Boolean>()
    val buyStatus=MutableLiveData<Boolean>()
    var addFlowerLiveData = MutableLiveData<ResultWrapper<AddFlower>>()

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
                }
            }
            .build()
        connectToBilling()
    }

    /**
     * connect to billing when user click on purchase button
     */
    fun connectToBilling() {
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
            buyStatus.postValue(true)
        }
    }




    fun connectSocket(msg:String,id:Int,userId:Int,count:Int){

        val uri= URI.create("${BuildConfig.SOCKET_URL}token=${sharedPrefs.getMessageId()}&room=${getRoomId(id,userId)}&userID=$userId")

        uri.toString().logs()

        socketClient=object : WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                "connected".logs()
                val json= getFlowerMessageJson(msg,userId,id,count)
                json.logs()
                socketClient?.send(json)

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

    fun getTopProfiles(){
        topLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.getTopProfile() }
            topLiveData.postValue(response)
        }
    }

    fun addFlowers(map:HashMap<String,String>){
        addFlowerLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.addFlowers(map) }
            addFlowerLiveData.postValue(response)
        }
    }

    fun sendRequest(map: HashMap<String, String>) {
        viewModelScope.launch {
            safeApiCall { apiService.sendRequest(map) }
        }
    }
}