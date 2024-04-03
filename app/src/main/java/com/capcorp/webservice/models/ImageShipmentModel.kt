package com.capcorp.webservice.models


import android.os.Parcel
import android.os.Parcelable

data class ImageShipmentModel(
    var thumbnail: String? = "",
    var original: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(thumbnail)
        parcel.writeString(original)
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