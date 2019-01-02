package edu.uoc.carles.antarec.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.controller.common.Utils.TRIP
import edu.uoc.carles.antarec.controller.header.HeaderDetailsFragment
import edu.uoc.carles.antarec.data.TripHeader
import kotlinx.android.synthetic.main.fragment_header_detail.*


class TripDetailHeaderActivity : AppCompatActivity() {

    private val REQUIRED = "Required"

    private lateinit var tripHeader: TripHeader
    private var headerDetailsFragment = HeaderDetailsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_header)

        tripHeader = intent.getParcelableExtra(TRIP)
        headerDetailsFragment = HeaderDetailsFragment.getInstance(tripHeader)
        supportFragmentManager.beginTransaction().add(R.id.header_details_container, headerDetailsFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trip_header, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        when (i) {
            R.id.action_save_header -> saveTripHeader()

            R.id.action_continue_to_days -> onContinueClick()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveTripHeader() {
        updateTrip()
    }

    private fun onContinueClick() {
        if (updateTrip()) {
            tripDetailActivity()
        }
    }

    private fun updateTrip(): Boolean {
        val updatedTrip = TripHeader(
            tripHeader.id,
            trip_title.text.toString(),
            place.text.toString(),
            from_date.text.toString(),
            to_date.text.toString(),
            bookmark.isChecked,
            Utils.getPersonsFromText(people.text),
            travelers.text.toString(),
            tripHeader.days,
            headerDetailsFragment.getLatitude(), headerDetailsFragment.getLongitude(),
            System.currentTimeMillis()
        )

        //To Date must be after From Date
        if (Utils.toDateBeforeFromDate(from_date.text.toString(), to_date.text.toString())) {
            to_date.error = REQUIRED
            Toast.makeText(applicationContext, R.string.to_before_from, Toast.LENGTH_SHORT).show()
            return false
        }
        val dbTrip = FirebaseDatabase.getInstance().getReference(Utils.PATH_TRIPS).child(tripHeader.id!!)
        dbTrip.setValue(updatedTrip)
        Toast.makeText(applicationContext, R.string.saved_successful, Toast.LENGTH_SHORT).show()
        return true
    }

    private fun tripDetailActivity() {
        val intent = Intent(this, TripDaysManagementActivity::class.java)
        intent.putExtra(TRIP, tripHeader)
        startActivity(intent)
    }

}
