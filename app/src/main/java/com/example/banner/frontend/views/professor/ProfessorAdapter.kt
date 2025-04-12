package com.example.banner.frontend.views.professor
import Teacher_
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.banner.R

class RecyclerAdapter3(
    private val teachers: MutableList<Teacher_>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter3.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.teacher_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(teachers[position], context)
    }

    override fun getItemCount(): Int = teachers.size

    fun getItem(position: Int): Teacher_ = teachers[position]

    fun removeItem(position: Int) {
        teachers.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Teacher_, position: Int) {
        teachers.add(position, item)
        notifyItemInserted(position)
    }

    fun updateData(newData: List<Teacher_>) {
        teachers.clear()
        teachers.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById(R.id.text_name)
        private val textTelNumber: TextView = view.findViewById(R.id.text_tel_number)
        private val textEmail: TextView = view.findViewById(R.id.text_email)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)

        fun bind(teacher: Teacher_, context: Context) {
            // Establecer los valores de los TextViews con los datos del Teacher_
            textName.text = teacher.name
            textTelNumber.text = "Teléfono: ${teacher.telNumber}"
            textEmail.text = "Email: ${teacher.email}"

            // Configurar el listener de clic en el ítem
            itemView.setOnClickListener {
                // Muestra un Toast con el ID del Teacher_
                Toast.makeText(context, "Teacher ID: ${teacher.id}", Toast.LENGTH_SHORT).show()
            }

            btnDelete.setOnClickListener {
                if (context is Professor) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteProfessor(position)
                    }
                }
            }
        }
    }
}