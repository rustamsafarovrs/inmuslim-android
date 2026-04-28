package tj.rsdevteam.inmuslim.data.models.api

import com.squareup.moshi.JsonClass

/**
 * Created by Rustam Safarov on 14/08/23.
 * github.com/rustamsafarovrs
 */

@JsonClass(generateAdapter = true)
data class RegionDTO(
    val id: Long,
    val name: String,
)
