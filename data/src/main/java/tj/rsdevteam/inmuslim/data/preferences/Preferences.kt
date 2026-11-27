package tj.rsdevteam.inmuslim.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

class Preferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {

        const val PREFS_FILE_NAME = "prefs"

        const val PREFS_REGION_ID = "region_id"
        const val PREFS_USER_ID = "user_id"
        const val PREFS_FIREBASE_TOKEN = "firebase_token"
        const val PREFS_REVIEW_SHOWN = "review_shown"
        const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        const val PREFS_DEFAULT_LOCALE_APPLIED = "default_locale_applied"
        const val PREFS_VISIT_DATE = "visit_date"
        const val PREFS_VISIT_DAYS = "visit_days"
    }

    val isEmpty: Boolean
        get() = prefs.all.isEmpty()

    var isDefaultLocaleApplied: Boolean
        get() = prefs.getBoolean(PREFS_DEFAULT_LOCALE_APPLIED, false)
        set(value) {
            editor.putBoolean(PREFS_DEFAULT_LOCALE_APPLIED, value).apply()
        }

    var visitDate: String
        get() = prefs.getString(PREFS_VISIT_DATE, null) ?: ""
        set(value) {
            editor.putString(PREFS_VISIT_DATE, value).apply()
        }

    var visitDays: Int
        get() = prefs.getInt(PREFS_VISIT_DAYS, 0)
        set(value) {
            editor.putInt(PREFS_VISIT_DAYS, value).apply()
        }

    fun saveRegionId(id: Long) {
        editor.putLong(PREFS_REGION_ID, id).apply()
    }

    fun getRegionId(): Long = prefs.getLong(PREFS_REGION_ID, -1)

    fun saveUserId(id: Long) {
        editor.putLong(PREFS_USER_ID, id).apply()
    }

    fun getUserId() = prefs.getLong(PREFS_USER_ID, -1)

    fun saveFirebaseToken(token: String) {
        editor.putString(PREFS_FIREBASE_TOKEN, token).apply()
    }

    fun getFirebaseToken(): String {
        return prefs.getString(PREFS_FIREBASE_TOKEN, null) ?: ""
    }

    fun saveReviewShown() {
        editor.putBoolean(PREFS_REVIEW_SHOWN, true).commit()
    }

    fun isReviewShown(): Boolean {
        return prefs.getBoolean(PREFS_REVIEW_SHOWN, false)
    }

    fun isHapticEnabled(): Boolean = prefs.getBoolean(KEY_HAPTIC_ENABLED, false)

    fun setHapticEnabled(enabled: Boolean) {
        editor.putBoolean(KEY_HAPTIC_ENABLED, enabled).apply()
    }
}
