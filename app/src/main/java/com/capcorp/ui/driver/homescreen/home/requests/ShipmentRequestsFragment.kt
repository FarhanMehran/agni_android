package com.capcorp.ui.driver.homescreen.home.requests

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.HomeActivity
import com.capcorp.ui.driver.homescreen.home.makeoffer.MakeAnOfferActivity
import com.capcorp.ui.driver.homescreen.mydeliveries.filter.FilterActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.report.ReportActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.OrderListing
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_requests.*
import java.util.*

class ShipmentRequestsFragment : Fragment(), RequestContract.View,
    RequestsAdapter.DeletedRequestPosition, RequestsAdapter.InsuranceOfferCallback,
    RequestsAdapter.SelectedRequestPosition, RequestsAdapter.SelectedReportPosition{

    var requestType: String? = "SHIPMENT"
    var currentListSize = 0
    val presenter = RequestsPresenter()
    private lateinit var adapter: RequestsAdapter
    private var orderListing = ArrayList<OrderListing>()
    val location = ArrayList<Double>()
    var isListEmpty = false
    private var isApiCalling = false
    private lateinit var activity: HomeActivity
    private var currentLocation = ArrayList<Double>()
    private var currentLocations: Location? = null
    private val REQ_CODE_PICK_UP = 105
    private val REQ_CODE_DROP_OFF = 106
    private var pickCountry: String = ""
    private var picUpLocation = ArrayList<Double>()
    private var dropOffLocation = ArrayList<Double>()

    private var clickedOrderId: String = ""
    private var recommendReward: String = ""
    private var selectedPos: String = ""
    private var mPayment: String = ""
    private var mDeliverDate: String = ""
    private var cardId: String = ""
    private var offerTypes: String = ""
    private var dropDownCountry: String = ""
    private var isFilterClicked = false
    private var isFirstTime = true
    private lateinit var dialogIndeterminate: DialogIndeterminate

    //private var locationHandler: LocationHandler? = null

    private var placePickerAddress: String? = null
    private var googlePlaceAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as HomeActivity
        currentLocation.clear()
        currentLocation.add(0.0)
        currentLocation.add(0.0)
        //locationHandler = LocationHandler(requireActivity(), this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(activity)
        setListeners()
        setRecyclerAdapter()

        swipeRefresh.setOnRefreshListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (CheckNetworkConnection.isOnline(context)) {
                        edtFrom.text = ""
                        edtTo.text = ""
                        pickCountry = ""
                        dropDownCountry = ""
                        orderListing.clear()
                        picUpLocation.clear()
                        dropOffLocation.clear()
//                        locationHandler?.getUserLocation()
                }
                presenter.getRequestsApiCall(
                    getAuthAccessToken(context), PAGE_LIMIT, 0,
                    requestType, picUpLocation, dropOffLocation, currentLocation
                )

            } else {
                if (CheckNetworkConnection.isOnline(context)) {
                    edtFrom.text = ""
                    edtTo.text = ""
                    pickCountry = ""
                    dropDownCountry = ""
                    orderListing.clear()
                    picUpLocation.clear()
                    dropOffLocation.clear()
//                    locationHandler?.getUserLocation()

                }

                presenter.getRequestsApiCall(
                    getAuthAccessToken(context), PAGE_LIMIT, 0,
                    requestType, picUpLocation, dropOffLocation, currentLocation
                )

            }
        }

        presenter.getRequestsApiCall(
            getAuthAccessToken(context), PAGE_LIMIT, 0,
            requestType, picUpLocation, dropOffLocation, currentLocation
        )

        swipeRefresh.setColorSchemeResources(R.color.here2dare_green, R.color.here2dare_green)



        tv_Filters.setOnClickListener {
            tv_Filters.isClickable = false
            startActivityForResult(
                Intent(activity, FilterActivity::class.java).putExtra(
                    ISFIRST_TIME,
                    isFirstTime
                ), 321
            )
        }

        //Should show tutorial
        /*val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        val tutorialShopper = sharedPref.getBoolean("shopperTutorial", false)
        if (tutorialShopper == false) {
            println("Valor de shopper! " + tutorialShopper)
            with(sharedPref.edit()) {
                putBoolean("shopperTutorial", true)
                apply()
            }
            activity.let {
                val intent = Intent(it, TutorialActivity::class.java)
                it.startActivity(intent)
            }
        }*/

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        if (visible && isResumed) {
            onResume()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        val userData = SharedPrefs.with(activity).getObject(USER_DATA, SignupModel::class.java)
        tv_Filters.isClickable = true

        if (!isFilterClicked) {
            permissionSafetyMethod()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onGetCurrentUpdate() {
        /*  LocationProvider.CurrentLocationBuilder(activity).build()i
                  .getLastKnownLocation(OnSuccessListener {
                      currentLocations = it
                      if (it != null) {
                          currentLocation.clear()
                          currentLocation.add(it.longitude.toFloat())
                          currentLocation.add(it.latitude.toFloat())
                          if (CheckNetworkConnection.isOnline(context)) {
                              orderListing.clear()
                              dialogIndeterminate.show()
                              presenter.getRequestsApiCall(getAuthAccessToken(context), PAGE_LIMIT, 0, requestType, pickCountry, dropDownCountry, currentLocation)
                          }

                      }
                  })*/


//        locationHandler?.getUserLocation()

    }


   /* override fun getLocation(location: Location) {

        currentLocation.clear()
        currentLocation.add(location.longitude)
        currentLocation.add(location.latitude)
        if (CheckNetworkConnection.isOnline(context)) {
            orderListing.clear()
            dialogIndeterminate.show()
            presenter.getRequestsApiCall(
                getAuthAccessToken(context), PAGE_LIMIT, 0,
                requestType, picUpLocation, dropOffLocation, currentLocation
            )
        }

    }*/


    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionSafetyMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onGetCurrentUpdate()
        } else onGetCurrentUpdate()

    }

    private fun setListeners() {
        edtFrom.setOnClickListener { openPlacePicker(REQ_CODE_PICK_UP, false) }
        edtTo.setOnClickListener { openPlacePicker(REQ_CODE_DROP_OFF, true) }
    }

    private fun openPlacePicker(reqCode: Int, destination: Boolean) {
        // Set the fields to specify which types of place data to return.
        val fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        )

        if (destination) {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setTypeFilter(TypeFilter.CITIES)
                .build(activity)
            startActivityForResult(intent, reqCode)

        } else {

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setTypeFilter(TypeFilter.CITIES)
                .setTypeFilter(TypeFilter.REGIONS)
                .build(activity)
            startActivityForResult(intent, reqCode)
        }
        // Start the autocomplete intent.

    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }
    }

    private fun isValidationsOk(): Boolean {
        return when {
            edtFrom.text.toString().isEmpty() -> {
                edtFrom.showSnack(getString(R.string.please_enter_pickup_address))
                false
            }
            edtTo.text.toString().isEmpty() -> {
                edtTo.showSnack(getString(R.string.please_enter_drop_off_address))
                false
            }
            else -> true
        }
    }


    private fun setRecyclerAdapter() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = activity.let { RequestsAdapter(it, this, this, orderListing, this, this, this) }
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_LIMIT
                ) {
                    if (!isListEmpty && !isApiCalling) {
                        isApiCalling = true
                        progress_bar.visibility = View.VISIBLE
                        orderListingApiCall()
                    }
                }
            }
        })
    }

    override fun insuranceOfferCallback(
        orderId: String?,
        recommendedReward: String,
        pos: Int,
        price: String?,
        deliverDate: String,
        offerType: String
    ) {
        clickedOrderId = orderId ?: ""
        recommendReward = recommendedReward
        selectedPos = pos.toString()
        mDeliverDate = deliverDate
        mPayment = price.toString()
        offerTypes = offerType
    }

    private fun orderListingApiCall() {
        presenter.getRequestsApiCall(
            getAuthAccessToken(context),
            PAGE_LIMIT,
            orderListing.size,
            requestType,
            picUpLocation,
            dropOffLocation,
            currentLocation
        )
    }

    @SuppressLint("SetTextI18n")
    fun setNoOrderText() {
        if (orderListing.isEmpty()) {
            tvNoData.visibility = View.VISIBLE
            //tvRequestsCount.text = orderListing.size.toString() + " " + getString(R.string.requests)
        } else {
            tvNoData.visibility = View.GONE
            //tvRequestsCount.text = orderListing.size.toString() + " " + getString(R.string.requests)
        }
    }

    override fun selectedRequestPos(pos: Int) {

    }

    private fun acceptOfferConfirm(cardId: String) {
        val alertLayout = layoutInflater.inflate(R.layout.layout_accept_offer, null)
        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(alertLayout)
        val mEdtDeliveryCharges = alertLayout.findViewById(R.id.etDescription) as TextInputEditText
        val tvCancel = alertLayout.findViewById(R.id.tv_cancel) as TextView
        val tvSubmit = alertLayout.findViewById(R.id.tv_submit) as TextView

        mEdtDeliveryCharges.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                val str = s.toString()
                if (str.length == 1 && str.startsWith(".")) {
                    mEdtDeliveryCharges.setText("")
                    mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                } else if (str.length == 1 && str.startsWith("0")) {
                    mEdtDeliveryCharges.setText("")
                    mEdtDeliveryCharges.setSelection(mEdtDeliveryCharges.text.toString().length)
                }
            }
        })
        tvSubmit.setOnClickListener {
            if (mEdtDeliveryCharges.text.toString().isEmpty()) {
                presenter.makeOffersAndAcceptOrderApiCall(
                    getAuthAccessToken(context),
                    clickedOrderId,
                    mPayment,
                    DriverAction.ACCEPT,
                    selectedPos.toInt(),
                    currentLocation.get(1).toDouble(),
                    currentLocation.get(0).toDouble(),
                    mDeliverDate,
                    cardId,
                    0.0
                )
            } else {
                var shipping: Double = 0.0
                shipping = mEdtDeliveryCharges.text.toString().toDouble()
                presenter.makeOffersAndAcceptOrderApiCall(
                    getAuthAccessToken(context),
                    clickedOrderId,
                    mPayment,
                    DriverAction.ACCEPT,
                    selectedPos.toInt(),
                    currentLocation.get(1).toDouble(),
                    currentLocation.get(0).toDouble(),
                    mDeliverDate,
                    cardId,
                    shipping
                )
            }

            dialog.dismiss()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra(POSITION, 0)
            orderListing.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, orderListing.size - 1)
            setNoOrderText()
        }
        if (requestCode == REQUESTED_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra(POSITION, 0)
            orderListing.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, orderListing.size - 1)
            setNoOrderText()
        }
        if (requestCode == 321 && resultCode == Activity.RESULT_OK) {
            /* if (CheckNetworkConnection.isOnline(context)) {
                 orderListing.clear()
                 presenter.getRequestsApiCall(getAuthAccessToken(context), PAGE_LIMIT, 0, requestType, pickCountry, dropDownCountry, currentLocation)
             }*/
            isFirstTime = false
        }
        if (requestCode == REQ_CODE_PICK_UP && resultCode == Activity.RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            val latitude = place.latLng?.latitude ?: 0.0
            val longitude = place.latLng?.longitude ?: 0.0
            val array = java.util.ArrayList<Double>()
            array.add(longitude)
            array.add(latitude)

            Log.d("Location", "driver pickup ->" + place.latLng.toString())

            placePickerAddress = place.address
            picUpLocation = array
            getAddress(latitude, longitude)
        }
        if (requestCode == REQ_CODE_DROP_OFF && resultCode == Activity.RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            val latitude = place.latLng?.latitude ?: 0.0
            val longitude = place.latLng?.longitude ?: 0.0
            val array = java.util.ArrayList<Double>()
            array.add(longitude)
            array.add(latitude)
            placePickerAddress = place.name

            Log.d("Location", "driver dropoff ->" + place.latLng.toString())

            dropOffLocation = array
            getAddress2(latitude, longitude)
        }
        if (requestCode == 402 && resultCode == Activity.RESULT_OK && data != null) {
            if (CheckNetworkConnection.isOnline(activity)) {
                cardId = data.getStringExtra("cardId").toString()
                if (offerTypes.equals("true")) {
                    startActivity(
                        Intent(context, MakeAnOfferActivity::class.java)
                            .putExtra(ORDER_ID, clickedOrderId)
                            .putExtra("itemPrice", recommendReward)
                            .putExtra("cardId", cardId)
                            .putExtra(POSITION, selectedPos)
                    )
                } else {
                    acceptOfferConfirm(cardId)
                    // presenter.makeOffersAndAcceptOrderApiCall(getAuthAccessToken(activity), clickedOrderId, mPayment, DriverAction.ACCEPT, selectedPos.toInt(), currentLocation.get(1).toDouble(), currentLocation.get(0).toDouble(), mDeliverDate, cardId, 0.0)
                }
            }
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder: Geocoder
        var addresses: List<Address>? = null
        geocoder = Geocoder(activity, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                googlePlaceAddress = addresses[0].countryName
            }
            val pickCountri = if (addresses != null && addresses.isNotEmpty()) {

                when {
                    addresses[0].locality != null -> {
                        addresses[0].locality
                    }
                    addresses[0].adminArea != null -> {
                        addresses[0].adminArea
                    }
                    addresses[0].countryName != null -> {
                        addresses[0].countryName
                    }
                    else -> {
                        ""
                    }
                }
            } else {
                Toast.makeText(activity, R.string.no_result_found, Toast.LENGTH_SHORT).show()
                ""
            }


            if (placePickerAddress == googlePlaceAddress) {
                edtFrom.text = placePickerAddress
                pickCountry = placePickerAddress!!

            } else {

                pickCountry = pickCountri!!
                edtFrom.text = "$pickCountry, $googlePlaceAddress"
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        view?.let { hideSoftKeyBoard(activity, it) }
    }

    private fun getAddress2(latitude: Double, longitude: Double) {
        val geocoder: Geocoder
        var addresses: List<Address>? = null
        geocoder = Geocoder(activity, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                googlePlaceAddress = addresses[0].countryName
            }
            val dropCountri = if (addresses != null && addresses.isNotEmpty()) {
                /*if (addresses[0].locality != null) {
                    addresses[0].locality
                } else if (addresses[0].subLocality != null) {
                    addresses[0].subLocality
                } else if (addresses[0].subAdminArea != null) {
                    addresses[0].subAdminArea
                } else if (addresses[0].adminArea != null) {
                    addresses[0].adminArea
                } else {
                    ""
                }*/
                if (addresses[0].locality != null) {
                    addresses[0].locality
                } else if (addresses[0].adminArea != null) {
                    addresses[0].adminArea
                } else if (addresses[0].countryName != null) {
                    addresses[0].countryName
                } else {
                    ""
                }
            } else {
                Toast.makeText(activity, R.string.no_result_found, Toast.LENGTH_SHORT).show()
                ""
            }

            if (placePickerAddress == googlePlaceAddress) {
                edtTo.text = placePickerAddress
                dropDownCountry = placePickerAddress!!

            } else {
                dropDownCountry = dropCountri
                edtTo.text = "$placePickerAddress,$googlePlaceAddress"

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (isValidationsOk()) {
            isFilterClicked = true
            orderListing.clear()
            adapter.notifyDataSetChanged()
            presenter.getRequestsApiCall(
                getAuthAccessToken(context),
                PAGE_LIMIT,
                0,
                requestType,
                picUpLocation,
                dropOffLocation,
                currentLocation
            )
            view?.let { hideSoftKeyBoard(activity, it) }
        }
    }

    override fun onApiSuccess(orderList: List<OrderListing>) {
        swipeRefresh.isRefreshing = false
        isApiCalling = false
        isFilterClicked = false
        currentListSize = orderList.size
        dialogIndeterminate.dismiss()
        Log.e("Success", orderList.toString())
        progress_bar.visibility = View.GONE
        isListEmpty = orderList.isEmpty()
        orderListing.addAll(orderList)
        adapter.notifyDataSetChanged()
        if (currentListSize == 0 && orderListing.isEmpty()) {
            tvNoData.visibility = View.VISIBLE
            //tvRequestsCount.text = orderListing.size.toString() + " " + getString(R.string.requests)
        } else {
            tvNoData.visibility = View.GONE
            //tvRequestsCount.text = orderListing.size.toString() + " " + getString(R.string.requests)
        }
    }

    override fun acceptOrderApiSuccess(position: Int) {
        val dialog = Dialog(activity,R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog_success)
        val text = dialog.findViewById(R.id.tvDescription) as TextView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        text.text = activity.getString(R.string.offer_accepted)
        tvTitle.text = activity.getString(R.string.success)
        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
        dialogButton.text = activity.getString(R.string.yes)
        dialogButton.setOnClickListener {
            dialog.dismiss()
            orderListing.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, orderListing.size - 1)
            if (orderListing.size == 0) {
                //tvRequestsCount.text = getString(R.string.requests)
                tvNoData.visibility = View.VISIBLE
            } else {
                //tvRequestsCount.text = orderListing.size.toString() + " " + getString(R.string.requests)
                tvNoData.visibility = View.GONE
            }
        }
        dialog.show()
    }

    override fun onFilterSuccess(signupModel: SignupModel) {
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        swipeRefresh.isRefreshing = false
        dialogIndeterminate.dismiss()
        progress_bar.visibility = View.GONE
        Toast.makeText(context, R.string.sww_error, Toast.LENGTH_LONG).show()
        isApiCalling = false
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        swipeRefresh.isRefreshing = false
        dialogIndeterminate.dismiss()
        progress_bar.visibility = View.GONE
        isApiCalling = false
        if (code == 401) {
            val dialog = Dialog(activity,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog_success)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = activity.getString(R.string.error)
            tvDescription.text = activity.getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = activity.getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(activity).remove(ACCESS_TOKEN)
                activity.finishAffinity()
                startActivity(Intent(activity, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            Toast.makeText(context, errorBody, Toast.LENGTH_LONG).show()
        }
    }

    override fun validationsFailure(type: String?) {
        swipeRefresh.isRefreshing = false
        dialogIndeterminate.dismiss()
        progress_bar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun deletedRequestPos(pos: Int) {
        orderListing.removeAt(pos)
        adapter.notifyDataSetChanged()
        setNoOrderText()
    }

    override fun selectedReportPos(orderId: String?, opposition_id: String, pos: Int) {
        startActivity(
            Intent(activity, ReportActivity::class.java).putExtra(OPPOSITION_ID, orderId)
                .putExtra(ORDER_ID, opposition_id)
        )
    }
}
