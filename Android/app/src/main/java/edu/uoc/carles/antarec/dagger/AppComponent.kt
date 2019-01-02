package edu.uoc.carles.antarec.dagger

import dagger.Component
import edu.uoc.carles.antarec.controller.day.DayDetailsFragment
import edu.uoc.carles.antarec.controller.header.HeaderDetailsFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [DetailsPresenterModule::class])
interface AppComponent {
    fun inject(dayDetails: DayDetailsFragment)
    fun inject(headerDetails: HeaderDetailsFragment)
}