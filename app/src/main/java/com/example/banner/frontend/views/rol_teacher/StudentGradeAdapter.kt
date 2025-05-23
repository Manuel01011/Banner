package com.example.banner.frontend.views.teacher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.banner.R

class StudentGradeAdapter(
    private var enrollments: List<Enrollment_>,
    private var currentGroupId: Int,
    private val onGradeChanged: (Int, Int, Double) -> Unit
) : RecyclerView.Adapter<StudentGradeAdapter.StudentGradeViewHolder>() {

    private val localGradeChanges = mutableMapOf<Pair<Int, Int>, Double>()

    inner class StudentGradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.student_name)
        val studentId: TextView = itemView.findViewById(R.id.student_id)
        val gradeInput: EditText = itemView.findViewById(R.id.grade_input)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentGradeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_grade, parent, false)
        return StudentGradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentGradeViewHolder, position: Int) {
        val enrollment = enrollments[position]
        val key = Pair(enrollment.studentId, enrollment.grupoId)

        holder.studentId.text = "ID: ${enrollment.studentId}"

        // Evita que se dispare el listener por `setText`
        holder.gradeInput.setOnFocusChangeListener(null)
        holder.gradeInput.setOnEditorActionListener(null)

        val grade = localGradeChanges[key] ?: enrollment.grade ?: 0.0

        // Usa tags para evitar reentradas no deseadas
        if (holder.gradeInput.tag != key || holder.gradeInput.text.toString() != grade.toString()) {
            holder.gradeInput.setText(grade.toString())
            holder.gradeInput.tag = key
        }

        holder.gradeInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateGrade(enrollment, holder.gradeInput.text.toString())
            }
        }

        holder.gradeInput.setOnEditorActionListener { _, _, _ ->
            updateGrade(enrollment, holder.gradeInput.text.toString())
            false
        }
    }

    private fun updateGrade(enrollment: Enrollment_, newGradeText: String) {
        val key = Pair(enrollment.studentId, enrollment.grupoId)
        val newGrade = newGradeText.toDoubleOrNull() ?: 0.0
        val currentGrade = localGradeChanges[key] ?: enrollment.grade ?: 0.0

        if (currentGrade != newGrade) {
            localGradeChanges[key] = newGrade
            onGradeChanged(enrollment.studentId, enrollment.grupoId, newGrade)
        }
    }

    override fun getItemCount(): Int = enrollments.size

    fun updateData(newEnrollments: List<Enrollment_>, newGroupId: Int) {
        this.enrollments = newEnrollments
        this.currentGroupId = newGroupId
        notifyDataSetChanged()
    }

    fun getModifiedGrades(): Map<Pair<Int, Int>, Double> {
        return localGradeChanges.toMap()
    }

    fun clearChanges(groupId: Int) {
        localGradeChanges.keys.filter { it.second == groupId }.forEach { localGradeChanges.remove(it) }
    }
}