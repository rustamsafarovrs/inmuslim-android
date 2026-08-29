package tj.rsdevteam.inmuslim.core

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.Serializable

/**
 * Created by Rustam Safarov on 5/8/25.
 * github.com/rustamsafarovrs
 */

@Serializable
sealed class TextRes {

    data class Raw(val value: String) : TextRes()

    data class Res(@StringRes val value: Int) : TextRes()

    data class ResParams(@StringRes val value: Int, val args: List<Any>) : TextRes()

    companion object {

        fun empty(): TextRes {
            return Raw("")
        }
    }
}

fun String.asTextRes(): TextRes {
    return TextRes.Raw(this)
}

@Composable
@ReadOnlyComposable
fun TextRes.composeString(): String {
    return LocalContext.current.getTextRes(this)
}

@Composable
@ReadOnlyComposable
fun TextRes.resolve(): String {
    return composeString()
}

@Composable
@ReadOnlyComposable
fun TextRes?.resolveOrEmpty(): String {
    return this?.composeString() ?: ""
}

fun textResId(@StringRes id: Int): TextRes.Res {
    return TextRes.Res(id)
}

@Suppress("SpreadOperator")
fun Context.getTextRes(textRes: TextRes): String {
    return when (textRes) {
        is TextRes.Raw -> textRes.value
        is TextRes.Res -> getString(textRes.value)
        is TextRes.ResParams -> getString(textRes.value, *textRes.args.toTypedArray())
    }
}

fun Context.getTextResOrEmpty(textRes: TextRes?): String {
    return if (textRes == null) "" else getTextRes(textRes)
}
