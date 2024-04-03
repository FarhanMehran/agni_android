package com.capcorp.webservice.models

import android.os.Parcel
import android.os.Parcelable

data class ItemImage(
    var thumbnail: String? = "",
    var original: String? = "",
    var localUri: String? = "",
    var transferId: Int? = -1
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(thumbnail)
        parcel.writeString(original)
        parcel.writeString(localUri)
        parcel.writeValue(transferId)
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