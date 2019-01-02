package edu.uoc.carles.antarec.ui.viewholder

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import edu.uoc.carles.antarec.data.TripHeader
import edu.uoc.carles.antarec.ui.activities.TripDetailHeaderActivity
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.controller.common.Utils.TRIP
import kotlinx.android.synthetic.main.trip_list_item.view.*

class TripItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var tripId: String

    fun bindTripItem(tripHeaderItem: TripHeader) = with(itemView){
        tripId = tripHeaderItem.id!!
        itemView.title.text = tripHeaderItem.title
        itemView.from_date.text = tripHeaderItem.fromDate
        itemView.to_date.text = tripHeaderItem.toDate
        itemView.bookmarkItem.isChecked = tripHeaderItem.bookmark

        setOnClickListener {
            val intent = Intent(itemView.context, TripDetailHeaderActivity::class.java)
            intent.putExtra(TRIP, tripHeaderItem)
            itemView.context.startActivity(intent)
        }

        var button = itemView.bookmarkItem as Button
        button.setOnClickListener()
        {   tripHeaderItem.bookmark = itemView.bookmarkItem.isChecked
            val dbTrip = FirebaseDatabase.getInstance().getReference(Utils.PATH_TRIPS).child(tripId)
            dbTrip.setValue(tripHeaderItem)
        }
    }

    fun selectItem(tripHeaderItem: TripHeader){
        itemView.setOnLongClickListener{
            if(!tripHeaderItem.isSelected()) {
                tripHeaderItem.setSelected(true)
                itemView.setBackgroundColor(Color.LTGRAY)
            }
            else{
                tripHeaderItem.setSelected(false)
                itemView.background = null
            }

            return@setOnLongClickListener true
        }
    }

}