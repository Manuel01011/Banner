package com.example.banner.frontend.views.rol_matriculador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.banner.R

class EnrollmentAdapter(
    private var enrollments: MutableList<Enrollment_>,
    private val onDelete: (Enrollment_) -> Unit
) : RecyclerView.Adapter<EnrollmentAdapter.EnrollmentViewHolder>() {

    class EnrollmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGrupo: TextView = view.findViewById(R.id.tvGrupo)
        val tvNota: TextView = view.findViewById(R.id.tvNota)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_enrollment, parent, false)
        return EnrollmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        val enrollment = enrollments[position]
        holder.tvGrupo.text = "Grupo ID: ${enrollment.grupoId}"
        holder.tvNota.text = "Nota: ${enrollment.grade}"
        holder.btnDelete.setOnClickListener {
            onDelete(enrollment)
        }
    }

    override fun getItemCount(): Int = enrollments.size

    fun updateData(newData: List<Enrollment_>) {
        enrollments.clear()
        enrollments.addAll(newData)
        notifyDataSetChanged()
    }
}