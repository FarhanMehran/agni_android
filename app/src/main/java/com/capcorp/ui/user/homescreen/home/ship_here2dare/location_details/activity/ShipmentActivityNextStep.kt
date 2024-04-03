package com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.utils.TRANSPORT_MIN_PRICE
import com.capcorp.utils.getFormatFromDate
import com.capcorp.utils.hideSoftKeyBoard
import com.capcorp.utils.showSnack
import com.capcorp.webservice.models.request_model.ShipDataRequest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_shipment_next.*
import java.util.*

class ShipmentActivityNextStep : BaseActivity(), View.OnClickListener {

    var shipData = ShipDataRequest()
    private val REQ_CODE_PICK_UP = 100
    private val REQ_CODE_DROP_OFF = 101
    private var myCalendar = Calendar.getInstance()
    private var pickPlace: Place? = null
    private var dropDownPlace: Place? = null
    private var placePickerAddress: String? = null
  //  private var googlePlaceAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_next)

        shipData = intent.getSerializableExtra("data") as ShipDataRequest

        tvBack.setOnClickListener(this)
        edtFrom.setOnClickListener(this)
        edtTo.setOnClickListener(this)
        edtDeliveryBefore.setOnClickListener(this)
        next.setOnClickListener(this)

        myCalendar.time = Date() // Using today's date
        myCalendar.add(Calendar.DATE, 21) // Adding 21 days
        edtDeliveryBefore.text = getFormatFromDate(myCalendar.time, "MMM dd, yyyy")

