package tj.rsdevteam.inmuslim.data.repositories

import retrofit2.HttpException
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.exceptions.ApiException
import tj.rsdevteam.inmuslim.data.exceptions.ConnectionTimeoutException
import tj.rsdevteam.inmuslim.data.exceptions.UnknownException
import tj.rsdevteam.inmuslim.data.models.api.MessageDTO
import java.io.IOException
import java.net.UnknownHostException

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

class ErrorHandler {

    fun <T : Any> getError(result: Result<*>): Resource<T> {
        return if (result.getOrNull() is MessageDTO) {
            Resource.Error(error = ApiException((result.getOrThrow() as MessageDTO).msg))
        } else if (result.exceptionOrNull() is UnknownHostException) {
            Resource.Error(error = ConnectionTimeoutException())
        } else if (result.exceptionOrNull() is HttpException) {
            Resource.Error(error = ApiException(result.exceptionOrNull()?.localizedMessage))
        } else if (result.exceptionOrNull() is IOException) {
            Resource.Error(error = ConnectionTimeoutException())
        } else {
            Resource.Error(error = UnknownException(result.exceptionOrNull()?.localizedMessage))
        }
    }
}
