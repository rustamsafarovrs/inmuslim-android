package tj.rsdevteam.inmuslim.feature.tasbih.data.repositories

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihRecordWithNameEntity
import tj.rsdevteam.inmuslim.res.R
import java.util.Calendar
import java.util.Date

class TasbihMappersTest {

    /** 28 April 2026, so "2026-04-28" is today and "2026-04-27" is yesterday. */
    private val now: Date = Calendar.getInstance().apply { set(2026, 3, 28, 12, 0, 0) }.time

    private fun record(tasbihId: Long, name: String, count: Int, date: String) =
        TasbihRecordWithNameEntity(tasbihId = tasbihId, tasbihName = name, count = count, date = date)

    @Test
    fun `groups records by date, newest day first`() {
        val days = listOf(
            record(1, "SubhanAllah", 33, "2026-04-25"),
            record(1, "SubhanAllah", 99, "2026-04-27"),
            record(2, "Alhamdulillah", 10, "2026-04-26"),
        ).toDayHistory(now)

        assertEquals(listOf("2026-04-27", "2026-04-26", "2026-04-25"), days.map { it.date })
    }

    @Test
    fun `sums the day total across tasbihs`() {
        val days = listOf(
            record(1, "SubhanAllah", 99, "2026-04-27"),
            record(2, "Alhamdulillah", 33, "2026-04-27"),
        ).toDayHistory(now)

        assertEquals(1, days.size)
        assertEquals(132, days.first().total)
        assertEquals(2, days.first().entries.size)
    }

    @Test
    fun `sorts entries of a day by count descending`() {
        val entries = listOf(
            record(1, "SubhanAllah", 10, "2026-04-27"),
            record(2, "Alhamdulillah", 90, "2026-04-27"),
            record(3, "Allahu Akbar", 50, "2026-04-27"),
        ).toDayHistory(now).first().entries

        assertEquals(listOf("Alhamdulillah", "Allahu Akbar", "SubhanAllah"), entries.map { it.tasbihName })
    }

    @Test
    fun `labels the day and every entry under it`() {
        val day = listOf(
            record(1, "SubhanAllah", 99, "2026-04-28"),
            record(2, "Alhamdulillah", 33, "2026-04-28"),
        ).toDayHistory(now).first()

        assertEquals(TextRes.Res(R.string.common_other_today), day.dateLabel)
        assertEquals(listOf(day.dateLabel, day.dateLabel), day.entries.map { it.dateLabel })
    }

    @Test
    fun `labels each day only once and shares it with its entries`() {
        val day = listOf(
            record(1, "SubhanAllah", 99, "2026-04-28"),
            record(2, "Alhamdulillah", 33, "2026-04-28"),
        ).toDayHistory(now).first()

        day.entries.forEach { assertSame(day.dateLabel, it.dateLabel) }
    }

    @Test
    fun `labels yesterday relative to now`() {
        val day = listOf(record(1, "SubhanAllah", 99, "2026-04-27")).toDayHistory(now).first()

        assertEquals(TextRes.Res(R.string.common_other_yesterday), day.dateLabel)
    }

    @Test
    fun `labels an older day with its absolute date`() {
        val day = listOf(record(1, "SubhanAllah", 99, "2026-04-25")).toDayHistory(now).first()

        assertTrue(day.dateLabel is TextRes.Raw)
    }

    @Test
    fun `keeps the tasbih id so a day entry can open its own history`() {
        val entry = listOf(record(7, "Astaghfirullah", 5, "2026-04-27")).toDayHistory(now)
            .first()
            .entries
            .first()

        assertEquals(7L, entry.tasbihId)
        assertEquals("2026-04-27", entry.date)
    }

    @Test
    fun `returns no days for no records`() {
        assertTrue(emptyList<TasbihRecordWithNameEntity>().toDayHistory(now).isEmpty())
    }
}
