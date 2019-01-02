package edu.uoc.carles.antarec.controller.day

import edu.uoc.carles.antarec.data.Day
import edu.uoc.carles.antarec.data.DayPicture
import edu.uoc.carles.antarec.data.Restaurant

interface DayDetailsPresenter {
    fun setView(view: DayDetailsView)
    fun displayPictures(pictures : List<DayPicture>)
    fun displayDetails(day: Day)
    fun displayLocation(day: Day)
    fun displayRestaurants(restaurants: List<Restaurant>)
}