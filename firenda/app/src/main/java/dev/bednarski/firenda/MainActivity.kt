package dev.bednarski.firenda

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var context: Context
    lateinit var alarmManager: AlarmManager
    lateinit var sharedPref: SharedPreferences
    lateinit var medicineViewModel: MedicineViewModel

    val NOTIFICATION_CHANNEL_ID = "dev.bednarski.firenda.NOTIFICATIONS"
    val SHARED_PREFERENCES = "dev.bednarski.firenda.PREFERENCES"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Save MainActivity context for later.
        context = this
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPref = getSharedPreferences(SHARED_PREFERENCES, 0)
        medicineViewModel = ViewModelProvider(this).get(MedicineViewModel::class.java)

        resetMedicineStatus()
        createNotificationChannel()

        // RecycleView item on delete callback.
        val deleteOnClick: (Int) -> Unit = { id ->
            medicineViewModel.delete(id)
            Snackbar.make(
                findViewById(R.id.main_layout),
                R.string.msg_deleted,
                Snackbar.LENGTH_LONG
            ).show()

            // Cancel reacquiring notification for deleted medicine.
            val intent = Intent(this, Receiver::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                this,
                id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }

        // RecycleView item on status toggle callback.
        val toggleOnClick: (Int) -> Unit = { id ->
            medicineViewModel.toggle(id)
            Snackbar.make(
                findViewById(R.id.main_layout),
                R.string.msg_toggled,
                Snackbar.LENGTH_SHORT
            ).show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = MedicineListAdapter(
            this,
            deleteClickListener = deleteOnClick,
            toggleClickListener = toggleOnClick
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup Room observer.
        medicineViewModel.allMedicines.observe(this, Observer { medicines ->

            // Update the cached copy of the words in the adapter.
            medicines?.let { adapter.setMedicines(it) }

            // Reschedule alarms after reboot.
            if (sharedPref.getBoolean("IS_REBOOT", false)) {

                val editor = sharedPref.edit()
                editor.putBoolean("IS_REBOOT", false)
                editor.apply()


                for (medicine in medicines) {

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
                }
            }
        })

        // Setup floating button action.
        val floatingButton = findViewById<FloatingActionButton>(R.id.button_floating)

        floatingButton.setOnClickListener {
            val intent = Intent(this@MainActivity, NewMedicineActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    // Resets medicine status after first launch of the day.
    private fun resetMedicineStatus() {
        val currentDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        if (sharedPref.getString("LAST_LAUNCHED", "0") != currentDate) {
            medicineViewModel.reset()

            sharedPref.edit().putString("LAST_LAUNCHED", currentDate).apply()
        }
    }

    // Creates app notification channel, but only on API 26+
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                R.string.hint_notification_title.toString(),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = R.string.hint_notification_description.toString()
            }

            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val name = intent!!.getStringExtra("MEDICINE_NAME")
            val dosage = intent.getStringExtra("MEDICINE_DOSAGE")

            val timeHour = intent.getStringExtra("MEDICINE_HOUR")
            val timeMinute = intent.getStringExtra("MEDICINE_MINUTE")
            val dosageUnit = intent.getStringExtra("MEDICINE_DOSAGE_UNIT")

            val medicine =
                Medicine(
                    id = 0,
                    name = name,
                    dosage = dosage,
                    hour = timeHour,
                    minute = timeMinute,
                    status = false,
                    dosageUnit = dosageUnit
                )

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

        } else {
            Snackbar.make(
                findViewById(R.id.main_layout),
                R.string.error_not_saved,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
