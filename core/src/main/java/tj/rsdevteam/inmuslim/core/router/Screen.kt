package tj.rsdevteam.inmuslim.core.router

import kotlinx.serialization.Serializable

/**
 * Created by Rustam Safarov on 2/28/26.
 * github.com/rustamsafarovrs
 */

sealed class Screen {

    @Serializable
    object Main : Screen()

    @Serializable
    object Settings : Screen()

    @Serializable
    object Regions : Screen()

    @Serializable
    object TasbihList : Screen()

    @Serializable
    data class TasbihCalculator(val tasbihId: Long) : Screen()

    @Serializable
    data class TasbihHistory(val tasbihId: Long) : Screen()
}
