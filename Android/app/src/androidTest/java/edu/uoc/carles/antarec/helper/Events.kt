package edu.uoc.carles.antarec.helper

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.withId

class Events {
    fun clickOnView(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(click())
    }

    fun writeText(@IdRes viewId: Int, text : String){
        onView(withId(viewId)).perform(replaceText(text), closeSoftKeyboard())
    }

    fun longClick(@IdRes viewId: Int){
        onView(withId(viewId)).perform(longClick())
    }
}