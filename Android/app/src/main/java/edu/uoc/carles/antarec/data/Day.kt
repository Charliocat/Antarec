package edu.uoc.carles.antarec.data

import android.os.Parcel
import android.os.Parcelable

class Day : Parcelable {

    var id: String? = null
    lateinit var headerId: String
    var placeName: String = ""
    var address: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var date: String? = null
    var notes: String? = null

    constructor()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    )

    constructor(id: String?, headerId: String, placeName: String, address: String, latitude: Double, longitude: Double, date: String?, notes: String?) {
        this.headerId = headerId
        this.id = id
        this.placeName = placeName
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.date = date
        this.notes = notes
    }

    override fun writeToParcel(parcel: Parcel?, i: Int) {
        parcel?.writeString(id)
        parcel?.writeString(headerId)
        parcel?.writeString(placeName)
        parcel?.writeString(address)
        parcel?.writeDouble(latitude)
        parcel?.writeDouble(longitude)
        parcel?.writeString(date)
        parcel?.writeString(notes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Day> {
        override fun createFromParcel(parcel: Parcel): Day {
            return Day(parcel)
        }

        override fun newArray(size: Int): Array<Day?> {
            return arrayOfNulls(size)
        }

    }
}