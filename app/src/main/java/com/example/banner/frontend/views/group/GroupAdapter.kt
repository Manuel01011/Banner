package com.example.banner.frontend.views.group
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.R

class RecyclerAdapter6(
    private val grupos: MutableList<Grupo_>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter6.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.group_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(grupos[position], context)
    }

    override fun getItemCount(): Int = grupos.size

    fun getItem(position: Int): Grupo_ = grupos[position]

    fun removeItem(position: Int) {
        grupos.removeAt(position)
        notifyItemRemoved(position)
    }


    fun updateData(newData: List<Grupo_>) {
        grupos.clear()
        grupos.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textId: TextView = view.findViewById(R.id.text_id)
        private val textNumber: TextView = view.findViewById(R.id.text_number)
        private val textYear: TextView = view.findViewById(R.id.text_year)
        private val textSchedule: TextView = view.findViewById(R.id.text_schedule)
        private val textCourse: TextView = view.findViewById(R.id.text_course)
        private val textTeacher: TextView = view.findViewById(R.id.text_teacher)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)

        fun bind(grupo: Grupo_, context: Context) {
            textId.text = "ID: ${grupo.id}"
            textNumber.text = "Group number: ${grupo.numberGroup}"
            textYear.text = "Year: ${grupo.year}"
            textSchedule.text = "Schedule: ${grupo.horario}"
            textCourse.text = "Cod: ${grupo.courseCod}"
            textTeacher.text = "ID: ${grupo.teacherId}"

            itemView.setOnClickListener {
                Toast.makeText(context, "Group ${grupo.numberGroup} - ${grupo.horario}", Toast.LENGTH_SHORT).show()
            }

            btnEdit.setOnClickListener {
                if (context is Group) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.editGroup(position)
                    }
                }
            }
            btnDelete.setOnClickListener {
                if (context is Group) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteGrupo(position)
                    }
                }
            }
        }
    }
}