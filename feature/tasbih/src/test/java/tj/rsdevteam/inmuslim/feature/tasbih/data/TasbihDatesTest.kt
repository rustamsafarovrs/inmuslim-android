package tj.rsdevteam.inmuslim.feature.tasbih.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TasbihDatesTest {

    private fun dateOf(year: Int, month: Int, day: Int): Date =
        Calendar.getInstance().apply { set(year, month - 1, day, 12, 0, 0) }.time

    @Test
    fun `formats today as an ISO key`() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        assertEquals(today, currentIsoDate())
    }

    @Test
    fun `recognises today and yesterday`() {
        val now = dateOf(2026, 4, 27)

        assertEquals(RelativeDay.TODAY, resolveRelativeDay("2026-04-27", now))
        assertEquals(RelativeDay.YESTERDAY, resolveRelativeDay("2026-04-26", now))
        assertEquals(RelativeDay.OTHER, resolveRelativeDay("2026-04-25", now))
    }

    @Test
    fun `crosses a month boundary when looking back a day`() {
        assertEquals(RelativeDay.YESTERDAY, resolveRelativeDay("2026-04-30", dateOf(2026, 5, 1)))
    }

    @Test
    fun `crosses a year boundary when looking back a day`() {
        assertEquals(RelativeDay.YESTERDAY, resolveRelativeDay("2025-12-31", dateOf(2026, 1, 1)))
    }
}
