package tj.rstech.uicomponents.bottomsheet.error

import tj.rsdevteam.inmuslim.core.TextRes

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

data class ErrorBottomSheetConfig(
    val msg: TextRes?,
    val title: TextRes? = null,
    val icon: Int? = null,
)
