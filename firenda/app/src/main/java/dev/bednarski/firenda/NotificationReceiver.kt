package dev.bednarski.firenda

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationReceiver : BroadcastReceiver() {
    private val NOTIFICATION_CHANNEL_ID = "dev.bednarski.firenda.NOTIFICATIONS"

    override fun onReceive(context: Context?, intent: Intent?) {
        val name = intent!!.getStringExtra("NOTIFICATION_MEDICINE_NAME")
        val dosageUnit = intent.getStringExtra("NOTIFICATION_MEDICINE_UNIT")

        // Create MainActivity intent for notification.
        val intent = Intent(context, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            context, 1000, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite_24dp)
            .setContentTitle("Firenda")
            .setContentText(context.getString(R.string.msg_notification) + " " + name + " " + dosageUnit.toLowerCase() + "!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}