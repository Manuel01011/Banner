package com.example.banner.frontend.views.enrollment
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.banner.R

class RecyclerAdapter7(
    private val matriculas: MutableList<Enrollment_>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter7.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.enrollemt_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(matriculas[position], context)
    }

    override fun getItemCount(): Int = matriculas.size

    fun getItem(position: Int): Enrollment_ = matriculas[position]

    fun removeItem(position: Int) {
        matriculas.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Enrollment_, position: Int) {
        matriculas.add(position, item)
        notifyItemInserted(position)
    }

    fun updateData(newData: List<Enrollment_>) {
        matriculas.clear()
        matriculas.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textStudentId: TextView = view.findViewById(R.id.text_student_id)
        private val textGroupId: TextView = view.findViewById(R.id.text_group_id)
        private val textGrade: TextView = view.findViewById(R.id.text_grade)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)

        fun bind(enrollment: Enrollment_, context: Context) {
            textStudentId.text = "ID Student: ${enrollment.studentId}"
            textGroupId.text = "ID Group: ${enrollment.grupoId}"
            textGrade.text = "Grade: ${enrollment.grade}"

            itemView.setOnClickListener {
                Toast.makeText(context, "Student Enrollment ID: ${enrollment.studentId}", Toast.LENGTH_SHORT).show()
            }

            btnEdit.setOnClickListener {
                if (context is Enrollment) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.editEnrollment(position)
                    }
                }
            }

            btnDelete.setOnClickListener {
                if (context is Enrollment) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteEnrollment(position)
                    }
                }
            }
        }
    }
}