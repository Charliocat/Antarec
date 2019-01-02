package edu.uoc.carles.antarec.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.controller.common.Utils.RESTAURANT
import edu.uoc.carles.antarec.data.Restaurant

class RestaurantSearcherActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var autocompleteFragment: PlaceAutocompleteFragment
    private var marker: Marker? = null
    private lateinit var restaurant: Restaurant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_restaurant)

        restaurant = Restaurant()
        if (intent.extras != null){
            val bundle: Bundle = intent.extras
            restaurant = bundle.getParcelable(RESTAURANT)
        }

        autocompleteFragment =
                fragmentManager?.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment

        var searchInput = autocompleteFragment.view.findViewById(R.id.place_autocomplete_search_input) as EditText
        searchInput.textSize = 16.0F

        val autocompleteFilter: AutocompleteFilter = AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_RESTAURANT).build()
        autocompleteFragment.setFilter(autocompleteFilter)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(selectedPlace: Place) {
                restaurant.address = selectedPlace.address!!.toString()
                restaurant.name = selectedPlace.name.toString()
                restaurant.id = selectedPlace.id
                restaurant.latitude = selectedPlace.latLng.latitude
                restaurant.longitude = selectedPlace.latLng.longitude
                marker?.remove()
                marker = mMap.addMarker(MarkerOptions().position(LatLng(restaurant.latitude, restaurant.longitude)).title(restaurant.name))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(restaurant.latitude, restaurant.longitude), 15F)
                    , 2000, null)
            }

            override fun onError(status: Status) {
                Log.w(
                    "RestSearcherActivity",
                    "initSearchFragment:setOnPlaceSelectedListener",
                    Throwable(status.statusMessage)
                )
            }
        })

     //    Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map_searcher, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        when (i) {
            R.id.action_save_data -> saveRestaurant()

            R.id.action_cancel -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = false
        
        if(restaurant.latitude != 0.0 && restaurant.longitude != 0.0){
            marker?.remove()
            marker = mMap.addMarker(MarkerOptions().position(LatLng(restaurant.latitude, restaurant.longitude)))
            mMap.setMinZoomPreference(5.0F)
            mMap.setMaxZoomPreference(16.0F)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(restaurant.latitude, restaurant.longitude), 12F)
                , 2000, null)
        }
    }

    private fun saveRestaurant(){
        if (restaurant.id != null){
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putParcelable(RESTAURANT, restaurant)
            resultIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        else {
            val resultIntent = Intent()
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
        finish()
    }
}
