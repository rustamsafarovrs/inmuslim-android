package tj.rsdevteam.inmuslim.data.models.network

import com.squareup.moshi.JsonClass

/**
 * Created by Rustam Safarov on 15/08/23.
 * github.com/rustamsafarovrs
 */

@JsonClass(generateAdapter = true)
data class RegisterUserResponse(
    val result: Int,
    val msg: String,
    val id: Long
)