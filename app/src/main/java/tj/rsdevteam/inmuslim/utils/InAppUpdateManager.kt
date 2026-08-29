package tj.rsdevteam.inmuslim.utils

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.AnalyticsTracker
import tj.rsdevteam.inmuslim.core.Const

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 *
 * Wraps the Google Play in-app update flow. Urgent updates (high priority or a long stale client)
 * are installed with the immediate flow, everything else is downloaded in the background with the
 * flexible flow and installed once the user confirms.
 */
class InAppUpdateManager(
    activity: ComponentActivity,
    private val analytics: AnalyticsTracker,
) : DefaultLifecycleObserver {

    private val appUpdateManager = AppUpdateManagerFactory.create(activity)

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                analytics.log(AnalyticsEvent.InAppUpdateCancelled(result.resultCode))
                Log.i(Const.LOGCAT, "In-app update flow finished with resultCode=${result.resultCode}")
            }
        }

    private val installListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                analytics.log(AnalyticsEvent.InAppUpdateDownloaded)
                isUpdateDownloaded = true
            }

            InstallStatus.FAILED -> analytics.log(AnalyticsEvent.InAppUpdateFailed("code=${state.installErrorCode()}"))

            else -> Unit
        }
    }

    /** True once a flexible update has been downloaded and is only waiting to be installed. */
    var isUpdateDownloaded by mutableStateOf(false)
        private set

    init {
        appUpdateManager.registerListener(installListener)
        activity.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        requestUpdateInfo { info ->
            when {
                info.installStatus() == InstallStatus.DOWNLOADED -> isUpdateDownloaded = true

                info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
                    updateTypeFor(info) == AppUpdateType.IMMEDIATE -> startUpdate(info, AppUpdateType.IMMEDIATE)

                else -> Unit
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        appUpdateManager.unregisterListener(installListener)
    }

    /**
     * Asks Play whether a newer version is available and starts the flow for it.
     * Call it once per cold start so the user is not prompted again after dismissing the dialog.
     */
    fun checkForUpdate() {
        requestUpdateInfo { info ->
            when {
                info.installStatus() == InstallStatus.DOWNLOADED -> isUpdateDownloaded = true

                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE -> {
                    val type = updateTypeFor(info)
                    analytics.log(AnalyticsEvent.InAppUpdateAvailable(analyticsNameOf(type)))
                    startUpdate(info, type)
                }

                else -> Unit
            }
        }
    }

    /** Restarts the app to install an already downloaded flexible update. */
    fun completeUpdate() {
        analytics.log(AnalyticsEvent.InAppUpdateCompleted)
        appUpdateManager.completeUpdate()
    }

    private fun requestUpdateInfo(onInfo: (AppUpdateInfo) -> Unit) {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info -> onInfo(info) }
            // Not an error worth an event: off the Play Store this is simply the normal answer,
            // and onResume asks again on every foreground.
            .addOnFailureListener { e -> Log.i(Const.LOGCAT, "In-app update is not available: ${e.message}") }
    }

    private fun startUpdate(info: AppUpdateInfo, @AppUpdateType type: Int) {
        if (!info.isUpdateTypeAllowed(type)) return
        analytics.log(AnalyticsEvent.InAppUpdateStarted(analyticsNameOf(type)))
        appUpdateManager.startUpdateFlowForResult(info, updateLauncher, AppUpdateOptions.newBuilder(type).build())
    }

    private fun analyticsNameOf(@AppUpdateType type: Int): String {
        return if (type == AppUpdateType.IMMEDIATE) IMMEDIATE_NAME else FLEXIBLE_NAME
    }

    @AppUpdateType
    private fun updateTypeFor(info: AppUpdateInfo): Int {
        val staleDays = info.clientVersionStalenessDays() ?: 0
        val isUrgent = info.updatePriority() >= HIGH_PRIORITY || staleDays >= IMMEDIATE_UPDATE_STALE_DAYS
        return if (isUrgent && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            AppUpdateType.IMMEDIATE
        } else {
            AppUpdateType.FLEXIBLE
        }
    }

    private companion object {

        /** Update priority set in the Play Developer API from which the update is forced. */
        const val HIGH_PRIORITY = 4

        /** Days the update may stay uninstalled before it is forced. */
        const val IMMEDIATE_UPDATE_STALE_DAYS = 14

        const val IMMEDIATE_NAME = "immediate"
        const val FLEXIBLE_NAME = "flexible"
    }
}
