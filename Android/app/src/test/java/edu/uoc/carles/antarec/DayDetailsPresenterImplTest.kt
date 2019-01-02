package edu.uoc.carles.antarec

import edu.uoc.carles.antarec.controller.day.DayDetailsPresenter
import edu.uoc.carles.antarec.controller.day.DayDetailsPresenterImpl
import edu.uoc.carles.antarec.controller.day.DayDetailsView
import edu.uoc.carles.antarec.data.Day
import edu.uoc.carles.antarec.data.DayPicture
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DayDetailsPresenterImplTest {

    @Mock
    private lateinit var view: DayDetailsView

    @Mock
    private lateinit var pictures: MutableList<DayPicture>

    private lateinit var presenter: DayDetailsPresenter

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        presenter = DayDetailsPresenterImpl()
    }

    @After
    fun teardown(){
        //presenter.destroy()
    }

    @Test
    fun shouldBeAbleToShowDetails(){
        val day = Day()
        presenter.setView(view)
        presenter.displayDetails(day)
        verify(view).displayDetails(day)
    }

    @Test(expected = UninitializedPropertyAccessException::class)
    fun shouldNotBeAbleToShowDetails(){
        val day = Day()
        presenter.displayDetails(day)
        verifyZeroInteractions(view)
    }

    @Test
    fun shouldBeAbleToShowPictures(){
        pictures = mutableListOf()
        pictures.add(DayPicture())
        presenter.setView(view)
        presenter.displayPictures(pictures)
        verify(view).displayPictures(pictures)
    }

    @Test(expected = UninitializedPropertyAccessException::class)
    fun shouldNotBeAbleToShowPictures(){
        pictures = mutableListOf()
        pictures.add(DayPicture())
        presenter.displayPictures(pictures)
        verifyZeroInteractions(view)
    }

}