package tj.rsdevteam.inmuslim.core.config

import tj.rsdevteam.inmuslim.core.BuildConfig

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

object BuildVars {

    val BUILD_TYPE = if (BuildConfig.DEBUG) BuildType.TEST else BuildType.PROD
    const val BASE_URL = BuildConfig.BASE_URL
}

enum class BuildType {
    TEST,
    PROD
}
