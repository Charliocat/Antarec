package edu.uoc.carles.antarec.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.uoc.carles.antarec.R
import org.jetbrains.anko.intentFor

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val background = object: Thread() {

            override fun run() {
                try{
                    Thread.sleep(3000)

                    startActivity(intentFor<MainActivity>())

                } catch (e: Exception){
                    e.printStackTrace()
                }
            }

        }

        background.start()

    }


}