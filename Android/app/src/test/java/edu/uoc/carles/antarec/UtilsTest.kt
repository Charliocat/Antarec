package edu.uoc.carles.antarec

import edu.uoc.carles.antarec.controller.common.Utils
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test


class UtilsTest {

    @Test
    fun toDatebeforteFromDate() {
        val dateFrom = "05/05/2018"
        val dateTo = "01/05/2018"
        assertTrue(Utils.toDateBeforeFromDate(dateFrom, dateTo))
    }

    @Test
    fun fromDateBeforeToDate() {
        val dateTo = "05/05/2018"
        val dateFrom = "01/05/2018"
        assertFalse(Utils.toDateBeforeFromDate(dateFrom, dateTo))
    }

}