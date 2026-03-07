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
}
