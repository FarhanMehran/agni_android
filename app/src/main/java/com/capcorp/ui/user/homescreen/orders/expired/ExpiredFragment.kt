package com.capcorp.ui.user.homescreen.orders.expired


import android.app.AlertDialog
import android.app.DatePickerDialog
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
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ExpiredFragment : Fragment(), RequestedContract.View, ExpiredAdapter.selectedOrderId {
    public val presenter = RequestedPresenter()
    private lateinit var expiredAdapter: ExpiredAdapter
    private var orderList = ArrayList<OrderListing>()
    private lateinit var layoutManager: LinearLayoutManager
    private var currentListSize = 0
    private var selectedOrderPosition = -1
    private var myCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
                "EXPIRED",
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
            expiredAdapter.notifyDataSetChanged()
            getOrderListingApi()
        }
    }


    private fun setRecyclerAdapter() {
        layoutManager = LinearLayoutManager(activity)
        rvRecycler.layoutManager = layoutManager
        expiredAdapter = ExpiredAdapter(rvRecycler.context, this, orderList, this@ExpiredFragment)
        rvRecycler.adapter = expiredAdapter

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

    override fun showLoader(isLoading: Boolean) {
        progress_bar.visibility = View.GONE
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

    override fun apiOrderListingResponse(data: List<OrderListing>) {
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
        expiredAdapter.notifyDataSetChanged()
    }

    override fun republishOrderSuccess() {
        orderList.removeAt(selectedOrderPosition)
        expiredAdapter.notifyDataSetChanged()
        if (orderList.size == 0) {
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
        }
    }

    override fun selectedOrderId(orderId: String?, position: Int) {
        openDateTimePicker(orderId)
        selectedOrderPosition = position

    }

    private fun openDateTimePicker(orderId: String?) {
        val datePickerDialog =
            activity?.let {
                DatePickerDialog(
                    it, { p0, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        presenter.republishApi(
                            getAuthAccessToken(activity),
                            orderId.toString(),
                            myCalendar.timeInMillis.toString()
                        )
                    }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        datePickerDialog?.datePicker?.minDate = System.currentTimeMillis()
        datePickerDialog?.show()
    }

    override fun cancelSucess() {
    }

    override fun deleteSuccess() {
        orderList.clear()
        expiredAdapter.notifyDataSetChanged()
        getOrderListingApi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }
}
