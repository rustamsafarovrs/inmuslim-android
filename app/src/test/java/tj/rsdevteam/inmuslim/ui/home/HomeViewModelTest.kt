package tj.rsdevteam.inmuslim.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.core.testing.MainDispatcherRule
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.data.models.api.GetTimingDTO
import tj.rsdevteam.inmuslim.data.models.api.TimingDTO
import tj.rsdevteam.inmuslim.data.preferences.inMemoryPreferences
import tj.rsdevteam.inmuslim.data.repositories.ErrorHandler
import tj.rsdevteam.inmuslim.data.repositories.TimingRepository
import tj.rsdevteam.inmuslim.data.repositories.UserRepository
import tj.rsdevteam.inmuslim.fakes.FakeApi
import java.net.UnknownHostException

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeApi()
    private val analytics = FakeAnalyticsTracker()

    // A registered install that already sent its push token, so starting the screen neither registers
    // the device nor asks Firebase for a token, in debug and release builds alike.
    private val preferences = inMemoryPreferences().apply {
        saveUserId(42)
        saveFirebaseToken("token")
    }

    private val timingResponse = Result.success(
        GetTimingDTO(
            result = 0,
            msg = "",
            region = "Dushanbe",
            beginDate = "2026-04-28",
            timing = TimingDTO(
                fajr = "04:00",
                sunrise = "05:30",
                zuhr = "12:30",
                asr = "16:30",
                sunset = "19:20",
                maghrib = "19:30",
                isha = "21:00",
            ),
        ),
    )

    private val timing = Timing(
        fajr = "04:00",
        sunrise = "05:30",
        zuhr = "12:30",
        asr = "16:30",
        sunset = "19:20",
        maghrib = "19:30",
        isha = "21:00",
    )

    private fun viewModel() = HomeViewModel(
        analytics,
        TimingRepository(api, preferences, ErrorHandler()),
        UserRepository(api, preferences, ErrorHandler()),
    )

    @Test
    fun `shows the prayer times and where the day is`() {
        api.timing = timingResponse

        val state = viewModel().state

        assertEquals(timing, state.timing)
        assertNotNull(state.hero)
        assertEquals(Prayer.entries.toSet(), state.statuses.keys)
        assertFalse(state.showLoading)
        assertNull(state.errorBottomSheetConfig)
    }

    @Test
    fun `shows a loader only while the first load is pending`() {
        api.timing = timingResponse
        api.holdResponses()
        val viewModel = viewModel()
        assertTrue(viewModel.state.showLoading)

        api.releaseResponses()
        assertFalse(viewModel.state.showLoading)

        api.holdResponses()
        viewModel.handleEvent(HomeUIEvent.Refresh)
        assertFalse(viewModel.state.showLoading)
        api.releaseResponses()
    }

    @Test
    fun `reports a failed load and shows the error`() {
        api.timing = Result.failure(UnknownHostException())

        val state = viewModel().state

        assertEquals("No internet connection", state.errorBottomSheetConfig?.msg)
        assertEquals(listOf(AnalyticsEvent.TimingLoadFailed("No internet connection")), analytics.events)
        assertFalse(state.showLoading)
    }

    @Test
    fun `dismissing the error hides it`() {
        api.timing = Result.failure(UnknownHostException())
        val viewModel = viewModel()

        viewModel.handleEvent(HomeUIEvent.DismissDialog)

        assertNull(viewModel.state.errorBottomSheetConfig)
    }

    @Test
    fun `refresh loads the times again`() {
        api.timing = Result.failure(UnknownHostException())
        val viewModel = viewModel()

        api.timing = timingResponse
        viewModel.handleEvent(HomeUIEvent.Refresh)

        assertEquals(timing, viewModel.state.timing)
        assertEquals(2, api.timingRequests.size)
    }

    @Test
    fun `starts with the stored review flag`() {
        api.timing = timingResponse
        preferences.saveReviewShown()

        assertTrue(viewModel().state.isReviewShown)
    }

    @Test
    fun `remembers that the review was requested`() {
        api.timing = timingResponse
        val viewModel = viewModel()
        assertFalse(viewModel.state.isReviewShown)

        viewModel.handleEvent(HomeUIEvent.ReviewShowed)

        assertTrue(viewModel.state.isReviewShown)
        assertTrue(preferences.isReviewShown())
        assertEquals(listOf(AnalyticsEvent.InAppReviewRequested), analytics.events)
    }
}
