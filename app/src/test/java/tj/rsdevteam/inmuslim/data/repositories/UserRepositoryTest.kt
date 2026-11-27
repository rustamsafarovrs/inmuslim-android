package tj.rsdevteam.inmuslim.data.repositories

import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.core.config.BuildType
import tj.rsdevteam.inmuslim.core.config.BuildVars
import tj.rsdevteam.inmuslim.data.exceptions.ConnectionTimeoutException
import tj.rsdevteam.inmuslim.data.models.User
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserBodyDTO
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserDTO
import tj.rsdevteam.inmuslim.data.preferences.inMemoryPreferences
import tj.rsdevteam.inmuslim.fakes.FakeApi
import java.net.UnknownHostException

class UserRepositoryTest {

    private val api = FakeApi()
    private val preferences = inMemoryPreferences()
    private val repository = UserRepository(api, preferences, ErrorHandler())

    @Test
    fun `registers the device and keeps its user id`() = runBlocking {
        api.registerUser = Result.success(RegisterUserDTO(result = 0, msg = "", id = 42))

        val rs = repository.registerUser("Google Pixel").last()

        assertEquals(User(42), (rs as Resource.Success).data)
        assertEquals(listOf(RegisterUserBodyDTO(name = "Google Pixel")), api.registerRequests)
        assertEquals(42L, repository.getUserId())
    }

    @Test
    fun `stays unregistered when the server rejects the device`() = runBlocking {
        api.registerUser = Result.success(RegisterUserDTO(result = 1, msg = "", id = 0))

        assertTrue(repository.registerUser("Google Pixel").last() is Resource.Error)
        assertEquals(-1L, repository.getUserId())
    }

    @Test
    fun `stays unregistered when offline`() = runBlocking {
        api.registerUser = Result.failure(UnknownHostException())

        val rs = repository.registerUser("Google Pixel").last()

        assertTrue((rs as Resource.Error).error is ConnectionTimeoutException)
        assertEquals(-1L, repository.getUserId())
    }

    @Test
    fun `debug builds never register`() {
        assumeTrue(BuildVars.BUILD_TYPE == BuildType.TEST)

        assertFalse(repository.needRegister())
    }

    @Test
    fun `remembers that the review was shown`() {
        assertFalse(repository.isReviewShown())

        repository.saveReviewShown()

        assertTrue(repository.isReviewShown())
    }

    @Test
    fun `asks for a review once the app was used on three different days`() {
        repository.recordVisit("2026-04-28")
        repository.recordVisit("2026-04-28")
        repository.recordVisit("2026-04-29")
        assertFalse(repository.shouldRequestReview())

        repository.recordVisit("2026-04-30")
        assertTrue(repository.shouldRequestReview())
    }

    @Test
    fun `never asks for a review again once it was requested`() {
        listOf("2026-04-28", "2026-04-29", "2026-04-30").forEach(repository::recordVisit)

        repository.saveReviewShown()

        assertFalse(repository.shouldRequestReview())
    }
}
