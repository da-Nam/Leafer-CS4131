package com.example.androidproject.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.errorprone.annotations.Keep
import java.io.Serializable

@Keep
data class PlantItem(var name: String? = null, var url: String? = null, var note: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlantItem> {
        override fun createFromParcel(parcel: Parcel): PlantItem {
            return PlantItem(parcel)
        }

        override fun newArray(size: Int): Array<PlantItem?> {
            return arrayOfNulls(size)
        }
    }
}