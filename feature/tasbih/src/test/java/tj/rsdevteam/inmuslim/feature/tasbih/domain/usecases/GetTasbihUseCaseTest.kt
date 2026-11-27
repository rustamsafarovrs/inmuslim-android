package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.fakes.FakeTasbihRepository

class GetTasbihUseCaseTest {

    private fun useCase(name: Resource<String>, count: Resource<Int>) = GetTasbihUseCase(
        FakeTasbihRepository().apply {
            tasbihName = flowOf(name)
            todayCount = flowOf(count)
        },
    )

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
