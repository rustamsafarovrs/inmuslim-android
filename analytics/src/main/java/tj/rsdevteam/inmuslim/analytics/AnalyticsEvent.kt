package tj.rsdevteam.inmuslim.analytics

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 *
 * Closed catalogue of everything the app reports. Adding a case here is the only way to add an
 * event, so the whole taxonomy stays reviewable in one file.
 *
 * [name] must match the Firebase constraints: at most 40 characters, snake_case, starting with a
 * letter and never prefixed with `firebase_`, `google_` or `ga_`.
 */
public sealed class AnalyticsEvent(
    public val name: String,
    public val params: Map<String, Any> = emptyMap(),
) {

    /** The launcher activity decided where to send the user. */
    public data class AppLaunched(val openOnboarding: Boolean) : AnalyticsEvent(
        name = "app_launched",
        params = mapOf(AnalyticsParam.IS_ONBOARDING to openOnboarding),
    )

    public data class RegionsLoaded(val count: Int) : AnalyticsEvent(
        name = "regions_loaded",
        params = mapOf(AnalyticsParam.COUNT to count),
    )

    public data class RegionsLoadFailed(val error: String?) : AnalyticsEvent(
        name = "regions_load_failed",
        params = mapOf(AnalyticsParam.ERROR to error.orUnknown()),
    )

    public data class RegionSelected(val regionId: Long) : AnalyticsEvent(
        name = "region_selected",
        params = mapOf(AnalyticsParam.REGION_ID to regionId),
    )

    /** The user confirmed the region, which is what actually switches the prayer times. */
    public data class RegionConfirmed(val regionId: Long) : AnalyticsEvent(
        name = "region_confirmed",
        params = mapOf(AnalyticsParam.REGION_ID to regionId),
    )

    public data class TimingLoadFailed(val error: String?) : AnalyticsEvent(
        name = "timing_load_failed",
        params = mapOf(AnalyticsParam.ERROR to error.orUnknown()),
    )

    public data object UserRegistered : AnalyticsEvent(name = "user_registered")

    public data class UserRegisterFailed(val error: String?) : AnalyticsEvent(
        name = "user_register_failed",
        params = mapOf(AnalyticsParam.ERROR to error.orUnknown()),
    )

    public data object MessagingIdUpdated : AnalyticsEvent(name = "messaging_id_updated")

    public data class MessagingIdUpdateFailed(val error: String?) : AnalyticsEvent(
        name = "messaging_id_update_failed",
        params = mapOf(AnalyticsParam.ERROR to error.orUnknown()),
    )

    public data object PushTokenRefreshed : AnalyticsEvent(name = "push_token_refreshed")

    public data class PushNotificationReceived(val hasImage: Boolean) : AnalyticsEvent(
        name = "push_notification_received",
        params = mapOf(AnalyticsParam.HAS_IMAGE to hasImage),
    )

    public data object InAppReviewRequested : AnalyticsEvent(name = "in_app_review_requested")

    public data object SettingsRegionClicked : AnalyticsEvent(name = "settings_region_clicked")

    public data object SettingsLanguageClicked : AnalyticsEvent(name = "settings_language_clicked")

    public data object TasbihAddDialogShown : AnalyticsEvent(name = "tasbih_add_dialog_shown")

    /**
     * A dhikr was created. The name the user typed is deliberately *not* reported — it is free
     * text that can hold anything personal, so only its length is.
     */
    public data class TasbihAdded(val nameLength: Int) : AnalyticsEvent(
        name = "tasbih_added",
        params = mapOf(AnalyticsParam.NAME_LENGTH to nameLength),
    )

    public data class TasbihOpened(val tasbihId: Long) : AnalyticsEvent(
        name = "tasbih_opened",
        params = mapOf(AnalyticsParam.TASBIH_ID to tasbihId),
    )

    /**
     * A counting session crossed a round number. Single taps are far too frequent to report,
     * so only milestones are.
     */
    public data class TasbihMilestoneReached(val tasbihId: Long, val count: Int) : AnalyticsEvent(
        name = "tasbih_milestone_reached",
        params = mapOf(AnalyticsParam.TASBIH_ID to tasbihId, AnalyticsParam.COUNT to count),
    )

    public data class TasbihReset(val tasbihId: Long, val count: Int) : AnalyticsEvent(
        name = "tasbih_reset",
        params = mapOf(AnalyticsParam.TASBIH_ID to tasbihId, AnalyticsParam.COUNT to count),
    )

    public data class TasbihHapticToggled(val enabled: Boolean) : AnalyticsEvent(
        name = "tasbih_haptic_toggled",
        params = mapOf(AnalyticsParam.ENABLED to enabled),
    )

    public data object TasbihHistoryOpened : AnalyticsEvent(name = "tasbih_history_opened")

    public data class TasbihEntryHistoryOpened(val tasbihId: Long) : AnalyticsEvent(
        name = "tasbih_entry_history_opened",
        params = mapOf(AnalyticsParam.TASBIH_ID to tasbihId),
    )

    public data class InAppUpdateAvailable(val updateType: String) : AnalyticsEvent(
        name = "in_app_update_available",
        params = mapOf(AnalyticsParam.UPDATE_TYPE to updateType),
    )

    public data class InAppUpdateStarted(val updateType: String) : AnalyticsEvent(
        name = "in_app_update_started",
        params = mapOf(AnalyticsParam.UPDATE_TYPE to updateType),
    )

    public data object InAppUpdateDownloaded : AnalyticsEvent(name = "in_app_update_downloaded")

    public data object InAppUpdateCompleted : AnalyticsEvent(name = "in_app_update_completed")

    public data class InAppUpdateCancelled(val resultCode: Int) : AnalyticsEvent(
        name = "in_app_update_cancelled",
        params = mapOf(AnalyticsParam.RESULT_CODE to resultCode),
    )

    /**
     * A download or install actually broke. Play merely having no update info is the normal state
     * off the Store and is not reported.
     */
    public data class InAppUpdateFailed(val error: String?) : AnalyticsEvent(
        name = "in_app_update_failed",
        params = mapOf(AnalyticsParam.ERROR to error.orUnknown()),
    )
}

private const val UNKNOWN = "unknown"

private fun String?.orUnknown(): String = if (isNullOrBlank()) UNKNOWN else this
