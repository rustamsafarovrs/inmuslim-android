package tj.rsdevteam.inmuslim

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tj.rsdevteam.inmuslim.utils.DefaultLocaleInitializer
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 8/14/23.
 * github.com/rustamsafarovrs
 */

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var defaultLocaleInitializer: DefaultLocaleInitializer

    override fun onCreate() {
        super.onCreate()
        defaultLocaleInitializer.applyOnFirstLaunch()
    }
}
