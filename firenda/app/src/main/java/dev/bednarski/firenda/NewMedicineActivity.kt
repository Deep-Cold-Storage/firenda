package dev.bednarski.firenda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class NewMedicineActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_medicine)

        val editName = findViewById<EditText>(R.id.edit_name)
        val editDosage = findViewById<EditText>(R.id.edit_dosage)
        val dosageSpinner = findViewById<Spinner>(R.id.dosageSpinner)

        val timePicker = findViewById<TimePicker>(R.id.time_picker)

        val button = findViewById<Button>(R.id.button_save)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            dosageSpinner.adapter = adapter
        }

        button.setOnClickListener {
            val replyIntent = Intent()

            if (TextUtils.isEmpty(editName.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val name = editName.text.toString()
                val dosage = editDosage.text.toString()
                val dosageUnit = dosageSpinner.getSelectedItem().toString()

                val timeHour = timePicker.hour.toString()
                val timeMinute = timePicker.minute.toString()

                replyIntent.putExtra("MEDICINE_NAME", name)
                replyIntent.putExtra("MEDICINE_DOSAGE", dosage)
                replyIntent.putExtra("MEDICINE_HOUR", timeHour)
                replyIntent.putExtra("MEDICINE_MINUTE", timeMinute)
                replyIntent.putExtra("MEDICINE_DOSAGE_UNIT", dosageUnit)

                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }
}