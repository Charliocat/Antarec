package edu.uoc.carles.antarec.controller.header

import edu.uoc.carles.antarec.data.TripHeader

interface HeaderDetailsPresenter {
    fun setView(view: HeaderDetailsView)
    fun showDetails(header: TripHeader)
}
