package com.capcorp.ui.payment.card_list

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.home.makeoffer.MakeAnOfferActivity
import com.capcorp.ui.payment.add_card.AddCardContract
import com.capcorp.ui.payment.add_card.AddCardPresenter
import com.capcorp.ui.payment.add_card.CardActivity
import com.capcorp.ui.payment.model.CardData
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.OrderListing
import com.codebrew.clikat.adapters.CardListAdapter
import com.codebrew.clikat.adapters.CardListAdapterForAccount
import com.codebrew.clikat.adapters.CardListAdapterForDriver
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_card_list.*

class CardListActivity : BaseActivity(), AddCardContract.View, CardListAdapter.DeleteCard,
    CardListAdapterForAccount.DeleteCard, CardListAdapterForDriver.DeleteCard {
    private lateinit var adapter: CardListAdapter
    private lateinit var adapterForDriver: CardListAdapterForDriver
    private lateinit var adapterForAccount: CardListAdapterForAccount
    private val presenter = AddCardPresenter()
    private val cardList = ArrayList<CardData>()
    private lateinit var layoutManager: LinearLayoutManagerWrapper
    private lateinit var dialogIndeterminate: DialogIndeterminate
    var listData: CardData? = null
    var selectedPos = 0
    private var userData: SignupModel? = null
    private var fromWhere: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)
        presenter.attachView(this)

        userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)


        dialogIndeterminate = DialogIndeterminate(this)
        tvAddNewCard.setOnClickListener {
            startActivity(Intent(this, CardActivity::class.java))
        }
        tvBack.setOnClickListener {
            onBackPressed()
        }

        if (intent != null && intent.hasExtra("from")) {
            fromWhere = intent.getStringExtra("from").toString()
        }
        if (fromWhere.equals("account"))
            setRecyclerAdapterForAccount()
        else if (fromWhere.equals("driver"))
            setRecyclerAdapterForDriver()
        else
            setRecyclerAdapter()
        if (userData?.type == "DRIVER" && fromWhere.equals("driver")) {
            tvDashView.visibility = View.VISIBLE
        } else {
            tvDashView.visibility = View.GONE
        }
        val orderListingDetail =
            Gson().fromJson(intent.getStringExtra("order"), OrderListing::class.java)

        btnSelectCard.setOnClickListener {
            if (adapterForDriver.getSelectedItemPosition() != null) {
                listData = adapterForDriver.getSelectedItemPosition()

                listData?.let {
                    startActivity(
                        Intent(this, MakeAnOfferActivity::class.java)
                            .putExtra(ORDER_ID, orderListingDetail._id)
                            .putExtra("itemPrice", orderListingDetail.recommendedReward)
                            .putExtra("date", orderListingDetail?.pickUpDate)
                            .putExtra("cardId", it.cardId)
                            .putExtra(POSITION, 0)
                    )
                }

            } else {
                val dialog = Dialog(this,R.style.DialogStyle)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.alert_dialog_success)
                val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
                val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
                val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
                tvTitle.text = getString(R.string.select_cards)
                tvDescription.text = getString(R.string.please_select_card)
                dialogButton.text = getString(R.string.yes)
                dialogButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getAllCards()
    }

    fun getAllCards() {
        if (CheckNetworkConnection.isOnline(this)) {
            presenter.onCardList(getAuthAccessToken(this))
        }
    }

    override fun deleteCard(pos: Int, cardId: String) {
        selectedPos = pos
        delete(cardId)
    }

    fun delete(cardId: String) {
        if (CheckNetworkConnection.isOnline(this)) {
            presenter.deleteCard(getAuthAccessToken(this), cardId)
        }
    }


    private fun setRecyclerAdapter() {
        layoutManager = LinearLayoutManagerWrapper(this)
        rvCardsCheckout.layoutManager = layoutManager
        adapter = CardListAdapter(this, cardList, this)
        rvCardsCheckout.adapter = adapter
    }

    private fun setRecyclerAdapterForDriver() {
        layoutManager = LinearLayoutManagerWrapper(this)
        rvCardsCheckout.layoutManager = layoutManager
        adapterForDriver = CardListAdapterForDriver(this, cardList, this)
        rvCardsCheckout.adapter = adapterForDriver
    }

    private fun setRecyclerAdapterForAccount() {
        layoutManager = LinearLayoutManagerWrapper(this)
        rvCardsCheckout.layoutManager = layoutManager
        adapterForAccount = CardListAdapterForAccount(this, cardList, this)
        rvCardsCheckout.adapter = adapterForAccount
    }


    override fun onCardListSuccess(list: List<CardData>?) {
        dialogIndeterminate.dismiss()
        cardList.clear()
        cardList.addAll(list as ArrayList)
        if (fromWhere.equals("account"))
            adapterForAccount.notifyDataSetChanged()
        else if (fromWhere.equals("driver"))
            adapterForDriver.notifyDataSetChanged()
        else
            adapter.notifyDataSetChanged()
        if (cardList.size == 0) {
            tvNoCardFound.visibility = View.VISIBLE
        } else {
            tvNoCardFound.visibility = View.GONE
        }
        if (intent.getStringExtra("from").equals("account")) {
            if (cardList.size == 0) {
                btnSelectCard.visibility = View.GONE
            } else {
                btnSelectCard.visibility = View.GONE
            }
        } else {
            if (cardList.size == 0) {
                btnSelectCard.visibility = View.GONE
            } else {
                btnSelectCard.visibility = View.VISIBLE
            }
        }

    }

    fun goBack(view: View) {
        onBackPressed()
    }

    override fun onAddCarcSuccess() {
    }

    override fun onDeleteCardSuccess() {
        cardList.removeAt(selectedPos)
        if (fromWhere.equals("account"))
            adapterForAccount.notifyDataSetChanged()
        else if (fromWhere.equals("driver"))
            adapterForDriver.notifyDataSetChanged()
        else
            adapter.notifyDataSetChanged()

    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        dialogIndeterminate.dismiss()
        llContainer_card.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        dialogIndeterminate.dismiss()
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
            llContainer_card.showSnack(errorBody ?: "")
        }
    }

    override fun validationsFailure(type: String?) {
    }

}
