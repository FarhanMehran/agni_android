package com.capcorp.ui.driver.homescreen.select_trips


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.images.Result
import com.capcorp.webservice.models.select_trips.AllTrips
import com.capcorp.webservice.models.select_trips.Data
import com.capcorp.webservice.models.select_trips.Rewards
import kotlinx.android.synthetic.main.fragment_requested.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SelectTripsFragment : Fragment(), SelectTripsContract.View {

    val presenter = SelectTripsPresenter()
    private lateinit var selectTripsAdapter: SelectTripsAdapter
    private var tripsList = ArrayList<Data>()
    private var upcomingRewards = ArrayList<Rewards>()
    private lateinit var layoutManager: LinearLayoutManager
    private var skip = 100
    private var currentListSize = 0
    private var selectedPosition = -1
    var isClear = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        tripsList.clear()

        setRecyclerAdapter()
        swipeRefreshListener()
    }

    override fun onStart() {
        super.onStart()
        swipeRefreshListener()
    }

    private fun swipeRefreshListener() {
        swipeRefresh.setColorSchemeResources(
            R.color.here2dare_green,
            R.color.here2dare_green,
            R.color.here2dare_green
        )
        swipeRefresh.setOnRefreshListener {
            tripsList.clear()
            selectTripsAdapter.notifyDataSetChanged()
            getTripsListingApi()
        }
    }

    private fun getTripsListingApi() {
        if (CheckNetworkConnection.isOnline(activity)) {
            presenter.tripsListing(
                getAuthAccessToken(context),
                PAGE_LIMIT.toString(),
                tripsList.size.toString()
            )
        }
    }

    private fun setRecyclerAdapter() {
        layoutManager = GridLayoutManager(activity, 2)
        rvRecycler.layoutManager = layoutManager
        selectTripsAdapter =
            SelectTripsAdapter(rvRecycler.context, tripsList,upcomingRewards, this@SelectTripsFragment)
        rvRecycler.adapter = selectTripsAdapter

        rvRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /*if (layoutManager.findLastVisibleItemPosition() == selectTripsAdapter.itemCount - 1) {
                    if (currentListSize >= 8) {
                        progress_bar.visibility = View.VISIBLE
                        getTripsListingApi()
                    }
                }*/
                var visibleItemCount = layoutManager.childCount
                var totalItemCount = layoutManager.itemCount
                var firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_LIMIT
                ) {
                    progress_bar.visibility = View.VISIBLE
                    isClear = false
                    getTripsListingApi()
                }
            }
        })
    }

    override fun showLoader(isLoading: Boolean) {
        progress_bar.visibility = View.VISIBLE
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
            dialog.setContentView(R.layout.alert_dialog_success)
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

    override fun apiTripsListing(data: AllTrips) {
//        tripsList.clear()
//        selectTripsAdapter.notifyDataSetChanged()
        swipeRefresh.isRefreshing = false
        progress_bar.visibility = View.GONE
        data.tripListing?.let {
            currentListSize = it.size + 1

            if (currentListSize == 1 && tripsList.size == 0) {
                tvNoData.visibility = View.VISIBLE
            } else {
                tvNoData.visibility = View.GONE
            }
            tripsList.addAll(it)
        }

        data.upcomingRewards?.let { upcomingRewards.addAll(it) }
        selectTripsAdapter.notifyDataSetChanged()
    }

    override fun responseImages(data: List<Result>, ivImage: ImageView) {
        if (data.isNotEmpty()) {
            val Dice = Random()
            val n: Int = Dice.nextInt(data.size)
            val imageURL = data.get(n).urls?.regular
            Glide.with(this).load(imageURL)
                .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(ivImage)
        }else{
            ivImage.setImageResource(R.drawable.trip_placeholder)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }


    override fun onResume() {
        super.onResume()
        tripsList.clear()
        selectTripsAdapter.notifyDataSetChanged()
        getTripsListingApi()
    }

}
