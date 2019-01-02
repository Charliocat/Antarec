package edu.uoc.carles.antarec.controller.common

import android.app.DatePickerDialog
import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.widget.TextView
import java.text.DateFormat
import java.util.*

object Utils {

    const val PATH_TRIPS: String = "trips"
    const val PATH_TRIP_DAYS: String = "trip-days"
    const val PATH_DAY_PICTURES: String = "day-pictures"
    const val PATH_DAY_RESTAURANTS: String = "day-restaurants"
    const val DAY: String = "DAY"
    const val TRIP: String = "TRIP"
    const val REQUIRED = "Required"
    const val PICTURE = "PICTURE"
    const val RESTAURANT = "RESTAURANT"

    fun pickUpDate(context: Context, dateText: TextView) {
        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        if (dateText.text.isNotEmpty()) {
            val parts = dateText.text.split("/")
            day = parts[0].toInt()
            month = parts[1].toInt() - 1
            year = parts[2].toInt()
        }

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener{ _ , mYear, mMonth, mDay ->
            month = mMonth + 1
            dateText.text = "$mDay/$month/$mYear"
        }, year, month, day )
        dpd.show()
    }

    // Custom method to format date
    private fun dateStringToDate(date: String?): Date?{
        if(date!!.isEmpty()) return null

        val parts = date.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt() - 1
        val year = parts[2].toInt()

        val c = Calendar.getInstance()
        // Create a Date variable/object with user chosen date
        c.set(year, month, day, 0, 0, 0)
        return c.time
    }

    fun toDateBeforeFromDate(fromDate: String, toDate: String): Boolean{
        val from = dateStringToDate(fromDate)
        val to = dateStringToDate(toDate)
        return to != null && to.before(from)
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun getPersonsFromText(people: Editable): Int =  if (people.isNotEmpty())  people.toString().toInt() else 0

}