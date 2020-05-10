package dev.bednarski.firenda

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val newMedicineActivityRequestCode = 1

    lateinit var alarmManager: AlarmManager
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        context = this

        val medicineViewModel = ViewModelProvider(this).get(MedicineViewModel::class.java)

        // Reset DB logic
        val sharedPref: SharedPreferences = getSharedPreferences("FIRENDA_PREFERENCES", 0)

        val currentDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        if (sharedPref.getString("FIRENDA_LAST_OPENED", "0") != currentDate) {
            val editor = sharedPref.edit()
            editor.putString("FIRENDA_LAST_OPENED", currentDate)
            editor.apply()

            medicineViewModel.reset()
        }


        val deleteOnClick: (Int) -> Unit = { id ->
            medicineViewModel.delete(id)
            Toast.makeText(this, "ID: $id. item deleted!", Toast.LENGTH_SHORT).show()

            // Set up notification
            val intent = Intent(this, Receiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                id.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            alarmManager.cancel(pendingIntent)
            Log.e("NOTIFICATION", "Notification deleted!")

        }

        val toggleOnClick: (Int) -> Unit = { id ->
            medicineViewModel.toggle(id)
            Toast.makeText(this, "ID: $id item toggled!", Toast.LENGTH_SHORT).show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = MedicineListAdapter(
            this,
            deleteClickListener = deleteOnClick,
            toggleClickListener = toggleOnClick
        )

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        medicineViewModel.allMedicines.observe(this, Observer { medicines ->

            // Update the cached copy of the words in the adapter.
            medicines?.let { adapter.setMedicines(it) }

            // Fix alarms after reboot
            if (sharedPref.getBoolean("FIRENDA_BOOT", false)) {

                val editor = sharedPref.edit()
                editor.putBoolean("FIRENDA_BOOT", false)
                editor.apply()
                Log.e("BOOT", "Fix boot flag to false!")


                for (medicine in medicines) {
                    // Set up notification

                    val intent = Intent(this, Receiver::class.java)
                    intent.putExtra("NOTIFICATION_MEDICINE_NAME", medicine.name)
                    intent.putExtra("NOTIFICATION_MEDICINE_UNIT", medicine.dosageUnit)

                    val pendingIntent = PendingIntent.getBroadcast(
                        this,
                        medicine.id.toInt(),
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )

                    val calendar: Calendar = Calendar.getInstance()

                    calendar.set(Calendar.HOUR_OF_DAY, medicine.hour.toInt());
                    calendar.set(Calendar.MINUTE, medicine.minute.toInt());

                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )

                    Log.e("BOOTFIX", "Fix notification for " + medicine.name)
                    Log.e("NOTIFICATION", "Notification set! " + calendar)
                }
            }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewMedicineActivity::class.java)
            startActivityForResult(intent, newMedicineActivityRequestCode)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val CHANNEL_ID = "NOTIFICATIONS_FIRENDA"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, "Notifications", importance).apply {
                description = "Send notification!"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newMedicineActivityRequestCode && resultCode == Activity.RESULT_OK) {

            val name = data!!.getStringExtra("MEDICINE_NAME")
            val dosage = data!!.getStringExtra("MEDICINE_DOSAGE")

            val timeHour = data!!.getStringExtra("MEDICINE_HOUR")
            val timeMinute = data!!.getStringExtra("MEDICINE_MINUTE")
            val dosageUnit = data!!.getStringExtra("MEDICINE_DOSAGE_UNIT")

            val medicine =
                Medicine(
                    id = 0,
                    name = name,
                    dosage = dosage,
                    hour = timeHour,
                    minute = timeMinute,
                    takenToday = false,
                    dosageUnit = dosageUnit
                )

            val medicineViewModel = ViewModelProvider(this).get(MedicineViewModel::class.java)

            val id = medicineViewModel.insert(medicine)

            // Set up notification
            val intent = Intent(this, Receiver::class.java)
            intent.putExtra("NOTIFICATION_MEDICINE_NAME", name)
            intent.putExtra("NOTIFICATION_MEDICINE_UNIT", dosageUnit)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                id.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            val calendar: Calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, timeHour.toInt());
            calendar.set(Calendar.MINUTE, timeMinute.toInt());

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Log.e("NOTIFICATION", "Notification set! " + calendar)

        } else {
            Toast.makeText(
                applicationContext,
                R.string.error_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val CHANNEL_ID = "NOTIFICATIONS_FIRENDA"

            Log.e("NOTIFICATION", "Recieved notification!")

            val name = intent!!.getStringExtra("NOTIFICATION_MEDICINE_NAME")
            val dosageUnit = intent!!.getStringExtra("NOTIFICATION_MEDICINE_UNIT")

            var builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_favorite_24dp)
                .setContentTitle("Firenda")
                .setContentText("Take your " + name + " " + dosageUnit + "!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }
        }
    }

    class BootBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) { // Do your work related to alarm manager

                val sharedPref: SharedPreferences =
                    context.getSharedPreferences("FIRENDA_PREFERENCES", 0)
                val editor = sharedPref.edit()
                editor.putBoolean("FIRENDA_BOOT", true)
                editor.apply()
                Log.e("BOOT", "Set boot flag!")


                val CHANNEL_ID = "NOTIFICATIONS_FIRENDA"
                var builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_favorite_24dp)
                    .setContentTitle("Firenda")
                    .setContentText("Firenda needs your attention!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(context)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(1, builder.build())
                }

            }
        }
    }

}
