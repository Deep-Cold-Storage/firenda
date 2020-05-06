package dev.bednarski.firenda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MedicineListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<MedicineListAdapter.MedicineViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    // Cached copy of medicines.
    private var medicines = emptyList<Medicine>()

    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView = inflater.inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val current = medicines[position]
        holder.medicineItemView.text = current.name
    }

    internal fun setWords(medicines: List<Medicine>) {
        this.medicines = medicines
        notifyDataSetChanged()
    }

    override fun getItemCount() = medicines.size
}