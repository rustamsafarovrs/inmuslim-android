package tj.rsdevteam.inmuslim.feature.tasbih.ui.entryhistory

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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.FakeAnalyticsTracker
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import tj.rsdevteam.inmuslim.feature.tasbih.data.currentIsoDate
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDatabase
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihRecordEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepositoryImpl
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.GetTasbihNameUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.ObserveEntryHistoryUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.testing.awaitUntil
import tj.rsdevteam.inmuslim.feature.tasbih.testing.successData

@RunWith(AndroidJUnit4::class)
class TasbihEntryHistoryViewModelTest {

    private val analytics = FakeAnalyticsTracker()
    private lateinit var db: TasbihDatabase
    private lateinit var repository: TasbihRepository
    private lateinit var viewModel: TasbihEntryHistoryViewModel
    private var tasbihId = 0L

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, TasbihDatabase::class.java).build()
        repository = TasbihRepositoryImpl(db.tasbihDao(), db, Preferences(context))
        tasbihId = runBlocking { repository.addTasbih("SubhanAllah").successData() }
    }

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) viewModel.viewModelScope.cancel()
        db.close()
    }

    private fun openHistory(): TasbihEntryHistoryViewModel {
        viewModel = TasbihEntryHistoryViewModel(
            analytics,
            ObserveEntryHistoryUseCase(repository),
            GetTasbihNameUseCase(repository),
            SavedStateHandle(mapOf("tasbihId" to tasbihId)),
        )
        awaitUntil({ viewModel.state.value }) { !it.base.isLoading && it.tasbihName.isNotEmpty() }
        return viewModel
    }

    @Test
    fun showsTheDaysTheTasbihWasCounted() {
        runBlocking {
            val dao = db.tasbihDao()
            dao.upsertRecord(TasbihRecordEntity(tasbihId = tasbihId, count = 10, date = "2026-04-25"))
            dao.upsertRecord(TasbihRecordEntity(tasbihId = tasbihId, count = 0, date = "2026-04-26"))
            dao.upsertRecord(TasbihRecordEntity(tasbihId = tasbihId, count = 40, date = "2026-04-27"))
        }

        val state = openHistory().state.value

        assertEquals("SubhanAllah", state.tasbihName)
        assertEquals(listOf("2026-04-27", "2026-04-25"), state.records.map { it.date })
        assertEquals(50, state.totalCount)
        assertEquals(40, state.bestCount)
        assertEquals(2, state.activeDays)
        assertNull(state.base.error)
        assertEquals(listOf(AnalyticsEvent.TasbihEntryHistoryOpened(tasbihId)), analytics.events)
    }

    @Test
    fun showsNothingForATasbihNeverCounted() {
        val state = openHistory().state.value

        assertEquals(emptyList<Any>(), state.records)
        assertEquals(0, state.totalCount)
    }

    @Test
    fun updatesWhenTheTasbihIsCountedAgain() {
        val viewModel = openHistory()

        runBlocking { repository.increment(tasbihId).last() }

        val records = awaitUntil({ viewModel.state.value.records }) { it.isNotEmpty() }
        assertEquals(listOf(currentIsoDate()), records.map { it.date })
    }
}
