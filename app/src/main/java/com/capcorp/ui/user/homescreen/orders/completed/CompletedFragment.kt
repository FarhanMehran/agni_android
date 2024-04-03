package com.capcorp.ui.user.homescreen.orders.completed

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.orders.requested.RequestedContract
import com.capcorp.ui.user.homescreen.orders.requested.RequestedPresenter
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.OrderListing
import kotlinx.android.synthetic.main.fragment_requested.*


class CompletedFragment : Fragment(), RequestedContract.View {
    private val presenter = RequestedPresenter()
    private lateinit var completedAdapter: CompleteAdapter
    private var orderList = ArrayList<OrderListing>()
    private lateinit var layoutManager: LinearLayoutManager
    private var currentListSize = 0

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

    private fun getOrderListingApi() {
        if (CheckNetworkConnection.isOnline(activity)) {
            presenter.orderListing(
                getAuthAccessToken(activity),
                "COMPLETED",
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
            completedAdapter.notifyDataSetChanged()
            getOrderListingApi()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            var isDriverRated = data?.getStringExtra(IS_DRIVERRATED)
            var position = data?.getIntExtra(POSITION, 0) ?: 0
            var updatedValue = orderList[position]
            updatedValue.isDriverRated = isDriverRated.toString()
            orderList.set(position, updatedValue)
            completedAdapter.notifyDataSetChanged()
        }
    }

    private fun setRecyclerAdapter() {
        layoutManager = LinearLayoutManager(activity)
        rvRecycler.layoutManager = layoutManager
        completedAdapter = CompleteAdapter(rvRecycler.context, orderList, this@CompletedFragment)
        rvRecycler.adapter = completedAdapter

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
                    getOrderListingApi()
                }
            }
        })
    }

    override fun apiOrderListingResponse(data: List<OrderListing>) {
    }

    override fun republishOrderSuccess() {
    }

    override fun cancelSucess() {
    }

    override fun deleteSuccess() {
        TODO("Not yet implemented")
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
                SharedPrefs.with(requireActivity()).remove(ACCESS_TOKEN)
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
        progress_bar.visibility = View.GONE
        swipeRefresh.isRefreshing = false
        progress_bar.visibility = View.GONE
        currentListSize = data.size + 1
        if (currentListSize == 1 && orderList.size == 0) {
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
        }
        orderList.addAll(data)
        completedAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

}