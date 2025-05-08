package com.example.banner.frontend.views.course
import Course_
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

class RecyclerAdapter4(
    private val cursos: MutableList<Course_>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter4.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.course_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(cursos[position], context)
    }

    override fun getItemCount(): Int = cursos.size

    fun getItem(position: Int): Course_ = cursos[position]

    fun removeItem(position: Int) {
        cursos.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int, updatedCourse: Course_) {
        cursos[position] = updatedCourse
        notifyItemChanged(position)
    }

    fun restoreItem(item: Course_, position: Int) {
        cursos.add(position, item)
        notifyItemInserted(position)
    }

    fun updateData(newData: List<Course_>) {
        cursos.clear()
        cursos.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textCod: TextView = view.findViewById(R.id.text_cod)
        private val textName: TextView = view.findViewById(R.id.text_name)
        private val textCredits: TextView = view.findViewById(R.id.text_credits)
        private val textHours: TextView = view.findViewById(R.id.text_hours)
        private val textCiclo: TextView = view.findViewById(R.id.text_ciclo)
        private val textCareer: TextView = view.findViewById(R.id.text_career)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)

        fun bind(course: Course_, context: Context) {
            textCod.text = "Code: ${course.cod}"
            textName.text = course.name
            textCredits.text = "Credits: ${course.credits}"
            textHours.text = "Hours: ${course.hours}"
            textCiclo.text = "Semester ID: ${course.cicloId}"
            textCareer.text = "Career code: ${course.careerCod}"

            // Click en el ítem completo
            itemView.setOnClickListener {
                Toast.makeText(context, "Curso: ${course.name}", Toast.LENGTH_SHORT).show()
            }

            // Botón de eliminar
            btnDelete.setOnClickListener {
                if (context is Course) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteCourse(position)
                    }
                }
            }

            // Botón de editar (AÑADIDO)
            btnEdit.setOnClickListener {
                if (context is Course) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.editCourse(position)
                    }
                }
            }
        }
    }
}