package tj.rsdevteam.inmuslim.data.models.api

import com.squareup.moshi.JsonClass

/**
 * Created by Rustam Safarov on 6/25/24.
 * github.com/rustamsafarovrs
 */

@JsonClass(generateAdapter = true)
data class MessageDTO(
    val result: Int,
    val msg: String,
)
