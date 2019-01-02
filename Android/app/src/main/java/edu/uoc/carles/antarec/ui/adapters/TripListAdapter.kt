package edu.uoc.carles.antarec.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.data.TripHeader
import edu.uoc.carles.antarec.ui.viewholder.TripItemViewHolder

class TripListAdapter(private val tripList: MutableList<TripHeader>) : RecyclerView.Adapter<TripItemViewHolder>() {

    override fun onBindViewHolder(holder: TripItemViewHolder, position: Int) {
        holder.bindTripItem(tripList[position])
        holder.selectItem(tripList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_list_item, parent, false)
       return TripItemViewHolder(view)
    }

    override fun getItemCount() = tripList.size

}