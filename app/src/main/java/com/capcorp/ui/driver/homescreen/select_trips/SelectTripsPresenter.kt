package com.capcorp.ui.driver.homescreen.select_trips

import android.widget.ImageView
import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.images.MainData
import com.capcorp.webservice.models.select_trips.AllTrips
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectTripsPresenter : BasePresenterImpl<SelectTripsContract.View>(),
    SelectTripsContract.Presenter {
    override fun tripsListing(acessToken: String, skip: String, pageNo: String) {
        getView()?.showLoader(true)
        RestClient.get().myTrips(acessToken, skip, pageNo)
            .enqueue(object : Callback<ApiResponse<AllTrips>> {
                override fun onResponse(
                    call: Call<ApiResponse<AllTrips>>,
                    response: Response<ApiResponse<AllTrips>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            response.body()?.data?.let { getView()?.apiTripsListing(it) }
                        } else {
                            getView()?.handleApiError(
                                response.body()?.statusCode,
                                response.body()?.message
                            )
                        }
                    } else {
                        val errorModel = getApiError(response.errorBody()?.string())
                        getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                    }
                }

                override fun onFailure(call: Call<ApiResponse<AllTrips>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }

    override fun getImages(dropdownCountry: String, ivImage: ImageView) {
        // getView()?.showLoader(true)
        RestClient.get()
            .getTripImages("https://api.unsplash.com/search/photos/?client_id=F-9gIpBqNotJZFMlCu7aPqZ9j1PJfHcQAkqJPP53NMc&orientation=portrait&query=" + dropdownCountry)
            .enqueue(object : Callback<MainData> {
                override fun onFailure(call: Call<MainData>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<MainData>,
                    response: Response<MainData>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.results != null) {
                            response.body()?.results?.let { getView()?.responseImages(it, ivImage) }
                        } else {
                           getView()?.responseImages(emptyList(), ivImage)
                        }
                    } else {
                        val errorModel = getApiError(response.errorBody()?.string())
                        getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                    }
                }

            })
    }
}