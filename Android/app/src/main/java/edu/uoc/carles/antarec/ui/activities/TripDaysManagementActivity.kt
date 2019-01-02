package edu.uoc.carles.antarec.ui.activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.controller.common.Utils.PATH_DAY_PICTURES
import edu.uoc.carles.antarec.controller.common.Utils.PATH_DAY_RESTAURANTS
import edu.uoc.carles.antarec.controller.common.Utils.PATH_TRIPS
import edu.uoc.carles.antarec.controller.common.Utils.PATH_TRIP_DAYS
import edu.uoc.carles.antarec.controller.day.DayDetailsFragment
import edu.uoc.carles.antarec.data.Day
import edu.uoc.carles.antarec.data.TripHeader
import kotlinx.android.synthetic.main.activity_day.*

class TripDaysManagementActivity : AppCompatActivity() {

    private lateinit var dbDaysReference: DatabaseReference
    private lateinit var dbHeaderReference: DatabaseReference
    private lateinit var header: TripHeader
    private lateinit var dayDetailsFragment: DayDetailsFragment
    lateinit var daysList: MutableList<Day>
    private var currentDaysList: MutableList<Day> = mutableListOf()
    var dayIndex: Int = 0
    var tripDays: Int = 0
    private var canAddNewDays = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)

        header = intent.getParcelableExtra(Utils.TRIP)

        dbHeaderReference = FirebaseDatabase.getInstance().getReference(PATH_TRIPS).child(header.id!!)
        dbDaysReference = FirebaseDatabase.getInstance().getReference(PATH_TRIP_DAYS).child(header.id!!)

        daysList = mutableListOf()

        fetchFirebaseData()

        daysListListener()

        btn_next_day.setOnClickListener {
            navigateToNextDay()
        }

        btn_previous_day.setOnClickListener {
            navigateToPreviousDay()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_day, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu!!.getItem(1).isEnabled = canAddNewDays
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                saveDay()
            }
            R.id.action_add_day -> {
                addNewDay()
                canAddNewDays = false
                buttonStatus()
            }
            R.id.action_attach_picture -> {
                attachPicture()
            }
            R.id.action_delete -> {
                deleteDay()
            }
            R.id.action_attach_restaurant -> {
                attachRestaurant()
            }
            R.id.action_trip_header -> {
                finish()
            }
            R.id.action_set_place -> {
                setPlace()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fetchFirebaseData() {
        dbDaysReference.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list: MutableList<Day> = mutableListOf()
                    if (!snapshot.exists()) {
//                        buttonEnable(add_new_day as Button, false)
                        canAddNewDays = false
                        addNewDay(true)
                    } else {
                        for (it in snapshot.children) {
                            list.add(it.getValue(Day::class.java)!!)
                        }
                    }
                    if (list.isNotEmpty()) {
                        tripDays = list.size
                        val dbTrip =
                            FirebaseDatabase.getInstance().getReference(PATH_TRIPS).child(header.id!!).child("days")
                        header.days = tripDays
                        dbTrip.setValue(header.days)

                        buttonStatus()
                        //display first day
                        displayDay(list[dayIndex])
                        currentDaysList.addAll(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TripDay", error.toException())
                }
            }
        )

    }

    private fun daysListListener() {
        dbDaysReference.addChildEventListener(
            object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val dayToAdd = snapshot.getValue(Day::class.java) as Day
                    //val day = daysList[dayIndex]
                    if (!daysList.contains(dayToAdd)) {
                        daysList.add(dayToAdd)
                    }
                    tripDays = daysList.size
                    buttonStatus()
                }

                override fun onChildRemoved(dayRemoved: DataSnapshot) {
                    val dayToRemove = dayRemoved.getValue(Day::class.java) as Day
                    if (dayIndex < daysList.size){
                        val day = daysList[dayIndex]
                        if (day.id == dayToRemove.id) {
                            daysList.removeAt(dayIndex)
                        }
                        deleteExtraData(dayToRemove)
                        currentDaysList = daysList
                        tripDays = daysList.size
                        buttonStatus()
                    }
                }

            }
        )
    }

    private fun buttonEnable(button: Button, enable: Boolean) {
        button.isEnabled = enable
    }

    private fun displayDay(day: Day) {
        dayDetailsFragment = DayDetailsFragment.getInstance(day)
        supportFragmentManager.beginTransaction().add(R.id.day_details_container, dayDetailsFragment).commit()
    }

    private fun navigateToNextDay() {
        dayIndex += 1
        if (dayIndex == daysList.size) {
            replaceFragmentDay(currentDaysList[dayIndex])
        } else {
            if (dayIndex < daysList.size) {
                replaceFragmentDay(daysList[dayIndex])
            }
        }
        buttonStatus()
    }

    private fun navigateToPreviousDay() {
        dayIndex -= 1
        if (dayIndex < daysList.size) {
            replaceFragmentDay(daysList[dayIndex])
            buttonStatus()
        }
    }

    private fun addNewDay(firstDay: Boolean = false) {
        if (firstDay) {
            //new first day
            val day = Day(null, header.id!!, "", "", 0.0, 0.0, header.fromDate, null)
            currentDaysList.add(day)
            tripDays = currentDaysList.size
            displayDay(day)
            buttonStatus()
        } else {
            if (dayIsSaved()) {
                updateDay()
                dayIndex = tripDays
                //new empty day
                val day = Day(null, header.id!!, "", "", 0.0, 0.0, null, null)
                replaceFragmentDay(day)
                currentDaysList.add(day)
                tripDays = currentDaysList.size
            }
        }
    }

    private fun replaceFragmentDay(day: Day) {
        dayDetailsFragment = DayDetailsFragment.getInstance(day)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.day_details_container, dayDetailsFragment)
        fragmentTransaction.commit()
    }

    private fun saveDay() {
        val day = dayDetailsFragment.day
        var dayId = day.id
        if (dayId.isNullOrBlank()) {
            dayId = dbDaysReference.push().key
            dayDetailsFragment.setDayId(dayId.toString())
        }
        dbDaysReference.child(dayId!!).setValue(day).addOnSuccessListener {
            dbHeaderReference.child("days").setValue(tripDays)
            Toast.makeText(this, R.string.day_saved, Toast.LENGTH_SHORT).show()
            canAddNewDays = true
        }
    }

    private fun updateDay() {
        val day = dayDetailsFragment.day
        val dayId = day.id
        dbDaysReference.child(dayId!!).setValue(day).addOnSuccessListener {
            dbHeaderReference.child("days").setValue(tripDays)
        }
        daysList[dayIndex] = day
    }

    private fun deleteDay() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.delete_title)
        dialog.setMessage(R.string.delete_warning_day)
        dialog.setPositiveButton(R.string.yes) { _, _ ->
            if (dayDetailsFragment.day.id == null) {
                Toast.makeText(this, R.string.must_save_day, Toast.LENGTH_SHORT).show()
            } else {
                dbDaysReference.child(dayDetailsFragment.day.id!!).removeValue().addOnSuccessListener {

                    dbHeaderReference.child("days").setValue(tripDays)
                    if (dayIndex == 0 && daysList.isEmpty()) {
                        finish()
                    } else {
                        if (dayIndex >= daysList.size) {
                            dayIndex = daysList.size - 1
                        }
                        replaceFragmentDay(daysList[dayIndex])
                        buttonStatus()
                    }
                }
            }
        }
        dialog.setNegativeButton(R.string.no) { _, _ -> }
        dialog.show()
    }

    private fun deleteExtraData(day: Day) {
        //Delete pictures pointer
        val dbPictures = FirebaseDatabase.getInstance().getReference(PATH_DAY_PICTURES).child(day.id!!)
        dbPictures.setValue(null)

        //Delete files
        val storage = FirebaseStorage.getInstance().getReference(Utils.PATH_DAY_PICTURES)
            .child(day.id!!)
        storage.delete()

        //Delete Restaurants
        val dbRestaurants = FirebaseDatabase.getInstance().getReference(PATH_DAY_RESTAURANTS).child(day.id!!)
        dbRestaurants.setValue(null)
    }


    private fun dayIsSaved(): Boolean {
        if (dayDetailsFragment.day.id == null) {
            Toast.makeText(this, R.string.must_save_day, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun attachPicture() {
        if (dayIsSaved()) {
            dayDetailsFragment.showPictureDialog()
        }
    }

    private fun attachRestaurant() {
        if (dayIsSaved()) {
            dayDetailsFragment.displayMapActivity()
        }
    }

    private fun setPlace() {
        dayDetailsFragment.placeSearcher()
    }

    private fun buttonStatus() {
        //This method enables o disables the buttons to navigate through days.
        when {
            tripDays <= 1 -> {
                buttonEnable(btn_next_day as Button, false)
                buttonEnable(btn_previous_day as Button, false)
            }

            tripDays > 1 -> {
                when (dayIndex) {
                    0 -> {
                        buttonEnable(btn_next_day as Button, true)
                        buttonEnable(btn_previous_day as Button, false)
                    }

                    currentDaysList.size - 1 -> {
                        buttonEnable(btn_next_day as Button, false)
                        buttonEnable(btn_previous_day as Button, true)
                    }

                    else -> {
                        buttonEnable(btn_next_day as Button, true)
                        buttonEnable(btn_previous_day as Button, true)
                    }

                }
            }
        }
    }

}