package com.capcorp.ui.user.homescreen.home.transport.productinformation

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.capcorp.R
import com.capcorp.ui.signup.phone_verification.HomePresenter
import com.capcorp.ui.user.homescreen.home.HomeContract
import com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity.ShipmentActivity
import com.capcorp.utils.LocaleManager
import com.capcorp.utils.getAuthAccessToken
import com.capcorp.webservice.models.KnowMoreResponse
import com.capcorp.webservice.models.home.Data
import com.capcorp.webservice.models.product_information.ProductInformation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class ProductInformationActivity : AppCompatActivity(), HomeContract.View {

    lateinit var context: ProductInformationActivity
    private var homePresenter = HomePresenter()

    fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_product_information)
        homePresenter.attachView(this)

        context = this@ProductInformationActivity

        if (intent != null && intent.hasExtra("url")) {

            val encodedString = LocaleManager.getHTMLContent(this).encode()

            val usernameBody: RequestBody? =
                intent.getStringExtra("url")?.replace("https://", "")?.replace("\\", "")?.let {
                    it
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                }
            val passwordBody: RequestBody? =
                encodedString.let {
                    it
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                }
            usernameBody?.let {
                passwordBody?.let { it1 ->
                    homePresenter.getProductInformation(
                        getAuthAccessToken(this),
                        it, it1
                    )
                }
            }
        }


    }


    override fun getHomeDetailsSuccess(data: Data) {

    }

    override fun getKnowMoreSuccess(data: KnowMoreResponse.Data) {

    }

    override fun getProductInformationSuccess(data: ProductInformation) {
        startActivity(
            Intent(this, ShipmentActivity::class.java)
                .putExtra("content", data).putExtra("url", intent.getStringExtra("url"))
        )
        finish()
    }

    override fun showLoader(isLoading: Boolean) {
    }

    override fun apiFailure() {
        finish()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        finish()
    }

    override fun validationsFailure(type: String?) {
        finish()
    }


}
