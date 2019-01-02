package edu.uoc.carles.antarec.data

import android.os.Parcel
import android.os.Parcelable
import edu.uoc.carles.antarec.R.id.*

class TripHeader: Parcelable {

    constructor()

    constructor(
        id: String?,
        title: String,
        place: String,
        fromDate: String,
        toDate: String,
        bookmark: Boolean,
        persons: Int,
        travelers: String,
        days: Int,
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ) {
        this.id = id!!
        this.title = title
        this.place = place
        this.fromDate = fromDate
        this.toDate = toDate
        this.bookmark = bookmark
        this.persons = persons
        this.travelers = travelers
        this.days = days
        this.latitude = latitude
        this.longitude = longitude
        this.timestamp = timestamp
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte().toInt() == 1,
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong()
    )

    var id: String? = null
    lateinit var  title: String
    lateinit var place: String
    lateinit var fromDate: String
    lateinit var toDate: String
    var bookmark: Boolean = false
    var persons: Int = 0
    var days: Int = 1
    var travelers: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private var timestamp: Long = 0
    private var isSelected: Boolean = false

    override fun writeToParcel(parcel: Parcel?, i: Int) {
        parcel?.writeString(id)
        parcel?.writeString(title)
        parcel?.writeString(place)
        parcel?.writeString(fromDate)
        parcel?.writeString(toDate)
        parcel?.writeByte(if(bookmark) 1 else 0 .toByte())
        parcel?.writeInt(persons)
        parcel?.writeString(travelers)
        parcel?.writeInt(days)
        parcel?.writeDouble(latitude)
        parcel?.writeDouble(longitude)
        parcel?.writeLong(timestamp)

    }

    override fun describeContents(): Int = 0


    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(value: Boolean) {
        isSelected = value
    }

    companion object CREATOR : Parcelable.Creator<TripHeader> {
        override fun createFromParcel(parcel: Parcel): TripHeader {
            return TripHeader(parcel)
        }

        override fun newArray(size: Int): Array<TripHeader?> {
            return arrayOfNulls(size)
        }

    }
}




