package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihHistoryEntry

class TasbihHistoryScreenStateTest {

    private val label = TextRes.Raw("")

    private fun day(date: String, vararg counts: Pair<Long, Int>) = TasbihDayHistory(
        date = date,
        dateLabel = label,
        total = counts.sumOf { it.second },
        entries = counts.map { (tasbihId, count) -> TasbihHistoryEntry(tasbihId, "", count, date, label) },
    )

    @Test
    fun `sums every day and counts the distinct tasbihs used`() {
        val state = TasbihHistoryScreenState(
            days = listOf(
                day("2026-04-27", 1L to 33, 2L to 10),
                day("2026-04-26", 1L to 99),
                day("2026-04-25", 3L to 5),
            ),
        )

        assertEquals(147, state.totalCount)
        assertEquals(3, state.activeDays)
        assertEquals(3, state.tasbihCount)
    }

    @Test
    fun `is all zeros without history`() {
        val state = TasbihHistoryScreenState()

        assertEquals(0, state.totalCount)
        assertEquals(0, state.activeDays)
        assertEquals(0, state.tasbihCount)
    }
}
