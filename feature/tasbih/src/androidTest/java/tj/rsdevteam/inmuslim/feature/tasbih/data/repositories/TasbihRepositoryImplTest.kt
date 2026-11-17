package tj.rsdevteam.inmuslim.feature.tasbih.data.repositories

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import tj.rsdevteam.inmuslim.feature.tasbih.data.currentIsoDate
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDatabase
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.testing.successData
import tj.rsdevteam.inmuslim.res.R

@RunWith(AndroidJUnit4::class)
class TasbihRepositoryImplTest {

    private lateinit var db: TasbihDatabase
    private lateinit var repository: TasbihRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, TasbihDatabase::class.java).build()
        repository = TasbihRepositoryImpl(db.tasbihDao(), db, Preferences(context))
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun addTasbihEmitsProgressThenTheNewId() = runBlocking {
        val emissions = repository.addTasbih("SubhanAllah").toList()

        assertTrue(emissions.first() is Resource.InProgress)
        val id = (emissions.last() as Resource.Success).data
        assertEquals("SubhanAllah", repository.getTasbihName(id).successData())
    }

    @Test
    fun observeTasbihsEmitsNewlyAddedTasbihWithTodayCount() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()
        repository.increment(id).last()

        assertEquals(listOf(Tasbih(id, "SubhanAllah", 1)), repository.observeTasbihs().successData())
    }

    @Test
    fun nameOfAnUnknownTasbihIsEmpty() = runBlocking {
        assertEquals("", repository.getTasbihName(404).successData())
    }

    @Test
    fun todayCountIsZeroBeforeAnyTap() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()

        assertEquals(0, repository.getTodayCount(id).successData())
    }

    @Test
    fun incrementAddsToTodayCount() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()

        repository.increment(id).last()
        repository.increment(id).last()

        assertEquals(2, repository.getTodayCount(id).successData())
    }

    @Test
    fun resetZeroesTodayCount() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()
        repository.increment(id).last()

        val rs = repository.reset(id).last()

        assertTrue(rs is Resource.Success)
        assertEquals(0, repository.getTodayCount(id).successData())
    }

    @Test
    fun resetBeforeAnyTapSucceeds() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()

        assertTrue(repository.reset(id).last() is Resource.Success)
        assertEquals(0, repository.getTodayCount(id).successData())
    }

    @Test
    fun incrementingAnUnknownTasbihReportsAnError() = runBlocking {
        assertTrue(repository.increment(404).last() is Resource.Error)
    }

    @Test
    fun historyGroupsTodaysCountsUnderToday() = runBlocking {
        val subhan = repository.addTasbih("SubhanAllah").successData()
        val hamd = repository.addTasbih("Alhamdulillah").successData()
        repository.increment(subhan).last()
        repository.increment(subhan).last()
        repository.increment(hamd).last()

        val day = repository.observeHistory().successData().single()

        assertEquals(currentIsoDate(), day.date)
        assertEquals(TextRes.Res(R.string.common_other_today), day.dateLabel)
        assertEquals(3, day.total)
        assertEquals(listOf("SubhanAllah", "Alhamdulillah"), day.entries.map { it.tasbihName })
    }

    @Test
    fun entryHistoryHidesADayThatWasReset() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()
        repository.increment(id).last()
        assertEquals(1, repository.observeEntryHistory(id).successData().size)

        repository.reset(id).last()

        assertTrue(repository.observeEntryHistory(id).successData().isEmpty())
    }

    @Test
    fun storesTheHapticSetting() {
        repository.setHapticEnabled(true)
        assertTrue(repository.isHapticEnabled())

        repository.setHapticEnabled(false)
        assertFalse(repository.isHapticEnabled())
    }
}
