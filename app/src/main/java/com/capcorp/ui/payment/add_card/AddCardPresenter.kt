package com.capcorp.ui.payment.add_card

import com.capcorp.ui.payment.model.Card
import com.capcorp.ui.payment.model.CardData
import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCardPresenter : BasePresenterImpl<AddCardContract.View>(), AddCardContract.Presenter {
    override fun addCard(accessToken: String, hashMap: HashMap<String, String>) {
        //getView()?.showLoader(true)
        RestClient.get().addCard(accessToken, hashMap)
            .enqueue(object : Callback<ApiResponse<CardData>> {
                override fun onFailure(call: Call<ApiResponse<CardData>>?, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<CardData>>?,
                    response: Response<ApiResponse<CardData>>?
                ) {
                    getView()?.showLoader(false)
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.onAddCarcSuccess()
                        } else {
                            getView()?.handleApiError(
                                response.body()?.statusCode,
                                response.body()?.message
                            )
                        }
                    } else {
                        val errorModel = getApiError(response?.errorBody()?.string())
                        getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                    }
                }

            })
    }

    override fun deleteCard(accessToken: String, cardId: String) {
        RestClient.get().deleteCard(accessToken, cardId)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>?
                ) {
                    getView()?.showLoader(false)
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.onDeleteCardSuccess()
                        } else {
                            getView()?.handleApiError(
                                response.body()?.statusCode,
                                response.body()?.message
                            )
                        }
                    } else {
                        val errorModel = getApiError(response?.errorBody()?.string())
                        getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                    }
                }

            })
    }

    override fun onCardList(accessToken: String) {
        getView()?.showLoader(true)
        RestClient.get().getCardList(accessToken)
            .enqueue(object : Callback<ApiResponse<Card>> {
                override fun onFailure(call: Call<ApiResponse<Card>>?, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Card>>?,
                    response: Response<ApiResponse<Card>>?
                ) {
                    getView()?.showLoader(false)
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.onCardListSuccess(response.body()?.data?.cardListing)
                        } else {
                            getView()?.handleApiError(
                                response.body()?.statusCode,
                                response.body()?.message
                            )
                        }
                    } else {
                        val errorModel = getApiError(response?.errorBody()?.string())
                        getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                    }
                }

            })

    }
}