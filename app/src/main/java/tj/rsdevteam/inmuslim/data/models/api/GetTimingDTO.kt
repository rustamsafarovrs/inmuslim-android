package tj.rsdevteam.inmuslim.data.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@JsonClass(generateAdapter = true)
data class GetTimingDTO(
    val result: Int,
    val msg: String,
    val region: String,
    @Json(name = "begin_date")
    val beginDate: String,
    val timing: TimingDTO,
)
