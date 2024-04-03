package com.capcorp.ui.user.homescreen.home.create_order_from_web

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.home.transport.productinformation.ProductInformationActivity
import com.capcorp.utils.DialogIndeterminate
import com.capcorp.utils.LocaleManager
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_open_webview_for_product_url.*


class CreateOrderFromWeb : BaseActivity() {
    var changedUrl: String = ""
    lateinit var progressDialog: SpotsDialog
    private lateinit var dialogIndeterminate: DialogIndeterminate
    var htmlContentBase64: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_webview_for_product_url)
        progressDialog = SpotsDialog(this, R.style.Custom)
        dialogIndeterminate = DialogIndeterminate(this)
        webview.settings.javaScriptEnabled = true

        webview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    dialogIndeterminate.dismiss()
                    changedUrl = view.url.toString()
                    //Log.e("url_changed",changedUrl+"")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                runOnUiThread {
                    dialogIndeterminate.show()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                changedUrl = view!!.url.toString()
                dialogIndeterminate.dismiss()
                Log.e("url_changed", changedUrl + "")

                view.evaluateJavascript("document.documentElement.outerHTML.toString()") {
                    val html = it.replace("\\u003C", "<").replace("\n", "").replace("\\", "")
                    Log.e("HtmlContent", html)

                    val data: ByteArray = html.toByteArray()
                    htmlContentBase64 = html
                    LocaleManager.storeHTMLContent(this@CreateOrderFromWeb, htmlContentBase64)
                    Log.e("HtmlContentBase64", htmlContentBase64)
                }


            }
        }
        intent.getStringExtra("url")?.let { webview.loadUrl(it) }


        next.setOnClickListener {
            startActivity(
                Intent(this, ProductInformationActivity::class.java)
                    .putExtra("url", changedUrl)
            )
            finish()
        }
    }


    fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogIndeterminate.dismiss()
    }
}
