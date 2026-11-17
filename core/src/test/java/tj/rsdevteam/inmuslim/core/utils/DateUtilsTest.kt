package tj.rsdevteam.inmuslim.core.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtilsTest {

    private val defaultLocale = Locale.getDefault()
    private val defaultTimeZone = TimeZone.getDefault()

    @Before
    fun setUp() {
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Dushanbe"))
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
        TimeZone.setDefault(defaultTimeZone)
    }

    @Test
    fun `formats a date with the requested pattern`() {
        val date = DateUtils.parseDate("2026-04-28", DateUtils.ISO_DATE)

        assertEquals("28 April 2026", DateUtils.formatDateTime(date, DateUtils.HUMAN_DATE))
    }

    @Test
    fun `reformats an ISO date-time string`() {
        assertEquals("28 April 2026 13:05", DateUtils.formatDateTime("2026-04-28T13:05:00"))
        assertEquals(
            "28 April",
            DateUtils.formatDateTime(
                "2026-04-28",
                resultPattern = DateUtils.HUMAN_DAY_AND_MONTH,
                dateTimePattern = DateUtils.ISO_DATE,
            ),
        )
    }

    @Test
    fun `formats a timestamp`() {
        val noonUtc = 1_777_377_600_000L // 2026-04-28 12:00 UTC, 17:00 in Dushanbe

        assertEquals("2026-04-28 17:00:00", DateUtils.formatDateTime(noonUtc, DateUtils.ISO_DATE_TIME))
    }

    @Test
    fun `parses as UTC only when asked to apply the time zone`() {
        val utc = DateUtils.parseDate("2026-04-28T12:00:00", applyTimeZone = true)
        val local = DateUtils.parseDate("2026-04-28T12:00:00")

        assertEquals(1_777_377_600_000L, utc?.time)
        assertEquals(1_777_377_600_000L - 5 * 60 * 60 * 1000, local?.time)
    }

    @Test
    fun `returns null for missing input`() {
        assertNull(DateUtils.formatDateTime(null as Date?))
        assertNull(DateUtils.formatDateTime(null as Long?))
        assertNull(DateUtils.formatDateTime(0L))
        assertNull(DateUtils.formatDateTime(null as String?))
        assertNull(DateUtils.formatDateTime(""))
        assertNull(DateUtils.parseDate(null))
        assertNull(DateUtils.parseDate(""))
    }

    @Test
    fun `formats a duration with only the units it needs`() {
        assertEquals("0 s", DateUtils.formatDuration(0))
        assertEquals("59 s", DateUtils.formatDuration(59_999))
        assertEquals("1 min 1 s", DateUtils.formatDuration(61_000))
        assertEquals("1 h 0 min 0 s", DateUtils.formatDuration(3_600_000))
        assertEquals("2 h 5 min 9 s", DateUtils.formatDuration((2 * 3600 + 5 * 60 + 9) * 1000L))
    }
}
