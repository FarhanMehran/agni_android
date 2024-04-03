package com.codebrew.clikat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import com.capcorp.ui.payment.model.CardData
import com.capcorp.utils.AlertDialogUtil
import kotlinx.android.synthetic.main.item_card_list_for_account.view.*

class CardListAdapterForDriver(
    private val mContext: Context, private val data: List<CardData>,
    private val deleteCard: CardListAdapterForDriver.DeleteCard
) : RecyclerView.Adapter<CardListAdapterForDriver.ViewHolder>() {


    var isSelectedd = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.item_card_list_for_account, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
        }

        @SuppressLint("SetTextI18n")
        fun bind(cardData: CardData) = with(itemView) {
            if (isSelectedd == adapterPosition) {
                ivTick!!.visibility = View.VISIBLE
            } else {
                ivTick!!.visibility = View.GONE
            }
            itemView.setOnLongClickListener {
                AlertDialogUtil.getInstance().createOkCancelDialog(mContext, R.string.delete_card,
                    R.string.delete_card_msg, R.string.yes,
                    R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                        override fun onOkButtonClicked() {
                            isSelectedd = -1
                            cardData.cardId?.let { it1 ->
                                deleteCard.deleteCard(
                                    adapterPosition,
                                    it1
                                )
                            }
                        }

                        override fun onCancelButtonClicked() {

                        }

                    }).show()
                true
            }

            tvAccountName!!.text = cardData.cardHolderName
            tvCardId!!.text = "**** **** **** " + cardData.last4
            tvExpiryDate!!.text = cardData.expMonth + "/" + cardData.expYear

            if (cardData.brand.equals("Visa")) {
                itemView.ivCardType.setImageResource(R.drawable.ic_visa)
            } else if (cardData.brand.equals("MasterCard")) {
                itemView.ivCardType.setImageResource(R.drawable.ic_mastercard)
            } else if (cardData.brand.equals("Discover")) {
                itemView.ivCardType.setImageResource(R.drawable.ic_discover)
            } else if (cardData.brand.equals("American Express")) {
                itemView.ivCardType.setImageResource(R.drawable.ic_amex)
            } else {
                itemView.ivCardType.setImageResource(R.drawable.ic_card_default)
            }

            itemView.setOnClickListener { vv ->
                isSelectedd = adapterPosition
                notifyDataSetChanged()
            }
        }
    }

    fun getSelectedItemPosition(): CardData? {
        if (isSelectedd >= 0) return data[isSelectedd]
        else return null
    }

    interface DeleteCard {
        fun deleteCard(pos: Int, cardId: String)
    }
}
