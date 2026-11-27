package tj.rsdevteam.inmuslim.feature.tasbih.ui.entryhistory

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord

class TasbihEntryHistoryScreenStateTest {

    private fun record(count: Int, date: String) =
        TasbihRecord(id = 0, tasbihId = 1, count = count, date = date, dateLabel = TextRes.Raw(date))

    @Test
    fun `sums the days and finds the best one`() {
        val state = TasbihEntryHistoryScreenState(
            records = listOf(record(33, "2026-04-27"), record(99, "2026-04-26"), record(10, "2026-04-25")),
        )

        assertEquals(142, state.totalCount)
        assertEquals(3, state.activeDays)
        assertEquals(99, state.bestCount)
    }

    @Test
    fun `is all zeros without records`() {
        val state = TasbihEntryHistoryScreenState()

        assertEquals(0, state.totalCount)
        assertEquals(0, state.activeDays)
        assertEquals(0, state.bestCount)
    }
}
