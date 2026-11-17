package tj.rsdevteam.inmuslim.fakes

import kotlinx.coroutines.CompletableDeferred
import tj.rsdevteam.inmuslim.data.api.Api
import tj.rsdevteam.inmuslim.data.models.api.GetRegionsDTO
import tj.rsdevteam.inmuslim.data.models.api.GetTimingDTO
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserBodyDTO
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserDTO
import tj.rsdevteam.inmuslim.data.models.api.UpdateMessagingIdBodyDTO
import tj.rsdevteam.inmuslim.data.models.api.UpdateMessagingIdDTO

class FakeApi : Api {

    var regions: Result<GetRegionsDTO>? = null
    var timing: Result<GetTimingDTO>? = null
    var registerUser: Result<RegisterUserDTO>? = null
    var updateMessagingId: Result<UpdateMessagingIdDTO>? = null

    val timingRequests = mutableListOf<Long>()
    val registerRequests = mutableListOf<RegisterUserBodyDTO>()

    private var gate: CompletableDeferred<Unit>? = null

    /** Keeps every call suspended until [releaseResponses], to observe the in-progress state. */
    fun holdResponses() {
        gate = CompletableDeferred()
    }

    fun releaseResponses() {
        val held = gate
        gate = null
        held?.complete(Unit)
    }

    override suspend fun getRegions(): Result<GetRegionsDTO> = respond { regions }

    override suspend fun getTiming(regionId: Long): Result<GetTimingDTO> {
        timingRequests += regionId
        return respond { timing }
    }

    override suspend fun registerUser(body: RegisterUserBodyDTO): Result<RegisterUserDTO> {
        registerRequests += body
        return respond { registerUser }
    }

    override suspend fun updateMessagingId(body: UpdateMessagingIdBodyDTO): Result<UpdateMessagingIdDTO> =
        respond { updateMessagingId }

    private suspend fun <T> respond(result: () -> Result<T>?): Result<T> {
        gate?.await()
        return result() ?: error("unused")
    }
}
