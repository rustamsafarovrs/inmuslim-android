package tj.rsdevteam.inmuslim.feature.tasbih.ui.calculator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDatabase
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepositoryImpl
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.GetTasbihUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.IncrementTasbihUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.IsHapticEnabledUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.ResetTasbihUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.SetHapticEnabledUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.testing.awaitUntil
import tj.rsdevteam.inmuslim.feature.tasbih.testing.successData

@RunWith(AndroidJUnit4::class)
class TasbihViewModelTest {

    private val analytics = FakeAnalyticsTracker()
    private lateinit var db: TasbihDatabase
    private lateinit var repository: TasbihRepository
    private lateinit var viewModel: TasbihViewModel
    private var tasbihId = 0L

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, TasbihDatabase::class.java).build()
        repository = TasbihRepositoryImpl(db.tasbihDao(), db, Preferences(context))
        repository.setHapticEnabled(false)
        tasbihId = runBlocking { repository.addTasbih("SubhanAllah").successData() }
    }

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) viewModel.viewModelScope.cancel()
        db.close()
    }

    private fun openTasbih(): TasbihViewModel {
        viewModel = TasbihViewModel(
            analytics,
            GetTasbihUseCase(repository),
            IncrementTasbihUseCase(repository),
            ResetTasbihUseCase(repository),
            SetHapticEnabledUseCase(repository),
            IsHapticEnabledUseCase(repository),
            SavedStateHandle(mapOf("tasbihId" to tasbihId)),
        )
        awaitUntil({ viewModel.state }) { it.name.isNotEmpty() }
        return viewModel
    }

    private fun awaitSavedCount(count: Int) {
        awaitUntil({ runBlocking { repository.getTodayCount(tasbihId).successData() } }) { it == count }
    }

    private fun TasbihViewModel.tap(times: Int) = repeat(times) { handleEvent(TasbihUIEvent.DidTap) }

    @Test
    fun loadsTheNameAndTodaysCount() {
        runBlocking {
            repository.increment(tasbihId).last()
            repository.increment(tasbihId).last()
        }

        val state = openTasbih().state

        assertEquals("SubhanAllah", state.name)
        assertEquals(2, state.count)
        assertEquals(AnalyticsEvent.TasbihOpened(tasbihId), analytics.events.first())
    }

    @Test
    fun countsAndSavesEveryTap() {
        val viewModel = openTasbih()

        viewModel.tap(3)

        assertEquals(3, viewModel.state.count)
        awaitSavedCount(3)
    }

    @Test
    fun reportsEveryThirtyThirdTap() {
        val viewModel = openTasbih()

        viewModel.tap(66)

        val milestones = analytics.events.filterIsInstance<AnalyticsEvent.TasbihMilestoneReached>()
        assertEquals(listOf(33, 66), milestones.map { it.count })
        assertTrue(milestones.all { it.tasbihId == tasbihId })
        awaitSavedCount(66)
    }

    @Test
    fun asksForConfirmationBeforeResetting() {
        val viewModel = openTasbih()
        viewModel.tap(5)

        viewModel.handleEvent(TasbihUIEvent.DidClickReset)
        assertTrue(viewModel.state.showResetConfirm)
        assertEquals(5, viewModel.state.count)

        viewModel.handleEvent(TasbihUIEvent.DidDismissResetConfirm)
        assertFalse(viewModel.state.showResetConfirm)
        assertEquals(5, viewModel.state.count)
        awaitSavedCount(5)
    }

    @Test
    fun confirmedResetZeroesAndSavesTheCount() {
        val viewModel = openTasbih()
        viewModel.tap(5)
        awaitSavedCount(5)

        viewModel.handleEvent(TasbihUIEvent.DidClickReset)
        viewModel.handleEvent(TasbihUIEvent.DidConfirmReset)

        assertEquals(0, viewModel.state.count)
        assertFalse(viewModel.state.showResetConfirm)
        assertEquals(AnalyticsEvent.TasbihReset(tasbihId, count = 5), analytics.events.last())
        awaitSavedCount(0)
    }

    @Test
    fun togglingHapticsSavesTheSetting() {
        val viewModel = openTasbih()
        assertFalse(viewModel.state.hapticEnabled)

        viewModel.handleEvent(TasbihUIEvent.DidToggleHaptic)

        assertTrue(viewModel.state.hapticEnabled)
        assertTrue(repository.isHapticEnabled())
        assertEquals(AnalyticsEvent.TasbihHapticToggled(enabled = true), analytics.events.last())
    }
}
