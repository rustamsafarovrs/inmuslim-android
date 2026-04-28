package tj.rsdevteam.inmuslim.data.models

import androidx.compose.runtime.mutableStateOf

/**
 * Created by Rustam Safarov on 6/25/24.
 * github.com/rustamsafarovrs
 */

data class Region(
    val id: Long,
    val name: String,
) {

    @Transient
    var selected = mutableStateOf(false)
}
