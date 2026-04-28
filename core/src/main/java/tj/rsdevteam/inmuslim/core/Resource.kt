package tj.rsdevteam.inmuslim.core

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

sealed class Resource<out T : Any>(open val data: T? = null) {

    class InProgress<T : Any>(data: T? = null) : Resource<T>(data)

    class Success<T : Any>(override val data: T) : Resource<T>(data)

    class Error<T : Any>(data: T? = null, val error: Throwable? = null) : Resource<T>(data)
}
