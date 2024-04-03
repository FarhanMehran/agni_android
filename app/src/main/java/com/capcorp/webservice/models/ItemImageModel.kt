package com.capcorp.webservice.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ItemImageModel(
    var thumbnail: String? = "",
    var original: String? = "",
    var localUri: String? = ""
) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(thumbnail)
        parcel.writeString(original)
        parcel.writeString(localUri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemImage> {
        override fun createFromParcel(parcel: Parcel): ItemImage {
            return ItemImage(parcel)
        }

        override fun newArray(size: Int): Array<ItemImage?> {
            return arrayOfNulls(size)
        }
    }
}