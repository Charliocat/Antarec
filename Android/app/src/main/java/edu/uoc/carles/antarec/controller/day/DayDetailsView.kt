package edu.uoc.carles.antarec.controller.day

import edu.uoc.carles.antarec.data.Day
import edu.uoc.carles.antarec.data.DayPicture
import edu.uoc.carles.antarec.data.Restaurant

interface DayDetailsView {
    fun displayDetails(day: Day)
    fun displayPictures(pictures: List<DayPicture>)
    fun displayRestaurants(restaurants: List<Restaurant>)
    fun displayLocation(day: Day)
}