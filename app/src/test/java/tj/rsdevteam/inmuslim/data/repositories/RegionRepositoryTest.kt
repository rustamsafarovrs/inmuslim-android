package tj.rsdevteam.inmuslim.data.repositories

import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.exceptions.ConnectionTimeoutException
import tj.rsdevteam.inmuslim.data.models.Region
import tj.rsdevteam.inmuslim.data.models.api.GetRegionsDTO
import tj.rsdevteam.inmuslim.data.models.api.RegionDTO
import tj.rsdevteam.inmuslim.data.preferences.inMemoryPreferences
import tj.rsdevteam.inmuslim.fakes.FakeApi
import java.net.UnknownHostException

class RegionRepositoryTest {

    private val api = FakeApi()
    private val preferences = inMemoryPreferences()
    private val repository = RegionRepository(api, preferences, ErrorHandler())

    @Test
    fun `emits progress, then the result`() = runBlocking {
        api.regions = Result.success(GetRegionsDTO(result = 0, msg = "", regions = emptyList()))

        val emissions = repository.getRegions().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.InProgress)
        assertTrue(emissions[1] is Resource.Success)
    }

    @Test
    fun `maps the regions`() = runBlocking {
        val dto = GetRegionsDTO(
            result = 0,
            msg = "",
            regions = listOf(RegionDTO(1, "Dushanbe"), RegionDTO(2, "Khujand")),
        )
        api.regions = Result.success(dto)

        val rs = repository.getRegions().last()

        assertEquals(listOf(Region(1, "Dushanbe"), Region(2, "Khujand")), (rs as Resource.Success).data)
    }

    @Test
    fun `reports a request the server rejected as an error`() = runBlocking {
        api.regions = Result.success(GetRegionsDTO(result = 1, msg = "Server is busy", regions = emptyList()))

        assertTrue(repository.getRegions().last() is Resource.Error)
    }

    @Test
    fun `reports no connection when the host is unreachable`() = runBlocking {
        api.regions = Result.failure(UnknownHostException())

        val rs = repository.getRegions().last()

        assertTrue((rs as Resource.Error).error is ConnectionTimeoutException)
    }

    @Test
    fun `saves the chosen region`() {
        assertEquals(-1L, repository.getRegionId())

        repository.saveRegionId(3)

        assertEquals(3L, repository.getRegionId())
        assertEquals(3L, preferences.getRegionId())
    }
}
