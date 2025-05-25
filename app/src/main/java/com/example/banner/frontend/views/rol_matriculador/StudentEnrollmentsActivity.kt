package com.example.banner.frontend.views.rol_matriculador

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class StudentEnrollmentsActivity : AppCompatActivity() {
    private lateinit var enrollmentAdapter: EnrollmentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var studentName: String
    private var studentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_enrollments)

        studentId = intent.getIntExtra("student_id", -1)
        studentName = intent.getStringExtra("student_name") ?: "Desconocido"

        findViewById<TextView>(R.id.tvStudentName).text = "Enrollments of: $studentName"

        recyclerView = findViewById(R.id.rvEnrollments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        enrollmentAdapter = EnrollmentAdapter(mutableListOf(),
            onDelete = { enrollment ->
                deleteEnrollment(enrollment)
            })

        recyclerView.adapter = enrollmentAdapter

        findViewById<Button>(R.id.btnAddEnrollment).setOnClickListener {
            showAddEnrollmentDialog()
        }

        loadEnrollments()
    }

    private fun loadEnrollments() {
        Thread {
            try {
                val response = HttpHelper.get("enrollments")
                val json = JSONObject(response)
                if (json.getString("status") == "success") {
                    val data = json.getJSONArray("data")
                    val allEnrollments = Gson().fromJson<List<Enrollment_>>(
                        data.toString(),
                        object : TypeToken<List<Enrollment_>>() {}.type
                    )
                    val filtered = allEnrollments.filter { it.studentId == studentId }

                    runOnUiThread {
                        enrollmentAdapter.updateData(filtered)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun deleteEnrollment(enrollment: Enrollment_) {
        val context = this

        AlertDialog.Builder(context)
            .setTitle("Confirm deletion")
            .setMessage("¿Are you sure you want to delete this enrollment?")
            .setPositiveButton("Sí") { _, _ ->
                Thread {
                    try {
                        val url = "enrollments/${enrollment.studentId}/${enrollment.grupoId}"
                        val response = HttpHelper.deleteRequest(url)
                        Log.d("DELETE", response.toString())

                        runOnUiThread {
                            loadEnrollments()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddEnrollmentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_enrollment, null)
        val spinnerGroup = dialogView.findViewById<Spinner>(R.id.spinnerGroup)
        val etGrade = dialogView.findViewById<TextView>(R.id.etGrade)

        val groupsList = mutableListOf<Grupo_>()
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf("Loading...")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGroup.adapter = adapter

        // Cargar grupos desde la API
        Thread {
            try {
                val response = HttpHelper.get("groups")
                val json = JSONObject(response)
                val data = json.getJSONArray("data")

                val loadedGroups = Gson().fromJson<List<Grupo_>>(
                    data.toString(),
                    object : TypeToken<List<Grupo_>>() {}.type
                )

                runOnUiThread {
                    groupsList.clear()
                    groupsList.addAll(loadedGroups)

                    val groupNames = groupsList.map { "${it.id} - ${it.horario}" }
                    val groupAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        groupNames
                    )
                    groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerGroup.adapter = groupAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

        AlertDialog.Builder(this)
            .setTitle("Agregar Matrícula")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val selectedGroupIndex = spinnerGroup.selectedItemPosition
                val selectedGroup = if (selectedGroupIndex in groupsList.indices) groupsList[selectedGroupIndex] else null
                val grade = etGrade.text.toString().toDoubleOrNull()

                if (selectedGroup != null && grade != null) {
                    val newEnrollment = Enrollment_(studentId, selectedGroup.id, grade)

                    Thread {
                        try {
                            val json = Gson().toJson(newEnrollment)
                            val response = HttpHelper.postRequest("enrollments", json)
                            Log.d("POST", response.toString())

                            runOnUiThread {
                                loadEnrollments()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("Por favor, seleccione un grupo y una nota válida")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}