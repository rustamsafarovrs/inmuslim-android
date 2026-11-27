package tj.rsdevteam.inmuslim.ui.home

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.core.testing.MainDispatcherRule
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.data.models.api.GetTimingDTO
import tj.rsdevteam.inmuslim.data.models.api.TimingDTO
import tj.rsdevteam.inmuslim.data.preferences.inMemoryPreferences
import tj.rsdevteam.inmuslim.data.repositories.ErrorHandler
import tj.rsdevteam.inmuslim.data.repositories.TimingRepository
import tj.rsdevteam.inmuslim.data.repositories.UserRepository
import tj.rsdevteam.inmuslim.fakes.FakeApi
import tj.rsdevteam.inmuslim.res.R
import java.net.UnknownHostException
import java.util.Calendar

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

    private var now = millisOf(day = 28, hour = 10)

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
        { now },
    )

    private fun millisOf(day: Int, hour: Int, minute: Int = 0, second: Int = 0): Long {
        return Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.APRIL, day, hour, minute, second)
        }.timeInMillis
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun tickTo(millis: Long) {
        now = millis
        mainDispatcherRule.dispatcher.scheduler.apply {
            advanceTimeBy(1_000)
            runCurrent()
        }
    }

    private fun HomeViewModel.startOn(day: Int) {
        now = millisOf(day = day, hour = 10)
        handleEvent(HomeUIEvent.DidStart)
    }

    @Test
    fun `shows the prayer times and where the day is`() {
        api.timing = timingResponse

        val state = viewModel().state

        assertEquals(timing, state.timing)
        assertEquals(Prayer.ZUHR, state.hero?.prayer)
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

        assertEquals(TextRes.Res(R.string.common_description_no_connection), state.errorBottomSheetConfig?.msg)
        assertNull(state.errorBottomSheetConfig?.title)
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
    fun `loads the new day's times at midnight`() {
        api.timing = timingResponse
        now = millisOf(day = 28, hour = 23, minute = 59, second = 59)
        val viewModel = viewModel()
        viewModel.handleEvent(HomeUIEvent.DidStart)
        assertEquals(1, api.timingRequests.size)

        tickTo(millisOf(day = 29, hour = 0))

        assertEquals(2, api.timingRequests.size)
    }

    @Test
    fun `does not reload when the next prayer starts`() {
        api.timing = timingResponse
        now = millisOf(day = 28, hour = 12, minute = 29, second = 59)
        val viewModel = viewModel()
        viewModel.handleEvent(HomeUIEvent.DidStart)

        tickTo(millisOf(day = 28, hour = 12, minute = 30))

        assertEquals(Prayer.ZUHR, viewModel.state.hero?.prayer)
        assertEquals(true, viewModel.state.hero?.isActive)
        assertEquals(1, api.timingRequests.size)
    }

    @Test
    fun `a failed reload at midnight is not retried every second`() {
        api.timing = timingResponse
        now = millisOf(day = 28, hour = 23, minute = 59, second = 59)
        val viewModel = viewModel()
        viewModel.handleEvent(HomeUIEvent.DidStart)

        api.timing = Result.failure(UnknownHostException())
        tickTo(millisOf(day = 29, hour = 0))
        tickTo(millisOf(day = 29, hour = 0, second = 1))

        assertEquals(2, api.timingRequests.size)
        assertEquals(timing, viewModel.state.timing)
    }

    @Test
    fun `loads the times again when the screen comes back on a later day`() {
        api.timing = timingResponse
        val viewModel = viewModel()
        viewModel.startOn(day = 28)
        viewModel.handleEvent(HomeUIEvent.DidStop)

        viewModel.startOn(day = 28)
        assertEquals(1, api.timingRequests.size)

        viewModel.handleEvent(HomeUIEvent.DidStop)
        viewModel.startOn(day = 30)
        assertEquals(2, api.timingRequests.size)
    }

    @Test
    fun `retries a failed load when the screen comes back`() {
        api.timing = Result.failure(UnknownHostException())
        val viewModel = viewModel()

        api.timing = timingResponse
        viewModel.handleEvent(HomeUIEvent.DidStart)

        assertEquals(timing, viewModel.state.timing)
        assertEquals(2, api.timingRequests.size)
    }

    @Test
    fun `asks for a review from the third day the app is used`() {
        api.timing = timingResponse
        val viewModel = viewModel()

        viewModel.startOn(day = 28)
        viewModel.startOn(day = 28)
        viewModel.startOn(day = 29)
        assertFalse(viewModel.state.shouldRequestReview)

        viewModel.startOn(day = 30)
        assertTrue(viewModel.state.shouldRequestReview)
    }

    @Test
    fun `remembers that the review was requested`() {
        api.timing = timingResponse
        val viewModel = viewModel()
        listOf(27, 28, 29).forEach { viewModel.startOn(day = it) }

        viewModel.handleEvent(HomeUIEvent.ReviewShowed)
        viewModel.startOn(day = 30)

        assertFalse(viewModel.state.shouldRequestReview)
        assertTrue(preferences.isReviewShown())
        assertEquals(listOf(AnalyticsEvent.InAppReviewRequested), analytics.events)
    }
}
