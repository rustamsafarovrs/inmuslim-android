package tj.rsdevteam.inmuslim.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Created by Rustam Safarov on 15/08/23.
 * github.com/rustamsafarovrs
 */

object Utils {

    fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}, Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT}"
    }

    fun openLanguageSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        } else {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            context.startActivity(intent)
        }
    }
}
