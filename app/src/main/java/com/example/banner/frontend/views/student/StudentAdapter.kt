package com.example.banner.frontend.views.student
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.R

class RecyclerAdapter5(
    private val estudiantes: MutableList<Student_>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter5.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.student_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(estudiantes[position], context)
    }

    override fun getItemCount(): Int = estudiantes.size

    fun getItem(position: Int): Student_ = estudiantes[position]

    fun removeItem(position: Int) {
        estudiantes.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Student_, position: Int) {
        estudiantes.add(position, item)
        notifyItemInserted(position)
    }

    fun updateData(newData: List<Student_>) {
        estudiantes.clear()
        estudiantes.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textId: TextView = view.findViewById(R.id.text_id)
        private val textName: TextView = view.findViewById(R.id.text_name)
        private val textTel: TextView = view.findViewById(R.id.text_tel)
        private val textEmail: TextView = view.findViewById(R.id.text_email)
        private val textBorn: TextView = view.findViewById(R.id.text_born)
        private val textCareer: TextView = view.findViewById(R.id.text_career)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        private val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)


        fun bind(student: Student_, context: Context) {
            textId.text = "ID: ${student.id}"
            textName.text = "Nombre: ${student.name}"
            textTel.text = "Teléfono: ${student.telNumber}"
            textEmail.text = "Email: ${student.email}"
            textBorn.text = "Fecha de nacimiento: ${student.bornDate}"
            textCareer.text = "Código de carrera: ${student.careerCod}"



            itemView.setOnClickListener {
                Toast.makeText(context, "Estudiante: ${student.name}", Toast.LENGTH_SHORT).show()
            }
            btnEdit.setOnClickListener {
                if (context is Student) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.editStudent(position)
                    }
                }
            }

            btnDelete.setOnClickListener {
                if (context is Student) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteStudent(position)
                    }
                }
            }
        }
    }
}