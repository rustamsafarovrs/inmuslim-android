package tj.rsdevteam.inmuslim.utils

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import javax.inject.Inject

class DefaultLocaleInitializer
@Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val preferences: Preferences,
) {

    fun applyOnFirstLaunch() {
        if (preferences.isDefaultLocaleApplied) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || !preferences.isEmpty || setDefaultLocale()) {
            preferences.isDefaultLocaleApplied = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setDefaultLocale(): Boolean {
        val localeManager = context.getSystemService(LocaleManager::class.java) ?: return false
        return runCatching {
            // Non-empty when Android restored the per-app language from a backup.
            if (localeManager.applicationLocales.isEmpty) {
                localeManager.applicationLocales = LocaleList.forLanguageTags(DEFAULT_LANGUAGE_TAG)
            }
        }.isSuccess
    }

    private companion object {
        // The tag the system picker stores for the "tg" entry of locales_config.xml; it doesn't recognise a bare "tg".
        const val DEFAULT_LANGUAGE_TAG = "tg-TJ"
    }
}
