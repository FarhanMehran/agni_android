package com.capcorp.ui

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.kbus.KBus
import com.capcorp.R
import com.capcorp.utils.*
import kotlinx.android.synthetic.main.activity_provider_location.*


class ProviderLocationActivity : AppCompatActivity() {

    private val CLOSE_ACTIVITY = 54212

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_location)


        GpsLocationReceiver.firstConnect = true

        KBus.subscribe<LocationEvent>(this) {
            showMessage(it.message)
        }

        btnContinue?.setOnClickListener {

            if (checkConnection(this))
                finish()
            else
                turnOnLocation()


        }


    }

    override fun onBackPressed() {
    }


    private fun turnOnLocation() {

        this.showDialog {
            setTitle(getString(R.string.location_on_message))
            positiveButton(getString(R.string.proceed)) { openLocatonSettings() }
        }
    }

    private fun openLocatonSettings() {
        this.showToast(getString(R.string.turn_on_location))
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }


    private fun checkConnection(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    override fun onStop() {
        super.onStop()
        KBus.unsubscribe(this)
    }

    private fun showMessage(message: String) {
        finish()

    }

    override fun onResume() {
        super.onResume()
        MyApplication.getInstnace()?.activityResumed()

        if (!checkConnection(this)) {
            tvHeader?.text = getString(R.string.gps_message)
            btnContinue?.text = getString(R.string.gps_settings)
        } else {

            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        MyApplication.getInstnace()?.activityPaused()
    }


}
