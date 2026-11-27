package tj.rsdevteam.inmuslim.ui.launch

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.AnalyticsProperty
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.core.testing.MainDispatcherRule
import tj.rsdevteam.inmuslim.data.preferences.inMemoryPreferences
import tj.rsdevteam.inmuslim.data.repositories.ErrorHandler
import tj.rsdevteam.inmuslim.data.repositories.RegionRepository
import tj.rsdevteam.inmuslim.data.repositories.UserRepository
import tj.rsdevteam.inmuslim.fakes.FakeApi

class LaunchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeApi()
    private val preferences = inMemoryPreferences()
    private val analytics = FakeAnalyticsTracker()

    private fun viewModel() = LaunchViewModel(
        analytics,
        RegionRepository(api, preferences, ErrorHandler()),
        UserRepository(api, preferences, ErrorHandler()),
    )

    @Test
    fun `opens onboarding when no region is chosen yet`() = runTest {
        val event = viewModel().event.first()

        assertEquals(LaunchVMEvent.OpenMain(openOnboarding = true), event)
        assertEquals(listOf(AnalyticsEvent.AppLaunched(openOnboarding = true)), analytics.events)
    }

    @Test
    fun `opens the prayer times when a region is chosen`() = runTest {
        preferences.saveRegionId(3)

        val event = viewModel().event.first()

        assertEquals(LaunchVMEvent.OpenMain(openOnboarding = false), event)
        assertEquals(listOf(AnalyticsEvent.AppLaunched(openOnboarding = false)), analytics.events)
    }

    @Test
    fun `identifies a registered install with its region`() = runTest {
        preferences.saveUserId(42)
        preferences.saveRegionId(3)

        viewModel().event.first()

        assertEquals(listOf("42"), analytics.userIds)
        assertEquals(mapOf(AnalyticsProperty.REGION_ID to "3"), analytics.userProperties)
    }

    @Test
    fun `clears identity on a new install instead of sending the unset sentinel`() = runTest {
        viewModel().event.first()

        assertEquals(listOf<String?>(null), analytics.userIds)
        assertTrue(analytics.userProperties.containsKey(AnalyticsProperty.REGION_ID))
        assertNull(analytics.userProperties[AnalyticsProperty.REGION_ID])
    }
}
