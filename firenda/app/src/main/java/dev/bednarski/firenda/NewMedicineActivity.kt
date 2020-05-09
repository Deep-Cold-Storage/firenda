package dev.bednarski.firenda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity

class NewMedicineActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_medicine)

        val editName = findViewById<EditText>(R.id.edit_name)
        val editDosage = findViewById<EditText>(R.id.edit_dosage)
        val timePicker = findViewById<TimePicker>(R.id.time_picker)

        val button = findViewById<Button>(R.id.button_save)

        button.setOnClickListener {
            val replyIntent = Intent()

            if (TextUtils.isEmpty(editName.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val name = editName.text.toString()
                val dosage = editDosage.text.toString()

                val timeHour = timePicker.hour.toString()
                val timeMinute = timePicker.minute.toString()

                replyIntent.putExtra("MEDICINE_NAME", name)
                replyIntent.putExtra("MEDICINE_DOSAGE", dosage)
                replyIntent.putExtra("MEDICINE_HOUR", timeHour)
                replyIntent.putExtra("MEDICINE_MINUTE", timeMinute)

                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }
}