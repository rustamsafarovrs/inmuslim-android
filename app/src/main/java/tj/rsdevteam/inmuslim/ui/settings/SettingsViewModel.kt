package tj.rsdevteam.inmuslim.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.AnalyticsTracker
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 */
@HiltViewModel
class SettingsViewModel
@Inject constructor(
    private val analytics: AnalyticsTracker,
) : ViewModel() {

    fun handleEvent(event: SettingsUIEvent) {
        when (event) {
            is SettingsUIEvent.DidClickRegion -> analytics.log(AnalyticsEvent.SettingsRegionClicked)
            is SettingsUIEvent.DidClickLanguage -> analytics.log(AnalyticsEvent.SettingsLanguageClicked)
        }
    }
}
