package edu.uoc.carles.antarec.controller.header

import edu.uoc.carles.antarec.data.TripHeader
import javax.inject.Inject

class HeaderDetailsPresenterImpl @Inject constructor() : HeaderDetailsPresenter{

    private lateinit var view: HeaderDetailsView

    override fun setView(view: HeaderDetailsView) {
        this.view = view
    }

    override fun showDetails(header: TripHeader) {
        if(isViewAttached()){
            view.showDetails(header)
        }
    }

    private fun isViewAttached(): Boolean {
        return view != null
    }

}