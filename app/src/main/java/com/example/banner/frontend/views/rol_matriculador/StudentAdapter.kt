package com.example.banner.frontend.views.rol_matriculador
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.R

class StudentAdapter(
    private var originalList: List<Student_>,
    private val onEnrollClick: (Student_) -> Unit,
    private val onClick: (Student_) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var filteredList: List<Student_> = originalList

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.textViewStudentName)
        private val emailText: TextView = itemView.findViewById(R.id.textViewStudentEmail)
        private val enrollButton: Button = itemView.findViewById(R.id.btnEnroll)

        fun bind(student: Student_) {
            nameText.text = student.name
            emailText.text = student.email

            itemView.setOnClickListener { onClick(student) }

            // Configuración del botón de matrícula
            enrollButton.setOnClickListener {
                onEnrollClick(student)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
            .let { StudentViewHolder(it) }
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateData(newData: List<Student_>) {
        originalList = newData
        filteredList = newData
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.name.contains(query, true) || it.email.contains(query, true)
            }
        }
        notifyDataSetChanged()
    }
}