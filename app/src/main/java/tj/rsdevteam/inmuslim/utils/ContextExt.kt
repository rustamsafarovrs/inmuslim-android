package tj.rsdevteam.inmuslim.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Created by Rustam Safarov on 8/20/23.
 * github.com/rustamsafarovrs
 */

internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    error("Activity not found")
}

fun Context.is24HourFormat(): Boolean {
    return android.text.format.DateFormat.is24HourFormat(this)
}
