package com.agnidating.agni.ui.activities.plans

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.logs
import com.agnidating.agni.utils.safeApiCall
import com.android.billingclient.api.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 05/24/2022
 */
@HiltViewModel
class PlansViewModel @Inject constructor(val apiService: ApiService):BaseViewModel() {

    private lateinit var billingClient: BillingClient
    var baseLiveData = MutableLiveData<ResultWrapper<BaseResponse>>()
    val purchaseResponse=MutableLiveData<ResultWrapper<BaseResponse>>()
    val connectStatus= MutableLiveData<Boolean>()

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
                            handleSubscription(purchase)
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

    suspend fun launchBillingFlow(activity: PlanSelectionActivity, pid: String) {
        try {
            pid.logs()
            val product= QueryProductDetailsParams.Product.newBuilder().setProductId(pid).setProductType(
                BillingClient.ProductType.SUBS).build()
            val productList = ArrayList<QueryProductDetailsParams.Product>()
            productList.add(product)
            val params = QueryProductDetailsParams.newBuilder()
            params.setProductList(productList)
            val productDetailsResult = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(params.build())
            }

            if (productDetailsResult.productDetailsList!!.isEmpty()){
                val baseResponse=BaseResponse()
                baseResponse.status=0
                baseResponse.message="Billing service not available"
                purchaseResponse.postValue(ResultWrapper.Error("",baseResponse))
            }else{
                val productDetails=BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(
                    productDetailsResult.productDetailsList?.get(0)!!
                ).setOfferToken( productDetailsResult.productDetailsList?.get(0)!!.subscriptionOfferDetails!![0].offerToken).build()

                val flowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productDetails).toMutableList())
                    .build()
                billingClient.launchBillingFlow(activity, flowParams)
            }

        } catch (e: Exception) {
        }
    }

    private suspend fun handleSubscription(purchase: Purchase) {
        if (purchase.purchaseState==Purchase.PurchaseState.PURCHASED){
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
         withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()
                ) {
                    viewModelScope.launch {
                        queryPurchases()
                    }
                }
            }
        }
    }
    private suspend fun queryPurchases() {
        val queryPurchasesParams=QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        val ackPurchaseResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(queryPurchasesParams)
        }
        upgradeSubscription(ackPurchaseResult.purchasesList[0])
    }

    fun upgradeSubscription(purchase: Purchase) {
        val map=HashMap<String,String>()
        map["txnId"]=purchase.purchaseToken
        map["plan"]="1 month"
        map["source"]="android"
        map["data"]=purchase.originalJson
        purchaseResponse.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response = safeApiCall { apiService.upgradeSubscription(map) }
            purchaseResponse.postValue(response)
        }
    }
}