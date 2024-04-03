package com.capcorp.webservice.models.parceloffices

import android.os.Parcel
import android.os.Parcelable


data class Branch(
    val location: ArrayList<Float>,
    val _id: String,
    val name: String,
    val address: String
) : Parcelable {
    constructor(source: Parcel) : this(
        ArrayList<Float>().apply {
            source.readList(
                this as List<*>,
                Float::class.java.classLoader
            )
        },
        source.readString().toString(),
        source.readString().toString(),
        source.readString().toString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeList(location as List<*>?)
        writeString(_id)
        writeString(name)
        writeString(address)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Branch> = object : Parcelable.Creator<Branch> {
            override fun createFromParcel(source: Parcel): Branch = Branch(source)
            override fun newArray(size: Int): Array<Branch?> = arrayOfNulls(size)
        }
    }
}