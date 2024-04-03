package com.capcorp.ui.driver.homescreen.payment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capcorp.R
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.thefinestartist.finestwebview.FinestWebView
import com.thefinestartist.finestwebview.FinestWebViewActivity
import com.thefinestartist.finestwebview.listeners.WebViewListener
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_connect_stripe.*


@Suppress("CAST_NEVER_SUCCEEDS")
class ConnectStripeActivity : AppCompatActivity(), ConectStripContract.View {
    var changedUrl: String? = ""
    var mAuthenticationCode: String? = null
    private val presenter = ConnectStripePresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var progressDialog: SpotsDialog? = null

    private var userData: SignupModel? = null
    var view: FinestWebView.Builder? = null


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_stripe)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        progressDialog = SpotsDialog(this, R.style.Custom)
        webview.clearCache(true)
        webview.settings.javaScriptEnabled = true

        userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)


//        val REDIRECT_URl = "https://connect.stripe.com/express/oauth/authorize?client_id=ca_HOtXRMgBZNVlNdJTav0DIS24BDFakoLW&response_type=code&state=${userData?._id}&redirect_uri=${BASE_URL}/user/connectWithStripeSignup%20target=#/"
//        val REDIRECT_URl =
//            "https://connect.stripe.com/express/oauth/authorize?client_id=ca_HOtX3XYS0hV5jkOmK6GSaYmhq0onDVnp&response_type=code&state=${userData?._id}&redirect_uri=${BASE_URL}/user/connectWithStripeSignup%20target=#/"

//        val REDIRECT_URl =
//            "https://connect.stripe.com/oauth/v2/authorize?" +
//                    "response_type=code&client_id=ca_HOtXRMgBZNVlNdJTav0DIS24BDFakoLW&scope=read_write&" +
//                    "redirect_uri=https://api-dev.h2d.app/api/user/connectWithStripeSignup&"+
//                    "stripe_user[email]="+userData?.emailId + "&stripe_user[country]="+userData?.countryISO+
//                    "&stripe_user[phone_number]="+userData?.fullNumber +"&stripe_user[first_name]=" + userData?.firstName +
//                    "&stripe_user[last_name]=" + userData?.lastName+"&stripe_user[business_type]=sole_prop&stripe_user[business_name]=H2D&stripe_user[url]=h2d.app&stripe_user[product_description]=Payment of traveler reward for delivering product to shopper"

       /* val REDIRECT_URl = intent.getStringExtra("url")
        if (REDIRECT_URl != null) {
            Log.d("connectwithstripe", REDIRECT_URl)
        }


        view = FinestWebView.Builder(this)
        view!!.backPressToClose(true)
        view!!.setWebViewListener(object : WebViewListener() {
            override fun onPageStarted(url: String?) {
                super.onPageStarted(url)
            }

            override fun onPageFinished(url: String?) {
                url?.let { it1 -> Log.e("ChangeURL", it1) }
                super.onPageFinished(url)
                if (url.equals("https://h2d.app/#/es")) {
                    Toast.makeText(
                        this@ConnectStripeActivity,
                        getString(R.string.account_linked_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    presenter.onConnectStripeApi(
                        getAuthAccessToken(this@ConnectStripeActivity),
                        userData?._id,
                        userData?.stripeConnectId
                    )

                }
            }

            override fun onPageCommitVisible(url: String?) {
                super.onPageCommitVisible(url)
            }
        })
        view!!.show("https://h2d.app/#/es")*/


//
        webview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    progressDialog!!.dismiss()
                    changedUrl = view?.url
                    changedUrl?.let { Log.e("ChangeURL", it) }

                    // changedUrl = view.url
                    //Log.e("url_changed",changedUrl+"")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressDialog?.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                changedUrl = view?.url
                changedUrl?.let { Log.e("ChangeURL", it) }
                progressDialog!!.dismiss()
               /* if (changedUrl?.contains("https://connect.stripe.com/connect/default/oauth/test?code=") == true) {
                    val uri = Uri.parse(changedUrl)
                    mAuthenticationCode = uri.getQueryParameter("code")
                }

                if (mAuthenticationCode != null) {
                    presenter.onConnectStripeApi(
                        getAuthAccessToken(this@ConnectStripeActivity),
                        mAuthenticationCode!!
                    )
                }*/
                if (changedUrl.equals("https://h2d.app")) {
                    Toast.makeText(
                        this@ConnectStripeActivity,
                        getString(R.string.account_linked_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    presenter.onConnectStripeApi(
                        getAuthAccessToken(this@ConnectStripeActivity),
                        userData?._id,
                        intent.getStringExtra("stripeID")
                    )

                }
                Log.e("url_changed", changedUrl + "")
            }
        }
        webview.settings.javaScriptEnabled = true
        intent.getStringExtra("url")?.let { webview.loadUrl(it) }
    }

    override fun onConnectStripeSuccess() {
        progressDialog?.dismiss()
        Toast.makeText(this, getString(R.string.account_linked_success), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        // linear?.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        //   linear?.showSnack(errorBody ?: getString(R.string.sww_error))
    }

    override fun validationsFailure(type: String?) {
    }
}
