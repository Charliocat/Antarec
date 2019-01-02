package edu.uoc.carles.antarec.data

import android.os.Parcel
import android.os.Parcelable

class Restaurant : Parcelable {

    var id: String? = null
    var name: String? = null
    var address: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble())

    constructor(id: String, name: String, address: String, latitude: Double, longitude: Double) {
        this.id = id
        this.name = name
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }

}