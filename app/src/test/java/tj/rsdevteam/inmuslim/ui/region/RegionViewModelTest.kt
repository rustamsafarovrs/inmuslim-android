package tj.rsdevteam.inmuslim.ui.region

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.AnalyticsProperty
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.core.testing.MainDispatcherRule
import tj.rsdevteam.inmuslim.data.models.Region
import tj.rsdevteam.inmuslim.data.models.api.GetRegionsDTO
import tj.rsdevteam.inmuslim.data.models.api.RegionDTO
import tj.rsdevteam.inmuslim.data.preferences.inMemoryPreferences
import tj.rsdevteam.inmuslim.data.repositories.ErrorHandler
import tj.rsdevteam.inmuslim.data.repositories.RegionRepository
import tj.rsdevteam.inmuslim.fakes.FakeApi
import java.net.UnknownHostException

class RegionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeApi()
    private val preferences = inMemoryPreferences()
    private val analytics = FakeAnalyticsTracker()

    private val regionsResponse = Result.success(
        GetRegionsDTO(result = 0, msg = "", regions = listOf(RegionDTO(1, "Dushanbe"), RegionDTO(2, "Khujand"))),
    )

    private fun viewModel() = RegionViewModel(analytics, RegionRepository(api, preferences, ErrorHandler()))

    private fun RegionViewModel.region(id: Long): Region = state.list.single { it.id == id }

    @Test
    fun `lists the regions and reports how many loaded`() {
        api.regions = regionsResponse

        val viewModel = viewModel()

        assertEquals(listOf(Region(1, "Dushanbe"), Region(2, "Khujand")), viewModel.state.list)
        assertFalse(viewModel.state.showLoading)
        assertEquals(listOf(AnalyticsEvent.RegionsLoaded(2)), analytics.events)
    }

    @Test
    fun `shows a loader while the regions load`() {
        api.regions = regionsResponse
        api.holdResponses()
        val viewModel = viewModel()
        assertTrue(viewModel.state.showLoading)

        api.releaseResponses()

        assertFalse(viewModel.state.showLoading)
    }

    @Test
    fun `reports a failed load and shows the error`() {
        api.regions = Result.failure(UnknownHostException())

        val viewModel = viewModel()

        assertEquals("No internet connection", viewModel.state.sheetConfig?.msg)
        assertEquals(listOf(AnalyticsEvent.RegionsLoadFailed("No internet connection")), analytics.events)
    }

    @Test
    fun `dismissing the error hides it`() {
        api.regions = Result.failure(UnknownHostException())
        val viewModel = viewModel()
        assertNotNull(viewModel.state.sheetConfig)

        viewModel.handleEvent(RegionUIEvent.DismissDialog)

        assertNull(viewModel.state.sheetConfig)
    }

    @Test
    fun `loads the regions again on retry`() {
        api.regions = Result.failure(UnknownHostException())
        val viewModel = viewModel()

        api.regions = regionsResponse
        viewModel.handleEvent(RegionUIEvent.GetRegions)

        assertEquals(2, viewModel.state.list.size)
    }

    @Test
    fun `selecting a region deselects the others`() {
        api.regions = regionsResponse
        val viewModel = viewModel()

        viewModel.handleEvent(RegionUIEvent.DidSelectRegion(viewModel.region(1)))
        viewModel.handleEvent(RegionUIEvent.DidSelectRegion(viewModel.region(2)))

        assertFalse(viewModel.region(1).selected.value)
        assertTrue(viewModel.region(2).selected.value)
        assertEquals(AnalyticsEvent.RegionSelected(2), analytics.events.last())
    }

    @Test
    fun `confirming saves the selected region and tags analytics with it`() {
        api.regions = regionsResponse
        val viewModel = viewModel()
        viewModel.handleEvent(RegionUIEvent.DidSelectRegion(viewModel.region(2)))

        viewModel.handleEvent(RegionUIEvent.DidClickConfirm)

        assertEquals(2L, preferences.getRegionId())
        assertEquals(AnalyticsEvent.RegionConfirmed(2), analytics.events.last())
        assertEquals("2", analytics.userProperties[AnalyticsProperty.REGION_ID])
    }

    @Test
    fun `confirming without a selection saves nothing`() {
        api.regions = regionsResponse
        val viewModel = viewModel()

        viewModel.handleEvent(RegionUIEvent.DidClickConfirm)

        assertEquals(-1L, preferences.getRegionId())
        assertTrue(analytics.events.none { it is AnalyticsEvent.RegionConfirmed })
        assertTrue(analytics.userProperties.isEmpty())
    }
}
