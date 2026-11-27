package tj.rsdevteam.inmuslim.data.repositories

import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.exceptions.ApiException
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.data.models.api.GetTimingDTO
import tj.rsdevteam.inmuslim.data.models.api.TimingDTO
import tj.rsdevteam.inmuslim.data.preferences.inMemoryPreferences
import tj.rsdevteam.inmuslim.fakes.FakeApi

class TimingRepositoryTest {

    private val api = FakeApi()
    private val preferences = inMemoryPreferences()
    private val repository = TimingRepository(api, preferences, ErrorHandler())

    private val response = GetTimingDTO(
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
    )

    @Test
    fun `asks for the times of the saved region`() = runBlocking {
        preferences.saveRegionId(7)
        api.timing = Result.success(response)

        repository.getTiming().last()

        assertEquals(listOf(7L), api.timingRequests)
    }

    @Test
    fun `maps the times`() = runBlocking {
        api.timing = Result.success(response)

        val rs = repository.getTiming().last()

        assertEquals(
            Timing(
                fajr = "04:00",
                sunrise = "05:30",
                zuhr = "12:30",
                asr = "16:30",
                sunset = "19:20",
                maghrib = "19:30",
                isha = "21:00",
            ),
            (rs as Resource.Success).data,
        )
    }

    @Test
    fun `reports a request the server rejected as an error`() = runBlocking {
        api.timing = Result.success(response.copy(result = 1))

        assertTrue(repository.getTiming().last() is Resource.Error)
    }

    @Test
    fun `reports an HTTP failure as an API error`() = runBlocking {
        api.timing = Result.failure(HttpException(Response.error<Unit>(404, "".toResponseBody())))

        val rs = repository.getTiming().last()

        assertTrue((rs as Resource.Error).error is ApiException)
    }
}
