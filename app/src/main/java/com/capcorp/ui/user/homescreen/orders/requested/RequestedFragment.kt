package com.capcorp.ui.user.homescreen.orders.requested

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
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
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_requested.*

class RequestedFragment : Fragment(), RequestedContract.View, RequestedAdapter.selectedOrderId {

    public val presenter = RequestedPresenter()
    private lateinit var requestedAdapter: RequestedAdapter
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

    override fun deleteSuccess() {
        orderList.clear()
        requestedAdapter.notifyDataSetChanged()
        getOrderListingApi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        orderList.clear()
        getOrderListingApi()
        setRecyclerAdapter()
        swipeRefreshListener()
    }

    private fun swipeRefreshListener() {
        swipeRefresh.setColorSchemeResources(
            R.color.here2dare_green,
            R.color.here2dare_green,
            R.color.here2dare_green
        )
        swipeRefresh.setOnRefreshListener {
            orderList.clear()
            requestedAdapter.notifyDataSetChanged()
            getOrderListingApi()
        }
    }

    private fun getOrderListingApi() {
        if (CheckNetworkConnection.isOnline(activity)) {
            presenter.orderListing(
                getAuthAccessToken(context),
                "REQUESTED",
                PAGE_LIMIT.toString(),
                orderList.size.toString()
            )
        }
    }

    private fun setRecyclerAdapter() {
        layoutManager = LinearLayoutManager(activity)
        rvRecycler.layoutManager = layoutManager
        requestedAdapter =
            RequestedAdapter(rvRecycler.context, this, orderList, this@RequestedFragment)
        rvRecycler.adapter = requestedAdapter

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
                        presenter.orderListing(
                            getAuthAccessToken(context),
                            "REQUESTED",
                            PAGE_LIMIT.toString(),
                            orderList.size.toString()
                        )
                    }
                }
            }
        })
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        swipeRefresh.isRefreshing = false
        progress_bar.visibility = View.GONE
        activity?.rvRecycler?.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        progress_bar.visibility = View.GONE
        swipeRefresh.isRefreshing = false
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

    override fun republishOrderSuccess() {
    }

    override fun apiOrderListing(data: List<OrderListing>) {
        orderList.clear()
        swipeRefresh.isRefreshing = false
        progress_bar.visibility = View.GONE
        currentListSize = data.size + 1

        if (currentListSize == 1 && orderList.size == 0) {
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
        }
        orderList.addAll(data)
        requestedAdapter.notifyDataSetChanged()

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
        requestedAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        //activity?.registerReceiver(broadcastReciever, IntentFilter(REQUEST_CREATED))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTED_RESULT_CODE && data != null) {
            if (resultCode == RESULT_OK) {

                val dialog = Dialog(requireActivity(),R.style.DialogStyle)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.alert_dialog_success)
                val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                tvTitle.text = getString(R.string.success)
                tvDescription.text = getString(R.string.offer_accepted)
                dialogButton.text = getString(R.string.ok)
                dialogButton.setOnClickListener {
                    dialog.dismiss()
                    if (orderList.size != 0) {
                        val position = data.getIntExtra(POSITION, 0)
                        orderList.removeAt(position)
                        requestedAdapter.notifyDataSetChanged()
                    }

                    if (orderList.size == 0) {
                        tvNoData.visibility = View.VISIBLE
                    } else {
                        tvNoData.visibility = View.GONE
                    }
                }
                dialog.show()
            }
            if (resultCode == RESULT_CANCELED) {
                // Toast.makeText(activity, "result cancelled", Toast.LENGTH_SHORT).show()
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
        requestedAdapter.notifyDataSetChanged()
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

}