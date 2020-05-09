package dev.bednarski.firenda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val newMedicineActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val medicineViewModel = ViewModelProvider(this).get(MedicineViewModel::class.java)

        val deleteOnClick: (Int) -> Unit = { id ->
            medicineViewModel.delete(id)
            Toast.makeText(this, "ID: $id. item deleted!", Toast.LENGTH_SHORT).show()
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



        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        medicineViewModel.allMedicines.observe(this, Observer { medicines ->

            // Update the cached copy of the words in the adapter.
            medicines?.let { adapter.setMedicines(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewMedicineActivity::class.java)
            startActivityForResult(intent, newMedicineActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newMedicineActivityRequestCode && resultCode == Activity.RESULT_OK) {

            val name = data!!.getStringExtra("MEDICINE_NAME")
            val dosage = data!!.getStringExtra("MEDICINE_DOSAGE")

            val timeHour = data!!.getStringExtra("MEDICINE_HOUR")
            val timeMinute = data!!.getStringExtra("MEDICINE_MINUTE")

            val medicine =
                Medicine(
                    id = 0,
                    name = name,
                    dosage = dosage,
                    hour = timeHour,
                    minute = timeMinute,
                    takenToday = false
                )

            val medicineViewModel = ViewModelProvider(this).get(MedicineViewModel::class.java)
            medicineViewModel.insert(medicine)

        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

}
