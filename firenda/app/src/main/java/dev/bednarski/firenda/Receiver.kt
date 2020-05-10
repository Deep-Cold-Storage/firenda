package dev.bednarski.firenda

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Receiver : BroadcastReceiver() {
    val NOTIFICATION_CHANNEL_ID = "dev.bednarski.firenda.NOTIFICATIONS"
    val SHARED_PREFERENCES = "dev.bednarski.firenda.PREFERENCES"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPref: SharedPreferences =
                context!!.getSharedPreferences(SHARED_PREFERENCES, 0)
            val editor = sharedPref.edit()
            editor.putBoolean("IS_REBOOT", true)
            editor.apply()

            var builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_favorite_24dp)
                .setContentTitle("Firenda")
                .setContentText(context.getString(R.string.msg_notification_attention))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }

        } else {
            val name = intent!!.getStringExtra("NOTIFICATION_MEDICINE_NAME")
            val dosageUnit = intent!!.getStringExtra("NOTIFICATION_MEDICINE_UNIT")

            var builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_favorite_24dp)
                .setContentTitle("Firenda")
                .setContentText(context.getString(R.string.msg_notification) + " " + name + " " + dosageUnit + "!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        }
    }
}