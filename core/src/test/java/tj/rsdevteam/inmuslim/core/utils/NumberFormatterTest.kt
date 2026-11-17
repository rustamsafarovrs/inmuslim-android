package tj.rsdevteam.inmuslim.core.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class NumberFormatterTest {

    private val defaultLocale = Locale.getDefault()

    @After
    fun tearDown() = Locale.setDefault(defaultLocale)

    @Test
    fun `groups thousands with the separator of the current locale`() {
        Locale.setDefault(Locale.US)
        assertEquals("1,234,567", NumberFormatter.format(1_234_567))

        Locale.setDefault(Locale.GERMANY)
        assertEquals("1.234.567", NumberFormatter.format(1_234_567))
    }

    @Test
    fun `leaves small numbers as they are`() {
        Locale.setDefault(Locale.US)

        assertEquals("0", NumberFormatter.format(0))
        assertEquals("999", NumberFormatter.format(999))
    }
}
