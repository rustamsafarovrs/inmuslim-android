package tj.rsdevteam.inmuslim.core

/**
 * Created by Rustam Safarov on 4/28/26.
 * github.com/rustamsafarovrs
 */

data class BaseState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isFormValid: Boolean = true,
    val isOffline: Boolean = false,
)
