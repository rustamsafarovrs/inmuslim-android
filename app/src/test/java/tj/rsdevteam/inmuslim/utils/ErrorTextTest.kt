package tj.rsdevteam.inmuslim.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.data.exceptions.ApiException
import tj.rsdevteam.inmuslim.data.exceptions.ConnectionTimeoutException
import tj.rsdevteam.inmuslim.data.exceptions.UnknownException
import tj.rsdevteam.inmuslim.res.R

class ErrorTextTest {

    @Test
    fun `being offline says there is no connection`() {
        assertEquals(TextRes.Res(R.string.common_description_no_connection), ConnectionTimeoutException().toErrorText())
    }

    @Test
    fun `anything else is a generic error`() {
        listOf(ApiException("HTTP 500"), UnknownException("boom"), null).forEach {
            assertEquals(TextRes.Res(R.string.common_description_unknown_error), it.toErrorText())
        }
    }
}
