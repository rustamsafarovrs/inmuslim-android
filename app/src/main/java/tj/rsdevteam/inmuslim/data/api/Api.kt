package tj.rsdevteam.inmuslim.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tj.rsdevteam.inmuslim.data.models.api.GetRegionsDTO
import tj.rsdevteam.inmuslim.data.models.api.GetTimingDTO
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserBodyDTO
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserDTO
import tj.rsdevteam.inmuslim.data.models.api.UpdateMessagingIdBodyDTO
import tj.rsdevteam.inmuslim.data.models.api.UpdateMessagingIdDTO

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

interface Api {

    @GET("region/get-regions")
    suspend fun getRegions(): Result<GetRegionsDTO>

    @GET("timing/get-timing")
    suspend fun getTiming(@Query("regionId") regionId: Long): Result<GetTimingDTO>

    @POST("user/register-user")
    suspend fun registerUser(@Body body: RegisterUserBodyDTO): Result<RegisterUserDTO>

    @POST("messaging/update-messaging-id")
    suspend fun updateMessagingId(@Body body: UpdateMessagingIdBodyDTO): Result<UpdateMessagingIdDTO>
}
