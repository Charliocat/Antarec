package edu.uoc.carles.antarec.dagger

import dagger.Module
import dagger.Provides
import edu.uoc.carles.antarec.controller.day.DayDetailsPresenter
import edu.uoc.carles.antarec.controller.day.DayDetailsPresenterImpl
import edu.uoc.carles.antarec.controller.header.HeaderDetailsPresenter
import edu.uoc.carles.antarec.controller.header.HeaderDetailsPresenterImpl
import javax.inject.Singleton

@Module
class DetailsPresenterModule{
    @Provides
    @Singleton
    fun provideDayDetailsPresenter(dayDetailPresenter: DayDetailsPresenterImpl): DayDetailsPresenter = dayDetailPresenter

    @Provides
    @Singleton
    fun provideHeaderDetailsPresenter(headerDetailPresenter: HeaderDetailsPresenterImpl): HeaderDetailsPresenter = headerDetailPresenter
}