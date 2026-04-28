package tj.rsdevteam.inmuslim.data.repositories

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.api.Api
import tj.rsdevteam.inmuslim.data.models.Message
import tj.rsdevteam.inmuslim.data.models.User
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserBodyDTO
import tj.rsdevteam.inmuslim.data.models.api.UpdateMessagingIdBodyDTO
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Rustam Safarov on 15/08/23.
 * github.com/rustamsafarovrs
 */

@Singleton
class UserRepository
@Inject constructor(
    private val api: Api,
    private val preferences: Preferences,
    private val errorHandler: ErrorHandler,
) {

    fun registerUser(name: String): Flow<Resource<User>> = flow {
        emit(Resource.InProgress())
        val result = api.registerUser(RegisterUserBodyDTO(name = name))
        if (result.isSuccess && result.getOrNull()?.result == 0) {
            preferences.saveUserId(result.getOrThrow().id)
            emit(Resource.Success(result.getOrThrow().toUser()))
        } else {
            emit(errorHandler.getError(result))
        }
    }

    fun updateMessagingId(): Flow<Resource<Message>> = flow {
        emit(Resource.InProgress())
        val token = suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resume(null)
                }
            }
        }
        if (token == null) {
            emit(Resource.Error(Message("Cannot get Firebase Messaging token")))
            return@flow
        }
        val result = api.updateMessagingId(UpdateMessagingIdBodyDTO(id = preferences.getUserId(), msgid = token))
        if (result.isSuccess && result.getOrNull()?.result == 0) {
            preferences.saveFirebaseToken(token)
            emit(Resource.Success(result.getOrThrow().toMessage()))
        } else {
            emit(errorHandler.getError(result))
        }
    }

    fun needRegister(): Boolean {
        return preferences.getUserId() == -1L
    }

    fun getUserId(): Long {
        return preferences.getUserId()
    }

    fun getFirebaseToken(): String {
        return preferences.getFirebaseToken()
    }

    fun isReviewShown(): Boolean {
        return preferences.isReviewShown()
    }

    fun saveReviewShown() {
        preferences.saveReviewShown()
    }
}
