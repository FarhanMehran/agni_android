package com.capcorp.ui.user.homescreen.orders.accepted

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.orders.requested.RequestedContract
import com.capcorp.ui.user.homescreen.orders.requested.RequestedPresenter
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_requested.*


class AcceptedFragment : Fragment(), RequestedContract.View, AcceptedAdapter.selectedOrderId {
    public val presenter = RequestedPresenter()
    private lateinit var acceptedAdapter: AcceptedAdapter
    private var orderList = ArrayList<OrderListing>()
    private lateinit var layoutManager: LinearLayoutManager
    private var currentListSize = 0
    private var selectedPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requested, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        orderList.clear()
        getOrderListingApi()
        setRecyclerAdapter()
        swipeRefreshListener()

    }

    private fun setRecyclerAdapter() {
        layoutManager = LinearLayoutManager(activity)
        rvRecycler.layoutManager = layoutManager
        acceptedAdapter = AcceptedAdapter(rvRecycler.context, this, orderList, this)
        rvRecycler.adapter = acceptedAdapter

        rvRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var visibleItemCount = layoutManager.childCount
                var totalItemCount = layoutManager.itemCount
                var firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_LIMIT
                ) {
                    progress_bar.visibility = View.VISIBLE
                    if (CheckNetworkConnection.isOnline(activity)) {
                        presenter.orderListingResponse(
                            getAuthAccessToken(activity),
                            "ACCEPTED",
                            PAGE_LIMIT.toString(),
                            orderList.size.toString()
                        )
                    }
                }
            }
        })
    }

    private fun getOrderListingApi() {
        if (CheckNetworkConnection.isOnline(activity)) {
            presenter.orderListingResponse(
                getAuthAccessToken(activity),
                "ACCEPTED",
                PAGE_LIMIT.toString(),
                orderList.size.toString()
            )
        }
    }

    private fun swipeRefreshListener() {
        swipeRefresh.setColorSchemeResources(
            R.color.here2dare_green,
            R.color.here2dare_green,
            R.color.here2dare_green
        )
        swipeRefresh.setOnRefreshListener {
            orderList.clear()
            acceptedAdapter.notifyDataSetChanged()
            getOrderListingApi()
        }
    }

    override fun republishOrderSuccess() {
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        swipeRefresh.isRefreshing = false
        progress_bar.visibility = View.GONE
        activity?.rvRecycler?.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        swipeRefresh.isRefreshing = false
        progress_bar.visibility = View.GONE
        if (code == 401) {
            val dialog = Dialog(requireActivity(),R.style.DialogStyle)
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
                SharedPrefs.with(activity).remove(ACCESS_TOKEN)
                activity?.finishAffinity()
                startActivity(Intent(activity, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            activity?.rvRecycler?.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {
        progress_bar.visibility = View.GONE
    }

    override fun apiOrderListing(data: List<OrderListing>) {
        orderList.clear()
        progress_bar.visibility = View.GONE
        swipeRefresh.isRefreshing = false
        currentListSize = data.size + 1

        if (currentListSize == 1 && orderList.size == 0) {
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
        }
        orderList.addAll(data)
        acceptedAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ACCEPT_LIST_DETAIL -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    /* if (data.getBooleanExtra(ORDER_CANCEL, false)) {
                         orderList.removeAt(acceptedAdapter.getSelectedPosition())
                     } else {
                         val orderListingDetail = Gson().fromJson(data.getStringExtra(ACCEPT_DETAIL), OrderListing::class.java)
                         orderList[orderList.indexOf(orderList.find { orderListing -> orderListing._id == orderListingDetail?._id })] = orderListingDetail
                         acceptedAdapter.notifyDataSetChanged()
                     }*/
                    /* orderList.clear()
                     if (CheckNetworkConnection.isOnline(activity)) {
                         presenter.orderListing(getAuthAccessToken(activity), "ACCEPTED", PAGE_LIMIT.toString(), "0")
                     }*/
                    val orderListingDetail = Gson().fromJson(
                        data.getStringExtra(ACCEPT_DETAIL),
                        OrderListing::class.java
                    )
                    if (orderListingDetail?.receiverInfo != null) {
                        val orderListing = orderListingDetail
                        orderListing.receiverInfo = orderListingDetail.receiverInfo
                        orderList.set(data.getIntExtra(POSITION, 0), orderListing)
                        acceptedAdapter.notifyDataSetChanged()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {

                }

            }
        }
    }

    override fun selectedOrderId(orderId: String?, position: Int) {
        selectedPosition = position
        if (CheckNetworkConnection.isOnline(activity)) {
            var jason = JsonObject()
            jason.addProperty("orderId", orderId)
            presenter.cancelApi(getAuthAccessToken(context), jason)
        }
    }

    override fun cancelSucess() {
        orderList.removeAt(selectedPosition)
        acceptedAdapter.notifyDataSetChanged()
        if (orderList.size == 0) {
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
        }
        Toast.makeText(
            activity,
            getString(R.string.Successfully_cancelled_order),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun deleteSuccess() {

    }

    override fun apiOrderListingResponse(data: List<OrderListing>) {
        swipeRefresh.isRefreshing = false
        progress_bar.visibility = View.GONE
        currentListSize = data.size + 1

        if (currentListSize == 1 && orderList.size == 0) {
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
        }
        orderList.addAll(data)
        acceptedAdapter.notifyDataSetChanged()
    }
}