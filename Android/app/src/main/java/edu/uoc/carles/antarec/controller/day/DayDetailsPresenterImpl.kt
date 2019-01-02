package edu.uoc.carles.antarec.controller.day

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.data.Day
import edu.uoc.carles.antarec.data.DayPicture
import edu.uoc.carles.antarec.data.Restaurant
import javax.inject.Inject

class DayDetailsPresenterImpl @Inject constructor() : DayDetailsPresenter {

    private lateinit var view: DayDetailsView
    override fun setView(view: DayDetailsView) {
        this.view = view
    }

    override fun displayDetails(day: Day) {
        if (isViewAttached()) {
            view.displayDetails(day)
        }
    }

    override fun displayPictures(picturesList : List<DayPicture>) {
        if (isViewAttached()) {
            view.displayPictures(picturesList)
        }
    }

    override fun displayRestaurants(restaurants: List<Restaurant>) {
        if (isViewAttached()) {
            view.displayRestaurants(restaurants)
        }
    }

    override fun displayLocation(day: Day) {
        if (isViewAttached()) {
            view.displayLocation(day)
        }
    }

    private fun isViewAttached(): Boolean {
        return view != null
    }

}