package com.example.banner.frontend.views.cicle
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Ciclo_
import com.example.banner.R

class RecyclerAdapter2(
    private val ciclos: MutableList<Ciclo_>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.semester_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(ciclos[position], context)
    }

    override fun getItemCount(): Int = ciclos.size

    fun getItem(position: Int): Ciclo_ = ciclos[position]

    fun removeItem(position: Int) {
        ciclos.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int,item: Ciclo_) {
        ciclos.set(position,item)
        notifyItemChanged(position)
    }
    fun restoreItem(item: Ciclo_, position: Int) {
        ciclos.add(position, item)
        notifyItemInserted(position)
    }

    fun updateData(newData: List<Ciclo_>) {
        ciclos.clear()
        ciclos.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle: TextView = view.findViewById(R.id.text_title)
        private val textDateRange: TextView = view.findViewById(R.id.text_date_range)
        private val textStatus: TextView = view.findViewById(R.id.text_status)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        private val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)

        fun bind(ciclo: Ciclo_, context: Context) {
            textTitle.text = "Semester ${ciclo.number} - ${ciclo.year}"
            textDateRange.text = "Del ${ciclo.dateStart} al ${ciclo.dateFinish}"
            textStatus.text = if (ciclo.is_active) "Active" else "Inactive"
            textStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (ciclo.is_active) android.R.color.holo_green_dark else android.R.color.darker_gray
                )
            )

            itemView.setOnClickListener {
                Toast.makeText(context, "Semester ID: ${ciclo.id}", Toast.LENGTH_SHORT).show()
            }

            btnDelete.setOnClickListener {
                // Lógica para eliminar desde el adaptador
                if (context is Semester) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteCiclo(position)
                    }
                }
            }
            btnEdit.setOnClickListener {
                if (context is Semester) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.editCiclo(position)
                    }
                }
            }
        }
    }
}