package tj.rsdevteam.inmuslim.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.Request
import tj.rsdevteam.inmuslim.core.Const
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.ui.MainActivity
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferences: Preferences

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null &&
            remoteMessage.notification!!.title != null &&
            remoteMessage.notification!!.body != null
        ) {
            sendNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!,
                remoteMessage,
            )
        }
    }

    private fun sendNotification(title: String, body: String, remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.common_common_push_notifications)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(tj.rsdevteam.inmuslim.R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Push-notification",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }
        if (remoteMessage.notification!!.imageUrl != null) {
            val bitmap = getBitmapFromUrl(remoteMessage.notification!!.imageUrl!!.toString())
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap),
            ).setLargeIcon(bitmap)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(imageUrl)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                return BitmapFactory.decodeStream(response.body.byteStream())
            } else {
                Log.e(Const.LOGCAT, "Error in getting notification image, code=" + response.code)
            }
        } catch (e: IOException) {
            Log.e(Const.LOGCAT, "Error in getting notification image, message=" + e.message)
        }
        return null
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if (this::preferences.isInitialized) {
            if (preferences.getFirebaseToken() != token) {
                preferences.saveFirebaseToken("")
            }
        }
        Log.i(Const.LOGCAT, token)
    }
}
