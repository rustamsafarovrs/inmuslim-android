package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository

class GetTasbihUseCaseTest {

    private class FakeTasbihRepository(
        private val name: Resource<String>,
        private val count: Resource<Int>,
    ) : TasbihRepository {

        override fun getTasbihName(tasbihId: Long): Flow<Resource<String>> = flowOf(name)

        override fun getTodayCount(tasbihId: Long): Flow<Resource<Int>> = flowOf(count)

        override fun observeTasbihs(): Flow<Resource<List<Tasbih>>> = error("unused")

        override fun observeEntryHistory(tasbihId: Long): Flow<Resource<List<TasbihRecord>>> = error("unused")

        override fun observeHistory(): Flow<Resource<List<TasbihDayHistory>>> = error("unused")

        override fun addTasbih(name: String): Flow<Resource<Long>> = error("unused")

        override fun increment(tasbihId: Long): Flow<Resource<Unit>> = error("unused")

        override fun reset(tasbihId: Long): Flow<Resource<Unit>> = error("unused")

        override fun isHapticEnabled(): Boolean = error("unused")

        override fun setHapticEnabled(enabled: Boolean) = error("unused")
    }

    private fun useCase(name: Resource<String>, count: Resource<Int>) =
        GetTasbihUseCase(FakeTasbihRepository(name, count))

    @Test
    fun `combines the name and today count into one tasbih`() = runBlocking {
        val result = useCase(Resource.Success("SubhanAllah"), Resource.Success(33)).invoke(7L).last()

        assertTrue(result is Resource.Success)
        assertEquals(Tasbih(7L, "SubhanAllah", 33), (result as Resource.Success).data)
    }

    @Test
    fun `reports a failing name lookup as an error`() = runBlocking {
        val error = IllegalStateException("boom")

        val result = useCase(Resource.Error(error = error), Resource.Success(33)).invoke(7L).last()

        assertTrue(result is Resource.Error)
        assertEquals(error, (result as Resource.Error).error)
    }

    @Test
    fun `reports a failing count lookup as an error`() = runBlocking {
        val error = IllegalStateException("boom")

        val result = useCase(Resource.Success("SubhanAllah"), Resource.Error(error = error)).invoke(7L).last()

        assertTrue(result is Resource.Error)
        assertEquals(error, (result as Resource.Error).error)
    }

    @Test
    fun `stays in progress until both parts arrive`() = runBlocking {
        val result = useCase(Resource.Success("SubhanAllah"), Resource.InProgress()).invoke(7L).last()

        assertTrue(result is Resource.InProgress)
    }
}
