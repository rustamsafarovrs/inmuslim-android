package tj.rsdevteam.inmuslim.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tj.rsdevteam.inmuslim.data.api.Api
import tj.rsdevteam.inmuslim.data.models.Resource
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@Singleton
class TimingRepository
@Inject constructor(
    private val api: Api,
    private val preferences: Preferences,
    private val errorHandler: ErrorHandler
) {

    fun getTiming(): Flow<Resource<Timing>> = flow {
        emit(Resource.InProgress())
        val result = api.getTiming(regionId = preferences.getRegionId())
        if (result.isSuccess && result.getOrNull()?.result == 0) {
            emit(Resource.Success(result.getOrThrow().timing.toTiming()))
        } else {
            emit(errorHandler.getError(result))
        }
    }
}
