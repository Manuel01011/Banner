package com.example.banner.frontend.views.rol_student
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.banner.R

class StudentHistoryAdapter(private var items: List<StudentHistoryItem>) :
    RecyclerView.Adapter<StudentHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.course_name)
        val grade: TextView = view.findViewById(R.id.course_grade)
        val credits: TextView = view.findViewById(R.id.course_credits)
        val cycle: TextView = view.findViewById(R.id.course_cycle)
        val teacher: TextView = view.findViewById(R.id.course_teacher)
        val career: TextView = view.findViewById(R.id.course_career)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.courseName.text = item.courseName
        holder.grade.text = "Grade: ${"%.2f".format(item.grade)}"
        holder.credits.text = "Credits: ${item.credits}"
        holder.cycle.text = "Cycle: ${item.cycle}"
        holder.teacher.text = "Teacher: ${item.teacher}"
        holder.career.text = "Career: ${item.careerName}"
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<StudentHistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

data class StudentHistoryItem(
    val courseName: String,
    val grade: Double,
    val credits: Int,
    val cycle: String,
    val teacher: String,
    val careerName: String
)