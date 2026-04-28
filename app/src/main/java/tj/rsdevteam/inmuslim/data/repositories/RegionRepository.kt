package tj.rsdevteam.inmuslim.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.api.Api
import tj.rsdevteam.inmuslim.data.models.Region
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rustam Safarov on 14/08/23.
 * github.com/rustamsafarovrs
 */

@Singleton
class RegionRepository
@Inject constructor(
    private val api: Api,
    private val preferences: Preferences,
    private val errorHandler: ErrorHandler,
) {

    fun getRegionId() = preferences.getRegionId()

    fun saveRegionId(id: Long) = preferences.saveRegionId(id)

    fun getRegions(): Flow<Resource<List<Region>>> = flow {
        emit(Resource.InProgress())
        val result = api.getRegions()
        if (result.isSuccess && result.getOrNull()?.result == 0) {
            emit(Resource.Success(result.getOrThrow().regions.map { it.toRegion() }))
        } else {
            emit(errorHandler.getError(result))
        }
    }
}
