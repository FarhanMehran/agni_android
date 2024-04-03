package com.capcorp.ui.user.homescreen.home.transport.addpictures

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capcorp.R
import com.capcorp.webservice.models.ItemImageModel
import kotlinx.android.synthetic.main.item_add_picture.view.*

class AddPictureAdapter(
    private val adapterCallback: AdapterCallback,
    private val deletedImagePos: DeletedImagePosition,
    private val mContext: Context,
    private val mImageList: MutableList<ItemImageModel>,
    private val listLimit: Int
) : RecyclerView.Adapter<AddPictureAdapter.ViewHolder>() {
    private val layoutInflater: LayoutInflater
    private var mItemCount: Int = 0

    init {
        mItemCount = mImageList.size
        layoutInflater = LayoutInflater.from(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_add_picture, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mImageList[position])
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }


    interface AdapterCallback {
        fun onAddImageClicked()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener {

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener {
                if (adapterPosition == 0) {
                    adapterCallback.onAddImageClicked()
                }
            }
        }


        fun bind(profilePicPojo: ItemImageModel) = with(itemView) {
            if (adapterPosition != 0) {
                Glide.with(context).load(profilePicPojo.localUri)
                    .into(itemView.ivUserImage)
                ivRemoveImage.visibility = View.GONE
                ivAddImageIcon.visibility = View.INVISIBLE
            } else {
                if (mImageList.size >= listLimit + 1) {
                    itemView.visibility = View.GONE
                } else {
                    itemView.visibility = View.VISIBLE
                    Glide.with(mContext).clear(itemView.ivUserImage)
                    //GlideApp.with(context).clear(itemView.ivUserImage);
                    itemView.ivRemoveImage.visibility = View.GONE
                    itemView.ivAddImageIcon.visibility = View.VISIBLE
                }
            }
        }

        private fun confirmRemoveImage(
            adapterPosition: Int,
            ivUserImage: ImageView,
            ivRemoveImage: ImageView,
            ivAddImageIcon: ImageView
        ) {
            AlertDialog.Builder(mContext, R.style.AlertDialogWhiteBGTheme)
                .setPositiveButton(R.string.dialog_remove) { dialog, which ->
                    /*mImageList.removeAt(adapterPosition)
                    mItemCount = mImageList.size*/
                    try {
                        /* notifyItemRemoved(adapterPosition)
                         notifyItemRangeChanged(adapterPosition, itemCount)*/
                        deletedImagePos.deletedImagePos(adapterPosition)
                        /*if (listLimit == Int.MAX_VALUE - 1) {
                            if (adapterPosition == 1 || adapterPosition == 2) {
                                // Glide.clear(ivUserImage);
                                ivRemoveImage.visibility = View.GONE
                                ivAddImageIcon.visibility = View.VISIBLE
                            }
                        }*/

                    } catch (e: ArrayIndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton(R.string.dialog_cancel, null)
                .setCancelable(true)
                .setMessage(R.string.dialog_confirm_remove)
                .show()
        }

        override fun onLongClick(p0: View?): Boolean {
            confirmRemoveImage(
                adapterPosition,
                itemView.ivUserImage,
                itemView.ivRemoveImage,
                itemView.ivAddImageIcon
            )
            return true
        }

    }

    interface DeletedImagePosition {
        fun deletedImagePos(pos: Int)
    }
}