        edtTravelerReward.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val price = s.toString()
                if (price.isEmpty()) {
                    edtTravelerReward.setText("$")
                    edtTravelerReward.setSelection(edtTravelerReward.length())
                }
            }
        })

        tvNote1.text = getString(R.string.we_recommand, "$" + String.format("%.2f",shipData.recommendedReward.toDouble()))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBack -> {
                finish()
            }
            R.id.edtFrom -> {
                openPlacePicker(REQ_CODE_PICK_UP, false)
            }
            R.id.edtTo -> {
                openPlacePicker(REQ_CODE_DROP_OFF, true)
            }
            R.id.edtDeliveryBefore -> {
                openDateTimePicker()
            }
            R.id.next -> {
                val itemDate = edtDeliveryBefore.text.toString().trim()
                val travelerReward = edtTravelerReward.text.toString().trim().replace("$", "")
                if (isValidationsOk(itemDate, travelerReward)) {
                    shipData.pickUpDate = myCalendar.timeInMillis.toString()
                    shipData.payment = travelerReward.toString()
                    startActivity(
                        Intent(this, SendMyOrderActivity::class.java)
                            .putExtra("data", shipData)
                    )

                }
            }

        }
    }

    private fun isValidationsOk(itemDate: String, travelerReward: String): Boolean {
        if (itemDate.isEmpty()) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.enter_pickup_time)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()

            }
            dialog.show()
            return false
        } else if (pickPlace == null) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.please_enter_pickup_address)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()

            }
            dialog.show()
            return false
        } else if (dropDownPlace == null) {
            view_time_root?.showSnack(getString(R.string.please_enter_drop_off_address))
            return false
        }else if (travelerReward.isEmpty()) {
            view_time_root?.showSnack(
                """${getString(R.string.you_have_to_pay_atleast)} ${
                    getString(
                        R.string.currency_sign
                    )
                }$TRANSPORT_MIN_PRICE ${getString(R.string.for_the_transport)}""".trimMargin()
            )
            return false
        } else if (travelerReward.toDouble() <= 0) {
            view_time_root?.showSnack(
                """${getString(R.string.you_have_to_pay_atleast)} ${
                    getString(
                        R.string.currency_sign
                    )
                }$TRANSPORT_MIN_PRICE ${getString(R.string.for_the_transport)}""".trimMargin()
            )
            return false
        } else {
            return true
        }
    }

    private fun openDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        val myCalendarMinDate = Calendar.getInstance()
        myCalendarMinDate.time = Date() // Using today's date
        myCalendarMinDate.add(Calendar.DATE, 1) // Adding 5 days

        datePickerDialog.datePicker.minDate = myCalendarMinDate.timeInMillis
        datePickerDialog.show()
    }

    var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            edtDeliveryBefore.text = getFormatFromDate(myCalendar.time, "MMM dd, yyyy")
        }


    private fun openPlacePicker(reqCode: Int, destination: Boolean) {
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        if (destination) {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setTypeFilter(TypeFilter.CITIES)
                .build(this)
            startActivityForResult(intent, reqCode)

        } else {

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setTypeFilter(TypeFilter.CITIES)
                .setTypeFilter(TypeFilter.REGIONS)
                .build(this)
            startActivityForResult(intent, reqCode)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Fragment Status", "On ACtivity result")
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQ_CODE_PICK_UP -> {
                    pickPlace = Autocomplete.getPlaceFromIntent(data)
                    val latitude = pickPlace?.latLng?.latitude ?: 0.0
                    val longitude = pickPlace?.latLng?.longitude ?: 0.0
                    val array = ArrayList<Double>()
                    array.add(longitude)
                    array.add(latitude)
                    placePickerAddress = pickPlace?.name
                    shipData.pickUpLocation = array

                    Log.d("Location", "Pickup shopper ->" + pickPlace?.latLng.toString())
                    edtFrom.text = "$placePickerAddress"
                    shipData.pickUpAddress = "$placePickerAddress"
                    shipData.pickUpCountry = "$placePickerAddress"
                    //getAddress(latitude, longitude)
                }
                REQ_CODE_DROP_OFF -> {
                    dropDownPlace = Autocomplete.getPlaceFromIntent(data)
                    val latitude = dropDownPlace?.latLng?.latitude ?: 0.0
                    val longitude = dropDownPlace?.latLng?.longitude ?: 0.0
                    val array = ArrayList<Double>()
                    array.add(longitude)
                    array.add(latitude)

                    Log.d("Location", "Dropoff shopper->" + dropDownPlace?.latLng.toString())

                    placePickerAddress = dropDownPlace?.name

                    shipData.dropDownLocation = array
                    edtTo.text = placePickerAddress
                    shipData.dropDownAddress = placePickerAddress.toString()
                    shipData.dropDownCountry = placePickerAddress.toString()

                    //getAddress2(latitude, longitude)
                }
            }
        }
    }

   /* private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder: Geocoder
        var addresses: List<Address>? = null
        geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                googlePlaceAddress = addresses[0].countryName
            }
            val pickCountry = if (addresses != null && addresses.isNotEmpty()) {
                if (addresses[0].locality != null) {
                    addresses[0].locality

                } else if (addresses[0].countryName != null) {
                    addresses[0].countryName
                } else {
                    ""
                }
            } else {
                Toast.makeText(this, R.string.no_result_found, Toast.LENGTH_SHORT).show()
                ""
            }

            if (placePickerAddress == googlePlaceAddress) {
                edtFrom.text = placePickerAddress
                shipData.pickUpAddress = placePickerAddress ?: ""
                shipData.pickUpCountry = placePickerAddress!!
            } else {

                edtFrom.text = "$placePickerAddress, $googlePlaceAddress"
                shipData.pickUpAddress = "$placePickerAddress, $googlePlaceAddress"
                shipData.pickUpCountry = "$placePickerAddress, $googlePlaceAddress"
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        hideSoftKeyBoard(this, view!!)
    }

    private fun getAddress2(latitude: Double, longitude: Double) {
        val geocoder: Geocoder
        var addresses: List<Address>? = null
        geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                googlePlaceAddress = addresses[0].countryName
            }
            val dropDownCountry = if (addresses != null && addresses.isNotEmpty()) {
                if (addresses[0].locality != null) {
                    addresses[0].locality

                } else if (addresses[0].countryName != null) {
                    addresses[0].countryName
                } else {
                    ""
                }
            } else {
                Toast.makeText(this, R.string.no_result_found, Toast.LENGTH_SHORT).show()
                ""
            }
            if (placePickerAddress == googlePlaceAddress) {
                edtTo.text = placePickerAddress
                shipData.pickUpAddress = placePickerAddress ?: ""
                shipData.pickUpCountry = placePickerAddress!!
            } else {
                edtTo.text = "$placePickerAddress, $googlePlaceAddress"
                shipData.pickUpAddress = "$placePickerAddress, $googlePlaceAddress"
                shipData.pickUpCountry = "$placePickerAddress, $googlePlaceAddress"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        hideSoftKeyBoard(this, view!!)
    }*/


}
