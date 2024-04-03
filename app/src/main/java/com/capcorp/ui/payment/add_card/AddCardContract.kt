package com.capcorp.ui.payment.add_card

import com.capcorp.ui.payment.model.CardData
import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView

class AddCardContract {
    interface View : BaseView {
        fun onCardListSuccess(list: List<CardData>?)
        fun onAddCarcSuccess()
        fun onDeleteCardSuccess()

    }

    interface Presenter : BasePresenter<View> {
        fun onCardList(accessToken: String)
        fun addCard(accessToken: String, hashMap: HashMap<String, String>)
        fun deleteCard(accessToken: String, cardId: String)

    }
}