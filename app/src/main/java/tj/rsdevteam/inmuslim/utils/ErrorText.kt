package tj.rsdevteam.inmuslim.utils

import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.data.exceptions.ConnectionTimeoutException
import tj.rsdevteam.inmuslim.res.R

fun Throwable?.toErrorText(): TextRes = when (this) {
    is ConnectionTimeoutException -> TextRes.Res(R.string.common_description_no_connection)
    else -> TextRes.Res(R.string.common_description_unknown_error)
}
