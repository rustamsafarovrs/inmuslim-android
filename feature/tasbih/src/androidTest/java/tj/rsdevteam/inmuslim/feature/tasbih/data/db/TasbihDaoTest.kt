package tj.rsdevteam.inmuslim.feature.tasbih.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasbihDaoTest {

    private lateinit var db: TasbihDatabase
    private lateinit var dao: TasbihDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, TasbihDatabase::class.java).build()
        dao = db.tasbihDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun countsOnlyTheRecordsOfTheGivenDay() = runBlocking {
        val id = dao.insert(TasbihEntity(name = "SubhanAllah"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = id, count = 33, date = "2026-04-28"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = id, count = 99, date = "2026-04-27"))

        val tasbihs = dao.observeAllWithCountFor("2026-04-28").first()

        assertEquals(listOf(TasbihWithTodayCountEntity(id, "SubhanAllah", 33)), tasbihs)
    }

    @Test
    fun listsEveryTasbihNewestFirstWithZeroWhenNotCounted() = runBlocking {
        val first = dao.insert(TasbihEntity(name = "SubhanAllah"))
        val second = dao.insert(TasbihEntity(name = "Alhamdulillah"))

        val tasbihs = dao.observeAllWithCountFor("2026-04-28").first()

        assertEquals(listOf(second, first), tasbihs.map { it.id })
        assertEquals(listOf(0, 0), tasbihs.map { it.todayCount })
    }

    @Test
    fun entryHistorySkipsEmptyDaysAndOtherTasbihs() = runBlocking {
        val id = dao.insert(TasbihEntity(name = "SubhanAllah"))
        val other = dao.insert(TasbihEntity(name = "Alhamdulillah"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = id, count = 10, date = "2026-04-25"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = id, count = 0, date = "2026-04-26"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = id, count = 40, date = "2026-04-27"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = other, count = 5, date = "2026-04-27"))

        val records = dao.observeEntryHistory(id).first()

        assertEquals(listOf("2026-04-27", "2026-04-25"), records.map { it.date })
        assertEquals(listOf(40, 10), records.map { it.count })
    }

    @Test
    fun historyNamesEachRecordAndSortsByDayThenCount() = runBlocking {
        val subhan = dao.insert(TasbihEntity(name = "SubhanAllah"))
        val hamd = dao.insert(TasbihEntity(name = "Alhamdulillah"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = subhan, count = 10, date = "2026-04-27"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = hamd, count = 90, date = "2026-04-27"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = subhan, count = 33, date = "2026-04-28"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = hamd, count = 0, date = "2026-04-28"))

        val history = dao.observeHistory().first()

        assertEquals(
            listOf(
                TasbihRecordWithNameEntity(subhan, "SubhanAllah", 33, "2026-04-28"),
                TasbihRecordWithNameEntity(hamd, "Alhamdulillah", 90, "2026-04-27"),
                TasbihRecordWithNameEntity(subhan, "SubhanAllah", 10, "2026-04-27"),
            ),
            history,
        )
    }

    @Test
    fun upsertReplacesTheRecordWithTheSameId() = runBlocking {
        val id = dao.insert(TasbihEntity(name = "SubhanAllah"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = id, count = 1, date = "2026-04-28"))
        val record = checkNotNull(dao.getRecord(id, "2026-04-28"))

        dao.upsertRecord(record.copy(count = 2))

        assertEquals(2, dao.getRecord(id, "2026-04-28")?.count)
        assertEquals(1, dao.observeEntryHistory(id).first().size)
    }

    @Test
    fun deletingATasbihDeletesItsRecords() = runBlocking {
        val id = dao.insert(TasbihEntity(name = "SubhanAllah"))
        dao.upsertRecord(TasbihRecordEntity(tasbihId = id, count = 33, date = "2026-04-28"))

        dao.delete(TasbihEntity(id = id, name = "SubhanAllah"))

        assertNull(dao.getById(id))
        assertNull(dao.getRecord(id, "2026-04-28"))
    }

    @Test
    fun findsNothingForAnUnknownId() = runBlocking {
        assertNull(dao.getById(404))
        assertNull(dao.getRecord(404, "2026-04-28"))
    }
}
