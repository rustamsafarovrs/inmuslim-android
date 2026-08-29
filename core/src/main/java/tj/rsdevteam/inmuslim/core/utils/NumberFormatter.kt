package tj.rsdevteam.inmuslim.core.utils

import java.text.NumberFormat

object NumberFormatter {

    /** Formats a number with the grouping separators of the current locale, e.g. 1 234. */
    fun format(number: Int): String {
        return NumberFormat.getNumberInstance().format(number)
    }
}
