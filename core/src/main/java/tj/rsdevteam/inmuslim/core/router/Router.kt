package tj.rsdevteam.inmuslim.core.router

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.toRoute

/**
 * Created by Rustam Safarov on 2/28/26.
 * github.com/rustamsafarovrs
 */

class Router(val controller: NavHostController) {

    fun navigate(route: Screen) {
        controller.navigate(route = route)
    }

    fun navigateUp(): Boolean {
        return controller.navigateUp()
    }

    fun navigateAsRoot(route: Screen) {
        controller.navigate(route) {
            popUpTo(controller.graph.id) {
                inclusive = true
            }
        }
    }

    fun navigate(route: Screen, popUpTo: Screen) {
        controller.navigate(route) {
            popUpTo(popUpTo)
        }
    }

    fun navigateWithReplace(route: Screen) {
        controller.navigate(route) {
            popUpTo(controller.currentDestination!!.id) {
                inclusive = true
            }
        }
    }

    fun popUpToMain() {
        controller.navigate(Screen.Main) {
            popUpTo(Screen.Main) {
                inclusive = true
            }
        }
    }

    inline fun <reified T> toRoute(): T {
        return controller.currentBackStackEntry!!.toRoute<T>()
    }
}

val LocalRouter = compositionLocalOf<Router> {
    error("Router not provided")
}
