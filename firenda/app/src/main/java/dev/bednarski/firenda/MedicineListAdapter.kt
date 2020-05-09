package dev.bednarski.firenda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MedicineListAdapter internal constructor(
    context: Context, private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<MedicineListAdapter.MedicineViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    // Cached copy of medicines.
    private var medicines = emptyList<Medicine>()


    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineItemName: TextView = itemView.findViewById(R.id.textName)
        val medicineItemDosage: TextView = itemView.findViewById(R.id.textDosage)
        val medicineItemTime: TextView = itemView.findViewById(R.id.textTime)

        val medicineDeleteButton: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView = inflater.inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val current = medicines[position]
        holder.medicineItemName.text = current.name
        holder.medicineItemTime.text = "Every day at " + current.hour + ":" + current.minute
        holder.medicineItemDosage.text = "Dosage: " + current.dosage

        holder.medicineDeleteButton.setOnClickListener { itemClickListener(current.id) }

    }

    internal fun setMedicines(medicines: List<Medicine>) {
        this.medicines = medicines
        notifyDataSetChanged()
    }

    override fun getItemCount() = medicines.size
}
