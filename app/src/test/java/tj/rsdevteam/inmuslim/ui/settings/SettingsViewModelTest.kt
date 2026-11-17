package tj.rsdevteam.inmuslim.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker

class SettingsViewModelTest {

    private val analytics = FakeAnalyticsTracker()
    private val viewModel = SettingsViewModel(analytics)

    @Test
    fun `reports which setting was opened`() {
        viewModel.handleEvent(SettingsUIEvent.DidClickRegion)
        viewModel.handleEvent(SettingsUIEvent.DidClickLanguage)

        assertEquals(
            listOf(AnalyticsEvent.SettingsRegionClicked, AnalyticsEvent.SettingsLanguageClicked),
            analytics.events,
        )
    }
}
