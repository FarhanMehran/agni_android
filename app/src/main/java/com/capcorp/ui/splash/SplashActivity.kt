package com.capcorp.ui.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.util.SparseIntArray
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capcorp.R
import com.capcorp.ui.signup.GetStartedActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.H2dFeeResponse
import com.codebrew.encober.utils.LocaleHelper
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


class SplashActivity : AppCompatActivity(), SplashContract.View {

    private var mErrorString: SparseIntArray? = null
    private val REQUEST_PERMISSIONS = 20
    lateinit var context: SplashActivity
    private val presenter = SplashPresenter()


    val quickPermissionsOption = QuickPermissionsOptions(
        handleRationale = true,
        rationaleMessage = "Custom rational message",
        permanentlyDeniedMessage = "Custom permanently denied message",
        rationaleMethod = { req -> rationaleCallback(req) },
        handlePermanentlyDenied = true,
        permanentDeniedMethod = { req -> permissionsPermanentlyDenied(req) }
    )

    private fun permissionsPermanentlyDenied(req: QuickPermissionsRequest) {
        explain()
    }


    private fun checkConnection(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun rationaleCallback(req: QuickPermissionsRequest) {

        showRationaleDialog(request = req)
    }

    private fun showRationaleDialog(request: QuickPermissionsRequest) {
        AlertDialog.Builder(context)
            .setPositiveButton(R.string.button_allow) { _, _ -> request.proceed() }
            .setCancelable(false)
            .setMessage(R.string.permission_location_rationale)
            .show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        presenter.attachView(this)
//
//        if(getAccessToken(this).toString().trim() != "bearer") {
//            presenter.getH2dFee(getAuthAccessToken(this))
//        }

        mErrorString = SparseIntArray()
        val currentapiVersion = Build.VERSION.SDK_INT
        context = this@SplashActivity
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        onLocation()
//        try {
//            val video = Uri.parse("android.resource://" + packageName + "/" + R.raw.videoh)
////            video_view.setVideoURI(video)
////            video_view.setOnCompletionListener(MediaPlayer.OnCompletionListener {
////                val array = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
////
////                onLocation()
////
////            })
////            video_view.start()
//        } catch (e: Exception) {
//            startTimer()
//        }
    }

    private fun explain() {
        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setMessage(R.string.permission_location_rationale)
            .setPositiveButton(getString(R.string.yes)) { paramDialogInterface, _ ->
                paramDialogInterface.dismiss()

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                finish()
            }
            .setNegativeButton(
                "Cancel"
            ) { paramDialogInterface, _ -> paramDialogInterface.dismiss() }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == 0)
                turnOnLocation()
            else
                onLocation()
        }
    }

    private fun turnOnLocation() {

        context.showDialog {
            setTitle(getString(R.string.location_on_message))
            positiveButton(getString(R.string.proceed)) { openLocatonSettings() }
        }
    }

    private fun openLocatonSettings() {
        context.showToast(getString(R.string.turn_on_location))
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
        finish()
    }


    private fun onLocation() = runWithPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,/* Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,*/
        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, options = quickPermissionsOption
    ) {


        if (checkConnection(this)) {
            startTimer()
        } else {
            turnOnLocation()
        }


    }


    override fun attachBaseContext(base: Context) {


        if (SharedPrefs.with(this).getString(PREF_LANG, "") == "") {

            val code = Locale.getDefault().language

            if (code == "es")
                SharedPrefs.with(this).save(PREF_LANG, "es")
            else
                SharedPrefs.with(this).save(PREF_LANG, "en")
        }

        super.attachBaseContext(
            LocaleHelper.onAttach(
                base,
                SharedPrefs.with(this).getString(PREF_LANG, "en")
            )
        )
    }

    private fun onPermissionsGranted(requestcode: Int) {
        startTimer()
    }

    private fun startTimer() {
        SharedPrefs.with(this).save(IS_FIRST_TIME, "true")
        Handler().postDelayed({
            if (!getAccessToken(this).isEmpty()) {
                Log.e("UserType",SharedPrefs.with(this).getString(USER_TYPE, ""))
                if (SharedPrefs.with(this).getString(USER_TYPE, "").equals(UserType.USER)) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else if (SharedPrefs.with(this).getString(USER_TYPE, "")
                        .equals(UserType.DRIVER)
                ) {
                    startActivity(
                        Intent(
                            this,
                            com.capcorp.ui.driver.homescreen.HomeActivity::class.java
                        )
                    )
                } else {
                    startActivity(
                        Intent(
                            this,
                            com.capcorp.ui.driver.homescreen.HomeActivity::class.java
                        )
                    )
                }
                finishAffinity()
            } else {
                if (SharedPrefs.with(this).getString(WALKTHROUGH_STATUS, "").equals("true")) {
                    startActivity(Intent(this, GetStartedActivity::class.java))
                    finishAffinity()
                } else {
                    startActivity(Intent(this, GetStartedActivity::class.java))
                    finishAffinity()
                }
            }


        }, 100)
    }

    override fun getH2DFeeSucess(h2dFee: H2dFeeResponse?) {
//        SharedPrefs.with(this).save(RECOMMENDED_FEE, h2dFee?.recommendedFee?.toFloat())

//        val fee = SharedPrefs.with(this).getFloat(RECOMMENDED_FEE, 10.0f)
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        rlSplashRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        rlSplashRoot.showSnack(errorBody ?: "")
    }

    override fun validationsFailure(type: String?) {
        rlSplashRoot.showSnack(type ?: "")
    }
}
