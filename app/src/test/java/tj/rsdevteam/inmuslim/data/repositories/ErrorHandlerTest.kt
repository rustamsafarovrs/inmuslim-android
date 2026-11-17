package tj.rsdevteam.inmuslim.data.repositories

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.exceptions.ApiException
import tj.rsdevteam.inmuslim.data.exceptions.ConnectionTimeoutException
import tj.rsdevteam.inmuslim.data.exceptions.UnknownException
import tj.rsdevteam.inmuslim.data.models.api.MessageDTO
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorHandlerTest {

    private val errorHandler = ErrorHandler()

    private fun errorOf(result: Result<*>): Throwable? =
        (errorHandler.getError<Unit>(result) as Resource.Error).error

    @Test
    fun `reports an unreachable host as no connection`() {
        assertTrue(errorOf(Result.failure<Unit>(UnknownHostException())) is ConnectionTimeoutException)
    }

    @Test
    fun `reports any other network failure as no connection`() {
        assertTrue(errorOf(Result.failure<Unit>(SocketTimeoutException())) is ConnectionTimeoutException)
    }

    @Test
    fun `reports an HTTP error as an API error with its status`() {
        val httpError = HttpException(Response.error<Unit>(500, "".toResponseBody()))

        val error = errorOf(Result.failure<Unit>(httpError))

        assertTrue(error is ApiException)
        assertTrue(error?.message.orEmpty().startsWith("HTTP 500"))
    }

    @Test
    fun `passes the server message of a MessageDTO through`() {
        val error = errorOf(Result.success(MessageDTO(result = 1, msg = "Region not found")))

        assertTrue(error is ApiException)
        assertEquals("Region not found", error?.message)
    }

    @Test
    fun `reports anything else as unknown, keeping its message`() {
        val error = errorOf(Result.failure<Unit>(IllegalStateException("boom")))

        assertTrue(error is UnknownException)
        assertTrue(error?.message.orEmpty().endsWith("boom"))
    }
}
