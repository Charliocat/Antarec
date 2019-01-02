package edu.uoc.carles.antarec.controller.header

import edu.uoc.carles.antarec.data.TripHeader

interface HeaderDetailsView {
    fun showDetails(header: TripHeader)
    fun moveCamera(latitude: Double, longitude: Double)
}
