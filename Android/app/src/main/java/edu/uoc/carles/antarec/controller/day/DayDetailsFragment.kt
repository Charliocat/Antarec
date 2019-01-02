package edu.uoc.carles.antarec.controller.day

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import edu.uoc.carles.antarec.BuildConfig
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.controller.common.GlideApp
import edu.uoc.carles.antarec.controller.common.Utils
import edu.uoc.carles.antarec.controller.common.Utils.DAY
import edu.uoc.carles.antarec.controller.common.Utils.PATH_DAY_PICTURES
import edu.uoc.carles.antarec.controller.common.Utils.PATH_DAY_RESTAURANTS
import edu.uoc.carles.antarec.controller.common.Utils.PICTURE
import edu.uoc.carles.antarec.controller.common.Utils.RESTAURANT
import edu.uoc.carles.antarec.dagger.DaggerAppComponent
import edu.uoc.carles.antarec.data.Day
import edu.uoc.carles.antarec.data.DayPicture
import edu.uoc.carles.antarec.data.Restaurant
import edu.uoc.carles.antarec.ui.activities.FullScreenImageActivity
import edu.uoc.carles.antarec.ui.activities.RestaurantSearcherActivity
import kotlinx.android.synthetic.main.fragment_day_detail.*
import kotlinx.android.synthetic.main.location.*
import kotlinx.android.synthetic.main.pictures_of_day.*
import kotlinx.android.synthetic.main.restaurant.*
import kotlinx.android.synthetic.main.restaurant.view.*
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DayDetailsFragment : Fragment, DayDetailsView, View.OnClickListener, View.OnLongClickListener {

    @Inject
    lateinit var presenter: DayDetailsPresenter

    lateinit var day: Day
    private lateinit var imageFilePath: String
    private lateinit var dayPictures: MutableList<DayPicture>
    private lateinit var dayRestaurants: MutableList<Restaurant>
    private lateinit var vPictures: LinearLayout
    private lateinit var vContainerScroll: HorizontalScrollView
    private var selectedRestaurant = SelectedRestaurant()
    private var mProgress: Double = 0.0
    private val gallery = 0
    private val camera = 1
    private val restaurantMap = 2
    private val placeMap = 3

    private class SelectedRestaurant(var id: String? = null, var view: View? = null)

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        DaggerAppComponent.builder().build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_day_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)
        vPictures = view.findViewById(R.id.pictures)
        vContainerScroll = view.findViewById(R.id.pictures_container)
        if (arguments != null) {
            val bundle = arguments as Bundle
            this.day = bundle.getParcelable(DAY)
            displayLocationView(false)
            dayPictures = mutableListOf()
            if (day.id != null) {
                val reference = FirebaseDatabase.getInstance().getReference(PATH_DAY_PICTURES).child(day.id!!)
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.w("DayDetailsFragment", "DayPictures:onCancelled", error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        dayPictures.clear()
                        if (snapshot.exists()) {
                            for (it in snapshot.children) {
                                val pic = it.getValue(DayPicture::class.java)
                                dayPictures.add(pic!!)
                            }
                        }
                        presenter.displayPictures(dayPictures)
                    }

                })

                reference.addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                    override fun onChildAdded(added: DataSnapshot, p1: String?) {}

                    //only implemented the one needed
                    override fun onChildRemoved(deleted: DataSnapshot) {
                        val storage = FirebaseStorage.getInstance().getReference(Utils.PATH_DAY_PICTURES)
                            .child(day.id!!).child(deleted.key.toString())
                        storage.delete()
                    }
                })

                //Fetch Restaurants
                dayRestaurants = mutableListOf()
                val dbRestaurants = FirebaseDatabase.getInstance().getReference(PATH_DAY_RESTAURANTS).child(day.id!!)
                dbRestaurants.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.w("DayDetailsFragment", "Restaurants:onCancelled", error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        dayRestaurants.clear()
                        if (snapshot.exists()) {
                            for (it in snapshot.children) {
                                dayRestaurants.add(it.getValue(Restaurant::class.java)!!)
                            }
                        }
                        presenter.displayRestaurants(dayRestaurants)
                    }
                })
            }

            presenter.displayDetails(day)
            presenter.displayLocation(day)
        }

        day_date.setOnClickListener {
            Utils.pickUpDate(context!!, day_date)
        }

        day_date.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editedText: Editable?) {
                day.date = editedText.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        day_notes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editedText: Editable?) {
                day.notes = editedText.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    override fun displayDetails(day: Day) {
        day_date.text = day.date
        day_notes.setText(day.notes)
    }

    override fun displayLocation(day: Day) {
        if (day.placeName.isNotEmpty()) {
            displayLocationView(true)
        }
    }

    override fun displayPictures(picList: List<DayPicture>) {
        if (picList.isEmpty()) {
            vPictures.visibility = View.GONE
            vContainerScroll.visibility = View.GONE

        } else {
            vPictures.visibility = View.VISIBLE
            vContainerScroll.visibility = View.VISIBLE

            vPictures.removeAllViews()

            val options: RequestOptions = RequestOptions().placeholder(R.color.primary_dark_material_dark)
                .centerCrop()
                .override(150, 150)

            for (picture in picList) {
                val thumbContainer = layoutInflater.inflate(R.layout.picture, pictures, false)
                val thumbView: ImageView = thumbContainer.findViewById(R.id.picture_thumb)
                thumbView.setTag(R.id.glide_tag, picture.uri)
                thumbView.setTag(R.id.path_tag, picture.path)
                thumbView.requestLayout()
                thumbView.setOnClickListener(this)
                thumbView.setOnLongClickListener(this)
                GlideApp.with(this).load(picture.uri).apply(options).into(thumbView)
                vPictures.addView(thumbContainer)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.picture_thumb -> {
                val imageUrl = view.getTag(R.id.glide_tag).toString()
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra(PICTURE, imageUrl)
                startActivity(intent)
            }
        }
    }

    override fun onLongClick(view: View?): Boolean {
        when (view?.id) {
            R.id.picture_thumb -> {
                deletePicture(view)
            }

            R.id.day_location -> {
                deleteLocation()
            }
        }
        return true
    }

    private fun deletePicture(view: View?) {
        var dialog = AlertDialog.Builder(context!!)
        dialog.setTitle(R.string.delete)
        dialog.setMessage(R.string.delete_picture_warning)
        dialog.setPositiveButton(R.string.yes) { _, _ ->
            val imageUrl = view?.getTag(R.id.glide_tag).toString()
            val storage = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            storage.delete().addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference(PATH_DAY_PICTURES).child(day.id!!).child(storage.name)
                    .removeValue()
                Toast.makeText(context!!, R.string.picture_deleted, Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setNegativeButton(R.string.no) { _, _ -> }

        dialog.show()
    }

    private fun deleteLocation() {
        var dialog = AlertDialog.Builder(context!!)
        dialog.setTitle(R.string.delete)
        dialog.setMessage(R.string.delete_location_warning)
        dialog.setPositiveButton(R.string.yes) { _, _ ->
            displayLocationView(false)
            day.placeName = ""
            day.address = ""
            day.latitude = 0.0
            day.longitude = 0.0
        }

        dialog.setNegativeButton(R.string.no) { _, _ -> }

        dialog.show()
    }

    companion object {
        fun getInstance(day: Day): DayDetailsFragment {
            val args = Bundle()
            args.putParcelable(DAY, day)
            val dayDetails = DayDetailsFragment()
            dayDetails.arguments = args
            return dayDetails
        }
    }

    //    fun getDay(): Day = this.day
    fun setDayId(id: String) {
        this.day.id = id
    }

    fun showPictureDialog() {
        val pictureDialog = android.app.AlertDialog.Builder(this.context)
        pictureDialog.setTitle(R.string.media_action)
        val pictureDialogItems = arrayOf("Select photo from gallery", "Select photo from camera")
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, gallery)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context?.packageManager) != null) {
            val photoFile = createImageFile()
            val photoURI =
                FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, camera)
        }
    }

    fun displayMapActivity(restaurant: Restaurant? = null) {
        startActivityForResult(generateRestaurantIntent(restaurant), restaurantMap)
    }

    fun placeSearcher() {
        val builder: PlacePicker.IntentBuilder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(activity), placeMap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                gallery -> {
                    if (data != null) {
                        val contentURI = data.data
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
                            savePicture(this.day, bitmap)

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(activity?.applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                camera -> {
                    val bitmap = BitmapFactory.decodeFile(imageFilePath)
                    galleryAddPic()
                    savePicture(this.day, bitmap)
                }

                restaurantMap -> {
                    if (data!!.hasExtra(RESTAURANT)) {
                        val restaurant = data.extras.getParcelable<Restaurant>(RESTAURANT)
                        if (selectedRestaurant.id != null && selectedRestaurant.id != restaurant.id) {
                            updateRestaurantView(selectedRestaurant, restaurant!!)
                            selectedRestaurant = SelectedRestaurant()//Empty
                        } else {
                            createRestaurant(restaurant!!)
                        }
                    }
                }

                placeMap -> {
                    val placePicker = PlacePicker.getPlace(context, data)
                    day.placeName = placePicker.name.toString()
                    day.address = placePicker.address.toString()
                    day.latitude = placePicker.latLng.latitude
                    day.longitude = placePicker.latLng.longitude
                    displayLocationView()
                }
            }
        }
    }

    private fun displayLocationView(enable: Boolean = true) {
        if (enable) {
            day_location.visibility = View.VISIBLE
            location_name.text = day.placeName
            location_address.text = day.address

            day_location.setOnLongClickListener(this)

        } else {
            day_location.visibility = View.GONE
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(imageFilePath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity?.sendBroadcast(mediaScanIntent)
        }
    }

    private fun savePicture(day: Day, bitmap: Bitmap) {
        val dbDaysReference = FirebaseDatabase.getInstance().getReference(Utils.PATH_DAY_PICTURES).child(day.id!!)
        val imageId = dbDaysReference.push().key
        val base64ImageString = encoder(reduceSize(bitmap))
        val storage =
            FirebaseStorage.getInstance().getReference(Utils.PATH_DAY_PICTURES).child(day.id!!).child(imageId!!)
        storage.putBytes(base64ImageString).addOnSuccessListener { taskSnapshot ->

            taskSnapshot?.storage?.downloadUrl?.addOnSuccessListener { uri ->
                Toast.makeText(activity?.applicationContext, R.string.picture_saved, Toast.LENGTH_SHORT).show()
                val path = taskSnapshot.storage.toString()
                val df = DateFormat.getDateTimeInstance()
                df.timeZone = TimeZone.getTimeZone("GMT+1")

                dbDaysReference.child(imageId)
                    .setValue(DayPicture(uri.toString(), path, df.format(Date()), System.currentTimeMillis()))
            }

        }.addOnFailureListener {
            Toast.makeText(context, R.string.picture_deleted, Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { taskSnapshot ->
            val currentProgress = (taskSnapshot.bytesTransferred.div(taskSnapshot.totalByteCount)) * 100
            if (currentProgress < mProgress + 15) {
                mProgress = currentProgress.toDouble()
                Log.d("DayDetailsFragment", "onProgress: upload is: $mProgress done")
            }
        }
    }

    private fun encoder(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    @Throws(FileNotFoundException::class)
    private fun reduceSize(bitmap: Bitmap): Bitmap {
        val targetW = 480
        val targetH = 360

        val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(ByteArrayInputStream(encoder(bitmap)), null, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        return BitmapFactory.decodeStream(ByteArrayInputStream(encoder(bitmap)), null, bmOptions)

    }

    private fun decoder(base64ImageStr: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(base64ImageStr, 0, base64ImageStr.size)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            imageFilePath = absolutePath
        }
    }

    private fun generateRestaurantIntent(restaurant: Restaurant? = null): Intent {
        if (restaurant == null) {
            return Intent(context, RestaurantSearcherActivity::class.java)
        }

        selectedRestaurant.id = restaurant.id
        val bundle = Bundle()
        bundle.putParcelable(RESTAURANT, restaurant)
        val intent = Intent(context, RestaurantSearcherActivity::class.java)
        intent.putExtras(bundle)
        return intent
    }

    override fun displayRestaurants(restaurants: List<Restaurant>) {
        for (restaurant in restaurants) {
            displayRestaurant(restaurant)
        }
    }

    private fun createRestaurant(restaurant: Restaurant) {
        displayRestaurant(restaurant)
        FirebaseDatabase.getInstance().getReference(PATH_DAY_RESTAURANTS).child(day.id!!).child(restaurant.id!!)
            .setValue(restaurant)
    }

    private fun deleteRestaurant(view: View?, idRestaurant: String) {
        day_detail_info.removeView(view)
        FirebaseDatabase.getInstance().getReference(PATH_DAY_RESTAURANTS).child(day.id!!).child(idRestaurant)
            .removeValue()
    }

    private fun updateRestaurantView(oldRestaurant: SelectedRestaurant, newRestaurant: Restaurant) {
        oldRestaurant.view?.place_name?.text = newRestaurant.name
        oldRestaurant.view?.restaurant_address?.text = newRestaurant.address

        val firebase = FirebaseDatabase.getInstance().getReference(PATH_DAY_RESTAURANTS).child(day.id!!)
        firebase.child(oldRestaurant.id!!).removeValue()
        firebase.child(newRestaurant.id!!).setValue(newRestaurant)
    }

    private fun displayRestaurant(restaurant: Restaurant) {
        val vRestaurant = layoutInflater.inflate(R.layout.restaurant, restaurant_item, false)
        vRestaurant.place_name.text = restaurant.name
        vRestaurant.restaurant_address.text = restaurant.address
        vRestaurant.restaurant_delete.setOnClickListener {
            deleteRestaurant(vRestaurant, restaurant.id!!)
        }
        vRestaurant.logo_restaurant.setOnClickListener {
            selectedRestaurant.view = vRestaurant
            selectedRestaurant.id = restaurant.id

            displayMapActivity(restaurant)
        }
        day_detail_info.addView(vRestaurant)
    }

}