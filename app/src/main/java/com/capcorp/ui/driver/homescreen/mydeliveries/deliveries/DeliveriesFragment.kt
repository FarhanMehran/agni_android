package com.capcorp.ui.driver.homescreen.mydeliveries.deliveries


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.home.makeoffer.MakeAnOfferActivity
import com.capcorp.ui.driver.homescreen.home.requests.RequestsAdapter
import com.capcorp.ui.driver.homescreen.mydeliveries.MyDeliveriesActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import kotlinx.android.synthetic.main.fragment_deliveries.*

class DeliveriesFragment : Fragment(), DeliveriesContract.View,
    RequestsAdapter.InsuranceOfferCallback, DeliveriesInterface {

    override fun rejectOrder() {
        adapter.notifyDataSetChanged()
        showToast(context, getString(R.string.Successfully_cancelled_order))
        presenter.getMyDeliveries(
            getAuthAccessToken(context),
            PAGE_LIMIT,
            0,
            orderStatus,
            pickCountry,
            dropDownCountry
        )
    }


    override fun onCanceled(
        reasons: String,
        id: String,
        payment: String,
        lat: Double,
        lng: Double,
        driverArrivalDate: String
    ) {


        presenter.makeOffersAndAcceptOrderApiCall(
            getAuthAccessToken(activity),
            id,
            payment,
            "REJECT",
            lat,
            lng,
            driverArrivalDate,
            reasons
        )

    }

    private var ordersList = ArrayList<OrderListing>()
    private lateinit var adapter: DeliveriesAdapter
    private val presenter = DeliveriesPresenter()
    private lateinit var orderStatus: String
    private var pickCountry: String = ""
    private var dropDownCountry: String = ""
    val RQ_CODE_MAKE_OFFER = 100
    private val RQ_CODE_OFFER_ACCEPTED = 200
    private var clickedOrderId: String = ""
    private var recommendReward: String = ""
    private var selectedPos: String = ""
    private var mPayment: String = ""
    private var mDeliverDate: String = ""
    private var cardId: String = ""
    private var offerTypes: String = ""


    companion object {
        fun newInstance(orderStatus: String?): DeliveriesFragment {
            val bundle = Bundle()
            bundle.putString("orderStatus", orderStatus)
            val fragment = DeliveriesFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_deliveries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        orderStatus = arguments?.getString("orderStatus") ?: ""
        if (orderStatus.equals(OrderStatusDriver.OFFER_MADE)) {
            orderStatus = OrderStatus.REQUESTED
        }
        pickCountry = MyDeliveriesActivity.pickCountry
        dropDownCountry = MyDeliveriesActivity.dropDownCountry
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.app_green)
        ordersList.clear()
        setAdapter()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        presenter.getMyDeliveries(
            getAuthAccessToken(context),
            PAGE_LIMIT,
            0,
            orderStatus,
            pickCountry,
            dropDownCountry
        )
    }

    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = DeliveriesAdapter(context, ordersList, orderStatus, this, this, this)
        recyclerView.adapter = adapter
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_MAKE_OFFER && resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra(POSITION, 0)
            val price = data.getDoubleExtra(PRICE, 0.0)
            val additional = data.getDoubleExtra("additional", 0.0)
            for (i in ordersList[position].offers!!.indices) {
                ordersList[i].offers!![i].totalPrice = price + additional
            }

            adapter.notifyDataSetChanged()
        }
        if (requestCode == RQ_CODE_OFFER_ACCEPTED && resultCode == Activity.RESULT_OK) {
            if (CheckNetworkConnection.isOnline(context)) {
                ordersList.clear()
                presenter.getMyDeliveries(
                    getAuthAccessToken(context),
                    PAGE_LIMIT,
                    0,
                    orderStatus,
                    pickCountry,
                    dropDownCountry
                )
            }
            activity?.sendBroadcast(Intent(ORDER_CANCEL))
        }
        if (requestCode == 145 && resultCode == Activity.RESULT_OK) {

        }
        if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            var isUserRated = data?.getStringExtra(IS_USERRATED)
            var position = data?.getIntExtra(POSITION, 0) ?: 0
            var updatedValue = ordersList[position]
            updatedValue.isUserRated = isUserRated.toString()
            ordersList.set(position, updatedValue)
            adapter.notifyDataSetChanged()
        }

        if (requestCode == 406 && resultCode == Activity.RESULT_OK && data != null) {
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
                }
            }
        }
    }

    private fun setListeners() {
        swipeRefresh.setOnRefreshListener {
            if (CheckNetworkConnection.isOnline(context)) {
                ordersList.clear()
                adapter.notifyDataSetChanged()
                presenter.getMyDeliveries(
                    getAuthAccessToken(context),
                    PAGE_LIMIT,
                    0,
                    orderStatus,
                    pickCountry,
                    dropDownCountry
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        // activity?.unregisterReceiver(broadcastReceiver)
    }

    override fun onDeliveriesApiSuccess(orderList: List<OrderListing>?) {
        ordersList.clear()
        swipeRefresh.isRefreshing = false
        ordersList.addAll(orderList as ArrayList)
        adapter.notifyDataSetChanged()
        Log.e("list size", "size" + orderList.size + "=======================================")
        if (orderList.isEmpty()) {
            tvMsg.visibility = View.VISIBLE
            when (orderStatus) {
                OrderStatus.REQUESTED -> tvMsg.text = getString(R.string.offers_made_empty_msg)

                OrderStatus.ACCEPTED -> tvMsg.text = getString(R.string.offers_accepted_empty_msg)

                OrderStatus.COMPLETED -> tvMsg.text = getString(R.string.offers_completed_empty_msg)
            }

        } else {
            tvMsg.visibility = View.GONE
        }
        progressBar.visibility = View.GONE
    }

    override fun showLoader(isLoading: Boolean) {
    }

    override fun apiFailure() {
        swipeRefresh.isRefreshing = false
        Toast.makeText(context, R.string.sww_error, Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        if (code == 401) {
            val dialog = Dialog(requireActivity(),R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = requireActivity().getString(R.string.error)
            tvDescription.text = requireActivity().getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = requireActivity().getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(requireActivity()).remove(ACCESS_TOKEN)
                requireActivity().finishAffinity()
                startActivity(Intent(requireActivity(), SplashActivity::class.java))
            }
            dialog.show()
        } else {
            Toast.makeText(context, errorBody, Toast.LENGTH_LONG).show()
        }
    }

    override fun validationsFailure(type: String?) {

    }
}
