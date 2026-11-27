package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.core.testing.MainDispatcherRule
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihHistoryEntry
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.ObserveHistoryUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.fakes.FakeTasbihRepository

class TasbihHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val analytics = FakeAnalyticsTracker()
    private val history = MutableStateFlow<Resource<List<TasbihDayHistory>>>(Resource.InProgress())
    private val repository = FakeTasbihRepository().also { it.history = history }

    private fun viewModel() = TasbihHistoryViewModel(analytics, ObserveHistoryUseCase(repository))

    @Test
    fun `reports that the history was opened`() {
        viewModel()

        assertEquals(listOf(AnalyticsEvent.TasbihHistoryOpened), analytics.events)
    }

    @Test
    fun `is loading until the history arrives`() {
        assertTrue(viewModel().state.base.isLoading)
    }

    @Test
    fun `shows the days`() {
        val viewModel = viewModel()
        val label = TextRes.Raw("27 April 2026")
        val days = listOf(
            TasbihDayHistory(
                date = "2026-04-27",
                dateLabel = label,
                total = 33,
                entries = listOf(TasbihHistoryEntry(1, "SubhanAllah", 33, "2026-04-27", label)),
            ),
        )

        history.value = Resource.Success(days)

        assertEquals(days, viewModel.state.days)
        assertFalse(viewModel.state.base.isLoading)
        assertNull(viewModel.state.base.error)
    }

    @Test
    fun `shows an error when loading fails`() {
        val viewModel = viewModel()
        val error = IllegalStateException("boom")

        history.value = Resource.Error(error = error)

        assertEquals(error, viewModel.state.base.error)
        assertFalse(viewModel.state.base.isLoading)
    }
}
