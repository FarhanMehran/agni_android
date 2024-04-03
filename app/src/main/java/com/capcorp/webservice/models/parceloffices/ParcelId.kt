package com.capcorp.webservice.models.parceloffices

import android.os.Parcel
import android.os.Parcelable
import com.capcorp.webservice.models.ItemImage


data class ParcelId(
    val _id: String,
    val name: String,
    val parcelCompanyPicURL: ItemImage?,
    val companyPicURL: ItemImage?,
    val location: ArrayList<Float>,
    val address: String


) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString().toString(),
        source.readString().toString(),
        source.readParcelable<ItemImage>(ItemImage::class.java.classLoader),
        source.readParcelable<ItemImage>(ItemImage::class.java.classLoader),
        ArrayList<Float>().apply {
            source.readList(
                this as List<*>,
                Float::class.java.classLoader
            )
        },
        source.readString().toString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(_id)
        writeString(name)
        writeParcelable(parcelCompanyPicURL, 0)
        writeParcelable(companyPicURL, 0)
        writeList(location as List<*>?)
        writeString(address)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ParcelId> = object : Parcelable.Creator<ParcelId> {
            override fun createFromParcel(source: Parcel): ParcelId = ParcelId(source)
            override fun newArray(size: Int): Array<ParcelId?> = arrayOfNulls(size)
        }
    }
}