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
        val spinnerDosage = findViewById<Spinner>(R.id.spinner_dosage)

        val buttonCreate = findViewById<Button>(R.id.button_create)
        val pickerTime = findViewById<TimePicker>(R.id.picker_time)

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.dosage_units,
            android.R.layout.simple_spinner_item

        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner.
            spinnerDosage.adapter = adapter
        }

        buttonCreate.setOnClickListener {
            val replyIntent = Intent()

            // Cancel when dosage or name is empty.
            if (TextUtils.isEmpty(editName.text) || TextUtils.isEmpty(editDosage.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)

            } else {
                replyIntent.putExtra("MEDICINE_NAME", editName.text.toString())
                replyIntent.putExtra("MEDICINE_DOSAGE", editDosage.text.toString())
                replyIntent.putExtra("MEDICINE_HOUR", pickerTime.hour.toString())
                replyIntent.putExtra("MEDICINE_MINUTE", pickerTime.minute.toString())
                replyIntent.putExtra("MEDICINE_DOSAGE_UNIT", spinnerDosage.selectedItem.toString())

                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }
}