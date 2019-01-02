package edu.uoc.carles.antarec.ui.activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.*
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.R.id.action_delete
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.controller.common.Utils.PATH_TRIPS
import edu.uoc.carles.antarec.controller.common.Utils.PATH_TRIP_DAYS
import edu.uoc.carles.antarec.data.TripHeader
import edu.uoc.carles.antarec.ui.adapters.TripListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.all_trips_list.*
import org.jetbrains.anko.intentFor


class MainActivity : AppCompatActivity() {

    private val reference: DatabaseReference by lazy { FirebaseDatabase.getInstance().getReference(Utils.PATH_TRIPS) }

    private lateinit var tripList: MutableList<TripHeader>
    private lateinit var displayTripList: MutableList<TripHeader>
    private var tripListListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        add_new_trip_button.setOnClickListener {
            startActivity(intentFor<AddNewTripActivity>())
        }
    }

    override fun onStart() {
        super.onStart()
        rv_trips_list.layoutManager = LinearLayoutManager(this)

        fetchDataFromFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayTripList.clear()
                    val search = newText.toLowerCase()
                    tripList.forEach {
                        if (it.title.toLowerCase().contains(search)) {
                            displayTripList.add(it)
                        }
                    }
                    rv_trips_list.adapter.notifyDataSetChanged()
                } else {
                    displayTripList.clear()
                    displayTripList.addAll(tripList)
                    rv_trips_list.adapter.notifyDataSetChanged()
                }

                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        when (i == action_delete) {
            true -> deleteSelectedItems()
            false -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onStop() {
        super.onStop()
        tripListListener ?: reference.removeEventListener(tripListListener!!)
    }

    private fun fetchDataFromFirebase() {
        tripList = mutableListOf()
        displayTripList = mutableListOf()

        tripListListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                displayTripList.clear()
                tripList.clear()
                if (snapshot.exists()) {
                    for (t in snapshot.children) {
                        val trip = t.getValue(TripHeader::class.java)
                        tripList.add(trip!!)
                    }
                    displayTripList.addAll(tripList)
                    rv_trips_list.adapter = TripListAdapter(displayTripList)

                    //alert adapter has changed
                    rv_trips_list.adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "loadItem:onCancelled", error.toException())
            }
        })
    }

    private fun deleteSelectedItems() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.delete)
        dialog.setMessage(R.string.delete_warning)
        dialog.setPositiveButton(R.string.yes) { _, _ ->
            var notEmpty = false
            tripList.forEach {
                if (it.isSelected()) {
                    var db = FirebaseDatabase.getInstance().getReference(PATH_TRIPS + "/${it.id}")
                    db.removeValue()
                    db = FirebaseDatabase.getInstance().getReference(PATH_TRIP_DAYS + "/${it.id}")
                    db.removeValue()
                    notEmpty = true
                }
            }
            if (!notEmpty) {
                Toast.makeText(this, R.string.no_trips_selected, Toast.LENGTH_SHORT).show()
            }
        }

        // Display a negative button_cancel on alert dialog
        dialog.setNegativeButton(R.string.no) { _, _ -> }

        dialog.show()
    }

}
