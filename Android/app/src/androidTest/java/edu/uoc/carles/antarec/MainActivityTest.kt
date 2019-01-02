package edu.uoc.carles.antarec

import edu.uoc.carles.antarec.helper.AcceptanceTest
import edu.uoc.carles.antarec.ui.activities.AddNewTripActivity
import edu.uoc.carles.antarec.ui.activities.MainActivity
import org.junit.Test

class MainActivityTest : AcceptanceTest<MainActivity>(MainActivity::class.java){

    @Test
    fun createNewTripButton(){
        events.clickOnView(R.id.add_new_trip_button)
        checkThat.nextOpenActivityIs(AddNewTripActivity::class.java)
    }

}