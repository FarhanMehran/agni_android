package com.capcorp.ui.user.homescreen.orders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailActivity
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailContract
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailPresenter
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestOfferAdapter
import com.capcorp.utils.*
import com.capcorp.webservice.models.DataApplyCoupon
import com.capcorp.webservice.models.DataRemoveCoupon
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.orders.Offer
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_before_detail.*


class RequestBeforeDetailActivity : BaseActivity(), View.OnClickListener,
    RequestDetailContract.View, RequestOfferAdapter.SelectedPos {


    private val TAG = javaClass.simpleName
    private var presenter = RequestDetailPresenter()
    private var orderId: String = ""
    var orderListingDetail: OrderListing? = null
    private lateinit var requestOfferAdapter: RequestOfferAdapter
    private var selectedPos = 0
    var offerList: ArrayList<Offer> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_before_detail)
        presenter.attachView(this)

        setUpView()
        setRecyclerAdapter()
        clickListener()

        val data = Gson().fromJson(intent.getStringExtra(REQUEST_DETAIL), OrderListing::class.java)
        orderId = data._id.toString()
        setView(data)
        presenter.getOrderDetail(getAuthAccessToken(this), data._id.toString())
    }

    private fun setRecyclerAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvOffers.layoutManager = layoutManager
        requestOfferAdapter =
            RequestOfferAdapter(rvOffers.context, offerList, this, this@RequestBeforeDetailActivity)
        rvOffers.adapter = requestOfferAdapter
    }

    private var type: String? = null

    private fun setUpView() {
        type = intent.getStringExtra(USERTYPE)
    }

    private fun clickListener() {
        cvViewOrderDetail.setOnClickListener(this)
        tvBack.setOnClickListener(this)
    }


    @SuppressLint("HardwareIds")
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.tvBack -> {
                onBackPressed()
            }
            R.id.cvViewOrderDetail -> {
                startActivityForResult(
                    Intent(this, RequestDetailActivity::class.java)
                        .putExtra(REQUEST_DETAIL, Gson().toJson(orderListingDetail))
                        .putExtra("from_request", "true"), REQUESTED_RESULT_CODE
                )
            }
        }
    }

    override fun acceptRequestSuccess() {
    }

    override fun orderDetailSuccess(data: OrderListing) {
        orderId = data._id.toString()
        orderListingDetail = data
        setView(data)
    }

    override fun applyCouponSuccess(body: DataApplyCoupon) {

    }

    override fun removeCouponSuccess(body: DataRemoveCoupon?) {
    }

    override fun getH2DFeeSucess(h2dFee: H2dFeeResponse?) {

    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setView(data: OrderListing) {

        tvItemName.text = data.itemName
        tvRequiredBy.text = data.pickUpDate?.let { getOnlyDate(it, "EEE · MMM d") }
        when (data.orderType) {
            RequestType.TRANSPORT -> {
                Glide.with(this).load(data.itemImages?.get(0)?.original).into(ivImage)
                tvItemName.text = data.type
            }
            RequestType.PARCEL -> {
                if ((data.outerParcelImagesURL?.size ?: 0) > 0) {
                    Glide.with(this).load(data.outerParcelImagesURL?.get(0)?.original).into(ivImage)
                }
                tvItemName.text =
                    "${data.dimensionArray?.get(0)?.weight} kg · (${data.dimensionArray?.get(0)?.length} * ${
                        data.dimensionArray?.get(0)?.width
                    } * ${data.dimensionArray?.get(0)?.height}) cm"

            }
            RequestType.GROCERIES -> {
                tvItemName.text =
                    "${data.groceryItems.size} ${getString(R.string.items)} ${getString(R.string.from)} ${data.storeDetails.size} ${
                        getString(R.string.store)
                    }"
            }
            RequestType.SHIPMENT -> {
                if ((data.shipItemImages?.size ?: 0) > 0) {
                    Glide.with(this).load(data.shipItemImages?.get(0)?.original).into(ivImage)
                }
                tvItemName.text = data.itemName
            }
        }

        if ((data.offers?.size ?: 0) > 0) {
            tvNoOfferFound.visibility = View.GONE
            if (offerList.size > 0)
                offerList.clear()
            data.offers?.let {
                for (i in it.indices) {
                    val ofrs = it[i]
                    ofrs.orderStatus = data.orderStatus
                    offerList.add(ofrs)
                }
            }
            if (::requestOfferAdapter.isInitialized)
                requestOfferAdapter.notifyDataSetChanged()
        } else {
            tvNoOfferFound.visibility = View.VISIBLE
        }

    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        root_details_request.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        if (code == 401) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(this).remove(ACCESS_TOKEN)
                finishAffinity()
                startActivity(Intent(this, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            root_details_request.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {

    }

    override fun selectedPos(position: Int) {
        selectedPos = position
    }


}