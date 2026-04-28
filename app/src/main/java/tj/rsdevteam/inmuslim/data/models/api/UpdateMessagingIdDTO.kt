package tj.rsdevteam.inmuslim.data.models.api

import com.squareup.moshi.JsonClass

/**
 * Created by Rustam Safarov on 15/08/23.
 * github.com/rustamsafarovrs
 */

@JsonClass(generateAdapter = true)
data class UpdateMessagingIdDTO(
    val result: Int,
    val msg: String,
)
