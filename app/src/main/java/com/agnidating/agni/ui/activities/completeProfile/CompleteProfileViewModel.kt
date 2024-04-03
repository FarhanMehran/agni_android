package com.agnidating.agni.ui.activities.completeProfile

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.LoginResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.fragment.yourLocation.YourLocationFragment
import com.agnidating.agni.utils.safeApiCall
import com.agnidating.agni.utils.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(private val apiService: ApiService):BaseViewModel() {
    val updateLiveData=MutableLiveData<ResultWrapper<BaseResponse>>(null)
    val interestLiveData=MutableLiveData<ResultWrapper<LoginResponse>>(null)
    val locationLiveData=MutableLiveData<ResultWrapper<Location>>(null)
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    fun setName(name:String){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.setName(name) }
            updateLiveData.postValue(response)
        }
    }
    fun setLocation(map:HashMap<String,String>){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.setLocation(map) }
            updateLiveData.postValue(response)
        }
    }
    fun setBirthdate(map: HashMap<String, String>){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.setDateOfBirth(map) }
            updateLiveData.postValue(response)
        }
    }
    fun setGender(gender:String){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.setGender(gender) }
            updateLiveData.postValue(response)
        }
    }
    fun setInterest(gender:String){
        interestLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.setInterest(gender) }
            interestLiveData.postValue(response)
        }
    }
    fun setEducationOccupation(map: HashMap<String, String>){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateEducation(map) }
            updateLiveData.postValue(response)
        }
    }
    fun setReligion(map: HashMap<String, String>){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateReligion(map) }
            updateLiveData.postValue(response)
        }
    }
    fun setCommunity(map: HashMap<String, String>){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateCommunity(map) }
            updateLiveData.postValue(response)
        }
    }

    /**
     * start fetch location code
     */

    /**
     * initialize location client
     */
    fun initLocationClient(mContext: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
    }

    /**
     * initialize location callback
     */
    fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                try {
                    locationResult
                    for (location in locationResult.locations){
                        if (location!=null){
                            locationLiveData.postValue(ResultWrapper.Success(location))
                            break
                        }
                    }
                }catch (e:Exception){

                }
            }
        }
    }


    /**
     * check if user's gps setting is on or off
     */
    fun checkLocationSetting(activity:Activity) {
        val locationRequest=createLocationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            /**
             * gps setting is enabled by user
             * so process location update
             */
            fetchCurrentLocation(locationRequest)
        }
        task.addOnFailureListener {exception->
            /**
             * gps setting is not enabled by user
             * ask user to turn on location setting
             */
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(activity,
                        REQUEST_CHECK_SETTINGS
                    )
                    fetchCurrentLocation(locationRequest)
                    /**
                     * user enabled the location setting process location update
                     */

                } catch (sendEx: IntentSender.SendIntentException) {
                    "You need to enable location setting to use this app".toast(activity)
                }
            }
        }
    }

    /**
     * create location request object
     */
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }


    /**
     * fetch current location of user
     */
    private fun fetchCurrentLocation(locationRequest: LocationRequest) {
        locationLiveData.postValue(ResultWrapper.Loading())
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    /**
     * get address string from location object
     */
    fun getAddressFromLocation(location: Location?,mContext: Context): String {
        if (location!=null){
            val geoCoder= Geocoder(mContext, Locale.getDefault())
            val addresses= geoCoder.getFromLocation(location.latitude,location.longitude,1)
            if (addresses.isNotEmpty()){
                return addresses[0].locality
            }
            return ""
        }
        return ""
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS: Int = 101
    }
}