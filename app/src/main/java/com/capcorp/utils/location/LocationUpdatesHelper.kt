//package com.capcorp.utils.location
//
//import android.Manifest
//import android.content.*
//import android.content.pm.PackageManager
//import android.location.Location
//import android.net.Uri
//import android.os.Bundle
//import android.os.IBinder
//import android.os.Parcelable
//import android.os.PersistableBundle
//import android.preference.PreferenceManager
//import android.provider.Settings
//import android.util.Log
//import android.view.View
//import androidx.core.app.ActivityCompat
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.capcorp.BuildConfig
//import com.capcorp.R
//import com.capcorp.ui.base.BaseActivity
//import com.google.android.material.snackbar.Snackbar
//
//
//abstract class LocationUpdatesHelper : BaseActivity(),
//    SharedPreferences.OnSharedPreferenceChangeListener {
//
//    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
//
//    // The BroadcastReceiver used to listen from broadcasts from the service.
//    private var myReceiver: MyReceiver? = null
//
//    // A reference to the service used to get location updates.
//    var mService: LocationUpdatesService? = null
//
//    // Tracks the bound state of the service.
//    private var mBound = false
//
//    // Get location updates in derived class
//    abstract fun onLocationReceived(location: Location?)
//
//    private val mServiceConnection = object : ServiceConnection {
//
//        override fun onServiceConnected(name: ComponentName, service: IBinder) {
//            val binder = service as LocationUpdatesService.LocalBinder
//            mService = binder.service
//            mBound = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            mService = null
//            mBound = false
//        }
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        myReceiver?.let {
//            LocalBroadcastManager.getInstance(this).registerReceiver(
//                it,
//                IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
//            )
//        }
//    }
//
//    override fun onPause() {
//        if (myReceiver != null) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver!!)
//        }
//        super.onPause()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState, persistentState)
//        init()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        init()
//    }
//
//    private fun init() {
//        myReceiver = MyReceiver()
//
//        if (LocationUtils.requestingLocationUpdates(this)) {
//            if (!checkPermissions()) {
//                requestPermissions()
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        PreferenceManager.getDefaultSharedPreferences(this)
//            .registerOnSharedPreferenceChangeListener(this)
//
//        bindService(
//            Intent(this, LocationUpdatesService::class.java), mServiceConnection,
//            Context.BIND_AUTO_CREATE
//        )
//    }
//
//    fun requestLocationUpdates() {
//        if (!checkPermissions()) {
//            requestPermissions()
//        } else {
//            mService?.requestLocationUpdates()
//        }
//    }
//
//    fun stopLocationUpdtes() {
//        mService?.removeLocationUpdates()
//    }
//
//    override fun onStop() {
//        if (mBound) {
//            // Unbind from the service. This signals to the service that this activity is no longer
//            // in the foreground, and the service can respond by promoting itself to a foreground
//            // service.
//            unbindService(mServiceConnection)
//            mBound = false
//        }
//        PreferenceManager.getDefaultSharedPreferences(this)
//            .unregisterOnSharedPreferenceChangeListener(this)
//        super.onStop()
//    }
//
//    /**
//     * Returns the current state of the permissions needed.
//     */
//    private fun checkPermissions(): Boolean {
//        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//    }
//
//    private fun requestPermissions() {
//        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//
//        // Provide an additional rationale to the user. This would happen if the user denied the
//        // request previously, but didn't check the "Don't ask again" checkbox.
//        if (shouldProvideRationale) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.")
//            Snackbar.make(
//                window.decorView.rootView,
//                R.string.location_permisssion,
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction(R.string.ok, View.OnClickListener {
//                    // Request permission
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                        REQUEST_PERMISSIONS_REQUEST_CODE
//                    )
//                })
//                .show()
//        } else {
//            Log.i(TAG, "Requesting permission")
//            // Request permission. It's possible this can be auto answered if device policy
//            // sets the permission in a given state or the user denied the permission
//            // previously and checked "Never ask again".
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_PERMISSIONS_REQUEST_CODE
//            )
//        }
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        Log.i(TAG, "onRequestPermissionResult")
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            when {
//                grantResults.isEmpty() -> // If user interaction was interrupted, the permission request is cancelled and you
//                    // receive empty arrays.
//                    Log.i(TAG, "User interaction was cancelled.")
//                grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission was granted.
//                    mService?.requestLocationUpdates()
//                else -> // Permission denied.
//                    //                setButtonsState(false)
//                    Snackbar.make(
//                        window.decorView.rootView,
//                        R.string.permission_denied_explanation,
//                        Snackbar.LENGTH_INDEFINITE
//                    )
//                        .setAction(R.string.settings) {
//                            // Build intent that displays the App settings screen.
//                            val intent = Intent()
//                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                            val uri = Uri.fromParts(
//                                "package",
//                                BuildConfig.APPLICATION_ID, null
//                            )
//                            intent.data = uri
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            startActivity(intent)
//                        }
//                        .show()
//            }
//        }
//    }
//
//    /**
//     * Receiver for broadcasts sent by [LocationUpdatesService].
//     */
//    private inner class MyReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val location =
//                intent.getParcelableExtra<Parcelable>(LocationUpdatesService.EXTRA_LOCATION)
//            if (location != null) {
//                /*Toast.makeText(context, LocationUtils.getLocationText(location as Location?), Toast.LENGTH_SHORT).show()
//                onLocationReceived(location)*/
//            }
//        }
//    }
//
//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
//        // Update the buttons state depending on whether location updates are being requested.
//        if (s == LocationUtils.KEY_REQUESTING_LOCATION_UPDATES) {
////            setButtonsState(sharedPreferences.getBoolean(LocationUtils.KEY_REQUESTING_LOCATION_UPDATES,
////                    false))
//        }
//    }
//}