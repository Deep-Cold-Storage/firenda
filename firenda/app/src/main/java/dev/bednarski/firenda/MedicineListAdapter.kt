package dev.bednarski.firenda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView


class MedicineListAdapter internal constructor(
    context: Context,
    private val deleteClickListener: (Int) -> Unit,
    private val toggleClickListener: (Int) -> Unit
) : RecyclerView.Adapter<MedicineListAdapter.MedicineViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    // Cached copy of all Medicines.
    private var medicines = emptyList<Medicine>()


    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineItemName: TextView = itemView.findViewById(R.id.text_name)
        val medicineItemHint: TextView = itemView.findViewById(R.id.text_hint)

        val medicineDeleteButton: Button = itemView.findViewById(R.id.button_delete)
        val medicineStatusButton: ToggleButton = itemView.findViewById(R.id.button_toggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView = inflater.inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val current = medicines[position]

        holder.medicineItemName.text = current.name
        holder.medicineItemHint.text =
            "Take ${current.dosage} ${current.dosageUnit.toLowerCase()} at ${current.hour.padStart(
                2,
                '0'
            )}:${current.minute.padStart(2, '0')} today."
        holder.medicineStatusButton.setChecked(current.status)

        holder.medicineDeleteButton.setOnClickListener { deleteClickListener(current.id) }
        holder.medicineStatusButton.setOnClickListener { toggleClickListener(current.id) }
    }

    internal fun setMedicines(medicines: List<Medicine>) {
        this.medicines = medicines
        notifyDataSetChanged()
    }

    override fun getItemCount() = medicines.size
}
