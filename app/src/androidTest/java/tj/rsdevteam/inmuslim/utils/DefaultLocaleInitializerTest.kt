package tj.rsdevteam.inmuslim.utils

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tj.rsdevteam.inmuslim.data.preferences.Preferences

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.TIRAMISU)
class DefaultLocaleInitializerTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val localeManager = context.getSystemService(LocaleManager::class.java)

    // App.onCreate already ran the initializer once, so every test starts from a wiped install.
    @Before
    fun setUp() = resetInstall()

    @After
    fun tearDown() = resetInstall()

    private fun resetInstall() {
        context.getSharedPreferences(Preferences.PREFS_FILE_NAME, Context.MODE_PRIVATE).edit().clear().commit()
        localeManager.applicationLocales = LocaleList.getEmptyLocaleList()
    }

    private fun applyOnFirstLaunch() = DefaultLocaleInitializer(context, Preferences(context)).applyOnFirstLaunch()

    @Test
    fun switchesANewInstallToTajik() {
        applyOnFirstLaunch()

        assertEquals("tg-TJ", localeManager.applicationLocales.toLanguageTags())
        assertTrue(Preferences(context).isDefaultLocaleApplied)
    }

    @Test
    fun keepsALanguageRestoredFromABackup() {
        localeManager.applicationLocales = LocaleList.forLanguageTags("ru")

        applyOnFirstLaunch()

        assertEquals("ru", localeManager.applicationLocales.toLanguageTags())
        assertTrue(Preferences(context).isDefaultLocaleApplied)
    }

    @Test
    fun keepsTheLanguageOfAnUpdatedInstall() {
        Preferences(context).saveRegionId(3)

        applyOnFirstLaunch()

        assertTrue(localeManager.applicationLocales.isEmpty)
        assertTrue(Preferences(context).isDefaultLocaleApplied)
    }

    @Test
    fun keepsALanguageChosenAfterTheFirstLaunch() {
        applyOnFirstLaunch()
        localeManager.applicationLocales = LocaleList.forLanguageTags("en")

        applyOnFirstLaunch()

        assertEquals("en", localeManager.applicationLocales.toLanguageTags())
    }
}
