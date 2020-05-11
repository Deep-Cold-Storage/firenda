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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        context = this

        sharedPref = getSharedPreferences(SHARED_PREFERENCES, 0)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        medicineViewModel = ViewModelProvider(this).get(MedicineViewModel::class.java)

        resetMedicineStatus()

        GlobalScope.launch {
            createNotificationChannel()
            rescheduleNotifications()
        }

        // RecycleView item on delete callback.
        val deleteOnClick: (Int) -> Unit = { id ->
            medicineViewModel.delete(id)
            Snackbar.make(
                findViewById(R.id.main_layout),
                R.string.msg_deleted,
                Snackbar.LENGTH_LONG
            ).show()

            // Cancel reacquiring notification for deleted medicine.
            val intent = Intent(this, NotificationReceiver::class.java)
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

        // Setup LiveData observer.
        medicineViewModel.allMedicines.observe(this, Observer { medicines ->
            // Update the cached copy of the words in the adapter.
            medicines?.let { adapter.setMedicines(it) }
        })

        // Setup floating button action.
        val floatingButton = findViewById<FloatingActionButton>(R.id.button_floating)
        floatingButton.setOnClickListener {
            val intent = Intent(this@MainActivity, NewMedicineActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    // Reset all Medicine status after first launch of the day.
    // Store LAST_OPEN date in shared preferences.
    private fun resetMedicineStatus() {
        val currentDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        if (sharedPref.getString("LAST_LAUNCHED", "0") != currentDate) {
            medicineViewModel.reset()
            sharedPref.edit().putString("LAST_LAUNCHED", currentDate).apply()
        }
    }

    // Create app notification channel only for API 26+
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.hint_notification_title),
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

    // Schedule reminder notification for Medicine.
    private fun scheduleNotification(medicine: Medicine) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("NOTIFICATION_MEDICINE_NAME", medicine.name)
        intent.putExtra("NOTIFICATION_MEDICINE_UNIT", medicine.dosageUnit)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicine.id,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val calendar: Calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, medicine.hour.toInt())
        calendar.set(Calendar.MINUTE, medicine.minute.toInt())
        calendar.set(Calendar.SECOND, 0)

        // Check for notifications in the past.
        val alarmTime: Long = if (calendar.timeInMillis <= Calendar.getInstance().timeInMillis) {
            calendar.timeInMillis + (AlarmManager.INTERVAL_DAY + 1)
        } else {
            calendar.timeInMillis
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // Reschedule notifications in non-blocking way.
    private suspend fun rescheduleNotifications() {
        val medicines = AppDatabase.getDatabase(application).medicineDao().getAllMedicines()

        for (medicine in medicines) {
            scheduleNotification(medicine)
        }
    }

    // NewMedicineActivity callback.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val medicine = Medicine(
                id = 0,
                name = data!!.getStringExtra("MEDICINE_NAME"),
                dosage = data.getStringExtra("MEDICINE_DOSAGE"),
                hour = data.getStringExtra("MEDICINE_HOUR"),
                minute = data.getStringExtra("MEDICINE_MINUTE"),
                dosageUnit = data.getStringExtra("MEDICINE_DOSAGE_UNIT"),
                status = false
            )

            // Insert new Medicine and schedule notification.
            GlobalScope.launch {
                medicine.id = medicineViewModel.insert(medicine).toInt()
                scheduleNotification(medicine)
            }

        } else {
            Snackbar.make(
                findViewById(R.id.main_layout),
                getString(R.string.error_not_saved),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
