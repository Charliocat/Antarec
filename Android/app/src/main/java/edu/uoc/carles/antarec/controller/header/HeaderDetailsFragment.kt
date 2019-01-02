package edu.uoc.carles.antarec.controller.header

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.gms.common.api.Status
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
import com.google.firebase.database.FirebaseDatabase
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.dagger.DaggerAppComponent
import edu.uoc.carles.antarec.data.TripHeader
import kotlinx.android.synthetic.main.fragment_header_detail.*
import javax.inject.Inject

class HeaderDetailsFragment: Fragment, HeaderDetailsView, View.OnClickListener, OnMapReadyCallback {
    @Inject
    lateinit var presenter: HeaderDetailsPresenter

    private lateinit var header: TripHeader
    private lateinit var mapsPlace: String
    private lateinit var mMap: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var marker: Marker? = null
    private lateinit var autocompleteFragment: PlaceAutocompleteFragment
    private lateinit var mapFragment: SupportMapFragment

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        DaggerAppComponent.builder().build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_header_detail, container, false)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.setMinZoomPreference(8.0F)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isTiltGesturesEnabled = false

        if (!(latitude == 0.0 && longitude == 0.0)) {
            moveCamera(latitude, longitude)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

        if (arguments != null) {
            val bundle = arguments as Bundle
            this.header = bundle.getParcelable("HEADER")
            presenter.setView(this)
            presenter.showDetails(header)
            appbar.setExpanded(false)
        } else {
            this.header = TripHeader()
        }

        setClickListeners()

        initSearchFragment()
    }

    override fun showDetails(header: TripHeader) {
        trip_title.setText(header.title)
        bookmark.isChecked = header.bookmark
        from_date.text = header.fromDate
        to_date.text = header.toDate
        mapsPlace = header.place
        people.setText(header.persons.toString())
        travelers.setText(header.travelers)
        place.text = header.place
        latitude = header.latitude
        longitude = header.longitude
    }

    override fun onClick(view: View?) {
    }

    override fun moveCamera(latitude: Double, longitude: Double) {
        marker?.remove()
        marker = mMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
        mMap.setMinZoomPreference(5.0F)
        mMap.setMaxZoomPreference(16.0F)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 12F)
            , 2000, null)
    }

    private fun setClickListeners() {
        from_date.setOnClickListener {
            Utils.pickUpDate(context!!, from_date)
        }

        to_date.setOnClickListener {
            Utils.pickUpDate(context!!, to_date)
        }

        bookmark.setOnCheckedChangeListener { _, isChecked ->
            var bookmarkChangedValue = mutableMapOf<String, Any?>()
            bookmarkChangedValue["bookmark"] = isChecked
            if (header.id != null) {
                val dbTrip = FirebaseDatabase.getInstance().getReference(Utils.PATH_TRIPS).child(header.id!!)
                dbTrip.updateChildren(bookmarkChangedValue)
            }
        }
    }

    private fun initSearchFragment() {
        autocompleteFragment =
                activity?.fragmentManager?.findFragmentById(R.id.fragment_autocomplete) as PlaceAutocompleteFragment
        var searchInput = autocompleteFragment.view.findViewById(R.id.place_autocomplete_search_input) as EditText

        searchInput.textSize = 16.0F
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(selectedPlace: Place) {
                mapsPlace = selectedPlace.name.toString()
                place.text = mapsPlace
                latitude = selectedPlace.latLng.latitude
                longitude = selectedPlace.latLng.longitude

                moveCamera(latitude, longitude)
                var clearButton = autocompleteFragment.view.findViewById(R.id.place_autocomplete_clear_button) as View
                clearButton.visibility = View.GONE
            }

            override fun onError(status: Status) {
                Log.w(
                    "TripDetailHeaderActi",
                    "initSearchFragment:setOnPlaceSelectedListener",
                    Throwable(status.statusMessage)
                )
            }
        })
    }


    fun getLatitude(): Double = latitude
    fun getLongitude(): Double = longitude

    companion object {
        fun getInstance(header: TripHeader): HeaderDetailsFragment {
            var args = Bundle()
            args.putParcelable("HEADER", header)
            var headerDetails = HeaderDetailsFragment()
            headerDetails.arguments = args
            return headerDetails
        }
    }
}