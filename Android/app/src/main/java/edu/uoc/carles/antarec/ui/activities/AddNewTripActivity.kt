package edu.uoc.carles.antarec.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.controller.common.Utils.REQUIRED
import edu.uoc.carles.antarec.controller.common.Utils.TRIP
import edu.uoc.carles.antarec.controller.header.HeaderDetailsFragment
import edu.uoc.carles.antarec.data.TripHeader
import kotlinx.android.synthetic.main.fragment_header_detail.*


class AddNewTripActivity : AppCompatActivity() {
    private val reference: DatabaseReference by lazy { FirebaseDatabase.getInstance().getReference(Utils.PATH_TRIPS) }
    private var headerDetailsFragment = HeaderDetailsFragment()
    private lateinit var tripHeader: TripHeader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_header)

        supportFragmentManager.beginTransaction().add(R.id.header_details_container, headerDetailsFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trip_header, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        when (i) {
            R.id.action_save_header -> saveNewTrip()

            R.id.action_continue_to_days -> onContinueClick()

          //  R.id.action_delete -> deleteTrip()
          //  R.id.action_back -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNewTrip(){
        submitNewTrip()
    }

    private fun onContinueClick(){
        if(submitNewTrip()){
            tripDetailActivity()
        }
    }

    private fun submitNewTrip(): Boolean {
        if (TextUtils.isEmpty(trip_title.text.toString())) {
            trip_title.error = REQUIRED
            return false
        }

        if (TextUtils.isEmpty(from_date.text.toString())) {
            from_date.error = REQUIRED
            return false
        }

        if (Utils.toDateBeforeFromDate(from_date.text.toString(), to_date.text.toString())) {
            to_date.error = REQUIRED
            Toast.makeText(applicationContext,R.string.to_before_from , Toast.LENGTH_SHORT).show()
            return false
        }

        setEditingEnabled(false)
        Toast.makeText(this, R.string.posting, Toast.LENGTH_SHORT).show()

        val tripId = reference.push().key
        tripHeader = TripHeader(
            tripId!!,
            trip_title.text.toString(),
            place.text.toString(),
            from_date!!.text.toString(),
            to_date.text.toString(),
            bookmark.isChecked,
            Utils.getPersonsFromText(people.text),
            travelers.text.toString(),
            1,
            headerDetailsFragment.getLatitude(),
            headerDetailsFragment.getLongitude(),
            System.currentTimeMillis()
        )

        reference.child(tripId).setValue(tripHeader).addOnCompleteListener {
            Toast.makeText(applicationContext, R.string.saved_successful, Toast.LENGTH_SHORT).show()
        }

        setEditingEnabled(true)

        return true
    }

    private fun tripDetailActivity(){
        val intent = Intent(this, TripDaysManagementActivity::class.java)
        intent.putExtra(TRIP, tripHeader)
        startActivity(intent)
    }

    private fun setEditingEnabled(enabled: Boolean) {
        trip_title.isEnabled = enabled
        from_date.isEnabled = enabled
        to_date.isEnabled = enabled
        place.isEnabled = enabled
        people.isEnabled = enabled
    }

}