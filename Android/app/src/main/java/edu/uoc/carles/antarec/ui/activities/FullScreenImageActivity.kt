package edu.uoc.carles.antarec.ui.activities

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import edu.uoc.carles.antarec.R
import edu.uoc.carles.antarec.controller.common.GlideApp
import kotlinx.android.synthetic.main.full_screen_image.*
import java.net.URL

class FullScreenImageActivity: AppCompatActivity() {


    private lateinit var url : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_screen_image)

        url = intent.getStringExtra("PICTURE")

        GlideApp.with(this)
            .load(url)
            .into(fullScreenImageView)
    }

}