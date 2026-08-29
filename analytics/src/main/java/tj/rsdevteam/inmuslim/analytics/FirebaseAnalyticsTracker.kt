package tj.rsdevteam.inmuslim.analytics

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 *
 * [AnalyticsTracker] backed by Firebase Analytics. It is the only place in the project that talks
 * to the Firebase SDK, so swapping or adding a backend means writing another implementation.
 */
@Singleton
public class FirebaseAnalyticsTracker @Inject constructor(
    @ApplicationContext context: Context,
) : AnalyticsTracker {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun log(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name, event.params.toBundle())
    }

    override fun logScreenView(screen: AnalyticsScreen) {
        firebaseAnalytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to screen.screenName,
                FirebaseAnalytics.Param.SCREEN_CLASS to screen.screenName,
            ),
        )
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value?.take(MAX_PROPERTY_VALUE_LENGTH))
    }

    private fun Map<String, Any>.toBundle(): Bundle = Bundle(size).apply {
        forEach { (key, value) -> putParam(key, value) }
    }

    /** Firebase only accepts a handful of value types, everything else is reported as a string. */
    private fun Bundle.putParam(key: String, value: Any) {
        when (value) {
            is Int -> putLong(key, value.toLong())
            is Long -> putLong(key, value)
            is Float -> putDouble(key, value.toDouble())
            is Double -> putDouble(key, value)
            else -> putString(key, value.toString().take(MAX_VALUE_LENGTH))
        }
    }

    private companion object {

        /** Firebase caps an event parameter's string value at 100 characters. */
        const val MAX_VALUE_LENGTH = 100

        /** A user property value has a tighter cap, and Firebase drops a longer one outright. */
        const val MAX_PROPERTY_VALUE_LENGTH = 36
    }
}
